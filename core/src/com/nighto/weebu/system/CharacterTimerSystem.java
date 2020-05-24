package com.nighto.weebu.system;

import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.component.character.CharacterTimers;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.State;
import com.nighto.weebu.event.EventPublisher;

import java.util.Arrays;

/**
 * Responsible for processing all character timers and moving the character to
 * ann appropriate state or taking an appropriate action when they expire.
 *
 * Example: When a player exits hit-stun, or leaves landing lag, they should move into the correct state. These
 * timers are stored in the character data component.
 */
public class CharacterTimerSystem extends System{
    public CharacterTimerSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Arrays.asList(CharacterDataComponent.class, StateComponent.class));
    }

    @Override
    void process(Entity entity) {
        CharacterDataComponent characterData = entity.getComponent(CharacterDataComponent.class);
        StateComponent state = entity.getComponent(StateComponent.class);

        CharacterTimers characterTimers = characterData.getTimers();
        characterTimers.tickTimers(gameContext.getFrameDelta());

        if (state.inState(State.JUMPSQUAT)) {
            if (characterTimers.getJumpSquatTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.enterState(State.EXIT_JUMPSQUAT);
            }
        }

        if (state.inState(State.SIDESTEPPING)) {
            if (characterTimers.getSidestepTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.revertState();

                if (state.inState(State.SHIELDING)) {
                    state.enterState(State.STANDING);
                }
            }
        }

        if (state.inSubState(State.SUBSTATE_KNOCKBACK)) {
            if (characterTimers.getKnockbackTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.enterSubState(State.DEFAULT);
            }
        }
    }
}
