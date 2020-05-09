package com.nighto.weebu.event;

import com.nighto.weebu.event.events.Event;

public interface EventListener {
    void notify(Event event);
}
