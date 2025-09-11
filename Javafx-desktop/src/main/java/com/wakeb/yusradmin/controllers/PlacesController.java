package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.PlaceUpdateDto;
import com.wakeb.yusradmin.models.PaginatedResponse;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.models.PlaceDto;
import com.wakeb.yusradmin.models.ReviewRequestDTO;
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

    // ====== عناصر الواجهة (نفس الترويسة) ======
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<CATEGORY> filterComboBox;


    // شبكة الكروت
    @FXML private FlowPane cardsPane;
    @FXML private ScrollPane cardsScroll;

    @FXML private Pagination pagination;

    private int currentPage = 0;
    private int pageSize = 12;

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
        filterComboBox.valueProperty().addListener((obs, o, n) -> loadPlaces());

        // أول تحميل
        loadPlaces();
    }

    // ====== تحميل الأماكن من الخدمة ======
    // PlacesController.java - Fix pagination handling
    // PlacesController.java - Update loadPlaces method
    private void loadPlaces() {
        showLoading(true);
        String q = searchField.getText();
        CATEGORY cat = filterComboBox.getValue();

        try {
            if (q != null && !q.isBlank()) {
                Task<List<Place>> searchTask = placeService.searchPlaces(q);
                searchTask.setOnSucceeded(ev -> {
                    List<Place> result = searchTask.getValue();
                    places.setAll(result);
                    renderCards();
                    pagination.setPageCount(1);
                    showLoading(false);
                });
                searchTask.setOnFailed(ev -> {
                    handleErrors(searchTask.getException());
                    showLoading(false);
                });
                new Thread(searchTask).start();
            } else if (cat != null) {
                Task<PaginatedResponse<Place>> categoryTask = placeService.getPlacesByCategory(cat, currentPage, pageSize);
                categoryTask.setOnSucceeded(ev -> {
                    PaginatedResponse<Place> resp = categoryTask.getValue();
                    if (resp != null) {
                        places.setAll(resp.getContent());
                        renderCards();
                        pagination.setPageCount(resp.getTotalPages());
                    }
                    showLoading(false);
                });
                categoryTask.setOnFailed(ev -> {
                    handleErrors(categoryTask.getException());
                    showLoading(false);
                });
                new Thread(categoryTask).start();
            } else {
                Task<PaginatedResponse<Place>> allTask = placeService.getAllPlaces(currentPage, pageSize);
                allTask.setOnSucceeded(ev -> {
                    PaginatedResponse<Place> resp = allTask.getValue();
                    if (resp != null) {
                        places.setAll(resp.getContent());
                        renderCards();
                        pagination.setPageCount(resp.getTotalPages());
                    }
                    showLoading(false);
                });
                allTask.setOnFailed(ev -> {
                    handleErrors(allTask.getException());
                    showLoading(false);
                });
                new Thread(allTask).start();
            }
        } catch (Exception e) {
            handleErrors(e);
            showLoading(false);
        }
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
        edit.getStyleClass().addAll("card-button", "card-button__secondary");
        edit.setOnAction(e -> onEdit(p));

        Button delete = new Button("حذف");
        delete.getStyleClass().addAll("card-button","card-button__delete");
        delete.setOnAction(e -> onDelete(p));

        Button map = new Button("عرض ");
        map.getStyleClass().addAll("card-button");
        map.setOnAction(e -> openOnMap(p));

        HBox actions = new HBox(10, map, edit, delete);

        map.setMaxWidth(Double.MAX_VALUE);
        edit.setMaxWidth(Double.MAX_VALUE);
        delete.setMaxWidth(Double.MAX_VALUE);

        // تجميع الكرت
        VBox card = new VBox(16, img, name, meta, badges, actions);
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
        if (!FXUtil.confirm("تأكيد الحذف", "حذف \"" + place.getPlaceName() + "\"?")) return;

        Task<Void> task = placeService.deletePlaceById(place.getId());
        showLoading(true);

        task.setOnSucceeded(ev -> {
            showLoading(false);
            loadPlaces();
        });

        task.setOnFailed(ev -> {
            showLoading(false);
            Throwable ex = task.getException();
            handleErrors(ex);
        });

        Thread t = new Thread(task, "delete-place");
        t.setDaemon(true);
        t.start();
    }

    private void onEdit(Place place) {
        Dialog<PlaceUpdateDto> dialog = new Dialog<>();
        dialog.setTitle("تعديل" + place.getPlaceName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField name = new TextField(place.getPlaceName());
        name.setPromptText("تعديل الاسم");
        name.getStyleClass().add("input-field");

        ComboBox<CATEGORY> category = new ComboBox<>();

        List<CATEGORY> allCategories = new ArrayList<>(Arrays.asList(CATEGORY.values()));

        allCategories.remove(CATEGORY.ALL);

        ObservableList<CATEGORY> categoryOptions = FXCollections.observableArrayList(allCategories);
        category.setItems(categoryOptions);
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
            if (btn == ButtonType.OK) {
                String placeName = name.getText().trim();
                String placeCategory = category.getSelectionModel().getSelectedItem().getValue();
                PlaceUpdateDto dto = new PlaceUpdateDto(place.getId(), placeName, placeCategory);
                return dto;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(placeUpdateDto -> {
            Task<Place> task = placeService.updatePlaceById(placeUpdateDto);
            showLoading(true);

            task.setOnSucceeded(e -> {
                showLoading(false);
                loadPlaces();
            });

            task.setOnFailed(e -> {
                showLoading(false);
                handleErrors(task.getException());
            });

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