package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Cable;
import com.mycompany.gamezone.model.Controller;
import com.mycompany.gamezone.model.Memory;
import java.util.List;

/** One inner mapper per accessory type; a new type adds one mapper to the list. */
public class AccessoryRecordMapper {

    private static final String SEPARATOR = "|";
    private static final String CONTROLLER_TYPE = "CONTROLLER";
    private static final String CABLE_TYPE = "CABLE";
    private static final String MEMORY_TYPE = "MEMORY";

    private final List<TypeMapper<?>> mappers = List.of(
            new ControllerMapper(), new CableMapper(), new MemoryMapper());

    public String toLine(Accessory accessory) {
        for (TypeMapper<?> mapper : mappers) {
            if (mapper.supports(accessory)) {
                return mapper.toLine(accessory);
            }
        }
        return "";
    }

    /** @return the accessory, or null if the type key is unknown. */
    public Accessory toAccessory(String[] data) {
        for (TypeMapper<?> mapper : mappers) {
            if (mapper.typeKey.equals(data[0])) {
                return mapper.toAccessory(data);
            }
        }
        return null;
    }

    private static String join(Object... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            line.append(String.valueOf(values[i]));
        }
        return line.toString();
    }

    private static void addCompatibleConsoles(Accessory accessory, String consoleIds) {
        if (consoleIds == null || consoleIds.trim().isEmpty()) {
            return;
        }
        for (String consoleId : consoleIds.split(",")) {
            String trimmedId = consoleId.trim();
            if (!trimmedId.isEmpty()) {
                accessory.addCompatibleConsole(trimmedId);
            }
        }
    }

    private abstract static class TypeMapper<T extends Accessory> {
        private final String typeKey;
        private final Class<T> accessoryClass;

        private TypeMapper(String typeKey, Class<T> accessoryClass) {
            this.typeKey = typeKey;
            this.accessoryClass = accessoryClass;
        }

        boolean supports(Accessory accessory) {
            return accessoryClass.isInstance(accessory);
        }

        String toLine(Accessory accessory) {
            return typeKey + SEPARATOR + fieldsOf(accessoryClass.cast(accessory));
        }

        abstract String fieldsOf(T accessory);

        abstract Accessory toAccessory(String[] data);
    }

    private static final class ControllerMapper extends TypeMapper<Controller> {
        private ControllerMapper() {
            super(CONTROLLER_TYPE, Controller.class);
        }

        @Override
        String fieldsOf(Controller c) {
            return join(c.getId(), c.getTitle(), c.getPrice(), c.getStock(),
                    c.getConnectionType(),
                    String.join(",", c.getCompatibleConsoleIds()));
        }

        @Override
        Accessory toAccessory(String[] data) {
            Controller controller = new Controller(
                    data[1], data[2],
                    Double.parseDouble(data[3]),
                    Integer.parseInt(data[4]),
                    data[5]);
            addCompatibleConsoles(controller, data[6]);
            return controller;
        }
    }

    private static final class CableMapper extends TypeMapper<Cable> {
        private CableMapper() {
            super(CABLE_TYPE, Cable.class);
        }

        @Override
        String fieldsOf(Cable c) {
            return join(c.getId(), c.getTitle(), c.getPrice(), c.getStock(),
                    c.getLengthInMeters(), c.getConnectorType());
        }

        @Override
        Accessory toAccessory(String[] data) {
            return new Cable(
                    data[1], data[2],
                    Double.parseDouble(data[3]),
                    Integer.parseInt(data[4]),
                    Double.parseDouble(data[5]),
                    data[6]);
        }
    }

    private static final class MemoryMapper extends TypeMapper<Memory> {
        private MemoryMapper() {
            super(MEMORY_TYPE, Memory.class);
        }

        @Override
        String fieldsOf(Memory m) {
            return join(m.getId(), m.getTitle(), m.getPrice(), m.getStock(),
                    m.getCapacityInGb(), m.getMemoryType(),
                    String.join(",", m.getCompatibleConsoleIds()));
        }

        @Override
        Accessory toAccessory(String[] data) {
            Memory memory = new Memory(
                    data[1], data[2],
                    Double.parseDouble(data[3]),
                    Integer.parseInt(data[4]),
                    Integer.parseInt(data[5]),
                    data[6]);
            addCompatibleConsoles(memory, data[7]);
            return memory;
        }
    }
}