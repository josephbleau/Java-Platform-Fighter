package com.nighto.weebu.system;

import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.Iterator;
import java.util.List;

public abstract class System {
    protected final GameContext gameContext;
    protected final EventPublisher eventPublisher;

    protected List<Class<? extends Component>> requiredComponents;

    protected System(GameContext gameContext, EventPublisher eventPublisher, List<Class<? extends Component>> requiredComponents) {
        this.gameContext = gameContext;
        this.eventPublisher = eventPublisher;
        this.requiredComponents = requiredComponents;
    }

    public void process() {
        Iterator<Entity> entityIterator = gameContext.refreshEntitiesIterator();

        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            if (entity.isActive()) {
                boolean componentsPresentAndEnabled = true;

                for (Class requiredComponent : requiredComponents) {
                    Component component = entity.getComponent(requiredComponent);
                    componentsPresentAndEnabled &= component != null && component.isEnabled();
                }

                if (componentsPresentAndEnabled) {
                    process(entity);
                }
            }
        }
    }

    abstract void process(Entity entity);
}
