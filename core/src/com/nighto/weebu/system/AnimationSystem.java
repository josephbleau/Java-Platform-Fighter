package com.nighto.weebu.system;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.component.character.AttackDataComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.AttackData;
import com.nighto.weebu.event.EventPublisher;

import java.util.Arrays;

/**
 * Process animated components by updating their scaling, transforms, and events triggered
 * by the animation being currently played.
 *
 * Some animations when they end (aren't interrupted) may need to signal
 * to the event bus so that something special can occur.
 */
public class AnimationSystem extends System {
    public AnimationSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Arrays.asList(AnimationDataComponent.class, PhysicalComponent.class, StateComponent.class));
        timeBased = true;
    }

    @Override
    void process(Entity entity) {
        AnimationDataComponent animationData = entity.getComponent(AnimationDataComponent.class);
        PhysicalComponent physical = entity.getComponent(PhysicalComponent.class);
        StateComponent state = entity.getComponent(StateComponent.class);
        AttackDataComponent attackData = entity.getComponent(AttackDataComponent.class);


        if (entity.componentsEnabled(AnimationDataComponent.class, PhysicalComponent.class, StateComponent.class)) {
            Skeleton skeleton = animationData.skeleton;
            AnimationState animationState = animationData.animationState;

            setCurrentAnimation(animationData, state, attackData);

            // Update position, facing, and crouching status
            updatePosition(physical, skeleton);
            updateFacing(physical, skeleton);

            // Progress animation
            animationState.update(gameContext.getFrameDelta());

            // Apply animation to skeleton
            animationState.apply(skeleton);
        }
    }

    private void setCurrentAnimation(AnimationDataComponent animationData, StateComponent state, AttackDataComponent attackDataComponent) {
        AnimationState animationState = animationData.animationState;

        String animName = animationData.getAnimationForState(state.getState(), state.getSubState());
        float animSpeed = 1;

        String currentAnimName = animationState.getCurrent(0).getAnimation().getName();

        if (attackDataComponent != null) {
            AttackData attackData = attackDataComponent.attacks.get(animName);
            if (attackData != null) {
                animSpeed = attackData.animSpeed;
            }
        }

        // TODO: Tie this to ground speed
        if ("run".equals(animName)) {
            animSpeed = 2.3f;
        }

        if (!animName.equals(currentAnimName)) {
            animationState.setTimeScale(animSpeed);
            animationState.setAnimation(0, animName,true);
        }
    }

    private void updatePosition(PhysicalComponent physical, Skeleton skeleton) {
        skeleton.setX(WorldConstants.UNIT_TO_PX * (physical.position.x + physical.boundingBox.width/2));
        skeleton.setY(WorldConstants.UNIT_TO_PX * physical.position.y);

        skeleton.updateWorldTransform();
    }

    private void updateFacing(PhysicalComponent physical, Skeleton skeleton) {
        float leftScale = -(Math.abs(skeleton.getScaleX()));
        if (physical.facingRight) {
            skeleton.setScale(-leftScale, skeleton.getScaleY());
        } else {
            skeleton.setScale(leftScale, skeleton.getScaleY());
        }
    }
}
