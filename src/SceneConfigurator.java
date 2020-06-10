import org.jetbrains.annotations.NotNull;

public class SceneConfigurator {
    private final int ID;
    private final ParticleSystemInitializer initializer;

    public ParticleSystemInitializer getInitializer() {
        return initializer;
    }

    public ParticleSystemMover getMover() {
        return mover;
    }

    public ParticleSystemVisualizer getVisualizer() {
        return visualizer;
    }

    private final ParticleSystemMover mover;
    private final ParticleSystemVisualizer visualizer;

    SceneConfigurator(ParticleSystemInitializer initializer, ParticleSystemMover mover, ParticleSystemVisualizer visualizer, int id) {
        this.initializer = initializer;
        this.mover = mover;
        this.visualizer = visualizer;
        ID = id;
    }

    SceneConfigurator(ParticleSystemInitializer initializer, ParticleSystemMover mover, int id) {
        this.initializer = initializer;
        this.mover = mover;
        this.visualizer = new DefaultVisualizer();
        ID = id;
    }

    void initParticleSystem(ParticleSystem ps) {

        initializer.initParticleSystem(ps);
    }

    void move(ParticleSystem ps) {
        mover.move(ps);
    }

    void visualize(@NotNull ParticleSystem ps) {
        visualizer.visualize(ps);
    }

    void visualize2D(@NotNull ParticleSystem ps) {
        visualizer.visualize2D(ps);
    }

    int getID() {
        return ID;
    }
}