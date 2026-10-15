package main.java.ru.booking;

import main.java.ru.booking.exception.BookingConflictException;
import main.java.ru.booking.model.Bookable;
import main.java.ru.booking.model.Booking;
import main.java.ru.booking.model.Employee;
import main.java.ru.booking.model.Room;
import main.java.ru.booking.model.RoomType;
import main.java.ru.booking.model.TimeInterval;
import main.java.ru.booking.service.BookingService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Консольная демонстрация модели бронирования помещений.
 * Наполняет модель 35 объектами и показывает все операции базового уровня.
 */
public final class Main {

    private static final LocalDate DAY = LocalDate.of(2026, 5, 10);

    private Main() {
    }

    /**
     * Точка входа.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        var rooms = buildRooms();
        var employees = buildEmployees();
        var service = new BookingService();

        System.out.println("=== БРОНИРОВАНИЕ ===");
        seedBookings(service, rooms, employees);

        System.out.println("\n=== КОНФЛИКТ ===");
        demonstrateConflict(service, rooms, employees);

        System.out.println("\n=== СВОБОДНЫЕ ОКНА А-101 (" + DAY + ") ===");
        var room = byName(rooms, "А-101");
        for (var s : service.findFreeSlots(room, DAY)) {
            System.out.printf("  %s — %s%n",
                    s.start().toLocalTime(), s.end().toLocalTime());
        }

        System.out.println("\n=== ПОДБОР (>=10 мест, проектор, 15:00–16:00) ===");
        var wanted = interval(15, 0, 16, 0);
        for (var r : service.findAvailableRooms(rooms, 10, Set.of("проектор"), wanted)) {
            System.out.println("  " + r);
        }

        System.out.println("\n=== ОТМЕНА ===");
        var all = service.getBookings();
        System.out.println("  Было броней: " + all.size());
        if (!all.isEmpty()) {
            var victim = all.get(0);
            System.out.println("  Отменяем: " + victim.getDescription());
            service.cancel(victim);
        }
        System.out.println("  Стало броней: " + service.getBookings().size());

        System.out.println("\n=== ПОЛИМОРФИЗМ ===");
        if (!service.getBookings().isEmpty()) {
            Bookable b = service.getBookings().get(0);
            System.out.println("  " + b.getDescription());
        }

        var totalObjects = rooms.size() + employees.size() + 10;
        System.out.println("\nВсего объектов в модели: " + totalObjects);
    }

    private static Set<Room> buildRooms() {
        Set<Room> rooms = new LinkedHashSet<>();
        rooms.add(new Room("А-101", 30, RoomType.AUDITORIUM, Set.of("проектор", "доска")));
        rooms.add(new Room("А-102", 20, RoomType.AUDITORIUM, Set.of("проектор")));
        rooms.add(new Room("Л-201", 15, RoomType.LAB, Set.of("компьютеры")));
        rooms.add(new Room("П-301", 8,  RoomType.MEETING_ROOM, Set.of("телевизор")));
        rooms.add(new Room("П-302", 12, RoomType.MEETING_ROOM, Set.of("доска")));
        return rooms;
    }

    private static List<Employee> buildEmployees() {
        String[] names = {
                "Иванов", "Петров", "Сидоров", "Кузнецова", "Смирнов",
                "Волкова", "Морозов", "Новикова", "Фёдоров", "Егорова",
                "Павлов", "Соколова", "Виноградов", "Богданова", "Воробьёв",
                "Романова", "Гусев", "Киселёва", "Тарасов", "Белова"
        };
        var employees = new ArrayList<Employee>(names.length);
        for (var n : names) {
            employees.add(new Employee(n));
        }
        return employees;
    }

    private static void seedBookings(BookingService service,
                                     Set<Room> rooms,
                                     List<Employee> employees) {
        bookQuietly(service, byName(rooms, "А-101"), employees.get(0),
                interval(10, 0, 12, 0), "Лекция ООП");
        bookQuietly(service, byName(rooms, "А-102"), employees.get(1),
                interval(11, 0, 13, 0), "Семинар");
        bookQuietly(service, byName(rooms, "Л-201"), employees.get(2),
                interval(9, 0, 11, 0), "Лабораторная");
        bookQuietly(service, byName(rooms, "П-301"), employees.get(3),
                interval(14, 0, 15, 0), "Совещание");
        bookQuietly(service, byName(rooms, "П-302"), employees.get(4),
                interval(15, 0, 16, 0), "Встреча");
        bookQuietly(service, byName(rooms, "А-101"), employees.get(5),
                interval(13, 0, 14, 0), "Консультация");
        bookQuietly(service, byName(rooms, "Л-201"), employees.get(6),
                interval(16, 0, 18, 0), "Практика");
        bookQuietly(service, byName(rooms, "А-102"), employees.get(7),
                interval(17, 0, 18, 0), "Коллоквиум");
        bookQuietly(service, byName(rooms, "П-301"), employees.get(8),
                interval(18, 0, 20, 0), "Планёрка");
        bookQuietly(service, byName(rooms, "П-302"), employees.get(9),
                interval(19, 0, 21, 0), "Разбор");
    }

    private static void bookQuietly(BookingService service, Room room, Employee employee,
                                    TimeInterval interval, String purpose) {
        try {
            Booking b = service.book(room, employee, interval, purpose);
            System.out.println("  + " + b.getDescription());
        } catch (BookingConflictException e) {
            System.out.println("  ! " + e.getMessage());
        }
    }

    private static void demonstrateConflict(BookingService service,
                                            Set<Room> rooms,
                                            List<Employee> employees) {
        try {
            service.book(byName(rooms, "А-101"), employees.get(19),
                    interval(11, 30, 12, 30), "Повтор");
            System.out.println("  ! Конфликт не обнаружен (не ожидалось)");
        } catch (BookingConflictException e) {
            System.out.println("  Ожидаемое исключение: " + e.getMessage());
        }
    }

    private static TimeInterval interval(int fromHour, int fromMinute,
                                         int toHour, int toMinute) {
        return new TimeInterval(
                DAY.atTime(fromHour, fromMinute),
                DAY.atTime(toHour, toMinute));
    }

    private static Room byName(Set<Room> rooms, String name) {
        return rooms.stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}