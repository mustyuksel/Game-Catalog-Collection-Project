package org.example;

public class Game {
    private String imagePath;
    private String name;
    private String genre;
    private String developer;
    private String publisher;
    private String platforms;
    private String steamid;
    private String releaseYear;
    private String playtime;
    private String format;
    private String language;
    private int rating;
    private String[] tags;

    public Game(String name, String developer,String genre,String publisher,String platforms,String steamid,String
            releaseYear, String playtime, String format,String language,int rating,String[] tags,String imagePath) {
        this.name = name;
        this.developer = developer;
        this.genre = genre;
        this.publisher = publisher;
        this.platforms=platforms;
        this.steamid=steamid;
        this.releaseYear=releaseYear;
        this.playtime=playtime;
        this.format=format;
        this.language=language;
        this.rating=rating;
        this.tags=tags;
        this.imagePath=imagePath;
    }

    public String getName() { return name; }
    public String getDeveloper() { return developer; }
    public String getGenre() {return genre; }

    public int getRating() {return rating;}

    public String getPlatforms() {return platforms;}

    public String getPublisher() {return publisher;}

    public String getPlaytime() {return playtime;}

    public String getFormat() {return format;}

    public String getReleaseYear() {return releaseYear;}

    public String getLanguage() {return language;}

    public String getSteamid() {return steamid;}

    public String[] getTags() {return tags;}

    public String getImagePath() { return imagePath; }


    public void setName(String name) { this.name = name; }
    public void setDeveloper(String developer) { this.developer = developer; }
    public void setGenre(String genre) {this.genre = genre;}
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setPublisher(String publisher) {this.publisher = publisher;}
    public void setPlatforms(String platforms) {this.platforms = platforms;}
    public void setSteamid(String steamid) {this.steamid = steamid;}
    public void setReleaseYear(String releaseYear) {this.releaseYear = releaseYear;}
    public void setPlaytime(String playtime) {this.playtime = playtime;}
    public void setFormat(String format) {this.format = format;}
    public void setLanguage(String language) {this.language = language;}
    public void setRating(int rating) {this.rating = rating;}


    @Override
    public String toString() {

        return name + " (by " + developer + ")";
    }


}