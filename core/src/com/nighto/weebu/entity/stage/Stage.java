package com.nighto.weebu.entity.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.screen.StageScreen;
import com.nighto.weebu.system.GameContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Stage extends Entity {
    /** Gravity determines how fast an entity falls (in px/seconds) **/
    protected float gravity = 4.5f;

    /** Rect representing the blast zone (if a player exits this rect they die) **/
    protected Rectangle blastZone;
    protected Color blastZoneColor;

    /** List of rects that represent ledges in the level. When a player touches a ledge with their hitbox they
     * will "snap" to it (the top of their bounding-box will become aligned with the top of the ledge, and they
     * will protrude to the side of the ledge not touching the stage. */
    protected List<Rectangle> ledges;
    protected Color ledgeColor;

    public Stage(StageScreen stageScreen, GameContext gameContext) {
        super(stageScreen, gameContext);
        setActive(true);

        blastZoneColor = Color.RED;
        ledgeColor = Color.BLUE;
        ledges = new ArrayList<>();
    }

    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);

        // Draw the blast zone in red.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(blastZoneColor);
        shapeRenderer.rect(blastZone.x, blastZone.y, blastZone.width, blastZone.height);
        shapeRenderer.end();

        for (Rectangle ledge : ledges) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(ledgeColor);
            shapeRenderer.rect(ledge.x, ledge.y, ledge.width, ledge.height);
            shapeRenderer.end();
        }
    }

    public float getGravity() {
        return gravity;
    }

    @Override
    public List<Rectangle> getCollidables() {
        List<Rectangle> collidables = super.getCollidables();
        collidables.addAll(ledges);

        return collidables;
    }

    /** Returns true if the entity is completely outside of the blast zone **/
    public boolean inBounds(Entity entity) {
        for (Rectangle rect : entity.getTranslatedRects()) {
            if (blastZone.contains(rect)) {
                return true;
            }
        }

        return false;
    }

    public boolean isGround(Rectangle rect) {
        for (Rectangle rectangle : getTranslatedRects()) {
            if (rect.equals(rectangle)) {
                return true;
            }
        }

        return false;
    }

    public boolean isLedge(Ledge ledge) {
        for (Rectangle rectangle : ledges) {
            if (ledge.equals(rectangle)) {
                return true;
            }
        }

        return false;
    }
}
