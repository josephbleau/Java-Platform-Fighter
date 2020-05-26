package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class RenderingSystem extends System {
    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;
    private final SkeletonRenderer skeletonRenderer;

    public RenderingSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    @Override
    protected void process(Entity entity) {
        AnimationDataComponent animationData = entity.getComponent(AnimationDataComponent.class);

        // Required component
        if(animationData == null) {
            return;
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if (animationData.skeleton != null) {
            skeletonRenderer.draw(spriteBatch, animationData.skeleton);
        }

        spriteBatch.end();
    }
}
