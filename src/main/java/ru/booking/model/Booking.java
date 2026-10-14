package main.java.ru.booking.model;

import java.util.Objects;

/**
 * Бронь помещения сотрудником на интервал времени.
 */
public class Booking implements Bookable {

    private final Room room;
    private final Employee employee;
    private final TimeInterval interval;
    private final String purpose;

    /**
     * Создаёт бронь.
     *
     * @param room     помещение
     * @param employee сотрудник
     * @param interval интервал времени
     * @param purpose  цель брони
     * @throws NullPointerException если любой из аргументов равен {@code null}
     */
    public Booking(Room room, Employee employee, TimeInterval interval, String purpose) {
        this.room = Objects.requireNonNull(room, "room");
        this.employee = Objects.requireNonNull(employee, "employee");
        this.interval = Objects.requireNonNull(interval, "interval");
        this.purpose = Objects.requireNonNull(purpose, "purpose");
    }

    /**
     * Возвращает помещение.
     *
     * @return помещение
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Возвращает сотрудника.
     *
     * @return сотрудник
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Возвращает интервал брони.
     *
     * @return интервал
     */
    public TimeInterval getInterval() {
        return interval;
    }

    /**
     * Возвращает цель брони.
     *
     * @return цель
     */
    public String getPurpose() {
        return purpose;
    }

    @Override
    public String getDescription() {
        return "%s | %s | %s — %s | %s".formatted(
                room.getName(), employee.getName(),
                interval.start(), interval.end(), purpose);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}