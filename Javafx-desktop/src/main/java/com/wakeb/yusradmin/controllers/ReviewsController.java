package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.models.ReviewRow;
import com.wakeb.yusradmin.models.ReviewRequestDTO;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
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
import java.util.ResourceBundle;

public class ReviewsController implements Initializable {

    // === Table + Columns ===
    @FXML private TableView<ReviewRow> table;
    @FXML private TableColumn<ReviewRow, Long> colId;
    @FXML private TableColumn<ReviewRow, String> colUsername;
    @FXML private TableColumn<ReviewRow, String> colPlace;
    @FXML private TableColumn<ReviewRow, Integer> colReview;
    @FXML private TableColumn<ReviewRow, String> colDescription;
    @FXML private TableColumn<ReviewRow, String> colDate;
    @FXML private TableColumn<ReviewRow, String> colStatus;
    @FXML private TableColumn<ReviewRow, ReviewRow> colActions;

    @FXML private Pagination pagination;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn, nextBtn;

    @FXML private TextField searchField;
    @FXML private Button searchBtn;

    private ReviewService reviewService;

    private final ObservableList<ReviewRow> data = FXCollections.observableArrayList();

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 1;

    public void setService(ReviewService service) {
        this.reviewService = service;
        loadPage(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Map table columns
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colUsername.setCellValueFactory(c -> c.getValue().usernameProperty());
        colPlace.setCellValueFactory(c -> c.getValue().placeProperty());
        colReview.setCellValueFactory(c -> c.getValue().ratingProperty().asObject());
        colDescription.setCellValueFactory(c -> c.getValue().descriptionProperty());
        colDate.setCellValueFactory(c -> c.getValue().dateProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());

        // Actions column
        colActions.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button editBtn = new Button("تعديل");
            private final Button deleteBtn = new Button("حذف");
            private final HBox box = new HBox(6, editBtn, deleteBtn);
            { box.setAlignment(Pos.CENTER); }
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

        // Search field: digits-only
        searchField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null));
        searchBtn.setOnAction(e -> doSearchById());
        searchField.setOnAction(e -> doSearchById());

        // Initialize pagination
        initializePagination();
    }

    private void initializePagination() {
        // Pagination page index change
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> loadPage(newVal.intValue()));

        // Prev/Next buttons
        prevBtn.setOnAction(e -> {
            if (currentPage > 0) pagination.setCurrentPageIndex(currentPage - 1);
        });
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages - 1) pagination.setCurrentPageIndex(currentPage + 1);
        });
    }

    private void loadPage(int page) {
        if (reviewService == null) return;

        Task<PageResponse<ReviewResponseDTO>> task = reviewService.getAllReviewsAsync(page, pageSize);
        task.setOnSucceeded(e -> {
            PageResponse<ReviewResponseDTO> response = task.getValue();
            if (response != null) {
                data.setAll(response.getContent().stream().map(this::toRow).toList());
                totalPages = response.getTotalPages();
                currentPage = page;
                table.refresh();
                updatePaginationUI();
            }
        });
        task.setOnFailed(e -> {
            showError("فشل في تحميل التقييمات", task.getException());
            updatePaginationUI();
        });
        new Thread(task).start();
    }

    private void updatePaginationUI() {
        pageInfo.setText("صفحة " + (currentPage + 1) + " / " + totalPages);
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= totalPages - 1);
        pagination.setPageCount(totalPages);
        pagination.setCurrentPageIndex(currentPage);
    }

    // Convert DTO -> TableRow
    private ReviewRow toRow(ReviewResponseDTO dto) {
        return new ReviewRow(
                dto.id == null ? 0 : dto.id,
                dto.userName == null ? "" : dto.userName,
                dto.placeName == null ? "" : dto.placeName,
                dto.rating == null ? 0 : dto.rating,
                dto.description == null ? "" : dto.description,
                dto.reviewDate == null ? "" : dto.reviewDate,
                dto.status == null ? "" : dto.status
        );
    }

    // Search by ID
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

    private void onDeleteReview(ReviewRow row) {
        if (!confirm("تأكيد الحذف", "هل أنت متأكد من حذف التقييم رقم " + row.getId() + "؟")) return;
        Task<Void> task = reviewService.deleteReviewAsync(row.getId());
        task.setOnSucceeded(e -> {
            data.remove(row);
            doSearchById(); // keep filter
            table.refresh();
        });
        task.setOnFailed(e -> showError("فشل في حذف التقييم", task.getException()));
        new Thread(task, "delete-review-" + row.getId()).start();
    }

    private void onEditReview(ReviewRow row) {
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
//        gp.addRow(1, new Label("الحالة:"), statusBox);
        gp.addRow(2, new Label("الوصف:"), descArea);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                ReviewRequestDTO dto = new ReviewRequestDTO();
                String rtxt = ratingField.getText().trim();
                dto.rating = rtxt.isEmpty() ? null : Integer.parseInt(rtxt);
                dto.status = statusBox.getSelectionModel().getSelectedItem();
                dto.description = descArea.getText().isBlank() ? null : descArea.getText();
                return dto;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dto -> {
            Task<ReviewResponseDTO> task = reviewService.updateReviewAsync(row.getId(), dto);
            task.setOnSucceeded(e -> {
                ReviewResponseDTO updated = task.getValue();
                if (updated.rating != null) row.setRating(updated.rating);
                if (updated.status != null) row.setStatus(updated.status);
                if (updated.description != null) row.setDescription(updated.description);
                else row.setDescription(dto.description);
                if (updated.reviewDate != null) row.setDate(updated.reviewDate);
                table.refresh();
            });
            task.setOnFailed(e -> showError("فشل في تحديث التقييم", task.getException()));
            new Thread(task, "update-review-" + row.getId()).start();
        });
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
