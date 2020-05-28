package com.nighto.weebu.event.spine;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.component.character.AttackDataComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.entity.character.Character;

public class AnimationEventListener extends AnimationState.AnimationStateAdapter {
    private final static String ATTACK_ANIMATION_START = "AttackAnimationStart";
    private final static String ATTACK_ANIMATION_END = "AttackAnimationEnd";

    private final static String TRANSLATE_PLAYER_X_POS = "TranslatePlayerXPos";
    private final static String TRANSLATE_PLAYER_Y_POS = "TranslatePlayerYPos";

    private final Character character;

    public AnimationEventListener(Character character) {
        this.character = character;
    }

    @Override
    public void event(AnimationState.TrackEntry entry, Event event) {
        String evtName = event.getData().getName();
        PhysicalComponent physicalComponent = character.getComponent(PhysicalComponent.class);
        StateComponent stateComponent = character.getComponent(StateComponent.class);
        AnimationDataComponent animationDataComponent = character.getComponent(AnimationDataComponent.class);
        AttackDataComponent attackDataComponent = character.getComponent(AttackDataComponent.class);

        // When an attack ends we should enter the state we were previously in
        // unless the attack state was interrupted before this event could fire (this
        // scenario would be inherently handled by switching our animation before it ended).
        if (ATTACK_ANIMATION_END.equals(evtName)) {
            stateComponent.revertSubState();

            // Jank-ass tracking of which players have been hit by hitboxes already, clear when the anim ends.
            // TODO: Smarten up, do something better
            attackDataComponent.clearTargets();
        }

        // When an animation completes we will generally make sure the player skeleton is back in the same spot
        // as when it began, but occasionally the animation itself is going to move the player skeleton to a
        // new location (such as in a dash attack, or up-b type recovery. In these instances we need a way to inform
        // the engine to update the players in-world location to be offset by the distance traveled in the animation.
        if (TRANSLATE_PLAYER_X_POS.equals(evtName)) {
            physicalComponent.prevPosition.x = physicalComponent.position.x;

            // Set X position to root bones new world X position
            physicalComponent.position.x = animationDataComponent.skeleton.getBones().get(0).getWorldX();

            // If I didn't force the update here we were seeing 1-frame of the wrong animation, presumably because it was rendering
            // the current state's anim before checking to see if a new one should be applied? Need to look into it.
            animationDataComponent.forceUpdateAnimation(stateComponent);
        }

        if (TRANSLATE_PLAYER_Y_POS.equals(evtName)) {
            physicalComponent.position.y = animationDataComponent.skeleton.getBones().get(0).getWorldY();
            animationDataComponent.forceUpdateAnimation(stateComponent);
        }
    }
}
