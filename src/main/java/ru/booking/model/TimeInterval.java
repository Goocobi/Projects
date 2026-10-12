package main.java.ru.booking.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Неизменяемый интервал времени. Конец строго позже начала.
 *
 * @param start начало интервала
 * @param end   конец интервала
 */
public record TimeInterval(LocalDateTime start, LocalDateTime end) {

    /**
     * Компактный конструктор с проверкой корректности границ.
     *
     * @throws NullPointerException     если хотя бы одна граница равна {@code null}
     * @throws IllegalArgumentException если конец не позже начала
     */
    public TimeInterval {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Конец интервала должен быть позже начала: " + start + " — " + end);
        }
    }

    /**
     * Проверяет пересечение с другим интервалом.
     *
     * @param other другой интервал
     * @return {@code true}, если интервалы пересекаются
     */
    public boolean intersects(TimeInterval other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    /**
     * Возвращает длительность интервала.
     *
     * @return длительность в минутах
     */
    public long durationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}