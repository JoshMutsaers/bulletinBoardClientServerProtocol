public class Note {
    // Fields
    private int x, y, width, height;
    private String colour, message;

    // Constructor
    public Note(int noteX, int noteY, int noteWidth, int noteHeight, String noteColour, String noteMessage) {
        x = noteX;
        y = noteY;
        width = noteWidth;
        height = noteHeight;
        colour = noteColour;
        message = noteMessage;
    }

    // Getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getColour() {
        return colour;
    }
    public String getMessage() {
        return message;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    // Check if a note contains a certain location
    public boolean contains(int containsX, int containsY) {
        return containsX >= x && containsX < x + width && containsY >= y && containsY < y + height;
    }

    // Check if a note completely overlaps with this one
    public boolean overlap(Note other) {
        return this.x == other.x && this.y == other.y;
    }
}