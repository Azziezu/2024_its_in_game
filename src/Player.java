public class Player {

    int id;
    String name;
    boolean editable;
    int score;
    boolean active;

    Player(int id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.editable = false;
        this.score = 0;
        this.active = active;
    }

}
