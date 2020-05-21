package com.nighto.weebu.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.*;
import com.nighto.weebu.component.PhysicalComponent;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Character extends Entity {

    protected CharacterData characterData;
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

    protected State state;
    protected State subState;
    protected State prevState;
    protected State prevSubstate;

    protected Color sidestepColor;

    protected boolean facingRight = true;

    protected List<Attack> attacks = new ArrayList<>();

    protected TextureAtlas textureAtlas;
    protected SkeletonJson skeletonJson;
    protected SkeletonData skeletonData;
    protected AnimationStateData animationStateData;
    protected Skeleton skeleton;
    protected AnimationState animationState;

    public Character(StageScreen parentScreen) {
        super(parentScreen);

        this.stage = parentScreen.getStage();

        characterData = CharacterLoader.loadCharacterData();
        characterTimers = new CharacterTimers(characterData);

        textureAtlas = new TextureAtlas(Gdx.files.internal("core/assets/characters/sunflower/spine.atlas"));
        skeletonJson = new SkeletonJson(textureAtlas);
        skeletonData = skeletonJson.readSkeletonData(Gdx.files.internal("core/assets/characters/sunflower/skeleton.json"));
        skeletonData.setFps(60);
        animationStateData = new AnimationStateData(skeletonData);

        sidestepColor = Color.LIGHT_GRAY;

        width = getCharacterData().getHurtboxes().get(State.DEFAULT).width;
        height = getCharacterData().getHurtboxes().get(State.DEFAULT).height;

        registerEventHandler(new CollisionEventHandler(this));
        registerEventHandler(new DeathhEventHandler(this));

        enterState(State.DEFAULT);
        enterSubstate(State.SUBSTATE_DEFAULT);

        skeleton = new Skeleton(skeletonData);
        skeleton.setScale(.2f, .2f);

        animationState = new AnimationState(animationStateData);
        animationState.setAnimation(0, "idle", true);

        state = State.AIRBORNE;
    }

    private int x = 0;

    @Override
    public void update(float delta) {
        super.update(delta);

        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        skeleton.setX(Math.round(physicalComponent.position.x));
        skeleton.setY(Math.round(physicalComponent.position.y));
        skeleton.updateWorldTransform();

        if (getFacingRight()) {
            skeleton.setScale(0.2f, skeleton.getScaleY());
        } else {
            skeleton.setScale(-0.2f, skeleton.getScaleY());
        }

        if (inState(State.CROUCHING)) {
            skeleton.setScale(skeleton.getScaleX(), .2f/1.5f);
        } else {
            skeleton.setScale(skeleton.getScaleX(), .2f);
        }

        updateGravity(delta);
        updateTimers(delta);
        updateAttacks(delta);
        updateDirection(delta);

        animationState.update(delta);
        animationState.apply(skeleton);

        shield.update(delta);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);

        if (inState(State.SIDESTEPPING)) {
            currentColor = sidestepColor;
        } else {
            currentColor = defaultColor;
        }

        if (inState(State.SHIELDING)) {
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

        knockbackModifier = 0;
        characterTimers.resetTimers();
    }

    private void updateGravity(float delta) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        float proposedyVel = Math.max(characterData.getAttributes().fallSpeed, physicalComponent.velocity.y - stage.getGravity());

        if (physicalComponent.velocity.y > characterData.getAttributes().fallSpeed) {
            physicalComponent.velocity.y = proposedyVel;
        }

        if (inState(State.WALLSLIDING)) {
            physicalComponent.velocity.y = proposedyVel/3;
        }

        if (physicalComponent.velocity.y < 0 && fastFalling) {
            physicalComponent.velocity.y = proposedyVel * 2;
        }

        if (!activeControl) {
                if (inState(State.AIRBORNE)) {
                    physicalComponent.velocity.x /= getCharacterData().getAttributes().getAirFriction();
                } else if (inState(State.STANDING)) {
                    physicalComponent.velocity.x /= getCharacterData().getAttributes().getGroundFriction();
                }
        }
    }

    private void updateTimers(float delta) {
        characterTimers.tickTimers(delta);

        if (state == State.JUMPSQUAT) {
            if (characterTimers.getJumpSquatTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                enterState(State.EXIT_JUMPSQUAT);
            }
        }

        if (state == State.SIDESTEPPING) {
            if (characterTimers.getSidestepTimeRemaining() <= 0) {
                characterTimers.resetTimers();

                currentColor = defaultColor;

                enterState(prevState);

                if (inState(State.SHIELDING)) {
                    enterState(State.STANDING);
                }
            }
        }

        if (subState == State.SUBSTATE_KNOCKBACK) {
            if (characterTimers.getKnockbackTimeRemaining() <= 0) {
                characterTimers.resetTimers();
                enterSubstate(State.DEFAULT);
            }
        }
    }

    private void updateAttacks(float delta) {
        boolean attackPlaying = false;

        Iterator<Attack> attackIterator = attacks.iterator();

        while (attackIterator.hasNext()) {
            Attack attack = attackIterator.next();
            attack.update(delta);
            attackPlaying = attack.isPlaying();

            if (!attack.isActive()) {
                getStageScreen().removeEntity(attack);
                attackIterator.remove();
            }
        }

        if (!attackPlaying && inSubState(State.SUBSTATE_ATTACKING)) {
            enterSubstate(State.SUBSTATE_DEFAULT);
        }
    }

    private void updateDirection(float delta) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        if (!inState(State.HANGING, State.JUMPSQUAT, State.AIRBORNE, State.SIDESTEPPING)) {
            if (physicalComponent.velocity.x > 0) {
                facingRight = true;
            } else if (physicalComponent.velocity.x < 0) {
                facingRight = false;
            }
        }
    }

    public void handleInput() {}

    public void enterState(State newState) {
        prevState = state;
        state = newState;
    }

    public void enterSubstate(State newSubstate) {
        prevSubstate = subState;
        subState = newSubstate;
    }

    public boolean inState(State ... states) {
        boolean outcome = false;

        for (State checkingState : states) {
            outcome |= state == checkingState;
        }

        return outcome;
    }

    public boolean inSubState(State... substates) {
        for(State checkingState : substates) {
            if (subState.equals(checkingState)) {
                return true;
            }
        }
        return false;
    }

    public void enterKnockback(Attack attack) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        knockbackModifier += attack.getKnockbackModifierIncrease();

        physicalComponent.velocity.x = attack.getxImpulse() + (attack.getxImpulse() * knockbackModifier/50f);
        physicalComponent.velocity.y = attack.getyImpulse() + (attack.getyImpulse() * knockbackModifier/100f);

        characterTimers.setKnockbackTimeRemaining(attack.getKnockbackInduced());

        enterState(State.AIRBORNE);
        enterSubstate(State.SUBSTATE_KNOCKBACK);
    }

    public void startAttack(Attack attack) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        getStageScreen().registerEntity(attack);

        attack.spawn(physicalComponent.position.x, physicalComponent.position.y);

        if (attack.isPlaying()) {
            enterSubstate(State.SUBSTATE_ATTACKING);
        }

        attacks.add(attack);
    }

    public void snapToLedge(Ledge ledge) {
        enterState(State.HANGING);

        Rectangle playerRect = getRects().get(0);
        jumpCount = 0;

        if (ledge.hangLeft) {
            subState = State.SUBSTATE_HANGING_LEFT;
            teleport(ledge.x - playerRect.width + ledge.width, ledge.y + ledge.height - playerRect.height, false);
        } else {
            subState = State.SUBSTATE_HANGING_RIGHT;
            teleport(ledge.x, ledge.y + ledge.height - playerRect.height, false);
        }
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public void resetTimers() {
        characterTimers.resetTimers();
    }

    public boolean getFacingRight() {
        return facingRight;
    }

    public State getState() {
        return state;
    }

    public State getSubState() {
        return subState;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getJumpCount() {
        return jumpCount;
    }

    public void setJumpCount(int jumpCount) {
        this.jumpCount = jumpCount;
    }

    public Stage getStage() {
        return stage;
    }

    public GameController getGameController() {
        return gameController;
    }

    public float getWidth() {
        return width;
    }

    public void setFastFalling(boolean fastFalling) {
        this.fastFalling = fastFalling;
    }

    public boolean isFastFalling() {
        return fastFalling;
    }

    public void setActiveControl(boolean activeControl) {
        this.activeControl = activeControl;
    }
}
