package com.mycompany.gamezone.service;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;
import com.mycompany.gamezone.persistence.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceRegistrationTest {
    @Test
    void genericRegistrationAcceptsAnotherFamilyThroughAnInMemoryRepository() {
        MemoryRepository repository = new MemoryRepository();
        ProductService service = new ProductService(repository);
        Product collectible = new Product("X", "Collectible", 25, 2) {
            public String getDescription() { return getTitle(); }
        };
        service.register(collectible);
        ProductLookup lookup = service;
        assertSame(collectible, lookup.findById(" x "));
        assertEquals(List.of(collectible), repository.saved);
        assertEquals(1, repository.saves);
    }

    @Test
    void registrationKeepsCommonThenDuplicateThenFamilyValidation() {
        MemoryRepository repository = new MemoryRepository();
        ProductService service = new ProductService(repository);
        service.registerVideoGame("G", "Game", 100, 1, "PC", "RPG", "E");
        assertEquals("El campo título no es válido: no puede estar vacío.",
                assertThrows(InvalidDataException.class,
                        () -> service.registerVideoGame("G", " ", 100, 1, "", "", "")).getMessage());
        assertEquals("Ya existe un producto con el identificador G.",
                assertThrows(BusinessRuleException.class,
                        () -> service.registerVideoGame("G", "Game", 100, 1, "", "", "")).getMessage());
        assertEquals(1, repository.saves);
        assertEquals(1, service.listProducts().size());
    }

    @ParameterizedTest
    @CsvSource({"game,0,plataforma", "game,1,género", "game,2,clasificación de edad",
                "console,0,marca", "console,1,modelo", "console,2,generación"})
    void compatibilityMethodsKeepFamilyFieldOrderAndMessages(String family, int missing, String label) {
        MemoryRepository repository = new MemoryRepository();
        ProductService service = new ProductService(repository);
        String[] fields = {"value", "value", "value"};
        for (int i = missing; i < fields.length; i++) { fields[i] = " \t"; }
        InvalidDataException failure = assertThrows(InvalidDataException.class, () -> {
            if (family.equals("game")) {
                service.registerVideoGame("P", "Product", 100, 1, fields[0], fields[1], fields[2]);
            } else {
                service.registerConsole("P", "Product", 100, 1, fields[0], fields[1], fields[2]);
            }
        });
        assertEquals("El campo " + label + " no es válido: no puede estar vacío.", failure.getMessage());
        assertEquals(0, repository.saves);
    }

    @Test
    void loadingStaysPermissiveAndFamilyChecksKeepTrimSemantics() {
        MemoryRepository repository = new MemoryRepository();
        repository.loaded.add(new VideoGame("OLD", "Old", 100, 1, null, null, null));
        ProductService service = new ProductService(repository);
        repository.loaded.clear();
        assertNotNull(service.findById("OLD"));
        service.registerVideoGame("G", "Game", 100, 1, "\u2003", "RPG", "E");
        assertNotNull(service.findById("G"));
    }

    @Test
    void saveFailureRetainsExistingInMemoryMutationAndTypedCause() {
        MemoryRepository repository = new MemoryRepository();
        ProductService service = new ProductService(repository);
        repository.failure = new PersistenceException("write failed", new IllegalStateException("cause"));
        assertSame(repository.failure, assertThrows(PersistenceException.class,
                () -> service.registerVideoGame("G", "Game", 100, 1, "PC", "RPG", "E")));
        assertNotNull(service.findById("G"));
    }

    @Test
    void nullRepositoryAndNewNullProductEntryPointReportTypedInputErrors() {
        assertThrows(InvalidDataException.class, () -> new ProductService(null));
        ProductService service = new ProductService(new MemoryRepository());
        assertEquals("El campo producto no es válido: no puede ser nulo.",
                assertThrows(InvalidDataException.class, () -> service.register(null)).getMessage());
    }

    private static class MemoryRepository implements ProductRepository {
        final List<Product> loaded = new ArrayList<>();
        List<Product> saved;
        int saves;
        PersistenceException failure;
        public List<Product> loadAll() { return loaded; }
        public void saveAll(List<Product> products) {
            saves++;
            if (failure != null) { throw failure; }
            saved = new ArrayList<>(products);
        }
    }
}
