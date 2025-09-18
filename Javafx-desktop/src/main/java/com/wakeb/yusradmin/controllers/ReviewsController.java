package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.models.ReviewRequestDTO;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
import com.wakeb.yusradmin.models.ReviewRow;
import com.wakeb.yusradmin.services.ReviewService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ReviewsController {

    // === UI ===
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @FXML private TableView<ReviewRow> table;
    @FXML private TableColumn<ReviewRow, Long>    colId;
    @FXML private TableColumn<ReviewRow, String>  colUsername;
    @FXML private TableColumn<ReviewRow, String>  colPlace;
    @FXML private TableColumn<ReviewRow, Integer> colReview;
    @FXML private TableColumn<ReviewRow, String>  colDescription;
    @FXML private TableColumn<ReviewRow, String>  colDate;
    @FXML private TableColumn<ReviewRow, String>  colStatus;
    @FXML private TableColumn<ReviewRow, ReviewRow> colActions;

    @FXML private Button prevBtn, nextBtn;
    @FXML private Label pageInfo;

    // === State ===
    private final ObservableList<ReviewRow> data = FXCollections.observableArrayList();
    private ReviewService reviewService;

    private int currentPage = 0;
    private int pageSize    = 10;
    private int totalPages  = 1;

    public void setService(ReviewService service) {
        this.reviewService = service;
        loadPage(0);
    }

    @FXML
    public void initialize() {
        // Table mapping
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colUsername.setCellValueFactory(c -> c.getValue().usernameProperty());
        colPlace.setCellValueFactory(c -> c.getValue().placeProperty());
        colReview.setCellValueFactory(c -> c.getValue().ratingProperty().asObject());
        colDescription.setCellValueFactory(c -> c.getValue().descriptionProperty());
        colDate.setCellValueFactory(c -> c.getValue().dateProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());

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
                editBtn.getStyleClass().add("card-button");
                deleteBtn.getStyleClass().add("card-button");
                editBtn.setOnAction(e -> onEditReview(row));
                deleteBtn.setOnAction(e -> onDeleteReview(row));
                setGraphic(box);
            }
        });

        table.setItems(data);
        table.setPlaceholder(new Label("جاري التحميل..."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Search handlers (سيرفري)
        searchField.setOnAction(e -> refresh());
        if (searchButton != null) searchButton.setOnAction(e -> refresh());

        updatePagingUI(false);
    }

    // === Paging buttons (نفس Users) ===
    @FXML private void prevPage() { if (currentPage > 0) loadPage(currentPage - 1); }
    @FXML private void nextPage() { if (currentPage + 1 < totalPages) loadPage(currentPage + 1); }

    // === Load normal page ===
    private void loadPage(int page) {
        if (reviewService == null) return;

        String q = searchField.getText();
        if (q != null && !q.isBlank()) { // إذا فيه بحث، خلّه عبر refresh()
            refresh();
            return;
        }


        Task<PageResponse<ReviewResponseDTO>> task = reviewService.getAllReviewsAsync(page, pageSize);

        task.setOnSucceeded(e -> {
            PageResponse<ReviewResponseDTO> res = task.getValue();
            data.setAll(res.getContent().stream().map(this::toRow).toList());
            currentPage = page;
            totalPages  = Math.max(res.getTotalPages(), 1);
            updatePagingUI(true);
        });

        task.setOnFailed(e -> {
            showError("فشل في تحميل التقييمات", task.getException());
            updatePagingUI(false);
        });

        updatePagingUI(false);
        new Thread(task, "reviews-page").start();
    }

    private void updatePagingUI(boolean loaded) {
        if (!loaded) {
            pageInfo.setText("...");
            prevBtn.setDisable(true);
            nextBtn.setDisable(true);
            return;
        }
        pageInfo.setText("صفحة " + (currentPage + 1) + " / " + totalPages);
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage + 1 >= totalPages);
    }

    // === Server-side search by place name ===
    public void refresh() {
        if (reviewService == null) return;
        String q = searchField.getText();

        if (q != null && !q.isBlank()) {
            Task<PageResponse<ReviewResponseDTO>> task =
                    reviewService.getReviewByPlaceNameAsync(q.trim(), 0, pageSize);

            task.setOnSucceeded(e -> {
                PageResponse<ReviewResponseDTO> res = task.getValue();
                if (res != null) {
                    data.setAll(res.getContent().stream().map(this::toRow).toList());
                    currentPage = res.currentPage;
                    totalPages  = Math.max(res.getTotalPages(), 1);
                    pageInfo.setText("نتائج: " + res.getTotalElements() +
                            " (صفحة " + (currentPage + 1) + " / " + totalPages + ")");
                    prevBtn.setDisable(currentPage == 0);
                    nextBtn.setDisable(currentPage + 1 >= totalPages);
                } else {
                    data.clear();
                    pageInfo.setText("لا توجد نتائج");
                    prevBtn.setDisable(true);
                    nextBtn.setDisable(true);
                }
            });

            task.setOnFailed(e -> showError("فشل البحث", task.getException()));
            new Thread(task, "reviews-search").start();

        } else {
            loadPage(0);
        }
    }

    // === Convert DTO -> Row ===
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

    // === CRUD ===
    private void onDeleteReview(ReviewRow row) {
        if (!confirm("تأكيد الحذف", "هل أنت متأكد من حذف التقييم رقم " + row.getId() + "؟")) return;
        Task<Void> task = reviewService.deleteReviewAsync(row.getId());
        task.setOnSucceeded(e -> {
            if (data.size() == 1 && currentPage > 0) loadPage(currentPage - 1);
            else loadPage(currentPage);
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
        ratingField.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d{0,2}") ? c : null));

        TextArea descArea = new TextArea(row.getDescription() == null ? "" : row.getDescription());
        descArea.setPromptText("الوصف (اختياري)");
        descArea.setPrefRowCount(3);

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("التقييم:"), ratingField);
        gp.addRow(1, new Label("الوصف:"),  descArea);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                ReviewRequestDTO dto = new ReviewRequestDTO();
                String rtxt = ratingField.getText().trim();
                dto.rating = rtxt.isEmpty() ? null : Integer.parseInt(rtxt);
                dto.description = descArea.getText().isBlank() ? null : descArea.getText();
                return dto;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dto -> {
            Task<ReviewResponseDTO> task = reviewService.updateReviewAsync(row.getId(), dto);
            task.setOnSucceeded(e -> {
                ReviewResponseDTO updated = task.getValue();
                if (updated.rating != null)      row.setRating(updated.rating);
                if (updated.description != null) row.setDescription(updated.description);
                if (updated.reviewDate != null)  row.setDate(updated.reviewDate);
                if (updated.status != null)      row.setStatus(updated.status);
                table.refresh();
            });
            task.setOnFailed(e -> showError("فشل في تحديث التقييم", task.getException()));
            new Thread(task, "update-review-" + row.getId()).start();
        });
    }

    // === Helpers ===
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