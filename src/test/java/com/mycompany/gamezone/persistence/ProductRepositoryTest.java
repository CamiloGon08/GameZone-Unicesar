package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Controller;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    @TempDir
    Path directory;

    @Test
    void injectedConvertersSupportAnotherFamilyAndAreDefensivelyCopied() throws IOException {
        Path file = directory.resolve("collectibles.txt");
        ProductLineConverter converter = new ProductLineConverter() {
            public String getTypeKey() { return "COLLECTIBLE"; }
            public boolean supports(Product product) { return product instanceof Collectible; }
            public String toLine(Product product) {
                return "COLLECTIBLE," + product.getId() + "," + product.getTitle()
                        + "," + product.getPrice() + "," + product.getStock();
            }
            public Product toProduct(String id, String title, double price, int stock, String[] fields) {
                return new Collectible(id, title, price, stock);
            }
        };
        List<ProductLineConverter> converters = new ArrayList<>(List.of(converter));
        ProductRepository repository = new FileProductRepository(file.toString(), converters);
        converters.clear();
        repository.saveAll(List.of(new Collectible("X1", "Figure", 25, 2)));
        assertArrayEquals(("COLLECTIBLE,X1,Figure,25.0,2" + System.lineSeparator())
                .getBytes(Charset.defaultCharset()), Files.readAllBytes(file));
        Product restored = assertInstanceOf(Collectible.class, repository.loadAll().get(0));
        assertEquals("X1", restored.getId());
        assertEquals(25, restored.getPrice());
        assertEquals(2, restored.getStock());
    }

    private static class Collectible extends Product {
        Collectible(String id, String title, double price, int stock) {
            super(id, title, price, stock);
        }
        public String getDescription() { return getTitle(); }
    }

    @Test
    void bothFamiliesKeepTheirExactFileBytesAndRoundTripAttributes() throws IOException {
        Path file = directory.resolve("products.txt");
        ProductRepository repository = new FileProductRepository(file.toString());
        VideoGame game = new VideoGame("G1", "Game", 100.5, 2, "PC", "RPG", "E");
        Console console = new Console("C1", "Console", 200, 3, "Sony", "PS5", "9");

        repository.saveAll(List.of(game, console));

        String separator = System.lineSeparator();
        String expected = "VIDEOGAME,G1,Game,100.5,2,PC,RPG,E" + separator
                + "CONSOLE,C1,Console,200.0,3,Sony,PS5,9" + separator;
        assertArrayEquals(expected.getBytes(Charset.defaultCharset()), Files.readAllBytes(file));
        List<Product> loaded = repository.loadAll();
        assertEquals(2, loaded.size());
        VideoGame loadedGame = assertInstanceOf(VideoGame.class, loaded.get(0));
        Console loadedConsole = assertInstanceOf(Console.class, loaded.get(1));
        assertEquals("G1", loadedGame.getId());
        assertEquals("Game", loadedGame.getTitle());
        assertEquals(100.5, loadedGame.getPrice());
        assertEquals(2, loadedGame.getStock());
        assertEquals("PC", loadedGame.getPlatform());
        assertEquals("RPG", loadedGame.getGenre());
        assertEquals("E", loadedGame.getAgeRating());
        assertEquals("C1", loadedConsole.getId());
        assertEquals("Console", loadedConsole.getTitle());
        assertEquals(200.0, loadedConsole.getPrice());
        assertEquals(3, loadedConsole.getStock());
        assertEquals("Sony", loadedConsole.getBrand());
        assertEquals("PS5", loadedConsole.getModel());
        assertEquals("9", loadedConsole.getGeneration());
    }

    @Test
    void readingStillSkipsBlankLinesAndTrimsEveryField() throws IOException {
        Path file = directory.resolve("products.txt");
        Files.writeString(file, "\n \t\n VIDEOGAME , G1 , Game , 100.5 , 2 , PC , RPG , E \n");

        List<Product> loaded = new FileProductRepository(file.toString()).loadAll();

        assertEquals(1, loaded.size());
        VideoGame game = assertInstanceOf(VideoGame.class, loaded.get(0));
        assertEquals("G1", game.getId());
        assertEquals("Game", game.getTitle());
        assertEquals(100.5, game.getPrice());
        assertEquals(2, game.getStock());
        assertEquals("PC", game.getPlatform());
        assertEquals("RPG", game.getGenre());
        assertEquals("E", game.getAgeRating());
    }

    @ParameterizedTest
    @MethodSource("malformedRecords")
    void malformedRecordsKeepPhysicalLineContextAndOriginalCause(String record,
            Class<? extends RuntimeException> causeType, String causeMessage) throws IOException {
        Path file = directory.resolve("products.txt");
        Files.writeString(file, "\nVIDEOGAME,VALID,Game,100,2,PC,RPG,E\n \n" + record + "\n");

        PersistenceException failure = assertThrows(PersistenceException.class,
                () -> new FileProductRepository(file.toString()).loadAll());

        assertEquals("Read error: malformed product at line 4 of file '" + file + "'",
                failure.getMessage());
        assertEquals("PERSISTENCE_ERROR", failure.getErrorCode());
        assertNotNull(failure.getCause());
        assertEquals(causeType, failure.getCause().getClass());
        if (causeMessage != null) {
            assertEquals(causeMessage, failure.getCause().getMessage());
        }
    }

    @Test
    void unsupportedProductsKeepTheExistingWriteFailure() {
        Path file = directory.resolve("products.txt");
        PersistenceException failure = assertThrows(PersistenceException.class,
                () -> new FileProductRepository(file.toString()).saveAll(List.of(
                        new Controller("A1", "Pad", 50, 1, "WIRELESS"))));

        assertEquals("Write error: unsupported product type Controller for file '" + file + "'",
                failure.getMessage());
        assertEquals("PERSISTENCE_ERROR", failure.getErrorCode());
        assertNull(failure.getCause());
    }

    @Test
    void ioFailuresKeepOperationContextAndTheirTechnicalCause() {
        PersistenceException read = assertThrows(PersistenceException.class,
                () -> new FileProductRepository(directory.toString()).loadAll());
        assertEquals("Read error: could not load products from file '" + directory + "'",
                read.getMessage());
        assertInstanceOf(IOException.class, read.getCause());
        Path missingParent = directory.resolve("missing").resolve("products.txt");
        PersistenceException write = assertThrows(PersistenceException.class,
                () -> new FileProductRepository(missingParent.toString()).saveAll(List.of()));
        assertEquals("Write error: could not save products to file '" + missingParent + "'",
                write.getMessage());
        assertInstanceOf(IOException.class, write.getCause());
    }

    @Test
    void aMissingFileStillLoadsAsAnEmptyCatalog() {
        assertTrue(new FileProductRepository(directory.resolve("absent.txt").toString())
                .loadAll().isEmpty());
    }

    private static Stream<Arguments> malformedRecords() {
        return Stream.of(
                Arguments.of("VIDEOGAME,G1,Game,bad,2,PC,RPG,E", NumberFormatException.class, null),
                Arguments.of("CONSOLE,C1,Console,200,bad,Sony,PS5,9", NumberFormatException.class, null),
                Arguments.of("CONSOLE,C1,Console,200,3,Sony,PS5", ArrayIndexOutOfBoundsException.class, null),
                Arguments.of("VIDEOGAME,G1,Game,100,2,PC,RPG,", ArrayIndexOutOfBoundsException.class, null),
                Arguments.of("videogame,G1,Game,100,2,PC,RPG,E", IllegalArgumentException.class,
                        "Unknown product type in file: videogame"),
                Arguments.of("UNKNOWN,X1,Other,100,2,a,b,c", IllegalArgumentException.class,
                        "Unknown product type in file: UNKNOWN"),
                Arguments.of("UNKNOWN,X1,Other,bad,2,a,b,c", NumberFormatException.class, null));
    }
}
