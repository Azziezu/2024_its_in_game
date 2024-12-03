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
    ArrayList<Character> geradenLetters = new ArrayList<>(); // Houdt bij welke letters al geraden zijn
    String tijdelijkBericht = "";       // Bericht dat op het scherm weergegeven moet worden
    long berichtTijd = 0;               // Timer voor het bericht, hoe lang het zichtbaar blijft

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
    }

    // Slaat invoer van de spelernaam op
    public void voerSperlernaam() {
        SaxionApp.drawText(" Voer je naam: ", 100, 100, 20);
        SaxionApp.drawText(name, 248, 100, 20);
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

        // Laat een bericht zien met de tijd voor hoe lang het op scherm moet blijven
        if (!tijdelijkBericht.isEmpty() && System.currentTimeMillis() < berichtTijd) {
            SaxionApp.drawText(tijdelijkBericht, 300, 50, 25);
        } else {
            tijdelijkBericht = ""; // Verwijder bericht als tijd voorbij is
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
                    break;
                case ENDSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_R) {
                        currentScreen = STARTSCREEN;
                    }
                    break;
                default:
                    break;
            }

            // Deze code wordt alleen uitgevoerd wanneer het gamescherm actief is
            if (keyboardEvent.isKeyPressed() && currentScreen == GAMESCREEN) {
                char ingevoerdeLetter = (char) keyboardEvent.getKeyCode();
                // Als ingevoerde letter een letter is
                if (Character.isLetter(ingevoerdeLetter)) {
                    // Zorgt ervoor dat ingevoerde letter niet case sensitive is en altijd wordt herkend
                    ingevoerdeLetter = Character.toLowerCase(ingevoerdeLetter);

                    // Checkt of geraden letter al eerder geraden is met contains
                    if (geradenLetters.contains(ingevoerdeLetter)) {
                        toonBericht("Je hebt de letter " + ingevoerdeLetter + " al geraden!");
                    } else {
                        geradenLetters.add(ingevoerdeLetter); // Voeg de letter toe aan de lijst

                        // Veranderd char in string omdat contains alleen string herkend
                        if (raadwoord.toLowerCase().contains(String.valueOf(ingevoerdeLetter))) {
                            toonBericht("Letter wat je hebt ingevoerd is " + ingevoerdeLetter + " , goed geraden!");
                        } else {
                            toonBericht("Letter wat je hebt ingevoerd is " + ingevoerdeLetter + " , dit is helaas fout!");
                        }
                    }
                } else {
                    // Kan alleen een letter invoeren, geen andere tekens of cijfers
                    toonBericht("Voer alstublieft alleen een letter in.");
                }
            }

            // Onthoud het ingedrukte woord
            char letter = (char) keyboardEvent.getKeyCode();
            boolean isLetter = Character.isLetter(letter);
            if (isLetter || letter == ' ') {
                letter = Character.toLowerCase(letter);
                name = name + letter;
            }
        }
    }

    public void toonBericht(String tekst) {
        tijdelijkBericht = tekst;                           // Tekst
        berichtTijd = System.currentTimeMillis() + 2000;    // Laat de tekst 2 seconde staan
    }

    public void letterInvoeren(){
        SaxionApp.drawText("Voer een letter in: ", 20, 50, 30); // Tekst voor letter invoeren
        SaxionApp.drawText("Geraden letters: " + geradenLetters, 20, 100, 20); // Toon de geraden letters tekst
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
                    break;
                case ENDSCREEN:
                    break;
                default:
                    break;
            }
        }
    }

    public void drawStartScreen() {
        SaxionApp.drawText("Laat het hoofd niet rollen!", 30, 30, 24);
        SaxionApp.drawText("Druk op START om het spel te starten", 30, 60, 24);
        SaxionApp.drawText("Difficulty: " + difficultyToString(), 30, height - 240, 24);
        voerSperlernaam();

        for (Button button: buttonsStartScreen) {
            button.drawButton();
        }
    }

    public void drawGameScreen() {
        SaxionApp.drawText("Raad het woord: " + raadwoord, 20, 20, 24);
        letterInvoeren();
    }

    public void drawEndScreen() {
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

    public boolean gameOver() {
        return false;
    }
}