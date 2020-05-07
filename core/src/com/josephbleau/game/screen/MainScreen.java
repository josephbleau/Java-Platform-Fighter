package com.josephbleau.game.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.josephbleau.game.control.GamecubeController;
import com.josephbleau.game.control.NoopGamecubeController;
import com.josephbleau.game.entity.Entity;
import com.josephbleau.game.entity.player.Enemy;
import com.josephbleau.game.entity.player.Player;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.entity.stage.TestStage;
import com.josephbleau.game.event.EventHandler;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.DeathEvent;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements Screen {
    final Game game;
    final Stage stage;
    final Player player;
    final Enemy enemy;

    private List<Entity> entities;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private EventHandler eventHandler;

    private List<GamecubeController> controllers;

    public MainScreen(Game game) {
        this.game = game;
        this.stage = new TestStage();
        this.player = new Player(this.stage);
        this.enemy = new Enemy(this.stage);

        this.entities = new ArrayList<>();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 640);
        this.shapeRenderer = new ShapeRenderer();

        this.entities.add(this.stage);
        this.entities.add(player);
        this.entities.add(enemy);

        this.eventHandler = new EventHandler();
        this.eventHandler.registerEntities(this.entities);

        // Register controllers
        this.controllers = new ArrayList<>();
        for(int i = 0, port = 0; i < Controllers.getControllers().size; ++i) {
            Controller controller = Controllers.getControllers().get(i);
            if (controller.getName().equals(GamecubeController.MAYFLASH_ADAPTER_ID)) {
                this.controllers.add(new GamecubeController(controller, port++));
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        /* Input Loop */
        if (this.controllers.size() > 0) {
            player.handleInput(this.controllers.get(0));
        } else {
            player.handleInput(new NoopGamecubeController());
        }
        enemy.handleInput();

        /* Update Loop: All entities will run their physics calculations here. */
        for (Entity entity : entities) {
            entity.update(Gdx.graphics.getDeltaTime());
        }

        /* Outcome Loop: Collisions and interactions will be resolved here. */
        for (Entity outerEntity : entities) {
            if (!this.stage.inBounds(outerEntity)) {
                DeathEvent deathEvent = new DeathEvent(outerEntity);
                this.eventHandler.publish(deathEvent);
            }

            for (Entity innerEntity : entities) {
                if (outerEntity == innerEntity) {
                    continue;
                }

                if (innerEntity.isCollidable() && outerEntity.isCollidable()) {
                    CollissionEvent collissionEvent = outerEntity.intersects(innerEntity);

                    if (collissionEvent != null) {
                        this.eventHandler.publish(collissionEvent);
                    }
                }
            }
        }

        /* Render Loop **/
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);

        this.stage.render(this.shapeRenderer);

        for (Entity entity : entities) {
            entity.render(this.shapeRenderer);
        }
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
}
