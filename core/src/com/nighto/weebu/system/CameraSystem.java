package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.junkdrawer.CameraHelper;

import java.util.*;

public class CameraSystem extends System{

    // Camera translate lerp
    private final float timeToMovementLerp;
    private float timeInCurrentMovementLerp;
    private final Vector2 cameraSourceLocation;
    private final Vector2 cameraTargetLocation;


    // Camera zoom lerp
    private final float timeToZoomLerp;
    private float timeInCurrentZoomLerp;
    private float zoomTarget;
    private float zoomSource;

    public CameraSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());

        Vector2 positionalAverage = CameraHelper.positionalAverageOfCharacters(gameContext.getCharacterEntities());
        gameContext.getCamera().position.x = positionalAverage.x * WorldConstants.UNIT_TO_PX;
        gameContext.getCamera().position.y = positionalAverage.y * WorldConstants.UNIT_TO_PX;

        cameraTargetLocation = new Vector2(gameContext.getCamera().position.x, gameContext.getCamera().position.y);
        cameraSourceLocation = new Vector2(gameContext.getCamera().position.x, gameContext.getCamera().position.y);
        timeToMovementLerp = 15/60f;
        timeInCurrentMovementLerp = 0.1f;

        zoomSource = 1f;
        zoomTarget = 1.5f;
        timeToZoomLerp = 15/60f;
        timeInCurrentZoomLerp = 0f;
    }

    @Override
    void process(Entity entity) {}

    @Override
    public void process() {
        super.process();

        OrthographicCamera camera = gameContext.getCamera();

        timeInCurrentMovementLerp += gameContext.getFrameDelta();
        timeInCurrentZoomLerp += gameContext.getFrameDelta();

        float translateLerpProgress = Math.min(1f, timeInCurrentMovementLerp / timeToMovementLerp);
        float zoomLerpProgress = Math.min(1f, timeInCurrentZoomLerp / timeToZoomLerp);

        setCameraPositionToPositionAverageOfCharacters();
        if (translateLerpProgress < 1) {
            camera.position.x = Interpolation.linear.apply(cameraSourceLocation.x, cameraTargetLocation.x, translateLerpProgress);
            camera.position.y = Interpolation.linear.apply(cameraSourceLocation.y, cameraTargetLocation.y, translateLerpProgress);
        }

        zoomCameraUsingCharacterDistance();
        if (zoomLerpProgress < 1) {
            camera.zoom = Interpolation.sineIn.apply(zoomSource, zoomTarget, zoomLerpProgress);
        }

        gameContext.getCamera().update();
    }

    /**
     * Averages the position of all characters and trains the camera on that location. Then offsets the camera
     * soo that the players occupy the bottom third of the screen./
     */
    private void setCameraPositionToPositionAverageOfCharacters() {
        Vector2 positionalAverage = CameraHelper.positionalAverageOfCharacters(gameContext.getCharacterEntities());

        cameraTargetLocation.x = positionalAverage.x * WorldConstants.UNIT_TO_PX;
        cameraTargetLocation.y = positionalAverage.y * WorldConstants.UNIT_TO_PX;
        cameraTargetLocation.y += WorldConstants.VIEWPORT_HEIGHT * 1/3f;

        cameraSourceLocation.x = gameContext.getCamera().position.x;
        cameraSourceLocation.y = gameContext.getCamera().position.y;

        timeInCurrentMovementLerp = 0;
    }


    /**
     * Determine the camera zoom by taking the largest distance between two players (this should keep all players
     * on screen in most situations).
     */
    private void zoomCameraUsingCharacterDistance() {
        double maxDst = 0;

        List<Character> characters = gameContext.getCharacterEntities();

        for (int i = 0; i < characters.size(); ++i) {
            for (int j = i; j < characters.size(); ++j) {
                Character c1 = characters.get(i);
                Character c2 = characters.get(j);
                PhysicalComponent p1 = c1.getComponent(PhysicalComponent.class);
                PhysicalComponent p2 = c2.getComponent(PhysicalComponent.class);

                double x1 = p1.position.x;
                double x2 = p2.position.x;
                double y1 = p1.position.y;
                double y2 = p2.position.y;

                double dst = Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));
                maxDst = Math.max(dst, maxDst);
            }
        }

        zoomSource = gameContext.getCamera().zoom;
        zoomTarget = (float) (maxDst*WorldConstants.UNIT_TO_PX) / WorldConstants.VIEWPORT_WIDTH*3;
        zoomTarget = Math.max(zoomTarget, 2f);

        if (maxDst * WorldConstants.UNIT_TO_PX < WorldConstants.VIEWPORT_WIDTH/2) {
            zoomTarget = Math.min(zoomTarget, 1f);
        }

        timeInCurrentZoomLerp = 0;
    }
}
