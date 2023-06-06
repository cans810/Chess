import java.awt.*;

public class BoardRect extends Rectangle {
    Chess game;
    public Chessman chessPieceContained = null;
    public boolean canMove;

    public BoardRect(Chess game,int x,int y,int width,int height){
        this.game = game;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String containsPiece(){
        if (chessPieceContained != null){
            return "true";
        }
        return "false";
    }
}
