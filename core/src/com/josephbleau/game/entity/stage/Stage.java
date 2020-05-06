package com.josephbleau.game.entity.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.entity.Entity;

public abstract class Stage extends Entity {
    /** Gravity determines how fast an entity falls (in px/seconds) **/
    protected float gravity = 5.0f;

    /** Rect representing the blast zone (if a player exits this rect they die) **/
    protected Rectangle blastZone;

    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);

        // Draw the blast zone in red.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(this.blastZone.x, this.blastZone.y, this.blastZone.width, this.blastZone.height);
        shapeRenderer.end();
    }

    /** You cannot apply force to a stage. **/
    @Override
    public void applyForce(float x, float y) { }

    public float getGravity() {
        return this.gravity;
    }

    /** Returns true if the entity is completely outside of the blast zone **/
    public boolean inBounds(Entity entity) {
        for (Rectangle rect : entity.getTranslatedRects()) {
            if (this.blastZone.contains(rect)) {
                return true;
            }
        }

        return false;
    }
}
