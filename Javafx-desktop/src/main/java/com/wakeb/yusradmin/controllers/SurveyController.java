package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.SurveyRow;
import com.wakeb.yusradmin.services.SurveyService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
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
    @FXML private TableColumn<SurveyRow, Integer> colRating;
    @FXML private TableColumn<SurveyRow, String> colDescription;
    @FXML private TableColumn<SurveyRow, Boolean> colRead;
    @FXML private TableColumn<SurveyRow, Void> colActions;

    @FXML private Button btnRefresh;
    @FXML private CheckBox chkShowUnreadOnly;
    @FXML private Label lblStatus;
    @FXML private ProgressIndicator spinner;

    private final ObservableList<SurveyRow> masterData = FXCollections.observableArrayList();
    private final ObservableList<SurveyRow> filteredData = FXCollections.observableArrayList();

    // TODO: set your API base URL here (same you use in the app elsewhere)
    private final SurveyService api = new SurveyService("http://localhost:8080"); // <- change if needed

    @FXML
    private void initialize() {
        // Setup columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colRead.setCellValueFactory(param -> Bindings.createObjectBinding(param.getValue()::isRead));
        colRead.setCellFactory(tc -> {
            CheckBoxTableCell<SurveyRow, Boolean> cell = new CheckBoxTableCell<>();
            cell.setEditable(false);
            return cell;
        });

        addActionsColumn();

        table.setItems(filteredData);

        // filter toggle
        chkShowUnreadOnly.selectedProperty().addListener((obs, was, isNow) -> refilter());

        // refresh button
        btnRefresh.setOnAction(e -> loadData());

        // initial load
        loadData();
    }

    private void addActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnMarkRead = new Button("Mark as read");
            private final Button btnDelete = new Button("Delete");
            private final HBox box = new HBox(8, btnMarkRead, btnDelete);

            {
                btnMarkRead.setOnAction(e -> {
                    SurveyRow row = getTableView().getItems().get(getIndex());
                    if (row != null) {
                        row.setRead(true);
                        // If later you add backend endpoint, call it here (PUT).
                        table.refresh();
                        refilter();
                        setStatus("Survey " + row.getId() + " marked as read (local).");
                    }
                });

                btnDelete.setOnAction(e -> {
                    SurveyRow row = getTableView().getItems().get(getIndex());
                    if (row == null) return;

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Delete");
                    confirm.setHeaderText("Delete survey ID " + row.getId() + "?");
                    confirm.setContentText("This cannot be undone.");
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    private void loadData() {
        spinner.setVisible(true);
        setStatus("Loading...");
        Task<List<SurveyRow>> task = new Task<>() {
            @Override protected List<SurveyRow> call() throws Exception {
                return api.fetchAll();
            }
        };
        task.setOnSucceeded(e -> {
            List<SurveyRow> items = task.getValue();
            masterData.setAll(items);
            refilter();
            spinner.setVisible(false);
            setStatus("Loaded " + items.size() + " surveys.");
        });
        task.setOnFailed(e -> {
            spinner.setVisible(false);
            setStatus("Failed to load: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });
        new Thread(task, "surveys-load").start();
    }

    private void deleteSurvey(SurveyRow row) {
        spinner.setVisible(true);
        setStatus("Deleting survey " + row.getId() + "...");
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
            setStatus("Survey " + row.getId() + " deleted.");
        });
        task.setOnFailed(e -> {
            spinner.setVisible(false);
            setStatus("Delete failed: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });
        new Thread(task, "surveys-delete").start();
    }

    private void refilter() {
        boolean unreadOnly = chkShowUnreadOnly.isSelected();
        if (!unreadOnly) {
            filteredData.setAll(masterData);
        } else {
            filteredData.setAll(masterData.stream()
                    .filter(s -> !s.isRead())
                    .collect(Collectors.toList()));
        }
        // Visual cue for read rows (optional)
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(SurveyRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle(item.isRead()
                            ? "-fx-opacity: 0.6; -fx-background-color: -fx-background;"
                            : "");
                }
            }
        });
    }

    private void setStatus(String msg) {
        Platform.runLater(() -> lblStatus.setText(msg));
    }
}
