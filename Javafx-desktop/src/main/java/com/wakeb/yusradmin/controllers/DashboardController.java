package com.wakeb.yusradmin.controllers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private ImageView adminAvatar = new ImageView();
    @FXML private Label adminNameLabel = new Label("Admin Name");
    @FXML private Label adminEmailLabel = new Label("admin@email.com");

    @FXML private Label registeredUsersLabel = new Label("0 حساب");
    @FXML private Label totalReviewsLabel = new Label("0 تعليق");
    @FXML private Label totalPlacesLabel = new Label("0 مكان");
    @FXML private Label avgRatingLabel = new Label("0.0");
    @FXML private Label statusLabel = new Label("Status");

    @FXML private TableView<String> reviewsTableView = new TableView<>();
    @FXML private ProgressIndicator loadingIndicator = new ProgressIndicator();
    @FXML private StackPane contentPane = new StackPane();
    @FXML private VBox generalView = new VBox();

    public void start(Stage stage) {
        VBox root = new VBox(
                adminAvatar,
                adminNameLabel,
                adminEmailLabel,
                registeredUsersLabel,
                totalReviewsLabel,
                totalPlacesLabel,
                avgRatingLabel,
                statusLabel,
                loadingIndicator,
                reviewsTableView
        );
        contentPane.getChildren().add(root);

        Scene scene = new Scene(contentPane, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Dashboard Prototype");
        stage.show();

        Platform.runLater(() -> {
            statusLabel.setText("Loaded prototype data");
            loadingIndicator.setVisible(false);
            reviewsTableView.setItems(FXCollections.observableArrayList("Review 1", "Review 2"));
        });
    }
}