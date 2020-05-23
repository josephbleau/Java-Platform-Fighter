package com.nighto.weebu.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.*;
import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.component.character.CharacterTimers;
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

    protected CharacterTimers characterTimers;
    protected GameController gameController;

    protected int jumpCount = 0;
    protected float knockbackModifier = 0;

    protected float width;
    protected float height;
    protected boolean fastFalling;
    protected boolean activeControl;

    protected Shield shield;
    protected Stage stage;

    protected Color sidestepColor;

    protected List<Attack> attacks = new ArrayList<>();

    protected TextureAtlas textureAtlas;
    protected SkeletonJson skeletonJson;
    protected SkeletonData skeletonData;
    protected AnimationStateData animationStateData;
    protected Skeleton skeleton;
    protected AnimationState animationState;

    // Components
    protected StateComponent stateComponent;

    public Character(StageScreen parentScreen, GameContext gameContext) {
        super(parentScreen, gameContext);

        stage = parentScreen.getStage();

        // Spine animation
        textureAtlas = new TextureAtlas(Gdx.files.internal("core/assets/characters/sunflower/spine.atlas"));
        skeletonJson = new SkeletonJson(textureAtlas);
        skeletonData = skeletonJson.readSkeletonData(Gdx.files.internal("core/assets/characters/sunflower/skeleton.json"));
        skeletonData.setFps(60);
        animationStateData = new AnimationStateData(skeletonData);

        skeleton = new Skeleton(skeletonData);
        skeleton.setScale(.2f, .2f);

        animationState = new AnimationState(animationStateData);
        animationState.setAnimation(0, "idle", true);

        sidestepColor = Color.LIGHT_GRAY;

        stateComponent = new StateComponent();

        registerComponent(CharacterDataComponent.class, CharacterAttributesLoader.loadCharacterData());
        registerComponent(StateComponent.class, stateComponent);
        registerComponent(ControllerComponent.class, new ControllerComponent(gameController));

        registerEventHandler(new CollisionEventHandler(this));
        registerEventHandler(new DeathhEventHandler(this));

        ((StateComponent)getComponent(StateComponent.class)).enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);

        // TODO: Fix w/h, should be a part of the physical component
        width = 20;
        height = 60;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        skeleton.setX(Math.round(physicalComponent.position.x));
        skeleton.setY(Math.round(physicalComponent.position.y));
        skeleton.updateWorldTransform();

        if (physicalComponent.facingRight) {
            skeleton.setScale(0.2f, skeleton.getScaleY());
        } else {
            skeleton.setScale(-0.2f, skeleton.getScaleY());
        }

        if (stateComponent.inState(State.CROUCHING)) {
            skeleton.setScale(skeleton.getScaleX(), .2f/1.5f);
        } else {
            skeleton.setScale(skeleton.getScaleX(), .2f);
        }

        updateGravity(delta);
        updateAttacks(delta);

        animationState.update(delta);
        animationState.apply(skeleton);

        shield.update(delta);
    }

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
    public Skeleton getSkeleton() {
        return skeleton;
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
    public void spawn(float x, float y) {
        super.spawn(x, y);

        CharacterDataComponent characterData = getComponent(CharacterDataComponent.class);

        knockbackModifier = 0;
        characterData.getTimers().resetTimers();
    }

    private void updateGravity(float delta) {
        PhysicalComponent physical = getComponent(PhysicalComponent.class);
        CharacterDataComponent characterData = getComponent(CharacterDataComponent.class);

        float proposedyVel = Math.max(characterData.getActiveAttributes().getFallSpeed(), physical.velocity.y - stage.getGravity());

        if (physical.velocity.y > characterData.getActiveAttributes().getFallSpeed()) {
            physical.velocity.y = proposedyVel;
        }

        if (stateComponent.inState(State.WALLSLIDING)) {
            physical.velocity.y = proposedyVel/3;
        }

        if (physical.velocity.y < 0 && fastFalling) {
            physical.velocity.y = proposedyVel * 2;
        }

        if (!activeControl) {
                if (stateComponent.inState(State.AIRBORNE)) {
                    physical.velocity.x /= characterData.getActiveAttributes().getAirFriction();
                } else if (stateComponent.inState(State.STANDING)) {
                    physical.velocity.x /= characterData.getActiveAttributes().getGroundFriction();
                }
        }
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
        attack.spawn(physicalComponent.position.x, physicalComponent.position.y);

        if (attack.isPlaying()) {
            stateComponent.enterSubState(State.SUBSTATE_ATTACKING);
        }

        attacks.add(attack);
    }

    public void spawnShield() {
        PhysicalComponent physical = getComponent(PhysicalComponent.class);
        shield.spawn(physical.position.x, physical.position.y);
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
