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
    private float timeToMovementLerp;
    private float timeInCurrentMovementLerp;
    private Vector2 cameraSourceLocation;
    private Vector2 cameraTargetLocation;


    // Camera zoom lerp
    private float timeToZoomLerp;
    private float timeInCurrentZoomLerp;
    private float zoomTarget;
    private float zoomSource;

    private Map<UUID, Vector2> characterPositions;
    private UUID causedZoom;

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
        timeToZoomLerp = 0.3f;
        timeInCurrentZoomLerp = 0f;

        characterPositions = new HashMap<>();
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
        zoomTarget = Math.max(zoomTarget, 1);
        java.lang.System.out.println("zt: " + zoomTarget + " md: " + maxDst);

        timeInCurrentZoomLerp = 0;
    }
}
