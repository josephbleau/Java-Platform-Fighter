package com.nighto.weebu.event;

import com.nighto.weebu.event.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    private List<EventListener> listeners;

    public EventHandler() {
        this.listeners = new ArrayList<>();
    }

    public void registerListeners(List<? extends EventListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void publish(Event event) {
        for (EventListener listener : listeners) {
            listener.notify(event);
        }
    }
}
