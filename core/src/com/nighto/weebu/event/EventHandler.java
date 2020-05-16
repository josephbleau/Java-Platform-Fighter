package com.nighto.weebu.event;

import com.nighto.weebu.event.events.Event;

public interface EventHandler {
    boolean supports(Event event);
    void handle(Event event);
}
