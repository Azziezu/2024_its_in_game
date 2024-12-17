import nl.saxion.app.SaxionApp;
import java.awt.*;

public class Button {
    static final int ALIGNLEFT = 0;
    static final int ALIGNCENTER = 1;
    String label, action;
    int x, y, width, height, textAlign, fontSize;
    boolean active;

    Button(String label, String action, int x, int y, int width, int height) {
        this.label = label;
        this.action = action;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fontSize = 24;
        this.textAlign = ALIGNCENTER;
        this.active = true;
    }

    Button(String label, String action, int x, int y, int width, int height, int textAlign, int fontSize) {
        this.label = label;
        this.action = action;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textAlign = textAlign;
        this.fontSize = fontSize;
        this.active = true;
    }

    Button(String label, String action, int x, int y, int width, int height, int textAlign, int fontSize, boolean active) {
        this.label = label;
        this.action = action;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textAlign = textAlign;
        this.fontSize = fontSize;
        this.active = active;
    }

    public void drawButton() {
        if (active) {
            SaxionApp.setFill(Color.BLACK);
            SaxionApp.setBorderColor(Color.WHITE);
            SaxionApp.drawRectangle(x, y, width, height);

            SaxionApp.setTextDrawingColor(Color.WHITE);
            if (textAlign == ALIGNCENTER) {
                SaxionApp.drawText(label, x + (width / 3), y + (height / 3), fontSize);
            } else {
                SaxionApp.drawText(label, x + (width / 10), y + (height / 3), fontSize);
            }
        }
    }

}
