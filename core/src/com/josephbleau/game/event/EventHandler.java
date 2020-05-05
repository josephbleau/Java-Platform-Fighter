package com.josephbleau.game.event;

import com.josephbleau.game.entity.Entity;
import com.josephbleau.game.event.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    private List<EventListener> listeners;

    public EventHandler() {
        this.listeners = new ArrayList<>();
    }

    public void registerListeners(List<EventListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void registerEntities(List<Entity> listeners) {
        this.listeners.addAll(listeners);
    }

    public void publish(Event event) {
        for (EventListener listener : listeners) {
            listener.notify(event);
        }
    }
}
