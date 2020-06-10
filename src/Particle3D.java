import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

import java.util.Random;

public class Particle3D {
    private PVector location;
    private PVector velocity;
    private PVector acceleration;
    private float mass;
    private float radius;
    private int color_;
    private int lifeSpan;
    private boolean visible;

    Particle3D() {

        Random random = new Random(System.identityHashCode(this));
        this.location = new PVector(0xfffff, 0xfffff, 0xfffff);
        this.velocity = new PVector(0, 0, 0);
        this.acceleration = new PVector(0, 0, 0);
        this.color_ = random.nextInt();
        this.mass = 0.005f + random.nextFloat() * 0.045f;
        this.radius = 5 + mass * 250;
        this.lifeSpan = -1;
        this.visible = true;
    }

    Particle3D(float radius, float mass, int color_, int lp) {

        this();
        setColor_(color_);
        setRadius(radius);
        setLifeSpan(lp);
        setMass(mass);
    }

    Particle3D(PVector location, float radius, float mass, int color_, int ls) {

        this();
        setLocation(location);
        setMass(mass);
        setRadius(radius);
        setColor_(color_);
        setLifeSpan(ls);
    }

    public void move(PVector acc, float velocityLimit) {

        setAcceleration(acc);
        move(velocityLimit);
    }

    public void move() {

        if (isAlive()) {
            velocity.add(acceleration);
            location.add(velocity);
        }
        if (lifeSpan > 0) lifeSpan--;
    }

    public void move(float velocityLimit) {

        if (isAlive()) {
            velocity.add(acceleration);
            limitVelocity(velocityLimit);
            location.add(velocity);
        }
        if (lifeSpan > 0) lifeSpan--;
    }

    Particle3D(PVector location, PVector velocity, PVector acc, float radius, float mass, int c, int lp) {

        this.location = location;
        this.velocity = velocity;
        this.acceleration = acc;
        this.radius = radius;
        this.color_ = c;
        this.lifeSpan = lp;
        this.mass = mass;
        visible = true;
    }
    public void stop() {

        acceleration = new PVector(0, 0, 0);
        velocity = new PVector(0, 0, 0);
    }

    public PVector getLocation() {

        return location;
    }

    public void setLocation(@NotNull PVector p) {

        location.set(p.x, p.y, p.z);
    }

    private void limitVelocity(float velocityLimit) {

        if (velocity.magSq() > velocityLimit) {
            velocity.normalize();
            velocity.mult(velocityLimit);
        }
    }

    public void reverseVelocityX() {

        velocity.x *= -1;
    }

    public void reverseVelocityY() {

        velocity.y *= -1;
    }

    public void reverseVelocityZ() {

        velocity.z *= -1;
    }

    public boolean isAlive() {

        return lifeSpan != 0;
    }

    public boolean isDead() {

        return lifeSpan == 0;
    }

    public boolean isImmortal() {

        return lifeSpan == -1;
    }

    public boolean isInvisible(){
        return !visible;
    }

    public void transDeadToImmortal() {

        if (isDead())
            setLifeSpan(-1);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getMass() {

        return mass;
    }

    public void setMass(float mass) {

        this.mass = mass;
    }

    public PVector getVelocity() {

        return velocity;
    }

    public void setVelocity(@NotNull PVector v) {

        velocity.set(v.x, v.y, v.z);
    }

    public PVector getAcceleration() {

        return acceleration;
    }

    public void setAcceleration(@NotNull PVector acc) {

        acceleration.set(acc.x, acc.y, acc.z);
    }

    public float getRadius() {

        return radius;
    }

    public void setRadius(float radius) {

        this.radius = radius;
    }

    public int getColor_() {

        return color_;
    }

    public void setColor_(int color_) {

        this.color_ = color_;
    }

    public int getLifeSpan() {

        return lifeSpan;
    }

    public void setLifeSpan(int lp) {

        lifeSpan = lp;
    }

    public void move(PVector acc) {

        setAcceleration(acc);
        move();
    }

}
