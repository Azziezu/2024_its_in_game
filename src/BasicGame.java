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
    int vallendeMes = -105;
    String raadwoord;
    ArrayList<String> woordenlijstEasy = new ArrayList<>();
    ArrayList<String> woordenlijstNormal = new ArrayList<>();
    ArrayList<String> woordenlijstHard = new ArrayList<>();
    ArrayList<Button> buttonsStartScreen = new ArrayList<>();
    ArrayList<Button> buttonsLeaderBordScreen = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Character> geradenLetters = new ArrayList<>();
    ArrayList<Character> goedGeradenLetters = new ArrayList<>();
    ArrayList<Character> foutGeradenLetters = new ArrayList<>();
    String tijdelijkBericht = "";
    long berichtTijd = 0;
    Player player1;
    Player player2;
    boolean tweeSpelers = false;

    String CSVFile = "BasicGame/resources/FranseWoorden.csv";
    CsvReader reader = new CsvReader(CSVFile);

    String CSVscore = "BasicGame/resources/score.csv";
    CsvReader readerscore = new CsvReader(CSVscore);

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 800, 800, 40);
    }

    @Override
    public void init() {
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

        // Voeg knoppen toe aan startscreen
        buttonsStartScreen.add(new Button("EDIT", "EDITP1", 30, 290, 60, 30, Button.ALIGNLEFT, 20));
        buttonsStartScreen.add(new Button("EASY", "EASY", 30, height - 180, height / 3 - 45, 60));
        buttonsStartScreen.add(new Button("NORMAL", "NORMAL", (height / 3) + 15, height - 180, height / 3 - 30, 60));
        buttonsStartScreen.add(new Button("HARD", "HARD", (height / 3 * 2) + 15, height - 180, height / 3 - 45, 60));
        buttonsStartScreen.add(new Button("START", "START", 30, height - 90, (height / 2) - 45, 60));
        buttonsStartScreen.add(new Button("LEADERBOARD", "LEADERBOARD", (height / 2) + 15, height - 90, (height / 2) - 45, 60));
        buttonsStartScreen.add(new Button("SAVE EN SLUIT", "SAVE_AND_EXIT", 30, 500, (height / 2) - 45, 60));

        // Knop voor het leaderboard-scherm
        buttonsLeaderBordScreen.add(new Button("Back to menu", "Back to menu", 20, 20, 280, 80));
    }

    @Override
    public void loop() {
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
                toonTop5Scores();
                break;
            default:
                break;
        }

        // Laat tijdelijk bericht zien
        if (!tijdelijkBericht.isEmpty() && System.currentTimeMillis() < berichtTijd) {
            SaxionApp.drawText(tijdelijkBericht, 300, 50, 25);
        } else {
            tijdelijkBericht = "";
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
                            // Als de naam nog 'Speler 1' of 'Speler 2' is, leegmaken zodat je direct kunt typen
                            if (player.name.equals("Speler 1") || player.name.equals("Speler 2")) {
                                player.name = "";
                            }
                            naamInvoeren((char) keyboardEvent.getKeyCode(), player);
                        }
                    }
                    break;

                case GAMESCREEN:
                    if (gameLost() || gameWon()) {
                        if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                            currentScreen = ENDSCREEN;
                            scoreSysteem();
                        }
                    } else {
                        registreerIngevoerdeLetters((char) keyboardEvent.getKeyCode());
                    }
                    break;

                case ENDSCREEN:
                    if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                        currentScreen = STARTSCREEN;
                        resetGame();
                    }
                    break;

                case LEADERBOARDSCREEN:
                    // Geen speciale keyboard-events hier, tenzij je er iets mee wilt doen
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) {
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
                    buttonEvent(buttonsLeaderBordScreen);
                    break;
                default:
                    break;
            }
        }
    }

    public void buttonEvent(ArrayList<Button> buttons) {
        for (Button button : buttons) {
            if (isValidButtonClick(button)) {
                SaxionApp.playSound("BasicGame/resources/Mouse Click.wav");
                switch (button.action) {
                    case "1PLAYER":
                        if (tweeSpelers) {
                            tweeSpelers = false;
                            player2.active = false;
                            for (Button button2 : buttons) {
                                if (button2.action.equals("EDITP2")) {
                                    button2.active = false;
                                }
                            }
                        }
                        break;

                    case "2PLAYER":
                        if (!tweeSpelers) {
                            tweeSpelers = true;
                            player2.active = true;
                            for (Button button2 : buttons) {
                                if (button2.action.equals("EDITP2")) {
                                    button2.active = true;
                                }
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

                    case "SAVE_AND_EXIT": // Handle the SAVE button
                        savenPlayerScore();
                        sorterenScore();
                        toonBericht("Score is opgeslagen! Spel sluit nu.");
                        System.exit(0);
                        break;

                    case "LEADERBOARD":
                        currentScreen = LEADERBOARDSCREEN;
                        buttonEvent(buttonsLeaderBordScreen);
                        break;

                    case "Back to menu":
                        currentScreen = STARTSCREEN;
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
        SaxionApp.drawImage("BasicGame/resources/bestorming.png", 0, 0, 800, 800);
        SaxionApp.drawText("Laat het hoofd niet rollen!", 30, 30, 24);
        SaxionApp.drawText("Druk op START om het spel te starten", 30, 60, 24);
        SaxionApp.drawText("Score: " + players.get(0).score, 30, 100, 24);
        SaxionApp.drawText("Difficulty: " + difficultyToString(), 30, height - 240, 24);
        SaxionApp.drawText(player1.name, 100, 300, 20);

        for (Button button : buttonsStartScreen) {
            button.drawButton();
        }
    }

    public void drawGameScreen() {
        SaxionApp.drawImage("BasicGame/resources/opstand.jpg", 0, 0, 800, 800);
        if (gameWon()) {
            SaxionApp.drawImage("BasicGame/resources/Franchflagg.jpg", 0, 0, 800, 900);
        } else if (gameLost()) {
            SaxionApp.drawImage("BasicGame/resources/Bloeddigeguillitine.png", 160, 60, 700, 700);
            SaxionApp.drawImage("BasicGame/resources/doodcapybarra.png", 340, 470, 230, 180);
            SaxionApp.drawImage("BasicGame/resources/mes.png", 76, 58, 850, 800);
        } else {
            SaxionApp.drawImage("BasicGame/resources/Guillotine.png", 100, -100, 800, 800);
            SaxionApp.drawImage("BasicGame/resources/Capybarahoofd.png", 50, 80, 850, 775);
            SaxionApp.drawImage("BasicGame/resources/mes.png", 101, vallendeMes, 798, 600);
        }

        SaxionApp.drawText("Voer een letter in: ", 30, height - 160, 24);
        SaxionApp.drawText("Geraden letters: " + geradenLetters, 30, 100, 24);

        drawRaadWoord();
        for (int i = 0; i < players.size(); i++) {
            SaxionApp.drawText("Speler: " + players.get(i).name, 30 + (height * i), 20, 24);
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
            SaxionApp.drawText("Je score: " + players.get(i).score, 30 + (height * i), 50, 24);
        }
        SaxionApp.drawText("Spel voorbij!", 30, 80, 24);
        SaxionApp.drawText("Druk op spatie om opnieuw te spelen.", 30, 120, 24);
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
        tijdelijkBericht = tekst;
        berichtTijd = System.currentTimeMillis() + 2000;
    }

    public void editPlayerName(int playerId) {
        for (Player player : players) {
            player.editable = (player.id == playerId);
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
        if (Character.isLetter(ingevoerdeLetter)) {
            ingevoerdeLetter = Character.toLowerCase(ingevoerdeLetter);
            if (geradenLetters.contains(ingevoerdeLetter)) {
                toonBericht("Je hebt de letter " + ingevoerdeLetter + " al geraden!");
            } else {
                geradenLetters.add(ingevoerdeLetter);
                if (raadwoord.toLowerCase().contains(String.valueOf(ingevoerdeLetter))) {
                    toonBericht("Letter " + ingevoerdeLetter + " is correct!");
                    goedGeradenLetters.add(ingevoerdeLetter);
                } else {
                    toonBericht("Letter " + ingevoerdeLetter + " is fout!");
                    foutGeradenLetters.add(ingevoerdeLetter);
                    vallendeMes += 300 / raadwoord.length() + 1;
                    SaxionApp.playSound("BasicGame/resources/Knife_attack-2.wav");
                    if (vallendeMes > 195) {
                        vallendeMes = 195;
                        SaxionApp.playSound("BasicGame/resources/Thump-Body-Hit_TTX042901-2.wav");
                    }
                }
            }
        } else {
            toonBericht("Voer alleen letters in.");
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
        reader = new CsvReader(CSVFile); // Wordt opnieuw geïnitialiseerd
    }

    // Leest Franse woorden voor 'EASY', 'NORMAL' en 'HARD' in
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

    public void scoreSysteem() {
        // Afhankelijk van de difficulty punten erbij of eraf
        if (gameLost() && (difficulty == EASY || difficulty == NORMAL)) {
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
        // Score mag niet onder 0 komen
        if (players.get(0).score < 0) {
            players.get(0).score = 0;
        }
    }

    // Het inladen en inlezen van alles uit het csv bestand
    public ArrayList<Player> ladenScores(){
        ArrayList<Player> scoreLijst = new ArrayList<>();

        readerscore = new CsvReader(CSVscore);
        readerscore.setSeparator(',');
        readerscore.skipRow();

        while (readerscore.loadRow()) {
            String naam = readerscore.getString(0).trim();
            int score = Integer.parseInt(readerscore.getString(1).trim());
            Player speler = new Player(0, naam, false);
            speler.score = score;
            scoreLijst.add(speler);
        }

        return scoreLijst;
    }

    // Sorteert scores en slaat deze op
    public void sorteerScoreEnSlaOp(ArrayList<Player> scoreLijst) {
        scoreLijst.sort((p1, p2) -> p2.score - p1.score);

        try (FileWriter fw = new FileWriter(CSVscore, false)) {
            // Zorg voor de juiste header
            fw.write("name,score\n");
            for (Player speler : scoreLijst) {
                fw.write(speler.name + "," + speler.score + "\n");
            }
        } catch (IOException e) {
            SaxionApp.printLine("Niet gelukt");
        }
    }

    // Slaat op en zet scores gesorteerd terug
    public void savenPlayerScore() {
        try {
            ArrayList<Player> alleScores = ladenScores();
            // Speler toevoegen aan lijst
            alleScores.add(players.get(0));
            // Zet de scores weer gesorteerd terug
            sorteerScoreEnSlaOp(alleScores);
        } catch(Exception e) {
            SaxionApp.printLine("Kan niet opslaan");
        }
    }

    // Sorteer de score en schrijf opnieuw naar CSV
    public void sorterenScore() {
        ArrayList<Player> scoreLijst = ladenScores();
        sorteerScoreEnSlaOp(scoreLijst);
    }


    public void toonTop5Scores() {
        // Lijst om spelers in op te slaan
        ArrayList<Player> scoreLijst = ladenScores();
        // Sorteer lijst op basis van hoog naar laag
        scoreLijst.sort((p1, p2) -> p2.score - p1.score);

        SaxionApp.clear();
        SaxionApp.drawText("Top 5 scores!", 350, 60, 40);

        // Math.min vergelijkt twee waarden en geeft de kleinste van de twee terug.
        // Dus als scoreLijst.size() groter is dan 5, dan krijgen we hier 5 terug.
        int aantalScores = Math.min(scoreLijst.size(), 5);
        for (int i = 0; i < aantalScores; i++) {
            Player speler = scoreLijst.get(i);
            // Laat de ranking, naam en score zien. i + 1 zodat hij niet de header meepakt
            SaxionApp.drawText((i + 1) + ". " + speler.name + " - " + speler.score, 350, 150 + (i * 35), 30);
        }

        // Teken de 'Back to menu'-knop
        for (Button button : buttonsLeaderBordScreen) {
            button.drawButton();
        }
    }
}
