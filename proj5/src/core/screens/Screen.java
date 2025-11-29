package core.screens;

import utils.DS.recordlike.Point;

public interface Screen {
    public void handleKeyPress(char key);
    public void pollMouse(Point mouse);

    public void update();
    public void render();

    public int width();
    public int height();

    default public int xScale() {
        return width();
    }

    default public int yScale() {
        return height();
    }
}
