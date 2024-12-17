import nl.saxion.app.CsvReader;
import nl.saxion.app.SaxionApp;

import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.io.FileWriter;
import java.io.IOException;
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
    int vallendeMes = -105;
    String raadwoord;
    ArrayList<String> woordenlijstEasy = new ArrayList<>();
    ArrayList<String> woordenlijstNormal = new ArrayList<>();
    ArrayList<String> woordenlijstHard = new ArrayList<>();
    ArrayList<Button> buttonsStartScreen = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Character> geradenLetters = new ArrayList<>(); // Houdt bij welke letters al geraden zijn
    ArrayList<Character> goedGeradenLetters = new ArrayList<>();
    ArrayList<Character> foutGeradenLetters = new ArrayList<>();
    String tijdelijkBericht = "";       // Bericht dat op het scherm weergegeven moet worden
    long berichtTijd = 0;               // Timer voor het bericht, hoe lang het zichtbaar blijft
    Player player1;
    Player player2;
    boolean tweeSpelers = false;

    String CSVFile = "resources/FranseWoorden.csv";
    CsvReader reader = new CsvReader(CSVFile);

    String CSVscore = "resources/score.csv"; // Inlezen CSV bestand van score
    CsvReader readerscore = new CsvReader(CSVscore);

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 800, 800, 40);
    }

    @Override
    public void init() { // Voert eenmalig dingen uit aan het begin van het spel
        raadwoord = "Capybara";
        currentScreen = STARTSCREEN;
        difficulty = NORMAL;
        width = SaxionApp.getWidth();
        height = SaxionApp.getHeight();
        csvReader();

        player1 = new Player(1, "Speler 1", true);
        player2 = new Player(2, "Speler 2", false);
        players.add(player1);
        players.add(player2);

        buttonsStartScreen.add(new Button("1 SPELER", "1PLAYER", 30, 400, (height / 2) - 45, 60));
        buttonsStartScreen.add(new Button("2 SPELERS", "2PLAYER", (height / 2) + 15, 400, (height / 2) - 45, 60));
        buttonsStartScreen.add(new Button("EDIT", "EDITP1", 30, 290, 60, 30, Button.ALIGNLEFT, 20));
        buttonsStartScreen.add(new Button("EDIT", "EDITP2", (height / 2) + 30, 290, 60, 30, Button.ALIGNLEFT, 20, false));
        buttonsStartScreen.add(new Button("EASY", "EASY", 30, height - 180, height / 3 - 45, 60));
        buttonsStartScreen.add(new Button("NORMAL", "NORMAL", (height / 3) + 15, height - 180, height / 3 - 30, 60));
        buttonsStartScreen.add(new Button("HARD", "HARD", (height / 3 * 2) + 15, height - 180, height / 3 - 45, 60));
        buttonsStartScreen.add(new Button("START", "START", 30, height - 90, (height / 2) - 45, 60));
        buttonsStartScreen.add(new Button("LEADERBOARD", "LEADERBOARD", (height / 2) + 15, height - 90, (height / 2) - 45, 60));
        buttonsStartScreen.add(new Button("SAVE", "SAVE", 550, height - 780, height / 3 - 45, 60));
    }

    // Slaat invoer van de spelernaam op
    public void drawPlayerName() {
        SaxionApp.drawText(player1.name, 100, 300, 20);
        if (tweeSpelers) {
            SaxionApp.drawText(player2.name, (height / 2) + 100, 300, 20);
        }
    }

    @Override
    public void loop() {  // Blijft dingen herhalen gedurende het spel
        SaxionApp.clear();
        switch (currentScreen) {
            case STARTSCREEN:
                drawStartScreen();
                break;
            case GAMESCREEN:
                drawGameScreen();
                break;
            case ENDSCREEN:
                if (tweeSpelers) {
                    SaxionApp.resize(SaxionApp.getWidth() / 2, SaxionApp.getHeight());
                }
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
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_BACK_SPACE) {
                        for (Player player : players) {
                            if (player.editable && !player.name.isEmpty()) {
                                player.name = removeLastCharFromString(player.name);
                            }
                        }
                    }
                    for (Player player : players) {
                        if (player.editable) {
                            if (player.name.equals("Speler 1") || player.name.equals("Speler 2")) {
                                player.name = "";
                            }
                            naamInvoeren((char) keyboardEvent.getKeyCode(), player);
                        }
                    }
                    break;
                case GAMESCREEN:
                    if (gameLost()) {
                        if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                            currentScreen = ENDSCREEN;
                            scoreSysteem(); // Min punten gebaseerd op moeilijkheid
                        }
                    } else if (gameWon()) {
                        if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                            currentScreen = ENDSCREEN;
                            scoreSysteem(); // Plus punten gebaseerd op moeilijkheid
                        }
                    } else {
                        registreerIngevoerdeLetters((char) keyboardEvent.getKeyCode());
                    }

                    break;
                case ENDSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_R) {
                        currentScreen = STARTSCREEN;
                        resetGame();
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
                    case "1PLAYER":
                        if (tweeSpelers) {
                            tweeSpelers = false;
                            player2.active = false;
                            for (Button button2 : buttons) {
                                if (button2.action.equals("EDITP2")) { button2.active = false; }
                            }
                        }
                        break;
                    case "2PLAYER":
                        if (!tweeSpelers) {
                            tweeSpelers = true;
                            player2.active = true;
                            for (Button button2 : buttons) {
                                if (button2.action.equals("EDITP2")) { button2.active = true; }
                            }
                        }
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
                        raadwoord = kiesRandomWoord();
                        if (tweeSpelers) {
                            SaxionApp.resize(SaxionApp.getWidth() * 2, SaxionApp.getHeight());
                        }
                        currentScreen = GAMESCREEN;
                        break;
                    case "SAVE": // Handle the SAVE button
                        savePlayerScore();
                        toonBericht("Score is opgeslagen!");
                        break;
                    case "LEADERBOARD":
                        currentScreen = LEADERBOARDSCREEN;
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
        SaxionApp.drawImage("resources/bestorming.png",0,0,800,800);
        SaxionApp.drawText("Laat het hoofd niet rollen!", 30, 30, 24);
        SaxionApp.drawText("Druk op START om het spel te starten", 30, 60, 24);
        SaxionApp.drawText("Score: " + players.get(0).score, 30, 100, 24);
        SaxionApp.drawText("Difficulty: " + difficultyToString(), 30, height - 240, 24);
        drawPlayerName();

        for (Button button : buttonsStartScreen) {
            button.drawButton();
        }
    }

    public void drawGameScreen() {
        if (gameWon()||gameLost()){
            SaxionApp.drawImage("resources/Franchflagg.jpg", 0, 0, 800, 900);
        }
        else {
            SaxionApp.drawImage("resources/opstand.jpg",0,0,800,800);
        }
        SaxionApp.drawText("Raad het woord: " + raadwoord, 550, 20, 24);
        letterInvoeren();
        drawRaadWoord();
        for (int i = 0; i < players.size(); i++) {
            SaxionApp.drawText("Speler: " + players.get(i).name, 30 + (height * i), 20, 24);
            SaxionApp.drawText(players.get(i).name, (i * height) + 30, 60, 24);
            SaxionApp.drawText("Je score: " + players.get(i).score, 30 + (height * i), 50, 24);
        }
        if (gameWon()) {
            SaxionApp.drawText("Je hebt gewonnen!", 30, 300, 24);
            SaxionApp.drawText("Druk op spatie om door te gaan", 30, 330, 24);
        } else if (gameLost()) {
            SaxionApp.drawText("Je hebt verloren...", 30, 300, 24);
            SaxionApp.drawText("Druk op spatie om door te gaan", 30, 330, 24);
        }
    }

    public void drawEndScreen() {
        for (int i = 0; i < players.size(); i++) {
            SaxionApp.drawText("Speler: " + players.get(i).name, 30 + (height * i), 20, 24);
            SaxionApp.drawText(players.get(i).name, (i * height) + 30, 60, 24);
            SaxionApp.drawText("Je score: " + players.get(i).score, 30 + (height * i), 50, 24);
        }
        SaxionApp.drawText("Spel voorbij!", 20, 80, 24);
        SaxionApp.drawText("Druk op R om opnieuw te spelen.", 20, 120, 24);
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
        SaxionApp.drawText("Voer een letter in: ", 30, height - 160, 24); // Tekst voor letter invoeren
        SaxionApp.drawText("Geraden letters: " + geradenLetters, 30, 100, 24); // Toon de geraden letters tekst
        //laat de Guillotine zien
        SaxionApp.drawImage("resources/Guillotine.png", 100, -100, 800, 800);

        //De mes van de guilltine is zichtbaar
        SaxionApp.drawImage("resources/mes.png", 101, vallendeMes, 800, 680);
        //De mes van de guilltine is zichtbaar

        //  capybarahoofd is zichtbaar.
        SaxionApp.drawImage("resources/Capybarahoofd.png", 65, 80, 825, 775);

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
        // Als ingevoerde letter een letter is dan
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
                    // de mes valt 50 pixels naar beneden
                    vallendeMes += 300 / raadwoord.length() + 1;
                    // de mes mag niet lager zijn dan 195
                    if (vallendeMes > 195) {
                        // De mes blijft op de postie 195
                        vallendeMes = 195;
                    }
                }
            }
        } else {
            // Kan alleen een letter invoeren, geen andere tekens of cijfers
            toonBericht("Voer alstublieft alleen een letter in.");
        }
    }

    public boolean gameLost() {
        return foutGeradenLetters.size() == raadwoord.length();
    }

    public boolean gameWon() {
        for (char c : raadwoord.toLowerCase().toCharArray()) {
            if (!goedGeradenLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public void resetGame() {
        geradenLetters.clear();
        goedGeradenLetters.clear();
        foutGeradenLetters.clear();
        vallendeMes = -105;
        reader = new CsvReader(CSVFile);
    }

    public void csvReader() {
        reader.skipRow();
        reader.setSeparator(',');
        while (reader.loadRow()) {
            if (reader.getString(0).equals("Easy")) {
                woordenlijstEasy.add(reader.getString(1));
            } else if (reader.getString(0).equals("Normal")) {
                woordenlijstNormal.add(reader.getString(1));
            } else if (reader.getString(0).equals("Hard")) {
                woordenlijstHard.add(reader.getString(1));
            }
        }
    }

    public String kiesRandomWoord() {
        if (difficulty == EASY) {
            return woordenlijstEasy.get(SaxionApp.getRandomValueBetween(0, woordenlijstEasy.size() - 1));
        } else if (difficulty == NORMAL) {
            return woordenlijstNormal.get(SaxionApp.getRandomValueBetween(0, woordenlijstNormal.size() - 1));
        } else if (difficulty == HARD) {
            return woordenlijstHard.get(SaxionApp.getRandomValueBetween(0, woordenlijstHard.size() - 1));
        }
        return "Capybara";
    }

    public void scoreSysteem() {        // Geeft meer/minder punten gebaseerd op moeilijkheidsgraad
        if (gameLost() && difficulty == EASY || gameLost() && difficulty == NORMAL) {
            players.get(0).score -= 1;
        } else if (gameLost() && difficulty == HARD) {
            players.get(0).score -= 3;
        } else if (gameWon() && difficulty == EASY) {
            players.get(0).score++;
        } else if (gameWon() && difficulty == NORMAL) {
            players.get(0).score += 2;
        } else if (gameWon() && difficulty == HARD) {
            players.get(0).score += 3;
        }
    }

    public void savePlayerScore() {     // Slaat score van player op
        // Slaat de naam en de score van speler op in het bestand score.csv met komma etc
        try (FileWriter fw = new FileWriter("resources/score.csv", true)) {
            fw.write(" " + players.get(0).name + "," + players.get(0).score + "\n");
        } catch (IOException e) {       // Error wanneer het niet is gelukt
            SaxionApp.printLine("Error saving score");
        }
    }
}