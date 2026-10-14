package main.java.ru.booking.exception;

/**
 * Выбрасывается при попытке забронировать помещение
 * на интервал, который уже занят помещением или сотрудником.
 */
public class BookingConflictException extends Exception {

    /**
     * Создаёт исключение с описанием конфликта.
     *
     * @param message описание конфликта
     */
    public BookingConflictException(String message) {
        super(message);
    }
}

