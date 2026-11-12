package main.java.ru.booking.model;

import java.util.Objects;
import java.util.Set;

/**
 * Помещение. Класс-идентификатор: равенство определяется именем.
 */
public class Room {

    private final String name;
    private final int capacity;
    private final RoomType type;
    private final Set<String> equipment;

    /**
     * Создаёт помещение.
     *
     * @param name      уникальное имя помещения
     * @param capacity  вместимость в человеках, должна быть положительной
     * @param type      тип помещения
     * @param equipment оснащение помещения
     * @throws IllegalArgumentException если вместимость не положительна
     * @throws NullPointerException     если любой из аргументов равен {@code null}
     */
    public Room(String name, int capacity, RoomType type, Set<String> equipment) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        Objects.requireNonNull(equipment, "equipment");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть > 0");
        }
        this.capacity = capacity;
        // защитная копия, чтобы внешний набор не тёк внутрь
        this.equipment = Set.copyOf(equipment);
    }

    /**
     * Возвращает имя помещения.
     *
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает вместимость помещения.
     *
     * @return вместимость
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Возвращает тип помещения.
     *
     * @return тип
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Возвращает неизменяемое множество оснащения.
     *
     * @return оснащение
     */
    public Set<String> getEquipment() {
        return equipment;
    }

    /**
     * Проверяет, подходит ли помещение под требования.
     *
     * @param requiredCapacity требуемая вместимость
     * @param required         требуемое оснащение
     * @return {@code true}, если помещение удовлетворяет требованиям
     */
    public boolean fits(int requiredCapacity, Set<String> required) {
        return capacity >= requiredCapacity && equipment.containsAll(required);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof Room other) && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "%s (%s, %d мест)".formatted(name, type.getDisplayName(), capacity);
    }
}