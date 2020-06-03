package com.nighto.weebu.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PhysicalComponent extends Component {
    public Vector2 position;
    public Vector2 dimensions;

    public Vector2 prevPosition;
    public Vector2 velocity;
    public Vector2 prevVelocity;

    public Vector2 prevDimensions;

    public Rectangle wallSlidingOn;
    public Rectangle floorStandingOn;

    public boolean facingRight;
    public boolean collidable;
    
    public PhysicalComponent() {
        position = new Vector2();
        prevPosition = new Vector2();
        velocity = new Vector2();
        prevVelocity = new Vector2();
        dimensions = new Vector2();
        prevDimensions = new Vector2();
        collidable = true;
    }

    public PhysicalComponent(Vector2 startingPosition, Vector2 startingVelocity) {
        position = new Vector2(startingPosition);
        velocity = new Vector2(startingVelocity);

        prevPosition = new Vector2();
        prevVelocity = new Vector2();

        dimensions = new Vector2();
        prevDimensions = new Vector2();
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(position.x, position.y, dimensions.x, dimensions.y);
    }

    public Rectangle getPrevBoundingBox() {
        return new Rectangle(prevPosition.x, prevPosition.y, prevDimensions.x, prevDimensions.y);
    }

    public void move(float x, float y) {
        move(x, y, false);
    }

    public void move(float x, float y, boolean keepVelocity) {
        if (!keepVelocity) {
            velocity = new Vector2();
        }

        prevPosition = new Vector2(position);
        position = new Vector2(x, y);
    }


    public void setVelocity(float x, float y) {
        prevVelocity = new Vector2(velocity);
        velocity = new Vector2(x, y);
    }

    @Override
    public Component save() {
        PhysicalComponent physicalComponent = new PhysicalComponent();
        physicalComponent.position = new Vector2(position);
        physicalComponent.prevPosition = new Vector2(prevPosition);
        physicalComponent.velocity = new Vector2(velocity);
        physicalComponent.prevVelocity = new Vector2(prevVelocity);

        physicalComponent.dimensions = new Vector2(dimensions);
        physicalComponent.prevDimensions = new Vector2(prevDimensions);

        physicalComponent.wallSlidingOn = wallSlidingOn;
        physicalComponent.floorStandingOn = floorStandingOn;

        return physicalComponent;
    }

    @Override
    public void load(Component component) {
        PhysicalComponent physicalComponent = (PhysicalComponent) component;

        this.position = new Vector2(physicalComponent.position);
        this.prevPosition = new Vector2(physicalComponent.prevPosition);
        this.velocity = new Vector2(physicalComponent.velocity);
        this.prevVelocity = new Vector2(physicalComponent.prevVelocity);

        this.dimensions = new Vector2(physicalComponent.dimensions);
        this.prevDimensions = new Vector2(physicalComponent.prevDimensions);

        this.wallSlidingOn = physicalComponent.wallSlidingOn;
        this.floorStandingOn = physicalComponent.floorStandingOn;

        this.facingRight = physicalComponent.facingRight;
    }

    public float getFacingModifier() {
        return facingRight ? 1f : -1f;
    }
}
