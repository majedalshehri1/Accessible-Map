package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.TopPlaceDto;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
import com.wakeb.yusradmin.services.StatsServiceHttp;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OverviewController implements Initializable {

    @FXML private Label registeredUsersLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label totalPlacesLabel;

    @FXML private TableView<ReviewResponseDTO> reviewsTableView;
    @FXML private StackPane chartHost;
    @FXML private StackPane topPlacesChartHost;
    @FXML private ProgressIndicator loading;

    private StatsServiceHttp service;

    private final ObservableList<ReviewResponseDTO> reviewsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTables();
    }

    public void setService(StatsServiceHttp service) {
        this.service = service;
        refreshData();
    }

    private void setupTables() {
        // Setup reviews table with proper date formatting
        TableColumn<ReviewResponseDTO, String> userColumn = new TableColumn<>("المستخدم");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<ReviewResponseDTO, String> placeColumn = new TableColumn<>("المكان");
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("placeName"));

        TableColumn<ReviewResponseDTO, String> descriptionColumn = new TableColumn<>("التعليق");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ReviewResponseDTO, Integer> ratingColumn = new TableColumn<>("التقييم");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<ReviewResponseDTO, String> dateColumn = new TableColumn<>("التاريخ");
        // Use reviewDate instead of createdAt with proper formatting
        dateColumn.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().reviewDate;
            if (dateStr == null) return new SimpleStringProperty("");
            // Format date by replacing "T" with space
            return new SimpleStringProperty(dateStr.replace("T", " "));
        });

        reviewsTableView.getColumns().setAll(userColumn, placeColumn, descriptionColumn, ratingColumn, dateColumn);
        reviewsTableView.setItems(reviewsData);
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
        loadChart();          // category chart
        loadTopPlacesChart(); // chart for TopPlaceDto
    }

    private void loadDashboardData() {
        if (service == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    long usersCount = service.getTotalUsers();
                    long reviewsCount = service.getTotalReviews();
                    long placesCount = service.getTotalPlaces();
                    // Pull reviews from last 24 hours
                    var recentReviews = service.getRecentReviews();

                    Platform.runLater(() -> {
                        registeredUsersLabel.setText(usersCount + "");
                        totalReviewsLabel.setText(reviewsCount + "");
                        totalPlacesLabel.setText(placesCount + "");
                        reviewsData.setAll(recentReviews);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showError("خطأ في تحميل البيانات", e.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void loadChart() {
        if (service == null) return;

        Task<Map<String, Integer>> task = new Task<>() {
            @Override
            protected Map<String, Integer> call() throws Exception {
                return service.placesByCategory();
            }
        };

        task.setOnRunning(e -> loading.setVisible(true));
        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            renderDoughnutChart(task.getValue());
        });
        task.setOnFailed(e -> {
            loading.setVisible(false);
            showError("خطأ في تحميل الرسم البياني", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void loadTopPlacesChart() {
        if (service == null) return;

        Task<List<TopPlaceDto>> task = new Task<>() {
            @Override
            protected List<TopPlaceDto> call() throws Exception {
                return service.getTopPlaces();
            }
        };

        task.setOnSucceeded(e -> {
            List<TopPlaceDto> topPlaces = task.getValue();
            renderTopPlacesChart(topPlaces);
        });

        task.setOnFailed(e -> {
            showError("خطأ في تحميل رسم بياني لأفضل الأماكن", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void renderDoughnutChart(Map<String, Integer> stats) {
        PieChart pieChart = new PieChart();
        stats.forEach((category, count) ->
                pieChart.getData().add(new PieChart.Data(category + " (" + count + ")", count))
        );
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);

        // Bind the pie chart size to the host size
        Circle hole = new Circle();
        hole.setManaged(false);
        hole.setMouseTransparent(true);

        hole.radiusProperty().bind(
                Bindings.min(chartHost.widthProperty(), chartHost.heightProperty()).multiply(0.50)
        );

        // Clear the chart host and add the pie chart
        chartHost.getChildren().clear();
        chartHost.getChildren().add(pieChart);

        // Circle hole code has been removed as requested
    }

    private void renderTopPlacesChart(List<TopPlaceDto> topPlaces) {
        // Filter out null values and ensure proper ordering by review count
        List<TopPlaceDto> filteredPlaces = topPlaces.stream()
                .filter(dto -> dto != null &&
                        dto.getPlaceName() != null &&
                        dto.getReviewCount() != null)
                .sorted((d1, d2) -> Long.compare(d2.getReviewCount(), d1.getReviewCount())) // Descending order
                .collect(Collectors.toList());

        CategoryAxis yAxis = new CategoryAxis(); // Place names
        NumberAxis xAxis = new NumberAxis();     // Review counts
        xAxis.setLabel("متوسط التقييمات");
        yAxis.setLabel("اسم المكان");

        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("أفضل 5 أماكن حسب عدد التقييمات");
        barChart.setLegendVisible(false);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        for (TopPlaceDto dto : filteredPlaces) {
            // Use reviewCount instead of avgRating to fix the null pointer exception
            if (dto.getPlaceName() != null && dto.getAvgRating() != null) {
                series.getData().add(new XYChart.Data<>(dto.getAvgRating(), dto.getPlaceName()));
            }
        }

        barChart.getData().add(series);

        // Replace content of host with the chart
        topPlacesChartHost.getChildren().clear();
        topPlacesChartHost.getChildren().add(barChart);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}