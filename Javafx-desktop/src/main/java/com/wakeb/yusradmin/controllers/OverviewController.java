package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML private Label registeredUsersLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label totalPlacesLabel;
    @FXML private Label avgRatingLabel;

    @FXML private TableView<ReviewResponseDTO> reviewsTableView;
    @FXML private TableView<CategoryCount> placesByCategoryTable;
    @FXML private TableView<CategoryCount> reviewsByCategoryTable;

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
        TableColumn<ReviewResponseDTO, String> userColumn = new TableColumn<>("المستخدم");
        userColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserName()));

        TableColumn<ReviewResponseDTO, String> placeColumn = new TableColumn<>("المكان");
        placeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlaceName()));

        TableColumn<ReviewResponseDTO, String> descriptionColumn = new TableColumn<>("التعليق");
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<ReviewResponseDTO, Integer> ratingColumn = new TableColumn<>("التقييم");
        ratingColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getRating()).asObject());

        TableColumn<ReviewResponseDTO, String> dateColumn = new TableColumn<>("التاريخ");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt().toString()));

        reviewsTableView.getColumns().setAll(userColumn, placeColumn, descriptionColumn, ratingColumn, dateColumn);
        reviewsTableView.setItems(reviewsData);

        TableColumn<CategoryCount, String> categoryColumn = new TableColumn<>("الفئة");
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<CategoryCount, Integer> countColumn = new TableColumn<>("العدد");
        countColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCount().intValue()).asObject());

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
        registeredUsersLabel.setText("120 حساب");
        totalReviewsLabel.setText("450 تعليق");
        totalPlacesLabel.setText("85 مكان");
        avgRatingLabel.setText("4.2");

        reviewsData.add(new ReviewResponseDTO("أحمد محمد", "مطعم الريان", "طعام لذيذ وخدمة ممتازة", 5, LocalDateTime.now().minusDays(2)));
        reviewsData.add(new ReviewResponseDTO("فاطمة علي", "مقهى النجوم", "جو هادئ وقهوة رائعة", 4, LocalDateTime.now().minusDays(1)));
        reviewsData.add(new ReviewResponseDTO("خالد سعيد", "متحف التراث", "تجربة ثقافية رائعة", 5, LocalDateTime.now().minusHours(5)));
    }

    private void loadCategoryStats() {
        placesCategoryData.add(new CategoryCount("مطاعم", 25L)); // Use Long instead of int
        placesCategoryData.add(new CategoryCount("مقاهي", 18L));
        placesCategoryData.add(new CategoryCount("متاحف", 12L));

        reviewsCategoryData.add(new CategoryCount("مطاعم", 150L));
        reviewsCategoryData.add(new CategoryCount("مقاهي", 120L));
        reviewsCategoryData.add(new CategoryCount("متاحف", 80L));
    }
}