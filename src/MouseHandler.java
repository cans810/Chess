import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {

    Chess game;
    int whiteEatCtr = 0;
    int blackEatCtr = 0;

    public MouseHandler(Chess game){
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // white
        if (game.whiteSelectedAndPlaying){
            for (int i=0;i<game.board.length;i++) {
                for (int j = 0; j < game.board[0].length; j++) {
                    for (int k = 0; k < game.white.length; k++) { {
                        if (game.white[k] != null){
                            if (game.white[k].selected) {
                                if (game.board[i][j].intersects(x, y, 1, 1)) {
                                    if (game.board[i][j].chessPieceContained != game.whitePrevSelected && game.white[k].board[i][j].canMove){
                                        if (game.board[i][j].chessPieceContained != null){
                                            game.white[k].lastX = game.white[k].posX;
                                            game.white[k].lastY = game.white[k].posY;

                                            game.whiteAte[whiteEatCtr++] = game.board[i][j].chessPieceContained;

                                            // find the black piece that is eaten
                                            for (int g=0;g<game.black.length;g++){
                                                if (game.black[g] == game.board[i][j].chessPieceContained) {
                                                    game.black[g] = null;
                                                    break;
                                                }
                                            }

                                            int willMoveToX = game.board[i][j].x;
                                            int willMoveToY = game.board[i][j].y;
                                            game.board[i][j].chessPieceContained = null;
                                            game.white[k].posX = willMoveToX;
                                            game.white[k].posY = willMoveToY;
                                        }
                                        else{
                                            game.white[k].lastX = game.white[k].posX;
                                            game.white[k].lastY = game.white[k].posY;

                                            game.white[k].posX = game.board[i][j].x;
                                            game.white[k].posY = game.board[i][j].y;
                                        }
                                        if (game.white[k].pawnFirstMove){
                                            game.white[k].selected = false;
                                            game.white[k].pawnFirstMove = false;
                                        }
                                        else{
                                            game.white[k].selected = false;
                                        }
                                        game.playerTurn = "black";
                                        game.previousTurn = "white";
                                        game.whitePrevSelected = game.white[k];
                                        game.whiteLastPiecePlayed = game.white[k];

                                        if (game.checkCheckWhite()){
                                            if (game.whiteLastPiecePlayed != null){
                                                game.whiteLastPiecePlayed.posX = game.whiteLastPiecePlayed.lastX;
                                                game.whiteLastPiecePlayed.posY = game.whiteLastPiecePlayed.lastY;
                                                game.playerTurn = "white";
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }
        }

        if (game.playerTurn.equals("white")){
            for (int i=0;i<game.white.length;i++){
                if (game.white[i] != null){
                    if (game.white[i].solidArea.intersects(x,y,1,1)){
                        for (int i1=0;i1<game.white.length;i1++){
                            if (game.white[i1] != null){
                                if (game.white[i1].selected){
                                    game.resetAllPieceIndexBoard();
                                    game.white[i1].selected = false;
                                    Chess.whiteSelectedPieceChange = true;
                                }
                            }
                        }
                        if (game.white[i] == game.whitePrevSelected){
                            game.resetAllPieceIndexBoard();
                            game.whiteSelectedAndPlaying = false;
                            game.white[i].selected = false;
                            game.whitePrevSelected = null;
                            Chess.whiteSelectedPieceChange = true;
                        }
                        else{
                            game.whiteSelectedAndPlaying = true;
                            game.white[i].selected = true;
                            game.whitePrevSelected = game.white[i];
                        }
                    }
                }
            }
        }

        //black
        if (game.blackSelectedAndPlaying){
            for (int i=0;i<game.board.length;i++) {
                for (int j = 0; j < game.board[0].length; j++) {
                    for (int k = 0; k < game.black.length; k++) { {
                        if (game.black[k] != null){
                            if (game.black[k].selected) {
                                if (game.board[i][j].intersects(x, y, 1, 1)) {
                                    if (game.board[i][j].chessPieceContained != game.blackPrevSelected && game.black[k].board[i][j].canMove){
                                        if (game.board[i][j].chessPieceContained != null){
                                            game.black[k].lastX = game.black[k].posX;
                                            game.black[k].lastY = game.black[k].posY;

                                            game.blackAte[blackEatCtr++] = game.board[i][j].chessPieceContained;

                                            // find the black piece that is eaten
                                            for (int g=0;g<game.white.length;g++){
                                                if (game.white[g] == game.board[i][j].chessPieceContained) {
                                                    game.white[g] = null;
                                                    break;
                                                }
                                            }

                                            int willMoveToX = game.board[i][j].x;
                                            int willMoveToY = game.board[i][j].y;
                                            game.board[i][j].chessPieceContained = null;
                                            game.black[k].posX = willMoveToX;
                                            game.black[k].posY = willMoveToY;
                                        }
                                        else{
                                            game.black[k].lastX = game.black[k].posX;
                                            game.black[k].lastY = game.black[k].posY;

                                            game.black[k].posX = game.board[i][j].x;
                                            game.black[k].posY = game.board[i][j].y;
                                        }
                                        if (game.black[k].pawnFirstMove){
                                            game.black[k].selected = false;
                                            game.black[k].pawnFirstMove = false;
                                        }
                                        else{
                                            game.black[k].selected = false;
                                        }
                                        game.playerTurn = "white";
                                        game.previousTurn = "black";
                                        game.blackPrevSelected = game.black[k];
                                        game.blackLastPiecePlayed = game.black[k];
                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }
        }

        if (game.playerTurn.equals("black")){
            for (int i=0;i<game.black.length;i++){
                if (game.black[i] != null){
                    if (game.black[i].solidArea.intersects(x,y,1,1)){
                        for (int i1=0;i1<game.black.length;i1++){
                            if (game.black[i1] != null){
                                if (game.black[i1].selected){
                                    game.resetAllPieceIndexBoard();
                                    game.black[i1].selected = false;
                                    Chess.blackSelectedPieceChange = true;
                                }
                            }
                        }
                        if (game.black[i] == game.blackPrevSelected){
                            game.resetAllPieceIndexBoard();
                            game.blackSelectedAndPlaying = false;
                            game.black[i].selected = false;
                            game.blackPrevSelected = null;
                            Chess.blackSelectedPieceChange = true;
                        }
                        else{
                            game.blackSelectedAndPlaying = true;
                            game.black[i].selected = true;
                            game.blackPrevSelected = game.black[i];
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}
