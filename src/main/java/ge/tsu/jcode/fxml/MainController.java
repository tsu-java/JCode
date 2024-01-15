package ge.tsu.jcode.fxml;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static javafx.stage.FileChooser.ExtensionFilter;

public class MainController extends AbstractController implements Initializable {

    private final Tika tika = new Tika();
    private final State state = new State();

    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    public TreeView<Path> treeView;
    public TabPane tabPane;
    public SplitPane splitPane;

    public MainController() {
        super();
        setAfterStageInit((stage) -> {
            stage.titleProperty().bindBidirectional(state.folderProperty(), new State.TitleConverter());
            stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                double[] positions = splitPane.getDividerPositions();
                Platform.runLater(() -> {
                    splitPane.setDividerPositions(positions);
                });
            });
        });
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt")
        );

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Folder");

        treeView.setCellFactory((tv) -> new PathTreeCell());

        state.getOpenedItems().addListener((MapChangeListener<TreeItem<Path>, Tab>) change -> {
            if (change.wasAdded()) {
                tabPane.getTabs().add(change.getValueAdded());
                tabPane.getSelectionModel().select(change.getValueAdded());
            }

            if (change.wasRemoved()) {
                tabPane.getTabs().remove(change.getKey());
            }
        });

        treeView.setOnMouseClicked((mouseEvent) -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItem item = treeView.getSelectionModel().getSelectedItem();
                Tab tab = state.getOpenedItems().get(item);
                if (!Files.isDirectory((Path) item.getValue()) && tab == null) {
                    tab = new Tab(((Path) item.getValue()).getFileName().toString());
                    tab.setContent(getNodeForContent((Path) item.getValue()));
                    tab.setOnClosed((event) -> {
                        Tab t = (Tab) event.getTarget();
                        state.getOpenedItems().entrySet().stream()
                                .filter((entry) -> entry.getValue().equals(t))
                                .findAny()
                                .ifPresent((entry) -> state.getOpenedItems().remove(entry.getKey()));
                    });
                    state.getOpenedItems().put(item, tab);
                } else {
                    tabPane.getSelectionModel().select(tab);
                }
            }
        });
    }

    public void onOpenFolder(ActionEvent event) {
        File selectedFolder = directoryChooser.showDialog(stage);
        if (selectedFolder != null && selectedFolder.isDirectory()) {
            state.setFolder(selectedFolder.toPath());
            TreeItem<Path> root = createTreeItem(selectedFolder);
            root.setExpanded(true);
            treeView.setRoot(root);
        }
    }

    public void onSaveCurrentFile(ActionEvent event) {
        // TODO Needs implementation!
    }

    public void onQuit(ActionEvent event) {
        Platform.exit();
    }

    public void onAboutApplication(ActionEvent event) {
        // TODO Needs implementation!
    }

    private TreeItem<Path> createTreeItem(File file) {
        return createTreeItem(file.toPath());
    }

    private TreeItem<Path> createTreeItem(Path path) {
        TreeItem<Path> root = new TreeItem(path);
        if (Files.isDirectory(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.filter((p) -> !p.equals(path))
                        .forEach((p) -> root.getChildren().add(createTreeItem(p)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return root;
    }

    private Node getNodeForContent(Path path) {
        try {
            String mimeType = tika.detect(path);
            // TODO Add more types! We only support images for now :(
            if (mimeType.startsWith("image/")) {
                try (InputStream inputStream = Files.newInputStream(path)) {
                    return new ScrollPane(new ImageView(new Image(inputStream)));
                }
            } else {
                return new StackPane(new Label("Can't display contents of this file! " + mimeType));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class PathTreeCell extends TreeCell<Path> {
        protected void updateItem(Path item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : item.getFileName().toString());
        }
    }
}
