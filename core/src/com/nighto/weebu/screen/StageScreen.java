package com.nighto.weebu.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.TestStage;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.system.System;
import com.nighto.weebu.system.*;

import java.util.ArrayList;
import java.util.List;

public class StageScreen implements Screen {
    final Game game;
    final Stage stage;
    final Player player;
    final Player enemy;

    private GameContext gameContext;
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

        stage = new TestStage(this, gameContext);

        // TODO: Controller plug/unplug system with dynamic controller registration
        player = new Player(this, gameContext);
        player.registerComponent(ControllerComponent.class, new ControllerComponent(new GameController(gamecubeController, false)));

        enemy = new Player(this, gameContext);
        enemy.registerComponent(ControllerComponent.class, new ControllerComponent(new GameController(gamecubeController, false)));

        gameContext = new GameContext(stage);
        gameContext.registerEntity(player);
        gameContext.registerEntity(enemy);

        eventPublisher = new EventPublisher();
        eventPublisher.registerListeners(gameContext.getEntities());

        systems = new ArrayList<>();
        systems.add(new StateBasedInputSystem(gameContext, eventPublisher));
        systems.add(new PhysicsSystem(gameContext, eventPublisher));
        systems.add(new CollisionSystem(gameContext, eventPublisher));
        systems.add(new RenderingSystem(gameContext, eventPublisher));
        systems.add(new DebugRenderingSystem(gameContext, eventPublisher));
    }

    @Override
    public void render(float delta) {
        gameContext.setFrameDelta(delta);

        for (System system : systems) {
            system.process();
        }
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    public Stage getStage() {
        return stage;
    }
}
