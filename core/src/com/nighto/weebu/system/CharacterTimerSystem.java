package com.nighto.weebu.system;

import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.component.character.CharacterTimers;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.CharacterState;
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
        super(gameContext, eventPublisher, Arrays.asList(CharacterDataComponent.class, CharacterStateComponent.class));
        timeBased = true;
    }

    @Override
    void process(Entity entity) {
        CharacterDataComponent characterData = entity.getComponent(CharacterDataComponent.class);
        CharacterStateComponent state = entity.getComponent(CharacterStateComponent.class);

        CharacterTimers characterTimers = characterData.getTimers();
        characterTimers.tickTimers(gameContext.getFrameDelta());

        if (state.inState(CharacterState.STATE_JUMPSQUAT)) {
            if (characterTimers.getJumpSquatTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.enterState(CharacterState.STATE_EXIT_JUMPSQUAT);
            }
        }

        if (state.inState(CharacterState.STATE_SIDESTEPPING)) {
            if (characterTimers.getSidestepTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.enterState(CharacterState.STATE_STANDING);
            }
        }

        if (state.inState(CharacterState.STATE_AIRDODGE, CharacterState.STATE_DIRECTIONAL_AIRDODGE)) {
            if (characterTimers.getSidestepTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.enterState(CharacterState.STATE_AIRBORNE);
            }
        }

        if (state.inSubState(CharacterState.SUBSTATE_KNOCKBACK, CharacterState.SUBSTATE_TUMBLE)) {
            if (characterTimers.getKnockbackTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                state.enterSubState(CharacterState.STATE_DEFAULT);
            }
        }
    }
}
