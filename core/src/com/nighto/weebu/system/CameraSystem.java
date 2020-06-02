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

import java.util.Collections;

public class CameraSystem extends System{

    // Camera translate lerp
    private float timeToMovementLerp;
    private float timeInCurrentMovementLerp;
    private Vector2 cameraSourceLocation;
    private Vector2 cameraTargetLocation;


    // Camera zoom lerp
    private float timeToZoomLerp;
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
        timeToMovementLerp = 0.3f;
        timeInCurrentMovementLerp = 0.1f;

        zoomSource = 1f;
        zoomTarget = 1.5f;
        timeToZoomLerp = 1f;
        timeInCurrentZoomLerp = 2/60f;
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

        zoomCameraWhenCharactersNearEdgeOfScreen();
        if (zoomLerpProgress < 1) {
            camera.zoom = Interpolation.linear.apply(zoomSource, zoomTarget, zoomLerpProgress);
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
     * Determine if a player is within 20% of the total screen width and height of the sides and top/bottom respectively.
     * If they are, zoom the camera so they no longer occupy that zone. Zoom the camera back in when they leave that zone.
     * The goal of this is to keep all of the action visible and to keep players on screen.
     */
    private void zoomCameraWhenCharactersNearEdgeOfScreen() {
        float cameraLeftBound = gameContext.getCamera().position.x - WorldConstants.VIEWPORT_WIDTH/2;
        float cameraRightBound = gameContext.getCamera().position.x + WorldConstants.VIEWPORT_WIDTH/2;
        float cameraBottomBound = gameContext.getCamera().position.y - WorldConstants.VIEWPORT_HEIGHT/2;
        float cameraUpperBound = gameContext.getCamera().position.y + WorldConstants.VIEWPORT_HEIGHT/2;

        for (Character character : gameContext.getCharacterEntities()) {
            PhysicalComponent physicalComponent = character.getComponent(PhysicalComponent.class);

            float cameraZoom = gameContext.getCamera().zoom;

            float leftRightPct = .1f;
            float upDownPct = leftRightPct * (WorldConstants.VIEWPORT_WIDTH/WorldConstants.VIEWPORT_HEIGHT);

            float rightBound = (cameraRightBound - (leftRightPct * WorldConstants.VIEWPORT_WIDTH)) * cameraZoom;
            float leftBound = (cameraLeftBound +  (leftRightPct * WorldConstants.VIEWPORT_WIDTH)) / cameraZoom;
            float upperBound = (cameraUpperBound - (upDownPct * WorldConstants.VIEWPORT_HEIGHT)) * cameraZoom;
            float bottomBound = (cameraBottomBound + (upDownPct * WorldConstants.VIEWPORT_HEIGHT)) / cameraZoom;

            float xInPx = physicalComponent.position.x * WorldConstants.UNIT_TO_PX;
            float yInPx = physicalComponent.position.y * WorldConstants.UNIT_TO_PX;


            float diff = Math.abs(zoomSource - zoomTarget);

//            // TODO: Fix upper/lower bounds
//            if (zoomTarget != 1.5f && (xInPx >= rightBound || xInPx <= leftBound)) {
//                zoomSource = cameraZoom;
//                zoomTarget = 1.5f;
//                timeInCurrentZoomLerp = 0;
//            }

            java.lang.System.out.println("diff: " + diff + ", zt: " + zoomTarget + ", zs: " + zoomSource);
        }
    }
}
