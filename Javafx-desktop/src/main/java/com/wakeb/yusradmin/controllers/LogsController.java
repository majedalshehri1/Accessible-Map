// com.wakeb.yusradmin.controllers.LogsController.java
package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.AdminLogDto;
import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.services.AdminLogService;
import com.wakeb.yusradmin.util.FXUtil;
import com.wakeb.yusradmin.utils.LogFilter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LogsController {

    @FXML private TableView<AdminLogDto> table;
    @FXML private TableColumn<AdminLogDto, String> colTime;
    @FXML private TableColumn<AdminLogDto, String> colEntity;
    @FXML private TableColumn<AdminLogDto, String> colAction;
    @FXML private TableColumn<AdminLogDto, Number> colEntityId;
    @FXML private TableColumn<AdminLogDto, String> colActor;
    @FXML private TableColumn<AdminLogDto, String> colDesc;

    @FXML private ProgressIndicator loading;
    @FXML private Label statusLabel;

    @FXML private Button prevBtn, nextBtn;
    @FXML private Label pageInfo;
    @FXML private ComboBox<LogFilter> entityFilter;

    private final ObservableList<AdminLogDto> data = FXCollections.observableArrayList();
    private final AdminLogService service = new AdminLogService();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private int currentPage = 0;
    private int pageSize = 13;
    private int totalPages;

    @FXML
    private void initialize() {
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        colTime.setMinWidth(140);   colTime.setPrefWidth(140);   colTime.setMaxWidth(180);
        colEntity.setMinWidth(110); colEntity.setPrefWidth(120); colEntity.setMaxWidth(160);
        colAction.setMinWidth(100); colAction.setPrefWidth(110); colAction.setMaxWidth(150);
        colEntityId.setMinWidth(100); colEntityId.setPrefWidth(110); colEntityId.setMaxWidth(150);
        colActor.setMinWidth(150);  colActor.setPrefWidth(180);  colActor.setMaxWidth(220);
        colDesc.setMinWidth(300); colDesc.setPrefWidth(400);     colDesc.setMaxWidth(Double.MAX_VALUE);

        table.setPlaceholder(new Label("جاري التحميل..."));

        colEntityId.setCellValueFactory(c ->
                new SimpleLongProperty(c.getValue().getEntityId() == null ? 0L : c.getValue().getEntityId()));

        colEntity.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getEntityType())));
        colAction.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getAction())));
        colActor.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getActorName())));
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getDescription())));

        colTime.setCellValueFactory(c -> {
            var inst = c.getValue().getCreatedAt(); // Instant
            String txt = (inst == null) ? "" : fmt.format(inst.atZone(ZoneId.systemDefault()));
            return new SimpleStringProperty(txt);
        });

        entityFilter.setItems(FXCollections.observableArrayList(LogFilter.values()));
        entityFilter.getSelectionModel().select(LogFilter.ALL);
        entityFilter.valueProperty().addListener((observable, oldValue, newValue) -> loadPage(0));

        loadPage(0);
    }

    @FXML
    private void prevPage() {
        if (currentPage > 0) loadPage(currentPage - 1);
    }

    @FXML
    private void nextPage() {
        if (currentPage + 1 < totalPages) loadPage(currentPage + 1);
    }

    @FXML
    public void refresh() {
        loadPage(currentPage);
    }

    private void loadPage(int page) {
        loading.setVisible(true);
        statusLabel.setText("جاري التحميل...");
        updatePagingUI(false);

        String selectedType = (entityFilter.getValue() == null) ? null :
                entityFilter.getValue().getValue();

        Task<PageResponse<AdminLogDto>> t = service.pageAsync(page, pageSize , selectedType);
        t.setOnSucceeded(e -> {
            loading.setVisible(false);
            PageResponse<AdminLogDto> p = t.getValue();

            if (p == null) {
                data.clear();
                statusLabel.setText("لا توجد بيانات");
                currentPage = 0; totalPages = 1;
                updatePagingUI(true);
                return;
            }

            data.setAll(p.content);
            currentPage = p.currentPage;
            totalPages  = Math.max(p.totalPages, 1);

            statusLabel.setText("عدد السجلات  في الصفحة: " + data.size());
            updatePagingUI(true);
        });
        t.setOnFailed(e -> {
            loading.setVisible(false);
            statusLabel.setText("فشل التحميل: " + t.getException().getMessage());
            FXUtil.error("فشل تحميل السجلات", t.getException().getMessage());
            updatePagingUI(false);
        });

        new Thread(t, "logs-page").start();
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

    private static String safe(String s) { return s == null ? "" : s; }
}