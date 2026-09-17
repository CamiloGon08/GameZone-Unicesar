package com.gamezone.model;

import com.mycompany.gamezone.model.Product;
/**
 * Represents a video game product. In addition to the attributes common to
 * every product, a video game is characterized by the platform it was
 * developed for, its genre, and its recommended age rating.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Creates a new video game with the given attributes.
     *
     * @param id        unique identifier of the product
     * @param title     display title of the product
     * @param price     unit price of the product
     * @param stock     quantity currently available in inventory
     * @param platform  platform the game was developed for (e.g. "PS5", "PC")
     * @param genre     genre of the game (e.g. "RPG", "Shooter")
     * @param ageRating recommended age rating (e.g. "E", "T", "M")
     */
    public VideoGame(String id, String title, double price, int stock,
                      String platform, String genre, String ageRating) {
        super(id, title, price, stock);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Builds a description that integrates the common product attributes
     * with the video game's particular characteristics.
     *
     * @return a human-readable description of the video game
     */
    @Override
    public String getDescription() {
        return String.format("%s [Video game] - Platform: %s, Genre: %s, Age rating: %s, Price: %.2f, Stock: %d",
                getTitle(), platform, genre, ageRating, getPrice(), getStock());
    }

    /**
     * @return the platform the game was developed for
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform the new platform the game was developed for
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the genre of the game
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the new genre of the game
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return the recommended age rating of the game
     */
    public String getAgeRating() {
        return ageRating;
    }

    /**
     * @param ageRating the new recommended age rating of the game
     */
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }
}