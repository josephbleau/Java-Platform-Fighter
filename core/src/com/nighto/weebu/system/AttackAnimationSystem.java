package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.State;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.game.AttackEvent;
import javafx.geometry.BoundingBox;
import javafx.util.Pair;

import javax.naming.ldap.HasControls;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackAnimationSystem extends System {
    private final ShapeRenderer debugShapeRenderer;

    public AttackAnimationSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Arrays.asList(AnimationDataComponent.class, StateComponent.class));
        debugShapeRenderer = new ShapeRenderer();
    }

    @Override
    void process(Entity entity) {
        StateComponent stateComponent = entity.getComponent(StateComponent.class);
        AnimationDataComponent animationDataComponent = entity.getComponent(AnimationDataComponent.class);
        PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);

        if (!stateComponent.inSubState(State.SUBSTATE_ATTACKING) || !entity.getTag().equals("Player")) {
            return;
        }

        for (Pair<String, Polygon> hitbox : findActiveHitboxes(animationDataComponent.skeleton)) {
            Polygon attackPoly = hitbox.getValue();

            if (WorldConstants.DEBUG) {
                Rectangle r = attackPoly.getBoundingRectangle();
                debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                debugShapeRenderer.setColor(Color.GREEN);
                debugShapeRenderer.rect(r.x, r.y, r.width, r.height);
                debugShapeRenderer.end();
            }

            AttackEvent attackEvent = new AttackEvent(entity, attackPoly);
            eventPublisher.publish(attackEvent);
        }
    }

    private List<Pair<String, Polygon>> findActiveHitboxes(Skeleton skeleton) {
        List<Pair<String, Polygon>> activeHitBoxes = new ArrayList<>();

        for (Slot slot : skeleton.getSlots()) {
            Attachment attachment = slot.getAttachment();

            if (attachment instanceof BoundingBoxAttachment) {
                Polygon polygon = new Polygon(((BoundingBoxAttachment) attachment).getVertices());
                polygon.setPosition(slot.getBone().getWorldX(), slot.getBone().getWorldY());
                polygon.setRotation(slot.getBone().getWorldRotationX());
                polygon.setScale(slot.getBone().getWorldScaleX(), slot.getBone().getWorldScaleY());

                activeHitBoxes.add(new Pair<>(attachment.getName(), polygon));
            }
        }

        return activeHitBoxes;
    }
}
