package test.java.ru.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.ru.booking.exception.BookingConflictException;
import main.java.ru.booking.model.Booking;
import main.java.ru.booking.model.Employee;
import main.java.ru.booking.model.Room;
import main.java.ru.booking.model.RoomType;
import main.java.ru.booking.model.TimeInterval;
import main.java.ru.booking.service.BookingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingServiceTest {

    private BookingService service;
    private Room room;
    private Employee employee;
    private TimeInterval interval;

    @BeforeEach
    void setUp() {
        service = new BookingService();
        room = new Room("A-1", 10, RoomType.AUDITORIUM, Set.of("доска"));
        employee = new Employee("Тестов");
        interval = new TimeInterval(
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 11, 0));
    }

    @Test
    void book_success() throws BookingConflictException {
        var b = service.book(room, employee, interval, "Занятие");
        assertNotNull(b);
        assertEquals(1, service.getBookings().size());
    }

    @Test
    void book_roomConflict_throws() throws BookingConflictException {
        service.book(room, employee, interval, "Первая");
        var other = new Employee("Другой");
        var overlap = new TimeInterval(
                LocalDateTime.of(2026, 5, 10, 10, 30),
                LocalDateTime.of(2026, 5, 10, 11, 30));
        assertThrows(BookingConflictException.class,
                () -> service.book(room, other, overlap, "Вторая"));
    }

    @Test
    void book_employeeConflict_throws() throws BookingConflictException {
        service.book(room, employee, interval, "Первая");
        var otherRoom = new Room("B-2", 5, RoomType.LAB, Set.of());
        var overlap = new TimeInterval(
                LocalDateTime.of(2026, 5, 10, 10, 15),
                LocalDateTime.of(2026, 5, 10, 10, 45));
        assertThrows(BookingConflictException.class,
                () -> service.book(otherRoom, employee, overlap, "Вторая"));
    }

    @Test
    void book_adjacentIntervals_allowed() throws BookingConflictException {
        service.book(room, employee, interval, "Первая");
        var adjacent = new TimeInterval(
                LocalDateTime.of(2026, 5, 10, 11, 0),
                LocalDateTime.of(2026, 5, 10, 12, 0));
        assertDoesNotThrow(() ->
                service.book(room, new Employee("Другой"), adjacent, "Вторая"));
    }

    @Test
    void cancel_existing_returnsTrue() throws BookingConflictException {
        var b = service.book(room, employee, interval, "Занятие");
        assertTrue(service.cancel(b));
        assertEquals(0, service.getBookings().size());
    }

    @Test
    void cancel_unknown_returnsFalse() {
        assertFalse(service.cancel(
                new Booking(room, employee, interval, "Фантом")));
    }

    @Test
    void findFreeSlots_noBookings_fullDay() {
        var slots = service.findFreeSlots(room, LocalDate.of(2026, 5, 10));
        assertEquals(1, slots.size());
        assertEquals(9,  slots.get(0).start().getHour());
        assertEquals(21, slots.get(0).end().getHour());
    }

    @Test
    void findFreeSlots_withBooking_splitsDay() throws BookingConflictException {
        var date = LocalDate.of(2026, 5, 10);
        service.book(room, employee,
                new TimeInterval(date.atTime(10, 0), date.atTime(11, 0)), "X");
        var slots = service.findFreeSlots(room, date);
        assertEquals(2, slots.size());
    }

    @Test
    void findFreeSlots_ignoresOtherRooms() throws BookingConflictException {
        var date = LocalDate.of(2026, 5, 10);
        var otherRoom = new Room("Z-9", 5, RoomType.LAB, Set.of());
        service.book(otherRoom, employee,
                new TimeInterval(date.atTime(10, 0), date.atTime(11, 0)), "X");
        var slots = service.findFreeSlots(room, date);
        assertEquals(1, slots.size());
    }

    @Test
    void findAvailableRooms_byCapacity() {
        var small = new Room("S", 3, RoomType.MEETING_ROOM, Set.of());
        var big   = new Room("B", 20, RoomType.AUDITORIUM, Set.of());
        var result = service.findAvailableRooms(
                List.of(small, big), 10, Set.of(), interval);
        assertEquals(List.of(big), result);
    }

    @Test
    void findAvailableRooms_byEquipment() {
        var withProjector = new Room("P", 20, RoomType.AUDITORIUM, Set.of("проектор"));
        var without       = new Room("N", 20, RoomType.AUDITORIUM, Set.of("доска"));
        var result = service.findAvailableRooms(
                List.of(withProjector, without), 10, Set.of("проектор"), interval);
        assertEquals(List.of(withProjector), result);
    }

    @Test
    void findAvailableRooms_excludesBusyRoom() throws BookingConflictException {
        service.book(room, employee, interval, "X");
        var result = service.findAvailableRooms(
                List.of(room), 1, Set.of(), interval);
        assertTrue(result.isEmpty());
    }

    @Test
    void timeInterval_endBeforeStart_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new TimeInterval(
                        LocalDateTime.of(2026, 5, 10, 12, 0),
                        LocalDateTime.of(2026, 5, 10, 11, 0)));
    }

    @Test
    void equalsAndHashCode_contractForRoom() {
        var r1 = new Room("X", 10, RoomType.LAB, Set.of());
        var r2 = new Room("X", 99, RoomType.MEETING_ROOM, Set.of("стол"));
        var r3 = new Room("Y", 10, RoomType.LAB, Set.of());
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }
}