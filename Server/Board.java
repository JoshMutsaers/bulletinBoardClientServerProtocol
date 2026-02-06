import java.util.ArrayList;
public class Board {
    // Fields
    private ArrayList<Note> notes;
    private ArrayList<Pin> pins;
    private ArrayList<String> colours;
    private int boardWidth, boardHeight, noteWidth, noteHeight;

    // Constructor
    public Board(int bWidth, int bHeight, int nWidth, int nHeight, String[] validColours) {
        boardWidth = bWidth;
        boardHeight = bHeight;
        noteWidth = nWidth;
        noteHeight = nHeight;
        notes = new ArrayList<Note>();
        pins = new ArrayList<Pin>();
        colours = new ArrayList<String>();
        for (String colour : validColours) {
            colours.add(colour);
        }
    }

    // Remove all notes and pins from the board
    public synchronized String clear() {
        notes.clear();
        pins.clear();
        return "OK";
    }

    // Check if the note at a certain position is pinned
    private boolean isNotePinned(int noteX, int noteY) {
        for (Pin pin : pins) {
            if (pin.getX() >= noteX && pin.getX() < noteX + noteWidth && pin.getY() >= noteY && pin.getY() < noteY + noteHeight) {
                return true;
            }
        }
        return false;
    }

    // Removes all unpinned notes
    public synchronized String shake() {
        for (int i = notes.size() - 1; i >= 0; i--) {
            Note note = notes.get(i);
            if (!isNotePinned(note.getX(), note.getY())) {
                notes.remove(note);
            }
        }
        return "OK";
    }

    // Returns a list of where all of the pins are
    public synchronized String getPins() {
        String pinsOutput = "OK " + pins.size();
        for (Pin pin : pins) {
            pinsOutput += "\nPIN " + pin.getX() + " " + pin.getY();
        }
        return pinsOutput;
    }
}