package com.nighto.weebu.system;

import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.stage.Stage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GameContext {
    private final List<Entity> entities;
    private List<Entity> entitiesToRemove;
    private ListIterator<Entity> entityIterator;

    private final Stage stage;

    private float frameDelta;

    public GameContext(Stage stage) {
        this.stage = stage;

        entities = new LinkedList<>();
        entities.add(stage);

        entitiesToRemove = new LinkedList<>();

        entityIterator = entities.listIterator();
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Stage getStage() {
        return stage;
    }

    public float getFrameDelta() {
        return frameDelta;
    }

    public void setFrameDelta(float frameDelta) {
        this.frameDelta = frameDelta;
    }

    public Iterator<Entity> refreshEntitiesIterator() {
        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();

        return (entityIterator = entities.listIterator());
    }

    public void registerEntity(Entity entity) {
        entityIterator.add(entity);
    }

    public void removeEntity(Entity entity) { entitiesToRemove.remove(entity); }
}
