package com.wakeb.yusradmin.util.place;

import com.wakeb.yusradmin.models.Place;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;

import java.util.function.Consumer;

public class LinkPlaceCell extends TableCell<Place, Void> {
    private final Hyperlink hyperlink = new Hyperlink("عرض على الخريطة");
    private final Consumer<Place> onClickAction;

    public LinkPlaceCell(Consumer<Place> onClickAction) {
        this.onClickAction = onClickAction;

        hyperlink.setOnAction(event -> {
            Place place = getCurrentPlace();
            if (place != null) {
                onClickAction.accept(place);
            }
        });
    }

    @Override
    protected void updateItem(Void unused, boolean empty) {
        super.updateItem(unused, empty);

        if (empty || getCurrentPlace() == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        setGraphic(hyperlink);
    }

    private Place getCurrentPlace() {
        TableRow<Place> row = getTableRow();
        return row == null ? null : row.getItem();
    }
}
