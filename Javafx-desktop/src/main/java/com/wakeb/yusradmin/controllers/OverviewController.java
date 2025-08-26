package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
// Dummy data purpose for overview

public class OverviewController implements Initializable {

    @FXML private Label registeredUsersLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label totalPlacesLabel;
    @FXML private Label avgRatingLabel;

    @FXML private TableView<ReviewResponseDTO> reviewsTableView;
    @FXML private TableView<CategoryCount> placesByCategoryTable;
    @FXML private TableView<CategoryCount> reviewsByCategoryTable;

    // Comment out or remove the loadingIndicator since it's not in FXML
    // @FXML private ProgressIndicator loadingIndicator;

    //private final AdminService adminService = new AdminService();
    private final ObservableList<ReviewResponseDTO> reviewsData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> placesCategoryData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> reviewsCategoryData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTables();
        loadDashboardData();
        loadCategoryStats();
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

    private void loadDashboardData() {
        // Comment out setLoading since we don't have loadingIndicator
        // setLoading(true);

        // For testing/demo purposes, set some dummy data
        registeredUsersLabel.setText("120 حساب");
        totalReviewsLabel.setText("450 تعليق");
        totalPlacesLabel.setText("85 مكان");
        avgRatingLabel.setText("4.2");

//        // Load statistics
//        adminService.loadDashboardStats().thenAccept(stats -> {
//            Platform.runLater(() -> {
//                updateStatistics(stats);
//                setLoading(false);
//            });
//        }).exceptionally(ex -> {
//            Platform.runLater(() -> {
//                showError("خطأ", "فشل في تحميل الإحصائيات: " + ex.getMessage());
//                setLoading(false);
//            });
//            return null;
//        });
//
//        // Load latest reviews
//        adminService.loadLatestReviews().thenAccept(reviews -> {
//            Platform.runLater(() -> reviewsData.setAll(reviews));
//        }).exceptionally(ex -> {
//            Platform.runLater(() ->
//                    showError("خطأ", "فشل في تحميل التعليقات: " + ex.getMessage()));
//            return null;
//        });
    }

    private void loadCategoryStats() {
        // For testing/demo purposes, set some dummy data
        placesCategoryData.add(new CategoryCount("مطاعم", 25));
        placesCategoryData.add(new CategoryCount("مقاهي", 18));
        placesCategoryData.add(new CategoryCount("متاحف", 12));

        reviewsCategoryData.add(new CategoryCount("مطاعم", 150));
        reviewsCategoryData.add(new CategoryCount("مقاهي", 120));
        reviewsCategoryData.add(new CategoryCount("متاحف", 80));

        // Load places by category
//        adminService.loadPlacesByCategory().thenAccept(list -> {
//            Platform.runLater(() -> placesCategoryData.setAll(list));
//        }).exceptionally(ex -> {
//            Platform.runLater(() ->
//                    showError("خطأ", "فشل في تحميل إحصائيات الأماكن: " + ex.getMessage()));
//            return null;
//        });
//
//        // Load reviews by category
//        adminService.loadReviewsByCategory().thenAccept(list -> {
//            Platform.runLater(() -> reviewsCategoryData.setAll(list));
//        }).exceptionally(ex -> {
//            Platform.runLater(() ->
//                    showError("خطأ", "فشل في تحميل إحصائيات التقييمات: " + ex.getMessage()));
//            return null;
//        });
    }

    private void updateStatistics(DashboardStats stats) {
        registeredUsersLabel.setText(stats.getTotalUsers() + " حساب");
        totalReviewsLabel.setText(stats.getTotalReviews() + " تعليق");
        totalPlacesLabel.setText(stats.getTotalPlaces() + " مكان");
        avgRatingLabel.setText(String.format("%.1f", stats.getAverageRating()));
    }

    // Comment out setLoading since we don't have loadingIndicator
    // private void setLoading(boolean loading) {
    //     loadingIndicator.setVisible(loading);
    //     loadingIndicator.setProgress(loading ? -1 : 0);
    // }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}