package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    private static final double CELL_HEIGHT = 80; // Fixed cell height
    private static final double IMAGE_SIZE = 60;
    private ObservableList<Game> games;
    private FilteredList<Game> filteredGames;
    private ListView<Game> gameListView;
    private TextField searchField;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Game Management System");

        games = FXCollections.observableArrayList(FileHandler.readGames());
        filteredGames = new FilteredList<>(games, p -> true);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.TOP_CENTER);

        Label gmsTitle = new Label("Game Management System");
        gmsTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2A5058;");
        HBox titleHolder = new HBox(gmsTitle);
        titleHolder.setAlignment(Pos.CENTER_LEFT);
        titleHolder.setPadding(new Insets(0, 0, 10, 5));

        HBox mainFeatureHolder = new HBox(10);
        mainFeatureHolder.setAlignment(Pos.CENTER_LEFT);

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close()); // Or Platform.exit();
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem showManualItem = new MenuItem("Show Manual");
        showManualItem.setOnAction(e -> showHelpManual(stage));
        helpMenu.getItems().add(showManualItem);
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        layout.getChildren().add(menuBar);

        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button importButton = new Button("Import");
        Button exportButton = new Button("Export");

        HBox searchBox = new HBox(5);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search games...");
        Button searchButton = new Button("Search");
        Button clearButton = new Button("Clear");
        ToggleButton tagsButton = new ToggleButton("Tags");
        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchButton, clearButton, tagsButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        mainFeatureHolder.getChildren().addAll(addButton, editButton, deleteButton, searchBox, importButton, exportButton);
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        gameListView = new ListView<>();
        gameListView.setItems(filteredGames);
        gameListView.setFixedCellSize(CELL_HEIGHT);
        gameListView.setCellFactory(param -> new ListCell<Game>() {
            private final ImageView imageView = new ImageView();
            private final Label genre = new Label();
            private final Label name = new Label();
            private final Label developer = new Label();
            private final Label publisher = new Label();
            private final Label platforms = new Label();
            private final Label steamid = new Label();
            private final Label releaseYear = new Label();
            private final Label playtime = new Label();
            private final Label format = new Label();
            private final Label language = new Label();
            private final Label rating = new Label();
            private final HBox hbox = new HBox(15, imageView, name, developer, genre, publisher, platforms, steamid, releaseYear, playtime, format, language, rating); // 10 = spacing
            {
                imageView.setFitWidth(IMAGE_SIZE);
                imageView.setFitHeight(IMAGE_SIZE);
                imageView.setPreserveRatio(false);
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
                        imageView.setImage(null); // Shows empty space
                    }
                    name.setText(game.getName());
                    developer.setText(game.getDeveloper());
                    publisher.setText(game.getPublisher());
                    genre.setText(game.getGenre());
                    platforms.setText(game.getPlatforms());
                    steamid.setText(game.getSteamid());
                    releaseYear.setText(game.getReleaseYear());
                    playtime.setText(game.getPlaytime());
                    format.setText(game.getFormat());
                    language.setText(game.getLanguage());
                    rating.setText(String.valueOf(game.getRating()));
                    setGraphic(hbox);
                }
            }
        });
        VBox.setVgrow(gameListView, Priority.ALWAYS);

        layout.getChildren().addAll(titleHolder, mainFeatureHolder, gameListView);

        searchButton.setOnAction(e -> {
            if (tagsButton.isSelected()) {
                filterByGenre(searchField.getText());
            } else {
                filterGames(searchField.getText());
            }
        });

        clearButton.setOnAction(e -> {
            searchField.clear();
        });

        addButton.setOnAction(e -> handleAddGame(stage));

        editButton.setOnAction(e -> handleEditGame(stage));

        deleteButton.setOnAction(e -> handleDeleteGame());

        importButton.setOnAction(e -> handleImportGames(stage));


        gameListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // double-click
                Game selectedGame = gameListView.getSelectionModel().getSelectedItem();
                if (selectedGame != null) {
                    showGameDetails(stage, selectedGame);
                }
            }
        });

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
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

    private void filterByGenre(String searchText) {
        String lowerCaseFilter = searchText == null ? "" : searchText.toLowerCase().trim();
        filteredGames.setPredicate(game -> {
            if (lowerCaseFilter.isEmpty()) {
                return true;
            }
            return game.getGenre().toLowerCase().contains(lowerCaseFilter);
        });
    }

    private void handleAddGame(Stage owner) {
        GameDialog dialog = new GameDialog(owner);
        Optional<Game> result = dialog.showAndWaitForResult();
        result.ifPresent(newGame -> {
            try {
                FileHandler.addGame(newGame);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            games.add(newGame);
            gameListView.getSelectionModel().select(newGame);
            gameListView.scrollTo(newGame);
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
                FileHandler.deleteGame(tempOldName);
                FileHandler.addGame(editedGame);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            games.remove(selectedGame);
            games.add(editedGame);
            gameListView.refresh();
            gameListView.getSelectionModel().select(editedGame);
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
        Optional<Game> result = dialog.showAndWaitForResult();
        result.ifPresent(e -> {
            gameListView.refresh();
            gameListView.getSelectionModel().select(e);
        });
    }

    private void showHelpManual(Stage stage) {
        Stage helpStage = new Stage();
        helpStage.setTitle("User Manual");
        helpStage.initModality(Modality.WINDOW_MODAL);
        helpStage.initOwner(stage);

        // Enhanced layout with styled VBox
        VBox helpLayout = new VBox(15);
        helpLayout.setPadding(new Insets(20));
        helpLayout.setStyle("-fx-background-color: #f0f4f8; -fx-border-color: #d3e0ea; -fx-border-width: 2; -fx-border-radius: 10;");

        // Title with styling
        Label titleLabel = new Label("User Manual");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        // Content area with formatted text
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

        // Scroll pane for content
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

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Import Error",
                        "Failed to read the selected file: " + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Error",
                        "Invalid game data in file: " + e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}