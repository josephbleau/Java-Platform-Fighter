package com.nighto.weebu.system;

import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.system.inputhandlers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The purpose of this system is to process the current inputs in the context of the
 * entities current state. Specific inputs while in one state may cause the entity to
 * enter a different state. These decisions are handled by a chain of StateInputHandler classes.
 */
public class StateBasedInputSystem extends System {
    private final List<StateBasedInputHandler> stateBasedInputHandlers;

    public StateBasedInputSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.singletonList(ControllerComponent.class));

        stateBasedInputHandlers = new ArrayList<>();

        stateBasedInputHandlers.add(new NoInputInputHandler());
        stateBasedInputHandlers.add(new WallSlideInputHandler());
        stateBasedInputHandlers.add(new JumpSquatExitInputHandler());
        stateBasedInputHandlers.add(new HangingInputHandler());
        stateBasedInputHandlers.add(new ShieldStateInputHandler());
        stateBasedInputHandlers.add(new GroundedInputHandler());
        stateBasedInputHandlers.add(new CrouchingInputHandler());
        stateBasedInputHandlers.add(new AirborneInputHandler());
    }

    @Override
    public void process(Entity entity) {
        ControllerComponent controller = entity.getComponent(ControllerComponent.class);

        if (entity instanceof Character && controller.isEnabled()) {
            Character character = (Character) entity;
            controller.poll();

            // Each state input handler will query the components it needs from the entity, including the
            // StateComponent and ControllerComponent and act accordingly.
            for (StateBasedInputHandler stateBasedInputHandler : stateBasedInputHandlers) {

                // If a state input handler returns false it is signalling for us to not execute the remaining ones.
                if(!stateBasedInputHandler.handleInput(character)) {
                    return;
                }
            }
        }
    }
}
