package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.ReviewRow;
import com.wakeb.yusradmin.models.ReviewRequestDTO;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
import com.wakeb.yusradmin.services.AuthService;
import com.wakeb.yusradmin.services.ReviewService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReviewsController implements Initializable {

    // === Table + Columns ===
    @FXML private TableView<ReviewRow> table;
    @FXML private TableColumn<ReviewRow, Long>    colId;
    @FXML private TableColumn<ReviewRow, String>  colUsername;
    @FXML private TableColumn<ReviewRow, String>  colPlace;
    @FXML private TableColumn<ReviewRow, Integer> colReview;
    @FXML private TableColumn<ReviewRow, String> colDescription;
    @FXML private TableColumn<ReviewRow, String>  colDate;
    @FXML private TableColumn<ReviewRow, String>  colStatus;
    @FXML private TableColumn<ReviewRow, ReviewRow> colActions;

    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    private ReviewService reviewService;

    private final ObservableList<ReviewRow> data = FXCollections.observableArrayList();

    // Services (use your AuthService and ReviewService)

    public void setService(ReviewService service) {
        this.reviewService = service;
        loadAllReviewsAsync(); // Load reviews when service is set
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // map columns
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colUsername.setCellValueFactory(c -> c.getValue().usernameProperty());
        colPlace.setCellValueFactory(c -> c.getValue().placeProperty());
        colReview.setCellValueFactory(c -> c.getValue().ratingProperty().asObject());
        colDescription.setCellValueFactory(c -> c.getValue().descriptionProperty());
        colDate.setCellValueFactory(c -> c.getValue().dateProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());

        // stable actions column (bind row object, not index)
        colActions.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button editBtn   = new Button("تعديل");
            private final Button deleteBtn = new Button("حذف");
            private final HBox box         = new HBox(6, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color:#e5e7eb; -fx-background-radius:6;");
                deleteBtn.setStyle("-fx-background-color:#fee2e2; -fx-text-fill:#991b1b; -fx-background-radius:6;");
            }
            @Override
            protected void updateItem(ReviewRow row, boolean empty) {
                super.updateItem(row, empty);
                if (empty || row == null) { setGraphic(null); return; }
                editBtn.setOnAction(e -> onEditReview(row));
                deleteBtn.setOnAction(e -> onDeleteReview(row));
                setGraphic(box);
            }
        });

        table.setItems(data);

        // digits-only search by ID
        searchField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null));
        searchBtn.setOnAction(e -> doSearchById());
        searchField.setOnAction(e -> doSearchById());

        // initial load from backend: GET /api/admin/all/reviews  :contentReference[oaicite:9]{index=9}
    }

    // ---------- API wiring ----------

    private void loadAllReviewsAsync() {
        Task<List<ReviewResponseDTO>> task = reviewService.getAllReviewsAsync(); // :contentReference[oaicite:10]{index=10}
        task.setOnSucceeded(e -> {
            List<ReviewResponseDTO> list = task.getValue();
            data.setAll(list.stream().map(this::toRow).toList());
            table.refresh();
        });
        task.setOnFailed(e -> showError("فشل في تحميل التقييمات", task.getException()));
        new Thread(task, "load-reviews").start();
    }

    private void onDeleteReview(ReviewRow row) {
        // DELETE /api/admin/delete/review/{id}  :contentReference[oaicite:11]{index=11}
        if (!confirm("تأكيد الحذف", "هل أنت متأكد من حذف التقييم رقم " + row.getId() + "؟")) return;
        Task<Void> task = reviewService.deleteReviewAsync(row.getId()); // :contentReference[oaicite:12]{index=12}
        task.setOnSucceeded(e -> {
            data.remove(row);
            doSearchById(); // keep filter if searching
            table.refresh();
        });
        task.setOnFailed(e -> showError("فشل في حذف التقييم", task.getException()));
        new Thread(task, "delete-review-" + row.getId()).start();
    }

    private void onEditReview(ReviewRow row) {
        // PUT /api/admin/update/review/{id} (ReviewRequestDTO)  :contentReference[oaicite:13]{index=13}
        Dialog<ReviewRequestDTO> dialog = new Dialog<>();
        dialog.setTitle("تعديل التقييم #" + row.getId());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField ratingField = new TextField(String.valueOf(row.getRating()));
        ratingField.setPromptText("التقييم (1-5)");
        ratingField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,2}") ? c : null));

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("نشط", "محظور");
        statusBox.getSelectionModel().select(row.getStatus() == null ? "نشط" : row.getStatus());

        TextArea descArea = new TextArea(row.getDescription() == null ? "" : row.getDescription());
        descArea.setPromptText("الوصف (اختياري)");
        descArea.setPrefRowCount(3);

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("التقييم:"), ratingField);
//        gp.addRow(1, new Label("الحالة:"),  statusBox);
        gp.addRow(2, new Label("الوصف:"),   descArea);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                ReviewRequestDTO dto = new ReviewRequestDTO(); // your front-end DTO class  :contentReference[oaicite:14]{index=14}
                String rtxt = ratingField.getText().trim();
                dto.rating = rtxt.isEmpty() ? null : Integer.parseInt(rtxt);
                dto.status = statusBox.getSelectionModel().getSelectedItem();
                dto.description = descArea.getText().isBlank() ? null : descArea.getText();
                // (other fields exist on DTO, but backend only needs what you’re updating)
                return dto;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dto -> {
            Task<ReviewResponseDTO> task = reviewService.updateReviewAsync(row.getId(), dto); // :contentReference[oaicite:15]{index=15}
            task.setOnSucceeded(e -> {
                ReviewResponseDTO updated = task.getValue();
                if (updated.rating != null)       row.setRating(updated.rating);
                if (updated.status != null)       row.setStatus(updated.status);
                if (updated.description != null)  row.setDescription(updated.description); // ✅ update الوصف
                else                              row.setDescription(dto.description);     // fallback if API omits it
                if (updated.reviewDate != null)   row.setDate(updated.reviewDate);
                table.refresh();
            });
            task.setOnFailed(e -> showError("فشل في تحديث التقييم", task.getException()));
            new Thread(task, "update-review-" + row.getId()).start();
        });
    }

    private ReviewRow toRow(ReviewResponseDTO dto) {
        // Make sure your ReviewRow has a constructor or setters for description
        ReviewRow row = new ReviewRow(
                dto.id == null ? 0 : dto.id,
                dto.userName == null ? "" : dto.userName,
                dto.placeName == null ? "" : dto.placeName,
                dto.rating == null ? 0 : dto.rating,
                dto.description == null ? "" : dto.description,   // ✅ الوصف
                dto.reviewDate == null ? "" : dto.reviewDate,
                dto.status == null ? "" : dto.status
        );
        return row;
    }

    // ---------- Search (exact ID) ----------

    private void doSearchById() {
        String txt = searchField.getText().trim();
        if (txt.isEmpty()) {
            table.setItems(data);
            table.getSelectionModel().clearSelection();
            table.refresh();
            return;
        }
        long wantedId = Long.parseLong(txt);
        ObservableList<ReviewRow> filtered = data.filtered(r -> r.getId() == wantedId);
        table.setItems(filtered);
        table.getSelectionModel().clearSelection();
        if (!filtered.isEmpty()) {
            table.getSelectionModel().select(0);
            table.scrollTo(0);
        }
        table.refresh();
    }

    // ---------- Helpers ----------

    private void showError(String header, Throwable ex) {
        ex.printStackTrace();
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("خطأ");
        a.setHeaderText(header);
        a.setContentText(ex.getMessage());
        a.show();
    }

    private boolean confirm(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setTitle(title);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}