package com.nighto.weebu.system;

import com.badlogic.gdx.Gdx;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class DebugPrinterSystem extends System {
    protected boolean showState = true;
    protected boolean showVelocity = true;

    public DebugPrinterSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());
    }

    @Override
    void process(Entity entity) {
        if (entity instanceof  ProjectileAttack) {
            StateComponent stateComponent = entity.getComponent(StateComponent.class);

            if (stateComponent != null && stateComponent.isEnabled() && showState) {
                Gdx.app.debug("State", "" + stateComponent.getState());
            }

            PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);

            if (physicalComponent != null && physicalComponent.isEnabled() && showVelocity) {
                Gdx.app.debug("Velocity", physicalComponent.velocity.toString());
            }
        }
    }
}
