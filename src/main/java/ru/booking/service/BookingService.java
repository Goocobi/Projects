package main.java.ru.booking.service;

import main.java.ru.booking.exception.BookingConflictException;
import main.java.ru.booking.model.Booking;
import main.java.ru.booking.model.Employee;
import main.java.ru.booking.model.Room;
import main.java.ru.booking.model.TimeInterval;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Логика бронирования помещений: создание и отмена броней,
 * поиск свободных окон и подбор помещения по требованиям.
 */
public class BookingService {

    private static final LocalTime WORK_DAY_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_DAY_END   = LocalTime.of(21, 0);

    private final List<Booking> bookings = new ArrayList<>();

    /**
     * Создаёт бронь с проверкой пересечения по помещению и сотруднику.
     *
     * @param room     помещение
     * @param employee сотрудник
     * @param interval интервал времени
     * @param purpose  цель брони
     * @return созданная бронь
     * @throws BookingConflictException если помещение или сотрудник уже заняты
     *                                  на пересекающемся интервале
     */
    public Booking book(Room room, Employee employee,
                        TimeInterval interval, String purpose)
            throws BookingConflictException {

        for (var existing : bookings) {
            if (!existing.getInterval().intersects(interval)) {
                continue;
            }
            if (existing.getRoom().equals(room)) {
                throw new BookingConflictException(
                        "Помещение «%s» занято: %s".formatted(room.getName(), existing));
            }
            if (existing.getEmployee().equals(employee)) {
                throw new BookingConflictException(
                        "Сотрудник %s занят: %s".formatted(employee.getName(), existing));
            }
        }
        var booking = new Booking(room, employee, interval, purpose);
        bookings.add(booking);
        return booking;
    }

    /**
     * Отменяет существующую бронь.
     *
     * @param booking бронь для отмены
     * @return {@code true}, если бронь была найдена и удалена
     */
    public boolean cancel(Booking booking) {
        return bookings.remove(booking);
    }

    /**
     * Возвращает неизменяемый список всех броней.
     *
     * @return список броней
     */
    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    /**
     * Возвращает свободные окна помещения на указанную дату
     * в рабочие часы (9:00–21:00).
     *
     * @param room помещение
     * @param date дата
     * @return список свободных интервалов в порядке возрастания
     */
    public List<TimeInterval> findFreeSlots(Room room, LocalDate date) {
        var dayStart = date.atTime(WORK_DAY_START);
        var dayEnd   = date.atTime(WORK_DAY_END);

        var busy = bookings.stream()
                .filter(b -> b.getRoom().equals(room))
                .filter(b -> b.getInterval().start().toLocalDate().equals(date))
                .sorted(Comparator.comparing(b -> b.getInterval().start()))
                .toList();

        var free = new ArrayList<TimeInterval>();
        var cursor = dayStart;
        for (var b : busy) {
            var iv = b.getInterval();
            if (cursor.isBefore(iv.start())) {
                free.add(new TimeInterval(cursor, iv.start()));
            }
            if (iv.end().isAfter(cursor)) {
                cursor = iv.end();
            }
        }
        if (cursor.isBefore(dayEnd)) {
            free.add(new TimeInterval(cursor, dayEnd));
        }
        return free;
    }

    /**
     * Подбирает помещения по вместимости, оснащению и свободному времени.
     *
     * @param candidates список помещений-кандидатов
     * @param capacity   требуемая вместимость
     * @param equipment  требуемое оснащение
     * @param wanted     желаемый интервал
     * @return список подходящих помещений
     */
    public List<Room> findAvailableRooms(Collection<Room> candidates,
                                         int capacity,
                                         Set<String> equipment,
                                         TimeInterval wanted) {
        return candidates.stream()
                .filter(r -> r.fits(capacity, equipment))
                .filter(r -> bookings.stream().noneMatch(b ->
                        b.getRoom().equals(r) && b.getInterval().intersects(wanted)))
                .toList();
    }
}