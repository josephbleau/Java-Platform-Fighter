package com.nighto.weebu.component;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;

public class AnimationDataComponent extends Component {
    public TextureAtlas textureAtlas;

    public SkeletonJson skeletonJson;
    public SkeletonData skeletonData;
    public Skeleton skeleton;

    public AnimationStateData animationStateData;
    public AnimationState animationState;
}
