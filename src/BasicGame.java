import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BasicGame implements GameLoop {

    static final int STARTSCREEN = 1;
    static final int GAMESCREEN = 2;
    static final int ENDSCREEN = 3;

    int x, y; // Muiscoördinaten
    String raadwoord = "Capybara";
    String name= "";
    int gameState;


    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 1000, 1000, 40);
    }





    @Override
    public void init() { // Voert eenmalig dingen uit aan het begin van het spel
        raadwoord = "Capybara";
        gameState = STARTSCREEN;
        // SaxionApp.printLine(spelernaam);
        readFranseWoorden();
    }

    // Slaat invoer van de spelernaam op
    public void voerSperlernaam() {
        SaxionApp.drawText(" Voer je naam: ", 100, 100, 20);
        SaxionApp.drawText(name, 100, 200, 30);

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
        voerSperlernaam();

    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) { // Van startscherm naar het spel
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
            // if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {}
            boolean inputChar = keyboardEvent.isKeyPressed();

            name = name + "a";

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

    public void readFranseWoorden() {

        String file = "Resources\\Franse woorden.csv";
        BufferedReader reader = null;
        String line = "";

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");

                for (String index : row) {
                    System.out.printf("-%10s", index);
                }
                System.out.println();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}









