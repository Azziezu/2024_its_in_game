import nl.saxion.app.CsvReader;
import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

public class BasicGame implements GameLoop {

    static final int STARTSCREEN = 1;
    static final int GAMESCREEN = 2;
    static final int ENDSCREEN = 3;

    int x, y; // Muiscoördinaten
    String raadwoord;
    int gameState;


    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 1000, 1000, 40);
    }

    @Override
    public void init() { // Voert eenmalig dingen uit aan het begin van het spel
        raadwoord = "Capybara";
        gameState = STARTSCREEN;
        // SaxionApp.printLine(spelernaam);
    }

    // Slaat invoer van de spelernaam op
    public static String spelernaam;
    public String voerSperlernaam() {
        SaxionApp.printLine(" Voer je naam: ");
        String spelernaam = SaxionApp.readString();
        return spelernaam;
    }

    @Override
    public void loop() {  // Blijft dingen herhalen gedurende het spel
        SaxionApp.clear();
        switch (gameState) {
            case STARTSCREEN:
                drawStartScreen();
                break;
            case GAMESCREEN:
                drawGameScreen();
                break;
            case ENDSCREEN:
                drawEndScreen();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) { // Herkent invoer op toetsenbord
        if (keyboardEvent.isKeyPressed()) {
            switch (gameState) {
                case STARTSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                        gameState = GAMESCREEN;
                    }
                    break;
                case GAMESCREEN:
                    //
                    break;
                case ENDSCREEN:
                    //
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) { // Herkent invoer op muis
        x = mouseEvent.getX();
        y = mouseEvent.getY();
    }

    public void drawStartScreen() {
        SaxionApp.drawText("Laat het hoofd niet rollen!", 20, 20, 24);
        SaxionApp.drawText("Druk op spatie om het spel te starten", 20, 60, 24);
    }

    public void drawGameScreen() {
        SaxionApp.drawText("Voer een letter in: ", 5, 5, 16);
    }

    public void drawEndScreen() {
        //
    }

}






