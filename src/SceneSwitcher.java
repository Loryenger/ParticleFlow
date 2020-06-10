import processing.core.PApplet;

import java.util.HashMap;
import java.util.Map;

public class SceneSwitcher {
    int currentSceneID;
    final Map<Integer, SceneConfigurator> configurators;
    final ParticleSystem ps;
    final PApplet sketch;

    SceneSwitcher(PApplet sketch, ParticleSystem ps) {

        currentSceneID = 0;
        this.ps = ps;
        this.sketch = sketch;

        configurators = new HashMap<>();
        configurators.put(0, new SceneConfigurator(new DefaultInitializer(), new DefaultMover(), 0));
        configurators.put(1, new SceneConfigurator(
                new PrinterInitializer("/Users/loryenger/Downloads/2.jpg"), new PrinterMover(),1));
        configurators.put(2, new SceneConfigurator(new HiddenInitializer(), new FixedSwarmMover(),
                new PrinterVisualizer(), 2));
        configurators.put(3, new SceneConfigurator(new ExplodeInitializer(),
                new DefaultMover(), new FieldFlowVisualizer(),3));
        configurators.put(4, new SceneConfigurator(new ImmortalInitializer(),
                new PerlinMover(), new FieldFlowVisualizer(),4));
        configurators.put(5, new SceneConfigurator(new CircleInitializer(),
                new FountainMover(), new FieldFlowVisualizer(),5));
        configurators.put(6, new SceneConfigurator(new FountainInitializer(),
                new PrinterMover(50, 500L), new FieldFlowVisualizer(), 6));
        configurators.put(7, new SceneConfigurator(new PortrayInitializer(),
                new PortrayMover("/Users/loryenger/Downloads/6.png", ps), new FieldFlowVisualizer(),7));
    }

    void switchScene(Integer n) {

        if (configurators.containsKey(n)) {
            if (currentSceneID != n) {
                currentSceneID = n;
                System.out.println("Switched to Scene"+ n);
                configurators.get(n).initParticleSystem(ps);
                configurators.get(n).getMover().init(ps);
            }
        }
    }

    void scene() {

        configurators.get(currentSceneID).move(ps);
        if(sketch.sketchRenderer().equals(PApplet.P3D))
        configurators.get(currentSceneID).visualize(ps);
        else configurators.get(currentSceneID).visualize2D(ps);
    }
}
