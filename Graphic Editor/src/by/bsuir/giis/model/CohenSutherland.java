package by.bsuir.giis.model;

/**
 * @author Andrey
 */
public class CohenSutherland {
    private boolean up;
    private boolean down;
    private boolean right;
    private boolean left;

    public CohenSutherland() {
    }

    public CohenSutherland(boolean up, boolean down, boolean right, boolean left) {
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
}
