package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.*;
import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import com.wakeb.yusradmin.services.AdminService;
import com.wakeb.yusradmin.services.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private ImageView adminAvatar;
    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;

    @FXML private Label registeredUsersLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label totalPlacesLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<ReviewResponseDTO> reviewsTableView;
    @FXML private TableView<CategoryCount> placesByCategoryTable;
    @FXML private TableView<CategoryCount> reviewsByCategoryTable;

    @FXML private ProgressIndicator loadingIndicator;

    private final AdminService adminService = new AdminService();
    private final AuthService authService = new AuthService();
    private final ObservableList<ReviewResponseDTO> reviewsData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> placesCategoryData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> reviewsCategoryData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAdminProfile();
        setupTables();
        loadDashboardData();
        loadCategoryStats();
    }

    private void loadAdminProfile() {
        // Get current admin user from authentication
        //UserDto adminUser = authService.getCurrentUser();
//        if (adminUser != null) {
//            adminNameLabel.setText(adminUser.getUserName());
//            adminEmailLabel.setText(adminUser.getUserEmail());
//
//            // Load admin avatar
//            try {
//                Image avatar = new Image(getClass().getResourceAsStream("/images/avatars/" + adminUser.getUserId() + ".png"));
//                adminAvatar.setImage(avatar);
//            } catch (Exception e) {
//                // Use default avatar if specific one doesn't exist
//                adminAvatar.setImage(new Image(getClass().getResourceAsStream("/images/avatar.png")));
//            }
//        }
    }

    private void setupTables() {
        // Setup reviews table
        TableColumn<ReviewResponseDTO, String> userColumn = new TableColumn<>("المستخدم");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<ReviewResponseDTO, String> placeColumn = new TableColumn<>("المكان");
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("placeName"));

        TableColumn<ReviewResponseDTO, String> descriptionColumn = new TableColumn<>("التعليق");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ReviewResponseDTO, Integer> ratingColumn = new TableColumn<>("التقييم");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<ReviewResponseDTO, String> dateColumn = new TableColumn<>("التاريخ");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        reviewsTableView.getColumns().setAll(userColumn, placeColumn, descriptionColumn, ratingColumn, dateColumn);
        reviewsTableView.setItems(reviewsData);

        // Setup category tables
        TableColumn<CategoryCount, String> categoryColumn = new TableColumn<>("الفئة");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<CategoryCount, Integer> countColumn = new TableColumn<>("العدد");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        placesByCategoryTable.getColumns().setAll(categoryColumn, countColumn);
        reviewsByCategoryTable.getColumns().setAll(categoryColumn, countColumn);

        placesByCategoryTable.setItems(placesCategoryData);
        reviewsByCategoryTable.setItems(reviewsCategoryData);
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
        loadCategoryStats();
    }

    @FXML
    private void logout() {
        // authService.logout();
        NavigationManager.getInstance().navigateToScene(SceneType.LOGIN);
    }

    private void loadDashboardData() {
        setLoading(true);
        statusLabel.setText("جاري تحميل البيانات...");

        // Load statistics
        adminService.loadDashboardStats().thenAccept(stats -> {
            Platform.runLater(() -> {
                updateStatistics(stats);
                statusLabel.setText("تم تحميل البيانات بنجاح");
                setLoading(false);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showError("خطأ", "فشل في تحميل الإحصائيات: " + ex.getMessage());
                statusLabel.setText("خطأ في تحميل الإحصائيات");
                setLoading(false);
            });
            return null;
        });

        // Load latest reviews
        adminService.loadLatestReviews().thenAccept(reviews -> {
            Platform.runLater(() -> reviewsData.setAll(reviews));
        }).exceptionally(ex -> {
            Platform.runLater(() ->
                    showError("خطأ", "فشل في تحميل التعليقات: " + ex.getMessage()));
            return null;
        });
    }

    private void loadCategoryStats() {
        // Load places by category
        adminService.loadPlacesByCategory().thenAccept(list -> {
            Platform.runLater(() -> placesCategoryData.setAll(list));
        }).exceptionally(ex -> {
            Platform.runLater(() ->
                    showError("خطأ", "فشل في تحميل إحصائيات الأماكن: " + ex.getMessage()));
            return null;
        });

        // Load reviews by category
        adminService.loadReviewsByCategory().thenAccept(list -> {
            Platform.runLater(() -> reviewsCategoryData.setAll(list));
        }).exceptionally(ex -> {
            Platform.runLater(() ->
                    showError("خطأ", "فشل في تحميل إحصائيات التقييمات: " + ex.getMessage()));
            return null;
        });
    }

    private void updateStatistics(DashboardStats stats) {
        registeredUsersLabel.setText(stats.getTotalUsers() + " حساب");
        totalReviewsLabel.setText(stats.getTotalReviews() + " تعليق");
        totalPlacesLabel.setText(stats.getTotalPlaces() + " مكان");
        avgRatingLabel.setText(String.format("%.1f", stats.getAverageRating()));
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setProgress(loading ? -1 : 0);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}