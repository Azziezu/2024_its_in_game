import nl.saxion.app.SaxionApp;
import java.awt.*;

public class Button {

    String label;
    int x, y, width, height;

    Button(String label, int x, int y, int width, int height) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawButton() {
        SaxionApp.setFill(Color.BLACK);
        SaxionApp.setBorderColor(Color.WHITE);
        SaxionApp.drawRectangle(x, y, width, height);

        SaxionApp.setTextDrawingColor(Color.WHITE);
        SaxionApp.drawText(label, x + (width / 3), y + (height / 3), 24);
    }

}
