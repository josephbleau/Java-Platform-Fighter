package com.nighto.weebu.component.character;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.character.CharacterState;

import java.util.HashMap;
import java.util.Map;

public class AnimationDataComponent extends Component {
    public TextureAtlas textureAtlas;

    public SkeletonJson skeletonJson;
    public SkeletonData skeletonData;
    public Skeleton skeleton;

    public AnimationStateData animationStateData;
    public AnimationState animationState;

    private final Map<CharacterState, String> animationStateMap;
    private final Map<CharacterState, String> animationSubStateMap;

    public AnimationDataComponent() {
        animationStateMap = new HashMap<>();
        animationSubStateMap = new HashMap<>();
    }

    public void registerAnimationForState(CharacterState state, String animationName) {
        animationStateMap.put(state, animationName);
    }

    public void registerAnimationForSubState(CharacterState state, String animationName) {
        animationSubStateMap.put(state, animationName);
    }

    public String getAnimationForState(CharacterState state, CharacterState subState) {
        String stateAnimName = animationStateMap.get(state);
        String subStateAnimName = animationSubStateMap.get(subState);

        // Substate anims take priority
        return (subStateAnimName != null) ? subStateAnimName : (stateAnimName != null) ? stateAnimName : animationStateMap.get(CharacterState.STATE_DEFAULT);
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

    public void forceUpdateAnimation(CharacterStateComponent stateComponent) {
        String animName = getAnimationForState(stateComponent.getState(), stateComponent.getSubState());
        animationState.setAnimation(0, animName, true);
        animationState.apply(skeleton);
        animationState.update(0);
    }

    @Override
    public Component save() {
        return null;
    }

    @Override
    public void load(Component component) {

    }
}
