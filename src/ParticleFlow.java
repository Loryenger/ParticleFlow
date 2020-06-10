import peasy.PeasyCam;
import processing.core.PApplet;

import java.util.HashMap;
import java.util.Map;

public class ParticleFlow extends PApplet {

    PeasyCam cam;
    ParticleSystem ps;
    SceneSwitcher switcher;
    Map<Character, Integer> characterIntegerMap;

    public static void main(String[] passedArgs) {

        String[] appletArgs = new String[]{"ParticleFlow"};
        PApplet.main(appletArgs);
    }

    @Override
    public void settings() {

        size(1200, 600);
        pixelDensity(2);
    }

    @Override
    public void setup() {

        frameRate(60.0f);
        if(sketchRenderer().equals(P3D)){
            cam = new PeasyCam(this, width / 2.f, height / 2.f, 0, 1000);
            cam.setMinimumDistance(100);
            cam.setMaximumDistance(2000);
        }
        ps = new ParticleSystem(this, 2000);
        switcher = new SceneSwitcher(this, ps);
        characterIntegerMap = new HashMap<>();
        for (int i = 0; i < 10; i++)
            characterIntegerMap.put((char) (i + '0'), i);
    }

    @Override
    public void draw() {

        if (keyPressed)
            switcher.switchScene(characterIntegerMap.get(key));
        switcher.scene();
    }
}
