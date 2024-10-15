package game;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue(int i) {
        assert i == 0 || i == 1;
        if (i == 0) return x;
        return y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position that = (Position) obj;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        return x * 9 + y;  // '9' is the maximum value of y + 1
    }
}
