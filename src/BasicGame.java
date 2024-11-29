import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

public class BasicGame implements GameLoop {

    static final int STARTSCREEN = 1;
    static final int GAMESCREEN = 2;
    static final int ENDSCREEN = 3;

    int x, y; // Muiscoördinaten
    String raadwoord = "Capybara";
    String name = "";
    String geradenLetters = "";
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
        letterInvoeren();
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

            // Read letter input for guessing
            boolean inputChar = keyboardEvent.isKeyPressed();   // Waarde van key die ingevoerd word door gebruiker word opgeslagen in inputChar
            char letterRaden = (char) keyboardEvent.getKeyCode(); // Keycode is een numerieke waarde die wordt ingevoerd, met name van (char) word dit omgezet in een letter
            boolean isLetterRaden = Character.isLetter(letterRaden);    // Controleert of letterraden een letter meekrijgt van de gebruikers, anders geeft dit false

            if (inputChar) {
                SaxionApp.drawText("Je hebt de letter ingevoerd " + letterRaden, 200, 300, 24); // Geeft door welke letter is ingevoerd door speler
            } else {
                SaxionApp.drawText("Voer alsjeblieft maar 1 letter in!", 300, 200, 24);
            }



            // Onthoud het ingedrukte woord
            char letter = (char) keyboardEvent.getKeyCode();
            // Kijkt of er een letter word ingevoerd
            boolean isLetter = Character.isLetter(letter);
            // Controleert of er een letter of een spatie word ingevoerd.
            if (isLetter || letter == ' ') {
                // Zet de letters in kleine letters
                letter = Character.toLowerCase(letter);
                name = name + letter;

            }
        }
    }

    public void letterInvoeren(){
        SaxionApp.drawText("Voer een letter in: ", 150, 150, 24);
        SaxionApp.drawText(geradenLetters, 200, 200, 24);
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) { // Herkent invoer op muis
        x = mouseEvent.getX();
        y = mouseEvent.getY();
    }

    public void drawStartScreen() { // Start scherm
        SaxionApp.drawText("Laat het hoofd niet rollen!", 20, 20, 24);
        SaxionApp.drawText("Druk op spatie om het spel te starten", 20, 60, 24);
    }

    public void drawGameScreen() {  // Actief game scherm
        SaxionApp.drawText("Raad het woord: " + raadwoord, 20, 20, 24);
    }

    public void drawEndScreen() {   // Eind scherm
        SaxionApp.drawText("Spel voorbij!", 20, 20, 24);
        SaxionApp.drawText("Druk op R om opnieuw te spelen.", 20, 60, 24);
    }

}