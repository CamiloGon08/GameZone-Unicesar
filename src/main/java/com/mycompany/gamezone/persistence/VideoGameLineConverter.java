package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;

/** Keeps video-game record conversion in the persistence layer. */
public class VideoGameLineConverter implements ProductLineConverter {

    @Override
    public String getTypeKey() {
        return "VIDEOGAME";
    }

    @Override
    public boolean supports(Product product) {
        return product instanceof VideoGame;
    }

    @Override
    public String toLine(Product product) {
        VideoGame videoGame = (VideoGame) product;
        return getTypeKey() + "," + videoGame.getId() + "," + videoGame.getTitle() + ","
                + videoGame.getPrice() + "," + videoGame.getStock() + ","
                + videoGame.getPlatform() + "," + videoGame.getGenre() + ","
                + videoGame.getAgeRating();
    }

    @Override
    public Product toProduct(String id, String title, double price, int stock, String[] fields) {
        return new VideoGame(id, title, price, stock, fields[5], fields[6], fields[7]);
    }
}
