package com.nighto.weebu.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.controller.NoopGamecubeController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.TestStage;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.events.DeathEvent;
import com.nighto.weebu.system.PositionSystem;
import com.nighto.weebu.system.System;
import com.nighto.weebu.system.StateBasedInputSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StageScreen implements Screen {
    final Game game;
    final Stage stage;
    final Player player;
    final Player enemy;


    private List<Entity> entities;
    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;

    private EventPublisher eventPublisher;

    private List<GamecubeController> controllers;

    private List<System> systems;

    public StageScreen(Game game) {
        this.game = game;

        // Register controllers
        controllers = new ArrayList<>();

        for(Controller controller : Controllers.getControllers()) {
            if (controller.getName().equals(GamecubeController.MAYFLASH_ADAPTER_ID)) {
                controllers.add(new GamecubeController(controller));
            }
        }

        GamecubeController gamecubeController = null;

        if (controllers.size() > 0) {
            gamecubeController = controllers.get(0);
        }

        stage = new TestStage(this);

        // TODO: Controller plug/unplug system with dynamic controller registration
        player = new Player(this);
        player.registerComponent(ControllerComponent.class, new ControllerComponent(new GameController(gamecubeController, false)));

        enemy = new Player(this);
        enemy.registerComponent(ControllerComponent.class, new ControllerComponent(new GameController(gamecubeController, false)));

        entities = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();
        entitiesToAdd = new ArrayList<>();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        spriteBatch.setProjectionMatrix(camera.combined);

        entities.add(stage);
        entities.add(player);
        entities.add(enemy);

        eventPublisher = new EventPublisher();
        eventPublisher.registerListeners(entities);

        systems = new ArrayList<>();
        systems.add(StateBasedInputSystem.getInstance());
        systems.add(PositionSystem.getInstance());
    }

    @Override
    public void show() {

    }

    /** Primary game loop **/
    @Override
    public void render(float delta) {
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();

        /* Update Loop: All entities will run their physics calculations here. */
        for (Entity entity : entities) {
            entity.update(delta);
        }

        for (System system : systems) {
            system.process(entities);
        }

        /* Outcome Loop: Collisions and interactions will be resolved here. */
        for (Entity outerEntity : getActiveEntities()) {
            if (!stage.inBounds(outerEntity)) {
                DeathEvent deathEvent = new DeathEvent(outerEntity);
                eventPublisher.publish(deathEvent);
            }

            for (Entity innerEntity : getActiveEntities()) {
                if (outerEntity == innerEntity) {
                    continue;
                }

                if (innerEntity.isCollidable() && outerEntity.isCollidable()) {
                    eventPublisher.publish(outerEntity.intersects(innerEntity));
                }
            }
        }

        /* Render Loop **/
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        for (Entity entity : getActiveEntities()) {
            if (entity.getSkeleton() != null) {
                skeletonRenderer.draw(spriteBatch, entity.getSkeleton());
            }
        }

        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        stage.render(shapeRenderer);
        for (Entity entity : getActiveEntities()) {
            entity.render(shapeRenderer);
        }

        // Remove inactive entities
        entitiesToRemove.forEach(e -> entities.remove(e));
    }

    public List<Entity> getActiveEntities() {
        return entities.stream().filter(e->e.isActive()).collect(Collectors.toList());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void registerEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    public void removeEntity(Entity entity) { entitiesToRemove.add(entity); }

    public Stage getStage() {
        return stage;
    }
}
