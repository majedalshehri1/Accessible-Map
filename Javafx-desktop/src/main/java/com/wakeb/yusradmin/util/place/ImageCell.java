package com.wakeb.yusradmin.util.place;

import com.wakeb.yusradmin.models.Place;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.function.Consumer;

public class ImageCell extends TableCell<Place, Void> {
    private final ImageView imageView = new ImageView();

    public ImageCell() {
    }

    @Override
    protected void updateItem(Void unused, boolean empty) {
        super.updateItem(unused, empty);

        if (empty || getCurrentPlace() == null || !getCurrentPlace().getImageUrl().startsWith("https")) {
            setText(null);
            setGraphic(null);
            return;
        }

        Image image = new Image(getCurrentPlace().imageUrl);
        imageView.setImage(image);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        setGraphic(imageView);
        setText(null);
    }

    private Place getCurrentPlace() {
        TableRow<Place> row = getTableRow();
        return row == null ? null : row.getItem();
    }
}
