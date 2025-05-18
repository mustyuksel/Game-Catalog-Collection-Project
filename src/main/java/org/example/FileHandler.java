package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileHandler {
    private static final String FOLDER_NAME = "GameManagementSystem";
    private static final String COVER_NAME = "covers";
    private static final String FILE_NAME = "games.json";
    private static final Gson gson = new Gson();
    private static final Path filePath;
    static {
        String localAppData = System.getenv("LOCALAPPDATA");
        filePath = Paths.get(localAppData, FOLDER_NAME, FILE_NAME);
    }
    private static final Path coverPath;
    static {
        String localAppData = System.getenv("LOCALAPPDATA");
        coverPath = Paths.get(localAppData,FOLDER_NAME,COVER_NAME);
    }

    private static void ensureFileExists() throws IOException {
        if (!Files.exists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, "[]".getBytes());
        }
    }

    public static List<Game> readGames() throws IOException {
        ensureFileExists();
        String json = new String(Files.readAllBytes(filePath));
        return gson.fromJson(json, new TypeToken<List<Game>>(){}.getType());
    }

    public static void writeGames(List<Game> games) throws IOException {
        ensureFileExists();
        Files.write(filePath, gson.toJson(games).getBytes());
    }

    public static void addGame(Game game) throws IOException {
        List<Game> games = readGames();
        games.add(game);
        writeGames(games);
    }

    public static void deleteGame(String name) throws IOException {
        List<Game> games = readGames();
        games.removeIf(g -> g.getName().equalsIgnoreCase(name));
        writeGames(games);
    }
    public static void ensureCoverPathExists() throws IOException{
        ensureFileExists();
        if (!Files.exists(coverPath)) {
            Files.createDirectories(coverPath);
        }
    }
    public static String getCoverImagePath(String imageName) {
        String localAppData = System.getenv("LOCALAPPDATA");
        return Paths.get(
                localAppData,
                FOLDER_NAME,
                COVER_NAME,
                imageName
        ).toString();
    }

    // Load an Image object from the covers folder
    public static Image loadCoverImage(String imageName, double width, double height) {
        String imagePath = getCoverImagePath(imageName);
        return new Image("file:" + imagePath, width, height, true, true);
    }

    public static List<Game> importGamesFromFile(Path importPath) throws IOException {
        String json = new String(Files.readAllBytes(importPath));
        return gson.fromJson(json, new TypeToken<List<Game>>(){}.getType());
    }
    public static void exportGamesToFile(List<Game> gamesToExport, Path exportPath) throws IOException {
        String json = gson.toJson(gamesToExport);
        Files.write(exportPath, json.getBytes());
    }
}