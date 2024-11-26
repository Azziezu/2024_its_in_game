import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

public class BasicGame implements GameLoop {

    int x, y; // Muiscoördinaten
    String raadwoord = "Capybara";

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 1000, 1000, 40);
    }

    @Override
    public void init() { // Voert eenmalig dingen uit aan het begin van het spel

    }

    @Override
    public void loop() {  // Blijft dingen herhalen gedurende het spel
        SaxionApp.clear();

    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) { // Herkent invoer op toetsenbord
        if (keyboardEvent.isKeyPressed()) {
            // if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {}
        }
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) { // Herkent invoer op muis
        x = mouseEvent.getX();
        y = mouseEvent.getY();
    }
}






