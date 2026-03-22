package pl.sadowski.fieldservice.field.view;

import java.util.ArrayList;
import java.util.List;

public class DomainEvents {

    private static final ThreadLocal<List<Object>> CURRENT_EVENTS =
            ThreadLocal.withInitial(ArrayList::new);

    public static void raise(Object event) {
        CURRENT_EVENTS.get().add(event);
    }

    public static List<Object> drainEvents() {
        List<Object> events = new ArrayList<>(CURRENT_EVENTS.get());
        CURRENT_EVENTS.get().clear();
        return events;
    }
}
