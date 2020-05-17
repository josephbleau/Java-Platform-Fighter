package com.nighto.weebu.entity.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.character.event.CollisionEventHandler;
import com.nighto.weebu.entity.character.event.DeathhEventHandler;
import com.nighto.weebu.entity.player.Shield;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.event.events.CollissionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Character extends Entity {

    protected CharacterData characterData;
    protected CharacterTimers characterTimers;
    protected GameController gameController;

    protected int jumpCount = 0;

    protected float width;
    protected float height;
    protected boolean fastFalling = false;

    protected Shield shield;
    protected Stage stage;

    protected State state;
    protected State subState;
    protected State prevState;
    protected State prevSubstate;

    protected Color sidestepColor;

    protected boolean facingRight = true;

    protected List<Attack> attacks = new ArrayList<>();

    public Character(Stage stage) {
        this.stage = stage;

        characterData = CharacterLoader.loadCharacterData();
        characterTimers = new CharacterTimers(characterData);

        sidestepColor = Color.LIGHT_GRAY;

        width = getCharacterData().getHurtboxes().get(State.DEFAULT).width;
        height = getCharacterData().getHurtboxes().get(State.DEFAULT).height;

        registerEventHandler(new CollisionEventHandler(this));
        registerEventHandler(new DeathhEventHandler(this));

        enterState(State.DEFAULT);
        enterSubstate(State.SUBSTATE_DEFAULT);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        updateGravity(delta);
        updateTimers(delta);
        updateAttacks(delta);
        updateDirection(delta);

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
    public CollissionEvent intersects(Entity otherEntity) {
        CollissionEvent shieldCollision = shield.intersects(otherEntity);

        if (shieldCollision != null) {
            return shieldCollision;
        }

        return super.intersects(otherEntity);
    }

    private void updateGravity(float delta) {
        float proposedyVel = Math.max(characterData.getAttributes().fallSpeed, yVel - stage.getGravity());

        if (yVel > characterData.getAttributes().fallSpeed) {
            yVel = proposedyVel;
        }

        if (inState(State.WALLSLIDING)) {
            yVel = proposedyVel/3;
        }

        if (yVel < 0 && fastFalling) {
            yVel = proposedyVel * 2;
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
    }

    private void updateAttacks(float delta) {
        boolean attackPlaying = false;

        Iterator<Attack> attackIterator = attacks.iterator();

        while (attackIterator.hasNext()) {
            Attack attack = attackIterator.next();
            attack.update(delta);
            attackPlaying = attack.isPlaying();

            if (!attack.isActive()) {
                attackIterator.remove();
            }
        }

        if (!attackPlaying && inSubState(State.SUBSTATE_ATTACKING)) {
            enterSubstate(State.SUBSTATE_DEFAULT);
        }
    }

    private void updateDirection(float delta) {
        if (!inState(State.HANGING, State.JUMPSQUAT, State.AIRBORNE, State.SIDESTEPPING)) {
            if (xVel > 0) {
                facingRight = true;
            } else if (xVel < 0) {
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

    public void startAttack(Attack attack) {
        attack.spawn(xPos, yPos);

        if (attack.isPlaying()) {
            enterSubstate(State.SUBSTATE_ATTACKING);
        }

        attacks.add(attack);
    }

    public void snapToLedge(Ledge ledge) {
        setActive(false); // prevents gravity from applying
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
}
