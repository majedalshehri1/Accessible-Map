package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.PlaceUpdateDto;
import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.services.PlaceService;
import com.wakeb.yusradmin.util.FXUtil;
import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import com.wakeb.yusradmin.utils.CATEGORY;
import com.wakeb.yusradmin.utils.HostServicesSinglton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesController {

    // == UI ==
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<CATEGORY> filterComboBox;

    @FXML private FlowPane cardsPane;
    @FXML private ScrollPane cardsScroll;

    @FXML private Button prevBtn, nextBtn;
    @FXML private Label pageInfo;

    // == State ==
    private int currentPage = 0;
    private int pageSize    = 6;
    private int totalPages;

    private PlaceService placeService;
    private final ObservableList<Place> places = FXCollections.observableArrayList();

    private static final double CARD_WIDTH   = 260;
    private static final double IMAGE_HEIGHT = 150;

    @FXML
    private void initialize() {
        placeService = new PlaceService();

        filterComboBox.setItems(FXCollections.observableArrayList(CATEGORY.values()));
        filterComboBox.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(CATEGORY item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });
        filterComboBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(CATEGORY item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        searchField.setOnAction(e -> refresh());
        if (searchButton != null) searchButton.setOnAction(e -> refresh());
        filterComboBox.valueProperty().addListener((obs, o, n) -> loadPage(0));

        loadPage(0);
    }

    @FXML private void prevPage() { if (currentPage > 0) loadPage(currentPage - 1); }
    @FXML private void nextPage() { if (currentPage + 1 < totalPages) loadPage(currentPage + 1); }

    private void loadPage(int page) {
        if (placeService == null) return;

        String q = searchField.getText();
        if (q != null && !q.isBlank()) {
            refresh();
            return;
        }

        CATEGORY cat = filterComboBox.getValue();

        Task<PageResponse<Place>> task = (cat != null && cat != CATEGORY.ALL)
                ? placeService.getPlacesByCategory(cat, page, pageSize)
                : placeService.getAllPlaces(page, pageSize);

        task.setOnSucceeded(ev -> {
            PageResponse<Place> resp = task.getValue();
            currentPage = page;
            totalPages  = Math.max(resp.getTotalPages(), 1);
            places.setAll(resp.getContent());
            renderCards();
            updatePagingUI(true);
        });

        task.setOnFailed(ev -> {
            handleErrors(task.getException());
            updatePagingUI(false);
        });

        updatePagingUI(false);
        new Thread(task, "places-page").start();
    }

    public void refresh() {
        if (placeService == null) return;

        String q = searchField.getText();
        if (q != null && !q.isBlank()) {
            Task<List<Place>> task = placeService.searchPlaces(q.trim());
            task.setOnSucceeded(ev -> {
                List<Place> result = task.getValue();
                places.setAll(result == null ? List.of() : result);
                renderCards();

                currentPage = 0;
                totalPages  = 1;
                pageInfo.setText("نتائج: " + (result == null ? 0 : result.size()) + " (صفحة 1 / 1)");
                prevBtn.setDisable(true);
                nextBtn.setDisable(true);
            });
            task.setOnFailed(ev -> handleErrors(task.getException()));
            new Thread(task, "places-search").start();
        } else {
            loadPage(0);
        }
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

    // === رسم الكروت ===
    private void renderCards() {
        cardsPane.getChildren().clear();
        List<Node> nodes = new ArrayList<>(places.size());
        for (Place p : places) nodes.add(buildPlaceCard(p));
        cardsPane.getChildren().addAll(nodes);
    }

    private Node buildPlaceCard(Place p) {
        ImageView img = new ImageView();
        img.setFitWidth(CARD_WIDTH);
        img.setFitHeight(IMAGE_HEIGHT);
        img.setPreserveRatio(false);
        img.setSmooth(true);

        String firstImageUrl = p.getFirstImageUrl();
        if (firstImageUrl != null && !firstImageUrl.isBlank()) {
            Image image = new Image(firstImageUrl, CARD_WIDTH, IMAGE_HEIGHT, false, true, true);
            img.setImage(image);
        } else {
            img.setStyle("-fx-background-color: #eceff1;");
        }

        Label name = new Label(p.getPlaceName());
        name.getStyleClass().add("card-title");

        Label meta = new Label(p.getCategory() != null ? p.getCategory().getLabel() : "");
        meta.getStyleClass().add("card-meta");

        FlowPane badges = new FlowPane(6, 6);
        badges.getStyleClass().add("badges");
        AccessibilityFeatures[] feats = p.getAccessibilityFeatures();
        if (feats != null && feats.length > 0) {
            for (AccessibilityFeatures f : feats) {
                Label tag = new Label(f.getLabel());
                tag.getStyleClass().add("badge");
                badges.getChildren().add(tag);
            }
        } else {
            Text t = new Text("لا توجد خدمات محددة");
            t.getStyleClass().add("muted");
            badges.getChildren().add(t);
        }

        Button edit = new Button("تعديل");
        edit.getStyleClass().addAll("card-button", "card-button__secondary");
        edit.setOnAction(e -> onEdit(p));

        Button delete = new Button("حذف");
        delete.getStyleClass().addAll("card-button","card-button__delete");
        delete.setOnAction(e -> onDelete(p));

        Button map = new Button("عرض ");
        map.getStyleClass().addAll("card-button");
        map.setOnAction(e -> openOnMap(p));

        HBox actions = new HBox(10, map, edit, delete);

        VBox card = new VBox(16, img, name, meta, badges, actions);
        card.getStyleClass().add("place-card");
        card.setPrefWidth(CARD_WIDTH);

        return card;
    }

    // === CRUD ===
    private void onDelete(Place place) {
        if (!FXUtil.confirm("تأكيد الحذف", "حذف \"" + place.getPlaceName() + "\"?")) return;

        Task<Void> task = placeService.deletePlaceById(place.getId());
        task.setOnSucceeded(ev -> {
            if (places.size() == 1 && currentPage > 0) loadPage(currentPage - 1);
            else loadPage(currentPage);
        });
        task.setOnFailed(ev -> handleErrors(task.getException()));
        new Thread(task, "delete-place").start();
    }

    private void onEdit(Place place) {
        Dialog<PlaceUpdateDto> dialog = new Dialog<>();
        dialog.setTitle("تعديل " + place.getPlaceName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField name = new TextField(place.getPlaceName());
        name.setPromptText("تعديل الاسم");
        name.getStyleClass().add("input-field");

        ComboBox<CATEGORY> category = new ComboBox<>();
        List<CATEGORY> all = new ArrayList<>(Arrays.asList(CATEGORY.values()));
        all.remove(CATEGORY.ALL);
        category.setItems(FXCollections.observableArrayList(all));
        category.setValue(place.getCategory());
        category.getStyleClass().add("combo-box-field");

        category.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(CATEGORY item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });
        category.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(CATEGORY item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        GridPane gp = new GridPane();
        gp.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("اسم المكان:"), name);
        gp.addRow(1, new Label("نوع المكان:"), category);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) return new PlaceUpdateDto(name.getText().trim(), category.getValue());
            return null;
        });

        dialog.showAndWait().ifPresent(dto -> {
            Task<Place> task = placeService.updatePlaceById(place.getId(), dto);
            task.setOnSucceeded(e -> loadPage(currentPage));
            task.setOnFailed(e -> handleErrors(task.getException()));
            new Thread(task, "edit-place").start();
        });
    }

    private void openOnMap(Place place) {
        String lat = place.getLatitude();
        String lng = place.getLongitude();
        if (lat == null || lng == null || lat.isBlank() || lng.isBlank()) {
            FXUtil.error("خطأ", "إحداثيات الموقع غير متوفرة");
            return;
        }
        HostServicesSinglton.getHostServices().showDocument("https://www.google.com/maps/@" + lat + "," + lng + ",18z");
    }

    // === Helpers ===
    private void handleErrors(Throwable error) {
        if (error != null) error.printStackTrace();
        String msg;
        if (error instanceof SecurityException) msg = "Authentication required. Please login.";
        else if (error instanceof IllegalArgumentException) msg = "Invalid request. Please try again.";
        else if (error instanceof IOException) msg = "Network error. Please check your connection.";
        else msg = "Unknown error occurred: " + (error != null && error.getMessage() != null ? error.getMessage() : "");
        FXUtil.error("Error", msg);
    }
}