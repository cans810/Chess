import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Chessman {
    Chess game;

    String type;
    int posX;
    int posY;
    BufferedImage image;
    int width;
    int height;

    public boolean selected = false;

    BoardRect[][] board;

    int lastX;
    int lastY;

    public Rectangle solidArea;

    public boolean pawnFirstMove = false;

    public Chessman(Chess game){
        this.game = game;
    }

    public void setAttributes(){
        if (type.equals("rookW")){
            image = setup("rookW",120,120);
        }
        if (type.equals("knightW")){
            image = setup("knightW",120,120);
        }
        if (type.equals("kingW")){
            image = setup("kingW",120,120);
        }
        if (type.equals("queenW")){
            image = setup("queenW",120,120);
        }
        if (type.equals("pawnW")){
            image = setup("pawnW",120,120);
        }
        if (type.equals("bishopW")){
            image = setup("bishopW",120,120);
        }
        if (type.equals("rookB")){
            image = setup("rookB",120,120);
        }
        if (type.equals("knightB")){
            image = setup("knightB",120,120);
        }
        if (type.equals("kingB")){
            image = setup("kingB",120,120);
        }
        if (type.equals("queenB")){
            image = setup("queenB",120,120);
        }
        if (type.equals("pawnB")){
            image = setup("pawnB",120,120);
        }
        if (type.equals("bishopB")){
            image = setup("bishopB",120,120);
        }
    }

    /*public void update(BoardRect[][] board){
        this.board = board;
    }*/

    public BufferedImage setup(String path,int width,int height){
        BufferedImage image = null;

        this.width = width;
        this.height = height;

        try{
            image = ImageIO.read(getClass().getResourceAsStream(path +".png"));
            image = game.scaleImage(image,width,height);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(image,posX,posY,width,height,null);
        /*g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(10));
        g2.draw(solidArea);*/

        if (selected){
            g2.setColor(Color.green);
            g2.setStroke(new BasicStroke(10));
            g2.drawRoundRect(posX,posY,width,height,width,height);

            for (int i=0;i<8;i++){
                for (int j=0;j<8;j++){
                    if (board[i][j].canMove){
                        g2.setColor(Color.GREEN);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                        g2.fillRect(i*120,j*120,120,120);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Chessman{" +
                "posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
