package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Main extends Application {
    private static final double CELL_HEIGHT = 80; // Fixed cell height
    private static final double IMAGE_SIZE = 60;
    private ObservableList<Game> games;
    private FilteredList<Game> filteredGames;
    private ListView<Game> gameListView;
    private TextField searchField;
    private ComboBox<String> tagComboBox = new ComboBox<>();
    private FlowPane selectedTagsBox = new FlowPane();
    private ObservableList<String> selectedTags = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Game Management System");

        // Initialize games with error handling
        try {
            games = FXCollections.observableArrayList(FileHandler.readGames());
        } catch (IOException e) {
            System.err.println("Failed to read games: " + e.getMessage());
            e.printStackTrace();
            games = FXCollections.observableArrayList(); // Fallback to empty list
        }
        filteredGames = new FilteredList<>(games, p -> true);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #e6f0fa, #ffffff);");

        Label gmsTitle = new Label("Game Management System");
        gmsTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f; -fx-effect: dropshadow(gaussian, #a3bffa, 10, 0, 0, 0);");
        HBox titleHolder = new HBox(gmsTitle);
        titleHolder.setAlignment(Pos.CENTER_LEFT);
        titleHolder.setPadding(new Insets(0, 0, 15, 15));

        HBox mainFeatureHolder = new HBox(15);
        mainFeatureHolder.setAlignment(Pos.CENTER_LEFT);
        mainFeatureHolder.setPadding(new Insets(0, 15, 0, 15));
        mainFeatureHolder.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-border-color: #d3e0ea; -fx-border-width: 1; -fx-border-radius: 5;");

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #f0f4f8;");

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem showManualItem = new MenuItem("Show Manual");
        showManualItem.setOnAction(e -> showHelpManual(stage));
        helpMenu.getItems().add(showManualItem);
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        layout.getChildren().add(menuBar);

        Button addButton = new Button("Add");
        styleButton(addButton);
        Button editButton = new Button("Edit");
        styleButton(editButton);
        Button deleteButton = new Button("Delete");
        styleButton(deleteButton);
        Button importButton = new Button("Import");
        styleButton(importButton);
        Button exportButton = new Button("Export");
        styleButton(exportButton);

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search games...");
        searchField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d3e0ea; -fx-border-radius: 5;");
        Button searchButton = new Button("Search");
        styleButton(searchButton);
        Button clearButton = new Button("Clear");
        styleButton(clearButton);

        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchButton, clearButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        selectedTagsBox.setAlignment(Pos.CENTER_LEFT);
        selectedTagsBox.setHgap(5);
        selectedTagsBox.setVgap(5);
        selectedTagsBox.setPadding(new Insets(5));
        selectedTagsBox.setPrefWrapLength(200);

        ScrollPane tagsScrollPane = new ScrollPane(selectedTagsBox);
        tagsScrollPane.setFitToWidth(true);
        tagsScrollPane.setPrefViewportHeight(50);
        tagsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tagsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tagsScrollPane.setStyle("-fx-background-color: transparent;");

        HBox tagsBox = new HBox(10, new Label("Tags:"), tagComboBox, tagsScrollPane);
        tagsBox.setAlignment(Pos.CENTER_LEFT);

        VBox searchAndTagsBox = new VBox(10, searchBox, tagsBox);
        VBox.setVgrow(searchAndTagsBox, Priority.NEVER);

        mainFeatureHolder.getChildren().addAll(addButton, editButton, deleteButton, searchAndTagsBox, importButton, exportButton);
        HBox.setHgrow(searchAndTagsBox, Priority.ALWAYS);
        HBox.setHgrow(addButton, Priority.NEVER);
        HBox.setHgrow(editButton, Priority.NEVER);
        HBox.setHgrow(deleteButton, Priority.NEVER);
        HBox.setHgrow(importButton, Priority.NEVER);
        HBox.setHgrow(exportButton, Priority.NEVER);

        tagComboBox.setOnAction(e -> {
            String selectedTag = tagComboBox.getSelectionModel().getSelectedItem();
            if (selectedTag != null && !selectedTags.contains(selectedTag)) {
                selectedTags.add(selectedTag);
                updateSelectedTagsDisplay();
                filterBySelectedTags();
                tagComboBox.getSelectionModel().clearSelection();
            }
        });
        updateTagComboBox();

        gameListView = new ListView<>();
        gameListView.setItems(filteredGames);
        gameListView.setFixedCellSize(CELL_HEIGHT);
        gameListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        gameListView.setCellFactory(param -> new ListCell<Game>() {
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label developerLabel = new Label();
            private final Label genreLabel = new Label();
            private final Label playtimeLabel = new Label();
            private final Label ratingLabel = new Label();
            private final HBox hbox = new HBox(15, imageView, new VBox(5, nameLabel, developerLabel), new VBox(5, genreLabel, playtimeLabel), ratingLabel);

            {
                imageView.setFitWidth(IMAGE_SIZE);
                imageView.setFitHeight(IMAGE_SIZE);
                imageView.setPreserveRatio(false);

                // Style the name label to be bigger and bold
                nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                // Style other labels
                developerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
                genreLabel.setStyle("-fx-font-size: 12px;");
                playtimeLabel.setStyle("-fx-font-size: 12px;");
                ratingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2a5c8d;");

                // Set HBox alignment
                hbox.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || game == null) {
                    setGraphic(null);
                } else {
                    try {
                        FileHandler.ensureCoverPathExists();
                        imageView.setImage(FileHandler.loadCoverImage(game.getImagePath(), IMAGE_SIZE, IMAGE_SIZE));
                    } catch (Exception e) {
                        imageView.setImage(null);
                    }

                    nameLabel.setText(game.getName());
                    developerLabel.setText("by " + game.getDeveloper());
                    genreLabel.setText("Genre: " + game.getGenre());
                    playtimeLabel.setText("Playtime: " + game.getPlaytime());
                    ratingLabel.setText("Rating: " + game.getRating());

                    setGraphic(hbox);
                }
            }
        });
        VBox.setVgrow(gameListView, Priority.ALWAYS);

        layout.getChildren().addAll(titleHolder, mainFeatureHolder, gameListView);

        searchButton.setOnAction(e -> {
            filterGames(searchField.getText());
        });

        clearButton.setOnAction(e -> {
            searchField.clear();
        });

        addButton.setOnAction(e -> handleAddGame(stage));

        editButton.setOnAction(e -> handleEditGame(stage));

        deleteButton.setOnAction(e -> handleDeleteGame());

        importButton.setOnAction(e -> handleImportGames(stage));

        exportButton.setOnAction(e -> handleExportGames(stage));

        gameListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Game selectedGame = gameListView.getSelectionModel().getSelectedItem();
                if (selectedGame != null) {
                    showGameDetails(stage, selectedGame);
                }
            }
        });

        updateTagComboBox();
        Scene scene = new Scene(layout, 900, 600);
        // Temporarily comment out CSS loading to isolate the issue
        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #357abd; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-border-radius: 5; -fx-background-radius: 5;"));
    }

    private void filterGames(String searchText) {
        String lowerCaseFilter = searchText == null ? "" : searchText.toLowerCase().trim();
        filteredGames.setPredicate(game -> {
            if (lowerCaseFilter.isEmpty()) {
                return true;
            }
            if (game.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return game.getDeveloper().toLowerCase().contains(lowerCaseFilter);
        });
    }

    private void handleAddGame(Stage owner) {
        GameDialog dialog = new GameDialog(owner);
        Optional<Game> result = dialog.showAndWaitForResult();
        result.ifPresent(newGame -> {
            if (isGameNameDuplicate(newGame.getName())) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Game",
                        "A game with this name already exists. Please choose a different name.");
                return;
            }
            try {
                FileHandler.addGame(newGame);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            games.add(newGame);
            gameListView.getSelectionModel().select(newGame);
            gameListView.scrollTo(newGame);
            updateTagComboBox();
        });
    }

    private void handleEditGame(Stage owner) {
        Game selectedGame = gameListView.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to edit.");
            return;
        }
        String tempOldName = selectedGame.getName();

        GameDialog dialog = new GameDialog(owner, selectedGame, false);
        Optional<Game> result = dialog.showAndWaitForResult();
        result.ifPresent(editedGame -> {
            try {
                // Only check for duplicate if the name was changed
                if (!editedGame.getName().equalsIgnoreCase(tempOldName) &&
                        isGameNameDuplicate(editedGame.getName())) {
                    showAlert(Alert.AlertType.ERROR, "Duplicate Game",
                            "A game with this name already exists. Please choose a different name.");
                    return;
                }
                FileHandler.deleteGame(tempOldName);
                FileHandler.addGame(editedGame);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            games.remove(selectedGame);
            games.add(editedGame);
            gameListView.refresh();
            gameListView.getSelectionModel().select(editedGame);
            updateTagComboBox();
        });
    }

    private void handleDeleteGame() {
        Game selectedGame = gameListView.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Game: " + selectedGame.getName());
        confirmation.setContentText("Are you sure you want to delete this game? This action cannot be undone.");
        confirmation.initOwner(gameListView.getScene().getWindow());

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FileHandler.deleteGame(selectedGame.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            games.remove(selectedGame);
            updateTagComboBox();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(gameListView.getScene().getWindow());
        alert.showAndWait();
    }

    private void showGameDetails(Stage owner, Game selectedGame) {
        GameDialog dialog = new GameDialog(owner, selectedGame, true);
        dialog.showDetailsOnly();
        dialog.showAndWait();
    }

    private void showHelpManual(Stage stage) {
        Stage helpStage = new Stage();
        helpStage.setTitle("User Manual");
        helpStage.initModality(Modality.WINDOW_MODAL);
        helpStage.initOwner(stage);

        VBox helpLayout = new VBox(15);
        helpLayout.setPadding(new Insets(20));
        helpLayout.setStyle("-fx-background-color: #f0f4f8; -fx-border-color: #d3e0ea; -fx-border-width: 2; -fx-border-radius: 10;");

        Label titleLabel = new Label("User Manual");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e8f0; -fx-border-width: 1; -fx-border-radius: 5;");

        Label addLabel = new Label("Adding Games:");
        addLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
        Label addText = new Label("Use the 'Add' button to add new games.");
        addText.setWrapText(true);

        Label editLabel = new Label("Editing Games:");
        editLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
        Label editText = new Label("Click on a game and click 'Edit' to change its properties. Do not use the same name for two separate games.");
        editText.setWrapText(true);

        Label deleteLabel = new Label("Deleting Games:");
        deleteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
        Label deleteText = new Label("Click on a game and click 'Delete' to remove it.");
        deleteText.setWrapText(true);

        Label searchLabel = new Label("Searching Games:");
        searchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
        Label searchText = new Label("Use the search bar to find games by name and developer. Toggle the 'Tags' button to search by genre (this is WIP, with tag-based search coming soon).");
        searchText.setWrapText(true);

        Label coverImageLabel = new Label("Cover Images:");
        coverImageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
        Label coverImageText = new Label("Add cover images to the 'covers' folder in the same directory, then edit the game to match the image name using the 'Edit' button.");
        coverImageText.setWrapText(true);

        contentBox.getChildren().addAll(addLabel, addText, editLabel, editText, deleteLabel, deleteText, searchLabel, searchText, coverImageLabel, coverImageText);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        helpLayout.getChildren().addAll(titleLabel, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene helpScene = new Scene(helpLayout, 500, 400);
        helpStage.setScene(helpScene);
        helpStage.showAndWait();
    }

    private void handleImportGames(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Games from JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(owner);

        if (selectedFile != null) {
            try {
                List<Game> importedGames = FileHandler.importGamesFromFile(selectedFile.toPath());

                for (Game game : importedGames) {
                    if (game.getName() == null || game.getName().trim().isEmpty() ||
                            game.getDeveloper() == null || game.getDeveloper().trim().isEmpty()) {
                        throw new IllegalArgumentException("Imported games must have at least name and developer fields");
                    }
                }

                int addedCount = 0;
                for (Game game : importedGames) {
                    if (!games.contains(game)) {
                        FileHandler.addGame(game);
                        games.add(game);
                        addedCount++;
                    }
                }

                showAlert(Alert.AlertType.INFORMATION, "Import Successful",
                        String.format("Successfully imported %d/%d games", addedCount, importedGames.size()));
                updateTagComboBox();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Import Error",
                        "Failed to read the selected file: " + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Error",
                        "Invalid game data in file: " + e.getMessage());
            }
        }
    }

    private boolean isGameNameDuplicate(String gameName) {
        return games.stream().anyMatch(game -> game.getName().equalsIgnoreCase(gameName));
    }

    private void handleExportGames(Stage owner) {
        List<Game> selectedGames = gameListView.getSelectionModel().getSelectedItems();

        if (selectedGames.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select one or more games to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Selected Games");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        fileChooser.setInitialFileName("games_export.json");

        File exportFile = fileChooser.showSaveDialog(owner);

        if (exportFile != null) {
            try {
                String path = exportFile.getPath();
                if (!path.toLowerCase().endsWith(".json")) {
                    exportFile = new File(path + ".json");
                }

                FileHandler.exportGamesToFile(
                        new ArrayList<>(selectedGames),
                        exportFile.toPath()
                );

                showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                        String.format("Successfully exported %d games to:\n%s",
                                selectedGames.size(),
                                exportFile.getAbsolutePath()));

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Export Error",
                        "Failed to export games: " + e.getMessage());
            }
        }
    }

    private void updateTagComboBox() {
        Set<String> allTags = new HashSet<>();
        for (Game game : games) {
            if (game.getTags() != null) {
                allTags.addAll(Arrays.asList(game.getTags()));
            }
        }
        tagComboBox.getItems().setAll(allTags);
        tagComboBox.getItems().sort(String::compareToIgnoreCase);
    }

    private void updateSelectedTagsDisplay() {
        selectedTagsBox.getChildren().clear();
        for (String tag : selectedTags) {
            Button tagButton = new Button(tag);
            tagButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 2 8; -fx-border-radius: 5; -fx-background-radius: 5;");
            tagButton.setOnMouseEntered(e -> tagButton.setStyle("-fx-background-color: #357abd; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 2 8; -fx-border-radius: 5; -fx-background-radius: 5;"));
            tagButton.setOnMouseExited(e -> tagButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 2 8; -fx-border-radius: 5; -fx-background-radius: 5;"));
            tagButton.setOnAction(e -> {
                selectedTags.remove(tag);
                updateSelectedTagsDisplay();
                filterBySelectedTags();
            });
            selectedTagsBox.getChildren().add(tagButton);
        }
    }

    private void filterBySelectedTags() {
        filteredGames.setPredicate(game -> {
            if (selectedTags.isEmpty()) {
                return true;
            }

            if (game.getTags() == null || game.getTags().length == 0) {
                return false;
            }

            List<String> gameTags = Arrays.asList(game.getTags());
            return gameTags.containsAll(selectedTags);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}