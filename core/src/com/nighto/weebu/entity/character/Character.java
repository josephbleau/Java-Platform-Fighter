package com.nighto.weebu.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.nighto.weebu.component.*;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.character.event.CollisionEventHandler;
import com.nighto.weebu.entity.character.event.DeathhEventHandler;
import com.nighto.weebu.entity.player.Shield;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.event.events.CollisionEvent;
import com.nighto.weebu.screen.StageScreen;
import com.nighto.weebu.system.GameContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Character extends Entity {

    protected GameController gameController;

    protected int jumpCount = 0;
    protected float knockbackModifier = 0;

    protected float width;
    protected float height;

    protected Shield shield;
    protected Stage stage;

    protected Color sidestepColor;

    protected List<Attack> attacks = new ArrayList<>();

    // Components
    protected StateComponent stateComponent;
    protected AnimationDataComponent animationDataComponent;

    public Character(StageScreen parentScreen, GameContext gameContext) {
        super(parentScreen, gameContext);

        stage = parentScreen.getStage();
        sidestepColor = Color.LIGHT_GRAY;

        // Spine animation
        // TODO: Factor out animation / skeleton loading, should not be in character ctor
        animationDataComponent = new AnimationDataComponent();

        animationDataComponent.textureAtlas = new TextureAtlas(Gdx.files.internal("core/assets/characters/sunflower/spine.atlas"));
        animationDataComponent.skeletonJson = new SkeletonJson(animationDataComponent.textureAtlas);
        animationDataComponent.skeletonData = animationDataComponent.skeletonJson.readSkeletonData(Gdx.files.internal("core/assets/characters/sunflower/skeleton.json"));
        animationDataComponent.skeletonData.setFps(60);
        animationDataComponent.animationStateData = new AnimationStateData(animationDataComponent.skeletonData);
        animationDataComponent.skeleton = new Skeleton(animationDataComponent.skeletonData);
        animationDataComponent.skeleton.setScale(.2f, .2f);
        animationDataComponent.animationState = new AnimationState(animationDataComponent.animationStateData);
        animationDataComponent.animationState.setAnimation(0, "idle", true);

        stateComponent = new StateComponent();
        registerComponent(CharacterDataComponent.class, CharacterAttributesLoader.loadCharacterData());
        registerComponent(StateComponent.class, stateComponent);
        registerComponent(ControllerComponent.class, new ControllerComponent(gameController));
        registerComponent(AnimationDataComponent.class, animationDataComponent);

        registerEventHandler(new CollisionEventHandler(this));
        registerEventHandler(new DeathhEventHandler(this));

        ((StateComponent)getComponent(StateComponent.class)).enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);

        // TODO: Fix w/h, should be a part of the physical component
        width = 20;
        height = 60;
    }

    @Override
    public void update(float delta) {
        updateAttacks(delta);
        shield.update(delta);
    }

    // TODO: Move to debug renderer
    @Override
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);

        if (stateComponent.inState(State.SIDESTEPPING)) {
            currentColor = sidestepColor;
        } else {
            currentColor = defaultColor;
        }

        if (stateComponent.inState(State.SHIELDING)) {
            shield.setActive(true);
        } else {
            shield.setActive(false);
        }

        shield.render(shapeRenderer);

        attacks.forEach(attack -> attack.render(shapeRenderer));
    }

    @Override
    public List<CollisionEvent> intersects(Entity otherEntity) {
        List<CollisionEvent> shieldCollision = shield.intersects(otherEntity);

        if (shieldCollision != null) {
            return shieldCollision;
        }

        return super.intersects(otherEntity);
    }

    @Override
    public void teleport(float x, float y) {
        super.teleport(x, y);

        CharacterDataComponent characterData = getComponent(CharacterDataComponent.class);

        knockbackModifier = 0;
        characterData.getTimers().resetTimers();
    }

    // TODO: Attack processing system
    private void updateAttacks(float delta) {
        boolean attackPlaying = false;

        Iterator<Attack> attackIterator = attacks.iterator();

        while (attackIterator.hasNext()) {
            Attack attack = attackIterator.next();
            attack.update(delta);
            attackPlaying = attack.isPlaying();

            if (!attack.isActive()) {
                getGameContext().removeEntity(attack);
                attackIterator.remove();
            }
        }

        if (!attackPlaying && stateComponent.inSubState(State.SUBSTATE_ATTACKING)) {
            stateComponent.enterSubState(State.SUBSTATE_DEFAULT);
        }
    }

    // TODO: Attack system
    public void enterKnockback(Attack attack) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);
        CharacterDataComponent characterDataComponent = getComponent(CharacterDataComponent.class);

        knockbackModifier += attack.getKnockbackModifierIncrease();

        physicalComponent.velocity.x = attack.getxImpulse() + (attack.getxImpulse() * knockbackModifier/50f);
        physicalComponent.velocity.y = attack.getyImpulse() + (attack.getyImpulse() * knockbackModifier/100f);

        characterDataComponent.getTimers().setKnockbackTimeRemaining(attack.getKnockbackInduced());

        stateComponent.enterState(State.AIRBORNE);
        stateComponent.enterSubState(State.SUBSTATE_KNOCKBACK);
    }

    public void startAttack(Attack attack) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        getGameContext().registerEntity(attack);
        attack.teleport(physicalComponent.position.x, physicalComponent.position.y);

        if (attack.isPlaying()) {
            stateComponent.enterSubState(State.SUBSTATE_ATTACKING);
        }

        attacks.add(attack);
    }

    public void spawnShield() {
        PhysicalComponent physical = getComponent(PhysicalComponent.class);
        shield.teleport(physical.position.x, physical.position.y);
    }

    public void snapToLedge(Ledge ledge) {
        stateComponent.enterState(State.HANGING);

        Rectangle playerRect = getRects().get(0);
        jumpCount = 0;

        if (ledge.hangLeft) {
            stateComponent.enterSubState(State.SUBSTATE_HANGING_LEFT);
            teleport(ledge.x - playerRect.width + ledge.width, ledge.y + ledge.height - playerRect.height, false);
        } else {
            stateComponent.enterSubState(State.SUBSTATE_HANGING_RIGHT);
            teleport(ledge.x, ledge.y + ledge.height - playerRect.height, false);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public float getWidth() {
        return width;
    }
}
