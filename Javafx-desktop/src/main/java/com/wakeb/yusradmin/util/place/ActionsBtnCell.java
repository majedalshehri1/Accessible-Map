package com.wakeb.yusradmin.util.place;

import com.wakeb.yusradmin.models.Place;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class ActionsBtnCell extends TableCell<Place, Void> {
    private final Button deleteBtn =  new Button("حذف المكان");
    private final Button updateBtn =  new Button("تعديل المكان");
    private final HBox hBox = new HBox(8, deleteBtn, updateBtn);

    private final Consumer<Place> onEdit;
    private final Consumer<Place> onDelete;

    public ActionsBtnCell(Consumer<Place> onEdit, Consumer<Place> onDelete){
        this.onEdit = onEdit;
        this.onDelete = onDelete;

        hBox.getStyleClass().add("place-actions-box");
        deleteBtn.getStyleClass().addAll("small-cell-button", "small-cell-button-danger");
        updateBtn.getStyleClass().addAll("small-cell-button", "small-cell-button-secondary");

        deleteBtn.setOnAction(e -> {
            Place place = getCurrentPlace();
            onDelete.accept(place);
        });

        updateBtn.setOnAction(e -> {
            Place place = getCurrentPlace();
            onEdit.accept(place);
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

        setGraphic(hBox);
    }

    private Place getCurrentPlace() {
        TableRow<Place> row = getTableRow();
        return row == null ? null : row.getItem();
    }
}
