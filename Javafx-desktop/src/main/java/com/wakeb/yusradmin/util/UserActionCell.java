package com.wakeb.yusradmin.util;

import com.wakeb.yusradmin.models.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class UserActionCell extends TableCell<User, Void> {

    private final Button blockBtn   = new Button();
    private final Button editBtn    = new Button("Edit");
    private final Button deleteBtn  = new Button("Delete");
    private final HBox box          = new HBox(8, blockBtn, editBtn, deleteBtn);

    private final Consumer<User> onToggleBlock;
    private final Consumer<User> onEdit;
    private final Consumer<User> onDelete;

    public UserActionCell(Consumer<User> onToggleBlock,
                          Consumer<User> onEdit,
                          Consumer<User> onDelete) {
        this.onToggleBlock = onToggleBlock;
        this.onEdit = onEdit;
        this.onDelete = onDelete;

        box.setPadding(new Insets(2, 0, 2, 0));
        blockBtn.getStyleClass().addAll("btn-small","btn-danger");
        editBtn.getStyleClass().add("btn-small");
        deleteBtn.getStyleClass().add("btn-small");

        blockBtn.setOnAction(e -> {
            User u = getCurrentRowUser();
            if (u != null) onToggleBlock.accept(u);
        });

        editBtn.setOnAction(e -> {
            User u = getCurrentRowUser();
            if (u != null) onEdit.accept(u);
        });

        deleteBtn.setOnAction(e -> {
            User u = getCurrentRowUser();
            if (u != null) onDelete.accept(u);
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || getIndex() < 0 || getTableView() == null) {
            setGraphic(null);
            return;
        }

        User u = getCurrentRowUser();
        if (u == null) {
            setGraphic(null);
            return;
        }

        blockBtn.setText(u.isBlocked() ? "Unblock" : "Block");
        setGraphic(box);
    }

    private User getCurrentRowUser() {
        var row = getTableRow();
        return (row == null) ? null : row.getItem();
    }
}
