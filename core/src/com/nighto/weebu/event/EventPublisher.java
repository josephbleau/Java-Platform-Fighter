package com.nighto.weebu.event;

import com.nighto.weebu.event.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventPublisher {
    private List<EventListener> listeners;

    public EventPublisher() {
        listeners = new ArrayList<>();
    }

    public void registerListeners(List<? extends EventListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void publish(Event event) {
        for (EventListener listener : listeners) {
            listener.notify(event);
        }
    }

    public void publish(List<? extends Event> events) {
        events.forEach(this::publish);
    }
}
