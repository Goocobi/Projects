package main.java.ru.booking.model;

/**
 * Всё, что можно забронировать и описать.
 */
public interface Bookable {

    /**
     * Возвращает текстовое описание объекта.
     *
     * @return описание, пригодное для вывода пользователю
     */
    String getDescription();
}