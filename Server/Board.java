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

    // Post a new note to the board
    public synchronized String post(int x, int y, String colour, String message) {
        if (x < 0 || y < 0 || x + this.noteWidth > this.boardWidth || y + this.noteHeight > this.boardHeight) {
            return "ERROR OUT_OF_BOUNDS Note extends beyond the size of the board";
        }
        if (!this.colours.contains(colour)) {
            return "ERROR COLOUR_NOT_SUPPORTED Colour is not in the valid list";
        }
        for (Note note : notes) {
            if (note.getX() == x && note.getY() == y) {
                return "ERROR COMPLETE_OVERLAP A note already exists at these coordinates";
            }
        }
        notes.add(new Note(x, y, noteWidth, noteHeight, colour, message));
        return "OK";
    }

    // Add a pin to the board
    public synchronized String pin(int x, int y) {
        for (Note note : notes) {
            if (note.contains(x, y)) {
                pins.add(new Pin(x, y));
                return "OK";
            }
        }
        return "ERROR NO_NOTE_AT_COORDINATE No note contains the given point";
    }

    // Remove a pin from the board
    public synchronized String unpin(int x, int y) {
        for (Pin pin : pins) {
            if (x == pin.getX() && y == pin.getY()) {
                pins.remove(pin);
                return "OK";
            }
        }
        return "ERROR PIN_NOT_FOUND No pin exists at the given coordinates";
    }
    
    // Returns a list of notes that meet the given conditions
    public synchronized String getNotes(String colour, Integer containsX, Integer containsY, String refersTo) {
        String output = "OK ";
        String outputNotes = "";
        String pinnedStatus;
        int count = 0;
        boolean passes;
        for (Note note : notes) {
            passes = true;
            if (colour != null && !note.getColour().equals(colour)) {
                passes = false;
            }
            if (containsX != null && containsY != null && !note.contains(containsX, containsY)) {
                passes = false;
            }
            if (refersTo != null && !note.getMessage().contains(refersTo)) {
                passes = false;
            }
            if (passes) {
                count++;
                pinnedStatus = isNotePinned(note.getX(), note.getY()) ? "PINNED" : "UNPINNED";
                outputNotes += "\nNOTE " + note.getX() + " " + note.getY() + " " + note.getColour() +  " " + pinnedStatus + " " + note.getMessage();
            }
        }
        return output + count + outputNotes;
    }
}