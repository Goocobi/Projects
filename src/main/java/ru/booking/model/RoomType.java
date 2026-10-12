package main.java.ru.booking.model;

/**
 * Тип помещения. Перечисление с полем и телом константы.
 */
public enum RoomType {

    /** Аудитория для лекций и семинаров. */
    AUDITORIUM("Аудитория") {
        @Override
        public boolean needsProjector() {
            return true;
        }
    },

    /** Лаборатория с оборудованием. */
    LAB("Лаборатория") {
        @Override
        public boolean needsProjector() {
            return false;
        }
    },

    /** Переговорная для небольших встреч. */
    MEETING_ROOM("Переговорная") {
        @Override
        public boolean needsProjector() {
            return false;
        }
    };

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает человекочитаемое название типа.
     *
     * @return название для вывода пользователю
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Сообщает, требуется ли помещению этого типа проектор.
     *
     * @return {@code true}, если проектор обычно необходим
     */
    public abstract boolean needsProjector();
}