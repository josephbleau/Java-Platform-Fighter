package com.nighto.weebu.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DebugStateLoadingSystem extends System {
    Map<UUID, Map<Class<? extends Component>, Component>> savedState;

    public DebugStateLoadingSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, null);
        savedState = new HashMap<>();
    }

    @Override
    void process(Entity entity) {
    }

    @Override
    public void process() {
        if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
            saveCurrentState();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
            loadCurrentState();
        }

        super.process();
    }

    public void saveCurrentState() {
        for (Entity entity : gameContext.getEntities()) {
            Map<Class<? extends Component>, Component> savedComponentsByName = new HashMap<>();
            savedState.put(entity.getUuid(), savedComponentsByName);

            for (Class<? extends Component> componentName : entity.getComponents().keySet()) {
                Component component = entity.getComponent(componentName);
                Component newComponentCopy = component.save();

                if  (newComponentCopy != null) {
                    savedComponentsByName.put(componentName, newComponentCopy);
                }
            }
        }
    }

    public void loadCurrentState() {
        for (Entity entity : gameContext.getEntities()) {
            Map<Class<? extends Component>, Component> componentsForEntity = savedState.get(entity.getUuid());

            if (componentsForEntity != null) {
                for (Class<? extends Component> componentClass : componentsForEntity.keySet()) {
                    entity.getComponent(componentClass).load(componentsForEntity.get(componentClass));
                }
            }
        }
    }
}