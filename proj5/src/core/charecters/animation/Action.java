package core.charecters.animation;

public abstract class Action {
    public boolean uninterruptible;
    public boolean cycle;
    public boolean isDeath;
    public String name;

    public Action(String name, boolean uninterruptible, boolean cycle, boolean isDeath) {
        this.name = name;
        this.uninterruptible = uninterruptible;
        this.cycle = cycle;
        this.isDeath = isDeath;
    }

    public Action(String name, boolean uninterruptible, boolean cycle) {
        this.name = name;
        this.uninterruptible = uninterruptible;
        this.cycle = cycle;
    }

    public Action(String name) {
        this.name = name;
        this.uninterruptible = false;
        this.cycle = false;
    }

    public abstract void onFinish();
}
