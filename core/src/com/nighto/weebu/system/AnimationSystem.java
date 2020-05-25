package com.nighto.weebu.system;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.nighto.weebu.component.AnimationDataComponent;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.State;
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
    }

    @Override
    void process(Entity entity) {
        AnimationDataComponent animationData = entity.getComponent(AnimationDataComponent.class);
        PhysicalComponent physical = entity.getComponent(PhysicalComponent.class);
        StateComponent state = entity.getComponent(StateComponent.class);

        if (entity.componentsEnabled(AnimationDataComponent.class, PhysicalComponent.class, StateComponent.class)) {
            Skeleton skeleton = animationData.skeleton;
            AnimationState animationState = animationData.animationState;

            setCurrentAnimation(animationData, state);

            // Update position, facing, and crouching status
            updatePosition(physical, skeleton);
            updateFacing(physical, skeleton);
            updateCrouching(state, skeleton);

            // Progress animation
            animationState.update(gameContext.getFrameDelta());

            // Apply animation to skeleton
            animationState.apply(skeleton);
        }
    }

    private void setCurrentAnimation(AnimationDataComponent animationData, StateComponent state) {
        AnimationState animationState = animationData.animationState;

        String animName = animationData.getAnimationForState(state.getState());
        String currentAnimName = animationState.getCurrent(0).getAnimation().getName();

        if (!animName.equals(currentAnimName)) {
            animationState.setAnimation(0, animName, true);
        }
    }

    private void updatePosition(PhysicalComponent physical, Skeleton skeleton) {
        skeleton.setX(Math.round(physical.position.x));
        skeleton.setY(Math.round(physical.position.y));
        skeleton.updateWorldTransform();
    }

    private void updateFacing(PhysicalComponent physical, Skeleton skeleton) {
        if (physical.facingRight) {
            skeleton.setScale(0.2f, skeleton.getScaleY());
        } else {
            skeleton.setScale(-0.2f, skeleton.getScaleY());
        }
    }

    private void updateCrouching(StateComponent state, Skeleton skeleton) {
        if (state.inState(State.CROUCHING)) {
            skeleton.setScale(skeleton.getScaleX(), .2f/1.5f);
        } else {
            skeleton.setScale(skeleton.getScaleX(), .2f);
        }
    }
}
