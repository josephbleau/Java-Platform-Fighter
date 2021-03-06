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
import com.nighto.weebu.component.character.AttackDataComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.AttackData;
import com.nighto.weebu.entity.character.CharacterState;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.game.AttackEvent;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackGenerationSystem extends System {
    private final ShapeRenderer debugShapeRenderer;

    public AttackGenerationSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Arrays.asList(AnimationDataComponent.class, CharacterStateComponent.class, AttackDataComponent.class, PhysicalComponent.class));
        debugShapeRenderer = new ShapeRenderer();
    }

    @Override
    void process(Entity entity) {
        CharacterStateComponent stateComponent = entity.getComponent(CharacterStateComponent.class);
        AnimationDataComponent animationDataComponent = entity.getComponent(AnimationDataComponent.class);
        AttackDataComponent attackDataComponent = entity.getComponent(AttackDataComponent.class);
        PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);

        if (!stateComponent.inSubState(CharacterState.ATTACKING_STATES)) {
            return;
        }

        for (Pair<String, Polygon> hitbox : findActiveHitboxes(animationDataComponent.skeleton, physicalComponent.facingRight)) {
            Polygon attackPoly = hitbox.getValue();

            if (WorldConstants.DEBUG) {
                Rectangle r = attackPoly.getBoundingRectangle();

                debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                debugShapeRenderer.setColor(Color.RED);
                debugShapeRenderer.rect(
                        WorldConstants.UNIT_TO_PX * r.x,
                        WorldConstants.UNIT_TO_PX * r.y,
                        WorldConstants.UNIT_TO_PX * r.width,
                        WorldConstants.UNIT_TO_PX * r.height
                );
                debugShapeRenderer.end();
            }

            AttackData attackData = attackDataComponent.attacks.get(hitbox.getKey());

            if (attackData.usesPlayerDirection) {
                if (!physicalComponent.facingRight) {
                    attackData.knockback.x = Math.abs(attackData.knockback.x) * -1;
                }
            }

            AttackEvent attackEvent = new AttackEvent(entity, attackPoly, attackData);

            eventPublisher.publish(attackEvent);
        }
    }

    @Override
    public void process() {
        debugShapeRenderer.setProjectionMatrix(gameContext.getCamera().combined);
        super.process();
    }

    private List<Pair<String, Polygon>> findActiveHitboxes(Skeleton skeleton, boolean facingRight) {
        List<Pair<String, Polygon>> activeHitBoxes = new ArrayList<>();

        for (Slot slot : skeleton.getSlots()) {
            Attachment attachment = slot.getAttachment();

            if (attachment == null) {
                continue;
            }

            if (attachment.getName().contains("collidable")) {
                continue;
            }

            if (attachment instanceof BoundingBoxAttachment) {
                Polygon polygon = new Polygon(((BoundingBoxAttachment) attachment).getVertices());

                float facing = (facingRight) ? 0 : -180;

                float boneWorldX = slot.getBone().getWorldX() * WorldConstants.PX_TO_UNIT;
                float boneWorldY = slot.getBone().getWorldY() * WorldConstants.PX_TO_UNIT;
                float rotation = slot.getBone().getData().getRotation();

                polygon.setPosition(boneWorldX, boneWorldY);
                polygon.setRotation(rotation+facing);
                polygon.setScale(
                        slot.getBone().getWorldScaleX() * WorldConstants.PX_TO_UNIT,
                        slot.getBone().getWorldScaleY() * WorldConstants.PX_TO_UNIT
                );

                activeHitBoxes.add(new Pair<>(attachment.getName(), polygon));
            }
        }

        return activeHitBoxes;
    }
}
