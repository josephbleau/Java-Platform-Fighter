package com.nighto.weebu.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.NoopGamecubeController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.character.event.AttackEventListener;
import com.nighto.weebu.entity.character.event.CollisionEventHandler;
import com.nighto.weebu.entity.character.event.DeathhEventHandler;
import com.nighto.weebu.entity.character.loader.CharacterAttributesLoader;
import com.nighto.weebu.entity.character.loader.Characters;
import com.nighto.weebu.entity.shield.Shield;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.event.game.CollisionEvent;
import com.nighto.weebu.event.spine.AnimationEventListener;

import java.util.List;

public class Character extends Entity {

    protected GameController gameController;

    protected final Shield shield;

    // Components
    protected StateComponent stateComponent;
    protected AnimationDataComponent animationDataComponent;
    protected CharacterDataComponent characterDataComponent;
    protected ControllerComponent controllerComponent;

    public Character(Characters character) {
        shield = new Shield(new Circle(10, 30, 30), new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f));

        animationDataComponent = new AnimationDataComponent();

        stateComponent = new StateComponent();
        characterDataComponent = CharacterAttributesLoader.loadCharacterData(character.name);
        controllerComponent =  new ControllerComponent(new GameController(new NoopGamecubeController(), false));
        controllerComponent.registerController(new GameController(new NoopGamecubeController(), true));

        animationDataComponent.textureAtlas = new TextureAtlas(Gdx.files.internal("core/assets/characters/"+character.name+"/spine.atlas"));
        animationDataComponent.skeletonJson = new SkeletonJson(animationDataComponent.textureAtlas);
        animationDataComponent.skeletonData = animationDataComponent.skeletonJson.readSkeletonData(Gdx.files.internal("core/assets/characters/"+character.name+"/skeleton.json"));
        animationDataComponent.skeletonData.setFps(60);
        animationDataComponent.animationStateData = new AnimationStateData(animationDataComponent.skeletonData);
        animationDataComponent.skeleton = new Skeleton(animationDataComponent.skeletonData);
        animationDataComponent.skeleton.setScale(characterDataComponent.getInitialAttributes().getRenderScaleX(), characterDataComponent.getInitialAttributes().getRenderScaleY());
        animationDataComponent.animationState = new AnimationState(animationDataComponent.animationStateData);
        animationDataComponent.animationState.setAnimation(0, "idle", true);

        animationDataComponent.registerAnimationForState(State.RUNNING, "run");
        animationDataComponent.registerAnimationForState(State.DEFAULT, "idle");
        animationDataComponent.registerAnimationForState(State.JUMPSQUAT, "jumpsquat");
        animationDataComponent.registerAnimationForState(State.EXIT_JUMPSQUAT, "jumpsquat");
        animationDataComponent.registerAnimationForState(State.CROUCHING, "uptilt");
        animationDataComponent.registerAnimationForState(State.AIRBORNE, "airborne");
        animationDataComponent.registerAnimationForState(State.AIRDODGE, "airborne");
        animationDataComponent.registerAnimationForState(State.DIRECTIONAL_AIRDODGE, "airborne");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_TUMBLE, "tumble");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_ATTACKING, "jab");

        animationDataComponent.animationState.addListener(new AnimationEventListener(this));

        registerComponent(CharacterDataComponent.class, characterDataComponent);
        registerComponent(StateComponent.class, stateComponent);
        registerComponent(ControllerComponent.class, new ControllerComponent(gameController));
        registerComponent(AnimationDataComponent.class, animationDataComponent);
        registerComponent(ControllerComponent.class, controllerComponent);

        registerEventHandler(new CollisionEventHandler(this));
        registerEventHandler(new DeathhEventHandler(this));
        registerEventHandler(new AttackEventListener(this));

        // TODO: Stage spawn locations
        teleport(1920/2, 400);

        // TODO: Hurtboxes need to be formalized (part of spine?)
        ((StateComponent) getComponent(StateComponent.class)).enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
        Rectangle boundingBox = ((PhysicalComponent) getComponent(PhysicalComponent.class)).boundingBox;
        Rectangle prevBoundingBox = ((PhysicalComponent) getComponent(PhysicalComponent.class)).prevBoundingBox;

        boundingBox.width = 20;
        boundingBox.height = 150;
        prevBoundingBox.width = 20;
        prevBoundingBox.height = 150;

        getRects().add(boundingBox);
    }

    @Override
    public void update(float delta) {
        updateShield(delta);
    }

    @Override
    public List<CollisionEvent> intersects(Entity otherEntity) {
        List<CollisionEvent> collisionEvents = shield.intersects(otherEntity);
        collisionEvents.addAll(super.intersects(otherEntity));

        return collisionEvents;
    }

    @Override
    public void teleport(float x, float y) {
        super.teleport(x, y);

        CharacterDataComponent characterData = getComponent(CharacterDataComponent.class);
        characterData.getTimers().resetTimers();
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        if (WorldConstants.DEBUG) {
            super.render(shapeRenderer);
        }
    }

    public void updateShield(float delta) {
        shield.update(delta);

        if (stateComponent.inState(State.SHIELDING)) {
            shield.setActive(true);
        } else {
            shield.setActive(false);
        }
    }

    // TODO: Attack system
    public void enterKnockback(Attack attack) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);
        CharacterDataComponent characterDataComponent = getComponent(CharacterDataComponent.class);

        float knockbackModifier = characterDataComponent.getActiveAttributes().getKnockbackModifier() + attack.getKnockbackModifierIncrease();
        characterDataComponent.getActiveAttributes().setKnockbackModifier(knockbackModifier);

        physicalComponent.prevVelocity.x = physicalComponent.velocity.x;
        physicalComponent.prevVelocity.y = physicalComponent.velocity.y;
        physicalComponent.velocity.x = attack.getxImpulse() + (attack.getxImpulse() * knockbackModifier / 50f);
        physicalComponent.velocity.y = attack.getyImpulse() + (attack.getyImpulse() * knockbackModifier / 100f);

        characterDataComponent.getTimers().setKnockbackTimeRemaining(attack.getKnockbackInduced());

        stateComponent.enterState(State.AIRBORNE);

        if (attack.getKnockbackInduced() >= 30f/60f) {
            stateComponent.enterSubState(State.SUBSTATE_TUMBLE);
        } else {
            stateComponent.enterSubState(State.SUBSTATE_KNOCKBACK);
        }
    }

    public void startShielding() {
        PhysicalComponent physical = getComponent(PhysicalComponent.class);
        getGameContext().registerEntity(shield);
        shield.teleport(physical.position.x, physical.position.y);
    }

    public void snapToLedge(Ledge ledge) {
        stateComponent.enterState(State.HANGING);

        Rectangle playerRect = getRects().get(0);
        characterDataComponent.getActiveAttributes().setNumberOfJumps(characterDataComponent.getInitialAttributes().getNumberOfJumps());

        if (ledge.isHangLeft()) {
            stateComponent.enterSubState(State.SUBSTATE_HANGING_LEFT);
            teleport(ledge.boundingBox.x - playerRect.width + ledge.boundingBox.width, ledge.boundingBox.y + ledge.boundingBox.height - playerRect.height, false);
        } else {
            stateComponent.enterSubState(State.SUBSTATE_HANGING_RIGHT);
            teleport(ledge.boundingBox.x, ledge.boundingBox.y + ledge.boundingBox.height - playerRect.height, false);
        }
    }

    public Stage getStage() {
        return getGameContext().getStage();
    }
}
