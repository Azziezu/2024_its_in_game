import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

public class BasicGame implements GameLoop {    // Gameloop geeft je 4 methodes

    int x,y,z;        // Roep de int aan

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 1000, 1000, 40);
    }

    @Override
    public void init() {
        x = 500;
        y = 500;
        z = 30;
        int B = 40;
    }

    @Override
    public void loop() {
        SaxionApp.clear(); // Verwijderd voorgaande circle zodat het de muis volgt
        SaxionApp.drawCircle(x, y,100);

    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.getKeyCode() == keyboardEvent.VK_SPACE){
            SaxionApp.setFill(SaxionApp.getRandomColor());

        }
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) {
        x = mouseEvent.getX();
        y = mouseEvent.getY();
    }
}






