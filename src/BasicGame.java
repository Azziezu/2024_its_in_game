import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.util.ArrayList;

public class BasicGame implements GameLoop {

    static final int STARTSCREEN = 1;
    static final int GAMESCREEN = 2;
    static final int ENDSCREEN = 3;
    static final int EASY = 1;
    static final int NORMAL = 2;
    static final int HARD = 3;

    int mouseX, mouseY, width, height, currentScreen, difficulty;
    String raadwoord;
    String name = "";
    String geradenLetters = "";

    ArrayList<Button> buttonsStartScreen = new ArrayList<>();

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 900, 900, 40);
    }

    @Override
    public void init() { // Voert eenmalig dingen uit aan het begin van het spel
        raadwoord = "Capybara";
        currentScreen = STARTSCREEN;
        difficulty = NORMAL;
        width = SaxionApp.getWidth();
        height = SaxionApp.getHeight();

        buttonsStartScreen.add(new Button("EASY", 30, height - 180, width / 3 - 60, 60));
        buttonsStartScreen.add(new Button("NORMAL", width / 3 + 30, height - 180, width / 3 - 60, 60));
        buttonsStartScreen.add(new Button("HARD", (width / 3 * 2) + 30, height - 180, width / 3 - 60, 60));

        buttonsStartScreen.add(new Button("START", 30, height - 90, width - 60, 60));
        // buttonsStartScreen.add(new Button("LEADERBOARD", width / 2 + 30, height - 90, width / 2 - 60, 60));
    }

    // Slaat invoer van de spelernaam op
    public void voerSperlernaam() {
        SaxionApp.drawText(" Voer je naam: ", 100, 100, 20);
        SaxionApp.drawText(name, 100, 200, 30);
    }

    @Override
    public void loop() {  // Blijft dingen herhalen gedurende het spel
        SaxionApp.clear();
        switch (currentScreen) {
            case STARTSCREEN:
                drawStartScreen();
                break;
            case GAMESCREEN:
                if (gameOver()) {
                    currentScreen = ENDSCREEN;
                } else {
                    drawGameScreen();
                }
                break;
            case ENDSCREEN:
                drawEndScreen();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) { // Van startscherm naar het spel
        if (keyboardEvent.isKeyPressed()) {
            switch (currentScreen) {
                case STARTSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                        currentScreen = GAMESCREEN;
                    }
                    break;
                case GAMESCREEN:
                    //
                    break;
                case ENDSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_R) {
                        currentScreen = STARTSCREEN;
                    }
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
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
        if (mouseEvent.isMouseDown()) {
            switch (currentScreen) {
                case STARTSCREEN:
                    buttonEvent(buttonsStartScreen);
                    break;
                case GAMESCREEN:
                    // buttonEventHandler(buttonsGameScreen);
                    break;
                case ENDSCREEN:
                    // buttonEventHandler(buttonsEndScreen);
                    break;
                default:
                    break;
            }
        }
    }

    public void drawStartScreen() { // Start scherm
        SaxionApp.drawText("Laat het hoofd niet rollen!", 30, 30, 24);
        SaxionApp.drawText("Druk op START om het spel te starten", 30,  60, 24);
        SaxionApp.drawText("Difficulty: " + difficultyToString(), 30, height - 240, 24);
        voerSperlernaam();

        for (Button button: buttonsStartScreen) {
            button.drawButton();
        }
    }

    public void drawGameScreen() {  // Actief game scherm
        SaxionApp.drawText("Raad het woord: " + raadwoord, 20, 20, 24);
        letterInvoeren();
    }

    public void drawEndScreen() {   // Eind scherm
        SaxionApp.drawText("Spel voorbij!", 20, 20, 24);
        SaxionApp.drawText("Druk op R om opnieuw te spelen.", 20, 60, 24);
    }

    public void buttonEvent(ArrayList<Button> buttons) {
        for (Button button : buttons) {
            if (isValidButtonClick(button)) {
                executeButtonEvent(button);
            }
        }
    }

    public boolean isValidButtonClick(Button button) {
        return mouseX > button.x
                && mouseX < button.x + button.width
                && mouseY > button.y
                && mouseY < button.y + button.height;
    }

    public void executeButtonEvent(Button button) {
        switch (button.label) {
            case "EASY":
                difficulty = EASY;
                break;
            case "NORMAL":
                difficulty = NORMAL;
                break;
            case "HARD":
                difficulty = HARD;
                break;
            case "START":
                currentScreen = GAMESCREEN;
                break;
            case "LEADERBOARD":
                // TODO leaderboard knop
                // currentScreen = LEADERBOARD;
                break;
            default:
                break;
        }
    }

    public String difficultyToString() {
        return switch (difficulty) {
            case EASY -> "Easy";
            case NORMAL -> "Normal";
            case HARD -> "Hard";
            default -> "No difficulty set";
        };
    }

    public boolean gameOver() { // TODO methode die checkt of het spel voorbij is (guillotine gevallen of woord geraden)
        return false;
    }

}