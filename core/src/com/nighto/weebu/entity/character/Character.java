package com.nighto.weebu.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.*;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.*;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.NoopGamecubeController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.AttackData;
import com.nighto.weebu.entity.character.event.AttackEventListener;
import com.nighto.weebu.entity.character.event.CollisionEventHandler;
import com.nighto.weebu.entity.character.event.DeathhEventHandler;
import com.nighto.weebu.entity.character.loader.CharacterAttributesLoader;
import com.nighto.weebu.entity.character.loader.Characters;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.event.spine.AnimationEventListener;

import java.util.Collections;
import java.util.List;

public class Character extends Entity {

    protected GameController gameController;

    // Components
    protected StateComponent stateComponent;
    protected AnimationDataComponent animationDataComponent;
    protected CharacterDataComponent characterDataComponent;
    protected ControllerComponent controllerComponent;
    protected AttackDataComponent attackDataComponent;

    public Character(Characters character) {
        animationDataComponent = new AnimationDataComponent();
        stateComponent = new StateComponent();
        characterDataComponent = CharacterAttributesLoader.loadCharacterData(character.name);
        controllerComponent =  new ControllerComponent(new GameController(new NoopGamecubeController(), false));
        controllerComponent.registerController(new GameController(new NoopGamecubeController(), true));
        attackDataComponent = AttackDataComponent.loadAttackDataComponent(character);

        animationDataComponent.textureAtlas = new TextureAtlas(Gdx.files.internal("core/assets/characters/"+character.name+"/spine.atlas"));
        animationDataComponent.skeletonJson = new SkeletonJson(animationDataComponent.textureAtlas);
        animationDataComponent.skeletonData = animationDataComponent.skeletonJson.readSkeletonData(Gdx.files.internal("core/assets/characters/"+character.name+"/skeleton.json"));
        animationDataComponent.animationStateData = new AnimationStateData(animationDataComponent.skeletonData);
        animationDataComponent.skeleton = new Skeleton(animationDataComponent.skeletonData);
        animationDataComponent.skeleton.setScale(characterDataComponent.getInitialAttributes().getRenderScaleX(), characterDataComponent.getInitialAttributes().getRenderScaleY());
        animationDataComponent.animationState = new AnimationState(animationDataComponent.animationStateData);
        animationDataComponent.animationState.setAnimation(0, "idle", true);

        animationDataComponent.registerAnimationForState(State.RUNNING, "run");
        animationDataComponent.registerAnimationForState(State.DEFAULT, "idle");
        animationDataComponent.registerAnimationForState(State.JUMPSQUAT, "jumpsquat");
        animationDataComponent.registerAnimationForState(State.EXIT_JUMPSQUAT, "jumpsquat");
        animationDataComponent.registerAnimationForState(State.CROUCHING, "crouch");
        animationDataComponent.registerAnimationForState(State.AIRBORNE, "airborne");
        animationDataComponent.registerAnimationForState(State.AIRDODGE, "airborne");
        animationDataComponent.registerAnimationForState(State.DIRECTIONAL_AIRDODGE, "airborne");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_TUMBLE, "tumble");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_ATTACKING_NEUTRAL_NORMAL, "jab");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_ATTACKING_DASH_ATTACK, "dashattack");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_ATTACKING_NEUTRAL_AIR, "nair");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_ATTACKING_UP_TILT, "uptilt");
        animationDataComponent.registerAnimationForSubState(State.SUBSTATE_ATTACKING_DOWN_TILT, "downtilt");

        animationDataComponent.animationState.addListener(new AnimationEventListener(this));

        registerComponent(CharacterDataComponent.class, characterDataComponent);
        registerComponent(StateComponent.class, stateComponent);
        registerComponent(ControllerComponent.class, new ControllerComponent(gameController));
        registerComponent(AnimationDataComponent.class, animationDataComponent);
        registerComponent(ControllerComponent.class, controllerComponent);
        registerComponent(AttackDataComponent.class, attackDataComponent);

        registerEventHandler(new CollisionEventHandler(this));
        registerEventHandler(new DeathhEventHandler(this));
        registerEventHandler(new AttackEventListener(this));

        // TODO: Stage spawn locations
        spawn(15, 10);

        physicalComponent.dimensions = new Vector2(1, 2);
        physicalComponent.prevDimensions = new Vector2(1, 2);

        stateComponent.enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
    }

    @Override
    public List<Rectangle> getCollidables() {
        return Collections.singletonList(physicalComponent.getBoundingBox());
    }

    public void spawn(float x, float y) {
        physicalComponent.move(x, y, false);
        characterDataComponent.getTimers().resetTimers();
        characterDataComponent.getActiveAttributes().resetValues();
    }

    public void enterKnockback(AttackData attackData) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);
        CharacterDataComponent characterDataComponent = getComponent(CharacterDataComponent.class);

        float knockbackModifier = characterDataComponent.getActiveAttributes().getKnockbackModifier() + attackData.damage;
        characterDataComponent.getActiveAttributes().setKnockbackModifier(knockbackModifier);

        physicalComponent.prevVelocity.x = physicalComponent.velocity.x;
        physicalComponent.prevVelocity.y = physicalComponent.velocity.y;
        physicalComponent.velocity.x = attackData.knockback.x + attackData.knockback.x;
        physicalComponent.velocity.y = attackData.knockback.y + attackData.knockback.y;

        characterDataComponent.getTimers().setKnockbackTimeRemaining(1);

        stateComponent.enterState(State.AIRBORNE);
        stateComponent.enterSubState(State.SUBSTATE_KNOCKBACK);
    }

    public void snapToLedge(Ledge ledge) {
        stateComponent.enterState(State.HANGING);
        characterDataComponent.getActiveAttributes().decrementJumps();

        if (ledge.isHangLeft()) {
            stateComponent.enterSubState(State.SUBSTATE_HANGING_LEFT);

            float x = ledge.boundingBox.x - physicalComponent.dimensions.x + ledge.boundingBox.width;
            float y = ledge.boundingBox.y + ledge.boundingBox.height - physicalComponent.dimensions.y;

            spawn(x, y);
        } else {
            stateComponent.enterSubState(State.SUBSTATE_HANGING_RIGHT);

            float x = ledge.boundingBox.x;
            float y = ledge.boundingBox.y + ledge.boundingBox.height - physicalComponent.dimensions.y;

            spawn(x, y);
        }
    }

    public Stage getStage() {
        return getGameContext().getStage();
    }
}
