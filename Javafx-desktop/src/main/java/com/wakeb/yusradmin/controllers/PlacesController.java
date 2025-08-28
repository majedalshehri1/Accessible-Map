package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.PlaceUpdateDto;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.services.PlaceService;
import com.wakeb.yusradmin.util.FXUtil;
import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import com.wakeb.yusradmin.utils.CATEGORY;
import com.wakeb.yusradmin.utils.HostServicesSinglton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlacesController {

    // ====== عناصر الواجهة (نفس الترويسة) ======
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<CATEGORY> filterComboBox;

    // شبكة الكروت
    @FXML private FlowPane cardsPane;
    @FXML private ScrollPane cardsScroll;

    // ====== بيانات وخدمات ======
    private PlaceService placeService;
    private final ObservableList<Place> places = FXCollections.observableArrayList();

    // حجم الكرت والصورة
    private static final double CARD_WIDTH  = 260;
    private static final double IMAGE_HEIGHT = 150;

    @FXML
    private void initialize() {
        placeService = new PlaceService();

        // خيارات الفلتر بالعربي
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

        searchButton.setOnAction(e -> loadPlaces());
        searchField.setOnAction(e -> loadPlaces());
        filterComboBox.valueProperty().addListener((obs, o, n) -> loadPlaces());

        // أول تحميل
        loadPlaces();


    }

    // ====== تحميل الأماكن من الخدمة ======
    private void loadPlaces() {
        Task<List<Place>> fetchTask;
        String q = searchField.getText();

        if (q != null && !q.isBlank()) {
            fetchTask = placeService.getPlacesByName(q);
        } else if (filterComboBox.getValue() != null &&
                filterComboBox.getValue() != CATEGORY.ALL) {
            fetchTask = placeService.getPlacesByCategory(filterComboBox.getValue());
        } else {
            fetchTask = placeService.getAllPlaces();
        }

        showLoading(true);

        fetchTask.setOnSucceeded(ev -> {
            showLoading(false);
            places.setAll(fetchTask.getValue());
            renderCards();
        });

        fetchTask.setOnFailed(ev -> {
            showLoading(false);
            Throwable ex = fetchTask.getException();
            handleErrors(ex);
        });

        Thread t = new Thread(fetchTask, "load-places");
        t.setDaemon(true);
        t.start();
    }

    private void renderCards() {
        cardsPane.getChildren().clear();
        List<Node> nodes = new ArrayList<>(places.size());
        for (Place p : places) nodes.add(buildPlaceCard(p));
        cardsPane.getChildren().addAll(nodes);
    }

    private Node buildPlaceCard(Place p) {
        // الصورة
        ImageView img = new ImageView();
        img.setFitWidth(CARD_WIDTH);
        img.setFitHeight(IMAGE_HEIGHT);
        img.setPreserveRatio(false);
        img.setSmooth(true);

        if (p.getImageUrl() != null && !p.getImageUrl().isBlank()) {
            Image image = new Image(p.getImageUrl(), CARD_WIDTH, IMAGE_HEIGHT, false, true, true);
            img.setImage(image);
        } else {
            img.setStyle("-fx-background-color: #eceff1;");
        }

        Label name = new Label(p.getPlaceName());
        name.getStyleClass().add("card-title");

       Label meta = new Label(p.getCategory() != null ? p.getCategory().getLabel() : "");
        meta.getStyleClass().add("card-meta");

        // الخدمات كبادجات
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

        // أزرار الإجراءات
        Button edit = new Button("تعديل");
        edit.getStyleClass().addAll("button-primary", "btn-small");
        edit.setOnAction(e -> onEdit(p));

        Button delete = new Button("حذف");
        delete.getStyleClass().addAll("button-primaryDelete","btn-small");
        delete.setOnAction(e -> onDelete(p));

        Button map = new Button("عرض ");
        map.getStyleClass().addAll("button-primary","btn-small");
        map.setOnAction(e -> openOnMap(p));

        HBox actions = new HBox(8, edit, delete, map);

        map.setMaxWidth(Double.MAX_VALUE);
        edit.setMaxWidth(Double.MAX_VALUE);
        delete.setMaxWidth(Double.MAX_VALUE);

        // تجميع الكرت
        VBox card = new VBox(10, img, name, meta, badges, actions);
        card.getStyleClass().add("place-card");
        card.setPrefWidth(CARD_WIDTH);

        return card;
    }

    private String formatLocation(Place p) {
        String lat = p.getLatitude() == null ? "" : p.getLatitude();
        String lng = p.getLongitude() == null ? "" : p.getLongitude();
        return (lat.isBlank() || lng.isBlank()) ? "بدون إحداثيات" : (lat + ", " + lng);
    }

    // ====== إجراءات CRUD كما هي ======
    private void onDelete(Place place) {
        if (!FXUtil.confirm("Confirm Delete", "Delete place \"" + place.getPlaceName() + "\"?")) return;

        Task<Void> task = placeService.deletePlaceById(place.getId());
        task.setOnSucceeded(e -> loadPlaces());
        task.setOnFailed(e -> handleErrors(task.getException()));

        Thread t = new Thread(task, "delete-place");
        t.setDaemon(true);
        t.start();
    }

    private void onEdit(Place place) {
        TextInputDialog dlg = new TextInputDialog(place.getPlaceName());
        dlg.setTitle("Edit Place");
        dlg.setHeaderText(null);
        dlg.setContentText("New Place Name:");
        dlg.showAndWait().ifPresent(name -> {
            place.setPlaceName(name);
            Task<Place> task = placeService.updatePlaceById(
                    new PlaceUpdateDto(place.getId(), name,
                            place.getCategory() != null ? place.getCategory().getValue() : null)
            );
            task.setOnSucceeded(e -> loadPlaces());
            task.setOnFailed(e -> handleErrors(task.getException()));

            Thread t = new Thread(task, "edit-place");
            t.setDaemon(true);
            t.start();
        });
    }

    private void openOnMap(Place place) {
        String lat = place.getLatitude();
        String lng = place.getLongitude();
        if (lat == null || lng == null || lat.isBlank() || lng.isBlank()) {
            FXUtil.error("خطأ", "إحداثيات الموقع غير متوفرة");
            return;
        }
        String url = "https://www.google.com/maps/@" + lat + "," + lng + ",18z";
        HostServicesSinglton.getHostServices().showDocument(url);
    }

    // ====== مساعدات ======
    private void handleErrors(Throwable error) {
        String msg;
        if (error instanceof SecurityException) msg = "Authentication required. Please login.";
        else if (error instanceof IllegalArgumentException) msg = "Invalid request. Please try again.";
        else if (error instanceof IOException) msg = "Network Error. Please try again.";
        else msg = "unknown error. Please try again.";
        FXUtil.error("Error", msg);
    }

    private void showLoading(boolean loading) {
        if (loading) {
            cardsPane.getChildren().setAll(new Label("... جاري التحميل"));
        }
    }
}