public class Pin {
    // Fields
    private int x, y;

    // Constructor
    public Pin(int pinX, int pinY) {
        x = pinX;
        y = pinY;
    }

    // Getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    // Check if two pins are on the same location
    public boolean equals(Pin other) {
        return this.x == other.x && this.y == other.y;
    }
}