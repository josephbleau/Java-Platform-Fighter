package com.nighto.weebu.event;

import com.nighto.weebu.event.game.Event;

public interface EventListener {
    void notify(Event event);
    void registerEventHandler(EventHandler eventHandler);
}
