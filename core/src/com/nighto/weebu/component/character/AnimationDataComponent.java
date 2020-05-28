package com.nighto.weebu.component.character;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.character.State;

import java.util.HashMap;
import java.util.Map;

public class AnimationDataComponent extends Component {
    public TextureAtlas textureAtlas;

    public SkeletonJson skeletonJson;
    public SkeletonData skeletonData;
    public Skeleton skeleton;

    public AnimationStateData animationStateData;
    public AnimationState animationState;

    private final Map<State, String> animationStateMap;
    private final Map<State, String> animationSubStateMap;

    public AnimationDataComponent() {
        animationStateMap = new HashMap<>();
        animationSubStateMap = new HashMap<>();
    }

    public void registerAnimationForState(State state, String animationName) {
        animationStateMap.put(state, animationName);
    }

    public void registerAnimationForSubState(State state, String animationName) {
        animationSubStateMap.put(state, animationName);
    }

    public String getAnimationForState(State state, State subState) {
        String stateAnimName = animationStateMap.get(state);
        String subStateAnimName = animationSubStateMap.get(subState);

        // Substate anims take priority
        return (subStateAnimName != null) ? subStateAnimName : (stateAnimName != null) ? stateAnimName : animationStateMap.get(State.DEFAULT);
    }

    public Rectangle getWorldCollisionBoundingBox() {
        Slot collisionSlot = skeleton.findSlot("collision");

        if (collisionSlot == null) {
            return null;
        }

        BoundingBoxAttachment boundingBoxAttachment = (BoundingBoxAttachment) collisionSlot.getAttachment();
        float[] bbVerts = boundingBoxAttachment.getVertices();

        Polygon bbPoly = new com.badlogic.gdx.math.Polygon(bbVerts);
        bbPoly.translate(collisionSlot.getBone().getWorldX(), collisionSlot.getBone().getWorldY());
        bbPoly.setScale(collisionSlot.getBone().getScaleX(), collisionSlot.getBone().getWorldScaleY());
        bbPoly.setRotation(collisionSlot.getBone().getWorldRotationX());

        return bbPoly.getBoundingRectangle();
    }

    public void forceUpdateAnimation(StateComponent stateComponent) {
        String animName = getAnimationForState(stateComponent.getState(), stateComponent.getSubState());
        animationState.setAnimation(0, animName, true);
        animationState.apply(skeleton);
        animationState.update(0);
    }
}
