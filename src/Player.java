public class Player {

    int id;
    String name;
    boolean editable;
    int score;

    Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.editable = false;
        this.score = 0;
    }

}
