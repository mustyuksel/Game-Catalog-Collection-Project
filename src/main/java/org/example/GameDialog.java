package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;


public class GameDialog extends Stage {

    private final TextField nameField = new TextField();
    private final TextField developerField = new TextField();
    private final TextField genreField = new TextField();
    private final TextField publisherField = new TextField();
    private final TextField platformsField = new TextField();
    private final TextField steamidField = new TextField();
    private final TextField releaseYearField = new TextField();
    private final TextField playtimeField = new TextField();
    private final TextField formatField = new TextField();
    private final TextField languageField = new TextField();
    private final TextField ratingField = new TextField();
    private final TextField tagsField = new TextField();
    private final TextField imagePathField = new TextField();

    private Game resultGame = null; // To store the result


    public GameDialog(Window owner) {
        this(owner, null,false);
    }


    public GameDialog(Window owner, Game gameToEdit,boolean readOnly) {
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        if (gameToEdit == null) {
            setTitle("Add New Game");
        } else if (readOnly) {
            setTitle("Game Details");
        } else {
            setTitle("Edit Game");
        }

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        GridPane.setHgrow(nameField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Developer:"), 0, 1);
        grid.add(developerField, 1, 1);
        GridPane.setHgrow(developerField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Genre:"), 0, 2);
        grid.add(genreField, 1, 2);
        GridPane.setHgrow(genreField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Publisher:"), 0, 3);
        grid.add(publisherField, 1, 3);
        GridPane.setHgrow(publisherField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Platforms:"), 0, 4);
        grid.add(platformsField, 1, 4);
        GridPane.setHgrow(platformsField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("SteamID:"), 0, 5);
        grid.add(steamidField, 1, 5);
        GridPane.setHgrow(steamidField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Playtime:"), 0, 6);
        grid.add(playtimeField, 1, 6);
        GridPane.setHgrow(playtimeField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Format:"), 0, 7);
        grid.add(formatField, 1, 7);
        GridPane.setHgrow(formatField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Language:"), 0, 8);
        grid.add(languageField, 1, 8);
        GridPane.setHgrow(languageField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Rating:"), 0, 9);
        grid.add(ratingField, 1, 9);
        GridPane.setHgrow(ratingField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("ReleaseYear:"), 0, 10);
        grid.add(releaseYearField, 1, 10);
        GridPane.setHgrow(releaseYearField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Tags:"), 0, 11);
        grid.add(tagsField, 1, 11);
        GridPane.setHgrow(tagsField, javafx.scene.layout.Priority.ALWAYS);

        grid.add(new Label("Image Cover Name (include file extension):"), 0, 12);
        grid.add(imagePathField, 1, 12);
        GridPane.setHgrow(imagePathField, javafx.scene.layout.Priority.ALWAYS);


        if (gameToEdit != null) {
            nameField.setText(gameToEdit.getName());
            developerField.setText(gameToEdit.getDeveloper());
            genreField.setText(gameToEdit.getGenre());
            publisherField.setText(gameToEdit.getPublisher());
            platformsField.setText(gameToEdit.getPlatforms());
            steamidField.setText(gameToEdit.getSteamid());
            releaseYearField.setText(gameToEdit.getReleaseYear());
            playtimeField.setText(gameToEdit.getPlaytime());
            formatField.setText(gameToEdit.getFormat());
            languageField.setText(gameToEdit.getLanguage());
            ratingField.setText(String.valueOf(gameToEdit.getRating()));
            tagsField.setText(String.join(", ", gameToEdit.getTags()));
            imagePathField.setText(gameToEdit.getImagePath());

            nameField.setEditable(!readOnly);
            developerField.setEditable(!readOnly);
            genreField.setEditable(!readOnly);
            publisherField.setEditable(!readOnly);
            platformsField.setEditable(!readOnly);
            steamidField.setEditable(!readOnly);
            releaseYearField.setEditable(!readOnly);
            playtimeField.setEditable(!readOnly);
            formatField.setEditable(!readOnly);
            languageField.setEditable(!readOnly);
            ratingField.setEditable(!readOnly);
            tagsField.setEditable(!readOnly);
        }

        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(e -> saveAndClose(gameToEdit));

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.getChildren().addAll(cancelButton, saveButton);
        grid.add(buttonBox, 1,13 );

        Scene scene = new Scene(grid);
        setScene(scene);
        sizeToScene();
        setResizable(false);
    }


    private void saveAndClose(Game gameToEdit) {
        String name = nameField.getText().trim();
        String developer = developerField.getText().trim();
        String genre = genreField.getText().trim();
        String publisher = publisherField.getText().trim();
        String platform = platformsField.getText().trim();
        String steamid = steamidField.getText().trim();
        String releaseYear = releaseYearField.getText().trim();
        String playtime = playtimeField.getText().trim();
        String format= formatField.getText().trim();
        String language = languageField.getText().trim();
        String ratingText = ratingField.getText().trim();
        String  tagsText = tagsField.getText().trim();
        String imageText = imagePathField.getText().trim();

        if (name.isEmpty() || developer.isEmpty() || genre.isEmpty() ||publisher.isEmpty() ||platform.isEmpty()
                ||steamid.isEmpty()||releaseYear.isEmpty()||playtime.isEmpty()||format.isEmpty()||language.isEmpty()
                ||ratingText.isEmpty()||tagsText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter all fields");
            alert.initOwner(this);
            alert.showAndWait();
            return;
        }
        int rating = Integer.parseInt(ratingText);
        String[] tags = tagsText.isEmpty() ? new String[0] : tagsText.split("\\s*,\\s*");


        if (gameToEdit == null) {
            resultGame = new Game(name, developer,genre,publisher,platform,steamid,releaseYear,playtime,format,language,rating,tags,imageText);
        } else {
            resultGame = new Game(name, developer,genre,publisher,platform,steamid,releaseYear,playtime,format,language,rating,tags,imageText);
        }
        close();
    }
    public void showDetailsOnly() {
        HBox mainContainer = new HBox(20);
        mainContainer.setPadding(new Insets(15));

        // Left side details
        GridPane detailsGrid = new GridPane();
        detailsGrid.setAlignment(Pos.TOP_LEFT);
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);

        detailsGrid.add(createDetailRow("Name:", nameField.getText()), 0, 0);
        detailsGrid.add(createDetailRow("Developer:", developerField.getText()), 0, 1);
        detailsGrid.add(createDetailRow("Genre:", genreField.getText()), 0, 2);
        detailsGrid.add(createDetailRow("Publisher:", publisherField.getText()), 0, 3);
        detailsGrid.add(createDetailRow("Platforms:", platformsField.getText()), 0, 4);
        detailsGrid.add(createDetailRow("SteamID:", steamidField.getText()), 0, 5);
        detailsGrid.add(createDetailRow("Release Year:", releaseYearField.getText()), 0, 6);
        detailsGrid.add(createDetailRow("Playtime:", playtimeField.getText()), 0, 7);
        detailsGrid.add(createDetailRow("Format:", formatField.getText()), 0, 8);
        detailsGrid.add(createDetailRow("Language:", languageField.getText()), 0, 9);
        detailsGrid.add(createDetailRow("Rating:", ratingField.getText()), 0, 10);
        detailsGrid.add(createDetailRow("Tags:", tagsField.getText()), 0, 11);

        // Right side Cover Image
        ImageView coverImage = new ImageView();
        coverImage.setPreserveRatio(true);
        coverImage.setFitWidth(200); // Fixed width, height will maintain aspect ratio

        try {
            if (imagePathField.getText() != null && !imagePathField.getText().isEmpty()) {
                Image image = FileHandler.loadCoverImage(imagePathField.getText(), 200, 200);
                coverImage.setImage(image);
            }
        } catch (Exception e) {
            // If image fails to load, just leave it empty
        }

        ScrollPane scrollPane = new ScrollPane(detailsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(5));

        VBox imageContainer = new VBox(coverImage);
        imageContainer.setAlignment(Pos.TOP_CENTER);
        imageContainer.setPadding(new Insets(5));

        mainContainer.getChildren().addAll(scrollPane, imageContainer);

        Scene detailsScene = new Scene(mainContainer, 700, 500); // Slightly larger window
        setScene(detailsScene);
        setTitle("Game Details - " + nameField.getText());
        sizeToScene();
    }
    private HBox createDetailRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");

        Label value = new Label(valueText);
        value.setStyle("-fx-wrap-text: true;");
        value.setMaxWidth(Double.MAX_VALUE);

        HBox row = new HBox(10, label, value);
        row.setAlignment(Pos.BASELINE_LEFT);
        HBox.setHgrow(value, Priority.ALWAYS);

        return row;
    }

    public Optional<Game> showAndWaitForResult() {
        showAndWait();
        return Optional.ofNullable(resultGame);
    }
}

