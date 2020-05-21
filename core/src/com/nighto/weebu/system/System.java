package com.nighto.weebu.system;

import com.nighto.weebu.entity.Entity;

import java.util.List;

public abstract class System {
    public void process(List<Entity> entities) {
        entities.forEach(this::process);
    }

    abstract void process(Entity entity);
}
