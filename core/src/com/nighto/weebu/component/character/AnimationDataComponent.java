package com.nighto.weebu.component.character;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
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
}
