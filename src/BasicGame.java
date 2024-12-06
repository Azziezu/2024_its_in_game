import nl.saxion.app.CsvReader;
import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.util.ArrayList;

public class BasicGame implements GameLoop {

    static final int STARTSCREEN = 1;
    static final int GAMESCREEN = 2;
    static final int ENDSCREEN = 3;
    static final int LEADERBOARDSCREEN = 4;
    static final int EASY = 1;
    static final int NORMAL = 2;
    static final int HARD = 3;

    int mouseX, mouseY, width, height, currentScreen, difficulty;
   // Y cordinator om de mes te laten vallen
    int vallendeMes = 100;
    String raadwoord;
    ArrayList<String> woordenlijst = new ArrayList<>();
    ArrayList<Button> buttonsStartScreen = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Character> geradenLetters = new ArrayList<>(); // Houdt bij welke letters al geraden zijn
    ArrayList<Character> goedGeradenLetters = new ArrayList<>();
    ArrayList<Character> foutGeradenLetters = new ArrayList<>();
    String tijdelijkBericht = "";       // Bericht dat op het scherm weergegeven moet worden
    long berichtTijd = 0;               // Timer voor het bericht, hoe lang het zichtbaar blijft

    String CSVFile = "resources/FranseWoorden.csv";
    CsvReader reader = new CsvReader(CSVFile);

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
        csvReader();

        players.add(new Player(1, ""));
        // players.add(new Player(2, ""));

        buttonsStartScreen.add(new Button("EDIT", "EDITP1", 30, 290, 60, 30, Button.ALIGNLEFT, 20));
        // buttonsStartScreen.add(new Button("EDIT", "EDITP2", (width / 2) + 30, 290, 60, 30, Button.ALIGNLEFT, 20));
        buttonsStartScreen.add(new Button("EASY", "EASY", 30, height - 180, width / 3 - 45, 60));
        buttonsStartScreen.add(new Button("NORMAL", "NORMAL", (width / 3) + 15, height - 180, width / 3 - 30, 60));
        buttonsStartScreen.add(new Button("HARD", "HARD", (width / 3 * 2) + 15, height - 180, width / 3 - 45, 60));
        buttonsStartScreen.add(new Button("START", "START", 30, height - 90, (width / 2) - 45, 60));
        buttonsStartScreen.add(new Button("LEADERBOARD", "LEADERBOARD", (width / 2) + 15, height - 90, (width / 2) - 45, 60));
    }

    // Slaat invoer van de spelernaam op
    public void drawPlayerName() {
        SaxionApp.drawText("Voer je naam in: " + players.get(0).name, 100, 300, 20);
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
            case LEADERBOARDSCREEN:
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
    public void keyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.isKeyPressed()) {
            switch (currentScreen) {
                case STARTSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                        currentScreen = GAMESCREEN;
                    }
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_BACK_SPACE) {
                        for (Player player : players) {
                            if (player.editable) {
                                player.name = removeLastCharFromString(player.name);
                            }
                        }
                    }
                    for (Player player : players) {
                        if (player.editable) {
                            naamInvoeren((char) keyboardEvent.getKeyCode(), player);
                        }
                    }
                    break;
                case GAMESCREEN:
                    registreerIngevoerdeLetters((char) keyboardEvent.getKeyCode());
                    break;
                case ENDSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_R) {
                        currentScreen = STARTSCREEN;
                    }
                    break;
                case LEADERBOARDSCREEN:
                    break;
                default:
                    break;
            }
        }
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
                case LEADERBOARDSCREEN:
                    break;
                default:
                    break;
            }
        }
    }

    public void buttonEvent(ArrayList<Button> buttons) {
        for (Button button : buttons) {
            if (isValidButtonClick(button)) {
                switch (button.action) {
                    case "LEADERBOARD":
                        currentScreen = LEADERBOARDSCREEN;
                        break;
                    case "EDITP1":
                        editPlayerName(1);
                        break;
                    case "EDITP2":
                        editPlayerName(2);
                        break;
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
        }
    }

    public boolean isValidButtonClick(Button button) {
        return mouseX > button.x
                && mouseX < button.x + button.width
                && mouseY > button.y
                && mouseY < button.y + button.height;
    }

    public void drawStartScreen() {
        SaxionApp.drawText("Laat het hoofd niet rollen!", 30, 30, 24);
        SaxionApp.drawText("Druk op START om het spel te starten", 30, 60, 24);
        SaxionApp.drawText("Difficulty: " + difficultyToString(), 30, height - 240, 24);
        drawPlayerName();

        for (Button button : buttonsStartScreen) {
            button.drawButton();
        }
    }

    public void drawGameScreen() {
        SaxionApp.drawText("Raad het woord: " + raadwoord, 20, 20, 24);
        letterInvoeren();
        drawRaadWoord();

    }

    public void drawEndScreen() {
        SaxionApp.drawText("Spel voorbij!", 20, 20, 24);
        SaxionApp.drawText("Druk op R om opnieuw te spelen.", 20, 60, 24);
    }

    public void drawRaadWoord() {
        for (int i = 0; i < raadwoord.length(); i++) {
            int spacing = (height - 200) / raadwoord.length();
            if (goedGeradenLetters.contains(raadwoord.toLowerCase().charAt(i))) {
                SaxionApp.drawText("" + raadwoord.charAt(i), 100 + (spacing * i), height - 100, 24);
            } else {
                SaxionApp.drawText("_", 100 + (spacing * i), height - 100, 24);
            }
        }
    }

    public void naamInvoeren(char letter, Player player) {
        // Onthoud het ingedrukte woord
        boolean isLetter = Character.isLetter(letter);
        if (isLetter || letter == ' ') {
            if (!player.name.isEmpty()) {
                letter = Character.toLowerCase(letter);
            }
            player.name = player.name + letter;
        }
    }

    public String removeLastCharFromString(String string) {
        return string.substring(0, string.length() - 1);
    }

    public void toonBericht(String tekst) {
        tijdelijkBericht = tekst;                           // Tekst
        berichtTijd = System.currentTimeMillis() + 2000;    // Laat de tekst 2 seconde staan
    }

    public void letterInvoeren() {
        SaxionApp.drawText("Voer een letter in: ", 20, 50, 30); // Tekst voor letter invoeren
        SaxionApp.drawText("Geraden letters: " + geradenLetters, 20, 100, 20); // Toon de geraden letters tekst
        //laat de Guillitine zien
        SaxionApp.drawImage("resources/Guillotine.jpg", 450, 100, 400, 500);
        //De mes van de guilltine is zichtbaar
        SaxionApp.drawImage("resources/mes/mes.png", 450, vallendeMes, 250, 200);
    }


    public void editPlayerName(int playerId) {
        for (Player player : players) {
            player.editable = player.id == playerId;
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

    public void registreerIngevoerdeLetters(char ingevoerdeLetter) {
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
                    goedGeradenLetters.add(ingevoerdeLetter);
                } else {
                    toonBericht("Letter wat je hebt ingevoerd is " + ingevoerdeLetter + " , dit is helaas fout!");
                    foutGeradenLetters.add(ingevoerdeLetter);
                    vallendeMes +=50;
                }
            }
        } else {
            // Kan alleen een letter invoeren, geen andere tekens of cijfers
            toonBericht("Voer alstublieft alleen een letter in.");
        }
    }

    public boolean gameOver() {
        return false;
    }

    public void csvReader() {
        reader.skipRow();
        reader.setSeparator(',');
        while (reader.loadRow()) {
            if (reader.getString(0).equals(difficultyToString())) {
                woordenlijst.add(reader.getString(1));
            }
        }
    }
}

