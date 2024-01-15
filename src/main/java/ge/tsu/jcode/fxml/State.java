package ge.tsu.jcode.fxml;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;

import java.nio.file.Path;
import java.util.HashMap;

public class State {

    private ObjectProperty<Path> folder = new SimpleObjectProperty();
    private ObservableMap<TreeItem<Path>, Tab> openedItems = FXCollections.observableMap(new HashMap());

    public Path getFolder() {
        return folder.get();
    }

    public ObjectProperty<Path> folderProperty() {
        return folder;
    }

    public void setFolder(Path folder) {
        this.folder.set(folder);
    }

    public ObservableMap<TreeItem<Path>, Tab> getOpenedItems() {
        return openedItems;
    }

    public void setOpenedItems(ObservableMap<TreeItem<Path>, Tab> openedItems) {
        this.openedItems = openedItems;
    }

    public static class TitleConverter extends StringConverter<Path> {

        public String toString(Path path) {
            return path == null ? "JCode" : String.format("JCode: %s", path.getFileName().toString());
        }

        public Path fromString(String title) {
            throw new RuntimeException("This method should never be called!");
        }
    }
}
