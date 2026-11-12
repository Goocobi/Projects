package main.java.ru.booking.model;

import java.util.Objects;

/**
 * Сотрудник, который может бронировать помещение.
 */
public class Employee {

    private final String name;

    /**
     * Создаёт сотрудника.
     *
     * @param name ФИО сотрудника
     * @throws NullPointerException если имя равно {@code null}
     */
    public Employee(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Возвращает ФИО сотрудника.
     *
     * @return ФИО
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof Employee other) && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}