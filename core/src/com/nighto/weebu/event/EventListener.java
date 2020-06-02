package com.nighto.weebu.event;

import com.nighto.weebu.event.game.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class EventListener {
    protected final List<EventHandler> eventHandlers;

    public EventListener() {
        eventHandlers = new ArrayList<>();
    }

    public void notify(Event event) {
        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.supports(event)) {
                eventHandler.handle(event);
            }
        }
    }

    public void registerEventHandler(EventHandler eventHandler) {
        this.eventHandlers.add(eventHandler);
    }
}
