package com.nighto.weebu.component;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
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

    private Map<State, String> animationMap;

    public AnimationDataComponent() {
        animationMap = new HashMap<>();
    }

    public void registerAnimationForState(State state, String animationName) {
        animationMap.put(state, animationName);
    }

    public String getAnimationForState(State state) {
        String animName = animationMap.get(state);

        if (animName == null) {
            animName = animationMap.get(State.DEFAULT);
        }

        return animName;
    }
}
