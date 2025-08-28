// com.wakeb.yusradmin.controllers.UsersController.java
package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.User;
import com.wakeb.yusradmin.services.UserService;
import com.wakeb.yusradmin.util.FXUtil;
import com.wakeb.yusradmin.util.UserActionCell;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class UsersController {

    @FXML private TextField searchField;
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Long>   colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void>   colActions;

    private final ObservableList<User> data = FXCollections.observableArrayList();
    private UserService service;

    private static final double[] COLUMN_WIDTH_PERCENTAGES = {
            0.12, // ID
            0.15, // Name
            0.12, // Email
            0.22, // Role
            0.12, // Status
            0.23  // Actions
    };

    public void setService(UserService s) {
        this.service = s;
        refresh();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserEmail()));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBlocked() ? "محظور" : "نشط"));

        table.setItems(data);
        table.setPlaceholder(new Label("No users"));

        colActions.setCellFactory(tc -> new UserActionCell(
                this::onBlockToggle, this::onEdit, this::onDelete
        ));

        bindColumnsWidth();

        searchField.setOnAction(e -> refresh());
    }

    private void bindColumnsWidth() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colActions.setMinWidth(240);
        colActions.setPrefWidth(240);
        colActions.setMaxWidth(240);
    }

    public void refresh() {
        if (service == null) return;

        String q = searchField.getText();
        Task<List<User>> task = new Task<>() {
            @Override protected List<User> call() throws Exception {
                if (q != null && !q.isBlank()) return service.search(q);
                else return service.list();
            }
        };

        task.setOnSucceeded(e -> data.setAll(task.getValue()));
        task.setOnFailed(e -> FXUtil.error("Load Users Failed", task.getException().getMessage()));

        new Thread(task, "load-users").start();
    }

    private void onBlockToggle(User u) {
        Task<Void> t = new Task<>() {
            @Override protected Void call() throws Exception {
                if (u.isBlocked()) service.unblock(u.getId());
                else service.block(u.getId());
                return null;
            }
        };
        t.setOnSucceeded(e -> refresh());
        t.setOnFailed(e -> FXUtil.error("Block/Unblock Failed", t.getException().getMessage()));
        new Thread(t, "block-toggle").start();
    }

    private void onEdit(User u) {
        TextInputDialog dlg = new TextInputDialog(u.getUserName());
        dlg.setTitle("تعديل المستخدم");
        dlg.setHeaderText(null);                 // بدون هيدر
        dlg.setGraphic(null);                    // بدون أيقونة افتراضية
        dlg.setContentText("الاسم الجديد");

        DialogPane pane = dlg.getDialogPane();
        pane.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        pane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        pane.getStyleClass().add("modern-dialog");

        if (table.getScene() != null) {
            dlg.initOwner(table.getScene().getWindow());
        }

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
            t.setOnSucceeded(e -> refresh());
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
        t.setOnSucceeded(e -> refresh());
        t.setOnFailed(e -> FXUtil.error("Delete Failed", t.getException().getMessage()));
        new Thread(t, "delete-user").start();
    }
}