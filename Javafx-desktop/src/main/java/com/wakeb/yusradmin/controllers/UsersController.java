package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.PaginatedResponse;
import com.wakeb.yusradmin.models.User;
import com.wakeb.yusradmin.services.UserService;
import com.wakeb.yusradmin.util.FXUtil;
import com.wakeb.yusradmin.util.UserActionCell;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class UsersController {

    @FXML private TextField searchField;
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Long>   colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void>   colActions;

    @FXML private Button prevBtn, nextBtn;
    @FXML private Label pageInfo;

    private int currentPage = 0;
    private int pageSize = 11;
    private int totalPages;

    private final ObservableList<User> data = FXCollections.observableArrayList();
    private UserService service;

    public void setService(UserService s) {
        this.service = s;
        loadPage(0);
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserEmail()));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBlocked() ? "محظور" : "نشط"));

        table.setItems(data);
        table.setPlaceholder(new Label("جاري التحميل..."));

        colActions.setCellFactory(tc -> new UserActionCell(
                this::onBlockToggle, this::onEdit, this::onDelete
        ));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);        colActions.setMinWidth(240);
        colActions.setPrefWidth(240);
        colActions.setMaxWidth(240);

        searchField.setOnAction(e -> refresh());
    }


    @FXML
    private void prevPage() {
        if (currentPage > 0) loadPage(currentPage - 1);
    }

    @FXML
    private void nextPage() {
        if (currentPage + 1 < totalPages) loadPage(currentPage + 1);
    }

    private void loadPage(int page) {
        if (service == null) return;

        String q = searchField.getText();
        if (q != null && !q.isBlank()) {
            // Use paginated search
            Task<PaginatedResponse<User>> task = new Task<>() {
                @Override
                protected PaginatedResponse<User> call() throws Exception {
                    return service.search(q, page, pageSize);
                }
            };

            task.setOnSucceeded(e -> {
                PaginatedResponse<User> p = task.getValue();
                currentPage = p.getCurrentPage();
                totalPages = Math.max(p.getTotalPages(), 1);
                data.setAll(p.getContent());
                updatePagingUI(true);
            });

            task.setOnFailed(e -> {
                FXUtil.error("Search Users Failed", task.getException().getMessage());
                updatePagingUI(false);
            });

            updatePagingUI(false);
            new Thread(task, "users-search-page").start();
            return;
        }

        Task<PaginatedResponse<User>> task = new Task<>() {
            @Override
            protected PaginatedResponse<User> call() throws Exception {
                return service.list(page, pageSize);
            }
        };

        task.setOnSucceeded(e -> {
            PaginatedResponse<User> p = task.getValue();
            currentPage = p.getCurrentPage();
            totalPages = Math.max(p.getTotalPages(), 1);
            data.setAll(p.getContent());
            updatePagingUI(true);
        });

        task.setOnFailed(e -> {
            FXUtil.error("Load Users Failed", task.getException().getMessage());
            updatePagingUI(false);
        });

        updatePagingUI(false);
        new Thread(task, "users-page").start();
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


    public void refresh() {
        if (service == null) return;

        String q = searchField.getText();
        if (q != null && !q.isBlank()) {
            // Use paginated search starting from page 0
            Task<PaginatedResponse<User>> task = new Task<>() {
                @Override
                protected PaginatedResponse<User> call() throws Exception {
                    return service.search(q, 0, pageSize);
                }
            };

            task.setOnSucceeded(e -> {
                PaginatedResponse<User> p = task.getValue();
                data.setAll(p.getContent());  // Use getter method
                currentPage = 0;
                totalPages = Math.max(p.getTotalPages(), 1);  // Use getter method
                pageInfo.setText("نتائج: " + data.size() + " (صفحة " + (currentPage + 1) + " / " + totalPages + ")");
                prevBtn.setDisable(currentPage == 0);
                nextBtn.setDisable(currentPage + 1 >= totalPages);
            });

            task.setOnFailed(e -> FXUtil.error("Search Users Failed", task.getException().getMessage()));
            new Thread(task, "users-search-refresh").start();
        } else {
            loadPage(0); // Load first page without search
        }
    }


    private void onBlockToggle(User u) {
        Task<Void> t = new Task<>() {
            @Override protected Void call() throws Exception {
                if (u.isBlocked()) service.unblock(u.getId());
                else service.block(u.getId());
                return null;
            }
        };
        t.setOnSucceeded(e -> loadPage(currentPage)); // نرجّع نفس الصفحة
        t.setOnFailed(e -> FXUtil.error("Block/Unblock Failed", t.getException().getMessage()));
        new Thread(t, "block-toggle").start();
    }

    private void onEdit(User u) {
        TextInputDialog dlg = new TextInputDialog(u.getUserName());
        dlg.setTitle("تعديل المستخدم");
        dlg.setHeaderText(null);
        dlg.setGraphic(null);
        dlg.setContentText("الاسم الجديد");

        DialogPane pane = dlg.getDialogPane();
        pane.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        pane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        pane.getStyleClass().add("modern-dialog");

        if (table.getScene() != null) dlg.initOwner(table.getScene().getWindow());

        Button okBtn = (Button) pane.lookupButton(ButtonType.OK);
        okBtn.setText("حفظ");
        okBtn.getStyleClass().addAll("button-primary");

        Button cancelBtn = (Button) pane.lookupButton(ButtonType.CANCEL);
        cancelBtn.setText("إلغاء");
        cancelBtn.getStyleClass().addAll("button-secondary");

        dlg.getEditor().setPromptText("أدخل الاسم");

        dlg.showAndWait().ifPresent(name -> {
            u.setUserName(name);
            Task<Void> t = new Task<>() {
                @Override protected Void call() throws Exception {
                    service.update(u);
                    return null;
                }
            };
            t.setOnSucceeded(e -> loadPage(currentPage));
            t.setOnFailed(e -> FXUtil.error("Update Failed", t.getException().getMessage()));
            new Thread(t, "update-user").start();
        });
    }

    private void onDelete(User u) {
        if (!FXUtil.confirm("Confirm Delete", "Delete user \"" + u.getUserName() + "\"?")) return;
        Task<Void> t = new Task<>() {
            @Override protected Void call() throws Exception {
                service.delete(u.getId());
                return null;
            }
        };
        t.setOnSucceeded(e -> {
            if (data.size() == 1 && currentPage > 0) loadPage(currentPage - 1);
            else loadPage(currentPage);
        });
        t.setOnFailed(e -> FXUtil.error("Delete Failed", t.getException().getMessage()));
        new Thread(t, "delete-user").start();
    }
}