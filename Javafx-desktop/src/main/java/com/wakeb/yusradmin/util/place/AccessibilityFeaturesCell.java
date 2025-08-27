package com.wakeb.yusradmin.util.place;

import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

public class AccessibilityFeaturesCell extends TableCell<Place, Void> {

    @Override
    protected void updateItem(Void unused, boolean empty) {
        super.updateItem(unused, empty);

        Place place = getCurrentPlace();

        if (empty || place == null || place.getAccessibilityFeatures() == null) {
            setGraphic(null);
        } else if (place.getAccessibilityFeatures().length == 0) {
            setText("المكان لا يحتوي على خدمات");
        } else {
            FlowPane flowPane = new FlowPane();
            flowPane.setHgap(5);
            flowPane.setVgap(5);

            AccessibilityFeatures[] accessibilityFeatures = place.getAccessibilityFeatures();

            for (AccessibilityFeatures accessibilityFeature : accessibilityFeatures) {
                Label label = new Label(accessibilityFeature.getLabel());
                label.getStyleClass().add("feature-tag");
                flowPane.getChildren().add(label);
            }

            setGraphic(flowPane);
        }
    }

    private Place getCurrentPlace() {
        TableRow<Place> row = getTableRow();
        return row == null ? null : row.getItem();
    }
}
