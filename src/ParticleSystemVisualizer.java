import processing.core.PApplet;
import processing.core.PVector;
import sun.jvm.hotspot.utilities.IntArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

abstract public class ParticleSystemVisualizer {
    protected final ParticleVisualizer visualizer;
    ParticleSystemVisualizer(){
        visualizer = new ParticleVisualizer();
    }
    public void visualize(ParticleSystem ps) {
        setLight(ps.getSketch());
        updateFrame(ps);
        ps.getVisibleParticles().forEach(particle3D -> visualizer.visual(ps.getSketch(), particle3D));
    }

    public void visualize2D(ParticleSystem ps) {
        setBackground(ps.getSketch());
        updateFrame(ps);
        ps.getVisibleParticles().forEach(particle3D -> visualizer.visual2D(ps.getSketch(), particle3D));
    }

    abstract protected void updateFrame(ParticleSystem ps);
    abstract protected void setBackground(PApplet sketch);
    protected void setLight(PApplet sketch){
        sketch.background(0xff, 0xff, 0xff, 0xff);
        sketch.directionalLight(0xff, 0xff, 0xff, -1, 1, 0);
        sketch.ambientLight(0xe2, 0xb0, 0xff);
    }
}

class DefaultVisualizer extends ParticleSystemVisualizer {
    @Override
    protected void updateFrame(ParticleSystem ps) {

    }

    @Override
    protected void setBackground(PApplet sketch) {
        sketch.background(0);
    }
}

class DynamicSwarmVisualizer extends ParticleSystemVisualizer {

    @Override
    public void updateFrame(ParticleSystem ps) {

        if (ps.getSketch().mousePressed) {
            Random random = new Random(System.currentTimeMillis());
            int color_ = ps.getSketch().color(random.nextFloat() * 255, random.nextFloat() * 255, random.nextFloat() * 255);
            float mass = 0.005f + random.nextFloat() * 0.045f;
            float radius = 4 + mass * 150;
            ps.addParticle(new Particle3D(new PVector(ps.getSketch().mouseX, ps.getSketch().mouseY, 0),
                    radius, mass, color_, -1));
        }
    }

    @Override
    protected void setBackground(PApplet sketch) {
        /*
        sketch.noStroke();
        sketch.fill(0,10);
        sketch.rect(0, 0, sketch.width, sketch.height);
        */
        sketch.background(0);
    }
}

class FieldFlowVisualizer extends ParticleSystemVisualizer {
    @Override
    protected void updateFrame(ParticleSystem ps) {

    }

    @Override
    protected void setBackground(PApplet sketch) {
        sketch.noStroke();
        sketch.fill(0,10);
        sketch.rect(0, 0, sketch.width, sketch.height);
    }
}

class PrinterVisualizer extends ParticleSystemVisualizer{
    @Override
    protected void updateFrame(ParticleSystem ps) {
        if(ps.getSketch().mousePressed){
            ps.getInvisibleParticles().get(0).setVisible(true);
            ps.getInvisibleParticles().get(0).setLifeSpan(-1);
        }
    }

    @Override
    protected void setBackground(PApplet sketch) {
        sketch.background(0);
    }
}
class DelaunayVisualizer extends ParticleSystemVisualizer{
    DelaunayTriangulator triangulator;
    com.badlogic.gdx.utils.IntArray indices;
    DelaunayVisualizer(){
        triangulator=new DelaunayTriangulator();
        indices = new com.badlogic.gdx.utils.IntArray();
    }

    @Override
    protected void updateFrame(ParticleSystem ps) {
        float[] buffer = ps.getLocationArray();
        indices=triangulator.computeTriangles(buffer,false);
        ps.getSketch().fill(255);
        for (int i=0; i<indices.size; i+=3){
            ps.getSketch().stroke(ps.getParticles().get((int)buffer[indices.get(i)]/2).getColor_());
            ps.getSketch().triangle(buffer[indices.get(i)], buffer[indices.get(i)+1],
                    buffer[indices.get(i+1)], buffer[indices.get(i+1)+1],
                    buffer[indices.get(i+2)], buffer[indices.get(i+2)+1]);
        }

    }

    @Override
    protected void setBackground(PApplet sketch) {
        sketch.background(0);
    }
}