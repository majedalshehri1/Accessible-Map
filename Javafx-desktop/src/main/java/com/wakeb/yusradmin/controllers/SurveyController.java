package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.*;
import com.wakeb.yusradmin.services.SurveyService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyController {

    @FXML private TableView<SurveyRow> table;
    @FXML private TableColumn<SurveyRow, Long> colId;
    @FXML private TableColumn<SurveyRow, Long> colUserId;
    @FXML private TableColumn<SurveyRow, String> colUserName;
    @FXML private TableColumn<SurveyRow, Integer> colRating;
    @FXML private TableColumn<SurveyRow, String> colDescription;
    @FXML private TableColumn<SurveyRow, Boolean> colRead;
    @FXML private TableColumn<SurveyRow, Void> colActions;

    @FXML private Button btnRefresh;
    @FXML private ToggleButton btnShowUnreadOnly;
    @FXML private Label lblStatus;
    @FXML private ProgressIndicator spinner;

    private final ObservableList<SurveyRow> masterData = FXCollections.observableArrayList();
    private final ObservableList<SurveyRow> filteredData = FXCollections.observableArrayList();

    // Use your actual backend base URL
    private final SurveyService api = new SurveyService("http://localhost:8081");

    @FXML
    private void initialize() {
        // table editable to allow checkbox edits
        table.setEditable(true);

        // columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // READ? column: editable checkbox bound to readProperty()
        colRead.setEditable(true);
        colRead.setCellValueFactory(cd -> cd.getValue().readProperty());
        colRead.setCellFactory(CheckBoxTableCell.forTableColumn(colRead)); // toggles model automatically

        // Actions column: ONLY Delete now
        addActionsColumn();

        table.setItems(filteredData);

        // filter toggle
        btnShowUnreadOnly.selectedProperty().addListener((obs, was, isNow) -> refilter());

        // refresh button
        btnRefresh.setOnAction(e -> loadData());

        // green highlight for read rows
        setupRowHighlighting();

        // initial load
        loadData();
    }

    private void addActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("حذف");
            private final HBox box = new HBox(8, btnDelete);

            {
                btnDelete.getStyleClass().add("card-button");
                btnDelete.getStyleClass().add("card-button__delete"); // red styling
                box.setAlignment(Pos.CENTER);

                btnDelete.setOnAction(e -> {
                    SurveyRow row = getTableView().getItems().get(getIndex());
                    if (row == null) return;

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("تأكيد الحذف");
                    confirm.setHeaderText("حذف الاستطلاع رقم " + row.getId() + "؟");
                    confirm.setContentText("لا يمكن التراجع عن هذا الاجراء");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            deleteSurvey(row);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadData() {
        spinner.setVisible(true);
        setStatus("تحميل...");
        Task<List<SurveyRow>> task = new Task<>() {
            @Override protected List<SurveyRow> call() throws Exception {
                // fetchAll() should already set row.setRead(...) from backend JSON if available
                return api.fetchAll();
            }
        };
        task.setOnSucceeded(e -> {
            List<SurveyRow> items = task.getValue();

            // When checkbox toggles: refilter/refresh + push to backend (optimistic, with rollback)
            for (SurveyRow r : items) {
                r.readProperty().addListener((obs, oldV, newV) -> {
                    // Immediate UI response
                    refilter();
                    table.refresh();

                    // Push to backend
                    Task<Void> push = new Task<>() {
                        @Override protected Void call() throws Exception {
                            api.updateRead(r.getId(), newV);  // <-- requires this method in SurveyService
                            return null;
                        }
                    };
                    push.setOnFailed(ev -> {
                        // Roll back UI if server update failed
                        r.setRead(oldV);
                        refilter();
                        table.refresh();
                        setStatus("Failed to update read: " + push.getException().getMessage());
                    });
                    new Thread(push, "survey-update-read-" + r.getId()).start();
                });
            }

            masterData.setAll(items);
            refilter();
            spinner.setVisible(false);
            setStatus("تم تحميل " + items.size() + "استطلاع");
        });
        task.setOnFailed(e -> {
            spinner.setVisible(false);
            setStatus("فشل التحميل " + task.getException().getMessage());
            task.getException().printStackTrace();
        });
        new Thread(task, "surveys-load").start();
    }

    private void deleteSurvey(SurveyRow row) {
        spinner.setVisible(true);
        setStatus("يتم الحذف " + row.getId() + "...");
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                api.deleteById(row.getId());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            masterData.removeIf(s -> s.getId().equals(row.getId()));
            refilter();
            spinner.setVisible(false);
            setStatus("الاستطلاع رقم " + row.getId() + " حُذف");
        });
        task.setOnFailed(e -> {
            spinner.setVisible(false);
            setStatus("فشل عملية الحذف " + task.getException().getMessage());
            task.getException().printStackTrace();
        });
        new Thread(task, "surveys-delete").start();
    }

    private void refilter() {
        boolean unreadOnly = btnShowUnreadOnly.isSelected();
        if (!unreadOnly) {
            filteredData.setAll(masterData);
        } else {
            filteredData.setAll(masterData.stream()
                    .filter(s -> !s.isRead())
                    .collect(Collectors.toList()));
        }
        table.refresh();
    }

    private void setupRowHighlighting() {
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(SurveyRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle(item.isRead()
                            ? "-fx-background-color: rgba(46,204,113,0.20);" // light transparent green
                            : "");
                }
            }
        });
    }

    private void setStatus(String msg) {
        Platform.runLater(() -> lblStatus.setText(msg));
    }
}
