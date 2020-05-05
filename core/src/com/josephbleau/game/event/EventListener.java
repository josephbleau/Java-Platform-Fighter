package com.josephbleau.game.event;

import com.josephbleau.game.event.events.Event;

public interface EventListener {
    void notify(Event event);
}
