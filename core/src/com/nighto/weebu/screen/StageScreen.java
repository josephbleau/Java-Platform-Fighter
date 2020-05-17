package com.nighto.weebu.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.player.Enemy;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.TestStage;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.events.DeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StageScreen implements Screen {
    final Game game;
    final Stage stage;
    final Player player;
    final Enemy enemy;

    private List<Entity> entities;
    private List<Entity> entitiesToRemove;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private EventPublisher eventPublisher;

    private List<GamecubeController> controllers;

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
        player = new Player(this, new GameController(gamecubeController));
        enemy = new Enemy(this);

        entities = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        shapeRenderer = new ShapeRenderer();

        entities.add(stage);
        entities.add(player);
        entities.add(enemy);

        eventPublisher = new EventPublisher();
        eventPublisher.registerListeners(entities);
    }

    @Override
    public void show() {

    }

    /** Primary game loop **/
    @Override
    public void render(float delta) {
        player.handleInput();
        enemy.handleInput();

        /* Update Loop: All entities will run their physics calculations here. */
        for (Entity entity : getActiveEntities()) {
            entity.update(Gdx.graphics.getDeltaTime());
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
        entities.add(entity);
    }

    public void removeEntity(Entity entity) { entitiesToRemove.add(entity); }

    public Stage getStage() {
        return stage;
    }
}
