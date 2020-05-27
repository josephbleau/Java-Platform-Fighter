package com.nighto.weebu.event.spine;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.entity.character.Character;

public class AnimationEventListener extends AnimationState.AnimationStateAdapter {
    private final static String ATTACK_ANIMATION_START = "AttackAnimationStart";
    private final static String ATTACK_ANIMATION_END = "AttackAnimationEnd";

    private final Character character;

    public AnimationEventListener(Character character) {
        this.character = character;
    }

    @Override
    public void event(AnimationState.TrackEntry entry, Event event) {
        // When an attack ends we should enter the state we were previously in
        // unless the attack state was interrupted before this event could fire (this
        // scenario would be inherently handled by switching our animation before it ended).
        if (ATTACK_ANIMATION_END.equals(event.getData().getName())) {
            StateComponent stateComponent = character.getComponent(StateComponent.class);
            stateComponent.revertSubState();
        }
    }
}
