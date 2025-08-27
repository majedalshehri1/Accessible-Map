package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.*;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
import com.wakeb.yusradmin.services.StatsServiceHttp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.beans.binding.Bindings;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML private Label registeredUsersLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label totalPlacesLabel;

    @FXML private TableView<ReviewResponseDTO> reviewsTableView;
    @FXML private TableView<CategoryCount> placesByCategoryTable;
    @FXML private TableView<CategoryCount> reviewsByCategoryTable;

    @FXML private StackPane chartHost;
    @FXML private ProgressIndicator loading;

    private StatsServiceHttp service;
    private final ObservableList<ReviewResponseDTO> reviewsData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> placesCategoryData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> reviewsCategoryData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTables();
    }

    public void setService(StatsServiceHttp service) {
        this.service = service;
        loadDashboardData();
        loadCategoryStats();
        loadChart();
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
        loadChart();
    }

    private void loadDashboardData() {
        if (service == null) return;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Get counts from service
                    long usersCount = service.getTotalUsers();
                    long reviewsCount = service.getTotalReviews();
                    long placesCount = service.getTotalPlaces();

                    // Get recent reviews
                    var recentReviews = service.getRecentReviews();

                    // Update UI on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        registeredUsersLabel.setText(usersCount + " حساب");
                        totalReviewsLabel.setText(reviewsCount + " تعليق");
                        totalPlacesLabel.setText(placesCount + " مكان");

                        // Update reviews table
                        reviewsData.setAll(recentReviews);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        showError("خطأ في تحميل البيانات", e.getMessage());
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    private void loadCategoryStats() {
        if (service == null) return;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Get category data from service
                    Map<String, Integer> placesByCategory = service.placesByCategory();
                    Map<String, Integer> reviewsByCategory = service.reviewsByCategory();

                    // Update UI on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        // Update places by category table
                        placesCategoryData.clear();
                        placesByCategory.forEach((category, count) -> {
                            placesCategoryData.add(new CategoryCount(category, count));
                        });

                        // Update reviews by category table
                        reviewsCategoryData.clear();
                        reviewsByCategory.forEach((category, count) -> {
                            reviewsCategoryData.add(new CategoryCount(category, count));
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        showError("خطأ في تحميل الإحصائيات", e.getMessage());
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    private void loadChart() {
        if (service == null) return;

        Task<Map<String, Integer>> task = new Task<Map<String, Integer>>() {
            @Override
            protected Map<String, Integer> call() throws Exception {
                return service.placesByCategory();
            }
        };

        task.setOnRunning(e -> loading.setVisible(true));
        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            Map<String, Integer> stats = task.getValue();
            renderDoughnutChart(stats);
        });
        task.setOnFailed(e -> {
            loading.setVisible(false);
            showError("خطأ في تحميل الرسم البياني",
                    task.getException() != null ? task.getException().getMessage() : "Unknown error");
        });

        new Thread(task).start();
    }

    private void renderDoughnutChart(Map<String, Integer> stats) {
        PieChart pieChart = new PieChart();

        // Add data to the pie chart
        stats.forEach((category, count) -> {
            String label = category + " (" + count + ")";
            pieChart.getData().add(new PieChart.Data(label, count));
        });

        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);

        // Clear the chart host and add the pie chart
        chartHost.getChildren().clear();
        chartHost.getChildren().add(pieChart);

        // Create a hole in the middle for doughnut effect
        Circle hole = new Circle();
        hole.setManaged(false);
        hole.setMouseTransparent(true);

        // Bind the hole size to the chart size
        hole.radiusProperty().bind(
                Bindings.min(chartHost.widthProperty(), chartHost.heightProperty())
                        .multiply(0.28)
        );

        // Set hole color to match background
        hole.setFill(javafx.scene.paint.Color.WHITE);

        // Add the hole to the chart host
        StackPane.setAlignment(hole, Pos.CENTER);
        chartHost.getChildren().add(hole);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}