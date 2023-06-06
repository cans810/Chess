import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chess extends JPanel implements Runnable{

    public int tileSize = 120;

    public final int screenWidth = 960;
    public final int screenHeight = 960;

    public final int chessBoardRow = 8;
    public final int chessBoardCol = 8;

    public Chessman[] white;
    public Chessman whitePrevSelected;
    public Chessman whiteLastPiecePlayed;
    public Chessman[] whiteAte;
    public boolean whiteSelectedAndPlaying;
    public Chessman[] black;
    public Chessman blackPrevSelected;
    public Chessman blackLastPiecePlayed;
    public Chessman[] blackAte;
    public boolean blackSelectedAndPlaying;

    public boolean whiteCheck = false;
    public boolean blackCheck = false;

    public static boolean whiteSelectedPieceChange = false;
    public static boolean blackSelectedPieceChange = false;

    public BoardRect[][] board;

    public final int FPS = 300;

    Thread gameThread;
    MouseHandler mouseHandler = new MouseHandler(this);

    public String playerTurn = "white";
    public String previousTurn = "";

    public Chess(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setDoubleBuffered(true); // enabling this can improve games rendering performance
        this.setFocusable(true);
        this.addMouseListener(mouseHandler);
        setupGame();
    }

    public void setupGame(){
        whiteAte = new Chessman[16];
        blackAte = new Chessman[16];

        board = new BoardRect[8][8];

        for (int i=0;i<chessBoardCol;i++){
            for (int j=0;j<chessBoardRow;j++){
                board[i][j] = new BoardRect(this,i*tileSize,j*tileSize,tileSize,tileSize);
            }
        }

        white = new Chessman[16];

        white[0] = new Chessman(this);
        white[0].type = "rookW";
        white[0].setAttributes();
        white[0].board = new BoardRect[8][8];

        white[1] = new Chessman(this);
        white[1].type = "knightW";
        white[1].setAttributes();
        white[1].board = new BoardRect[8][8];

        white[2] = new Chessman(this);
        white[2].type = "bishopW";
        white[2].setAttributes();
        white[2].board = new BoardRect[8][8];

        white[3] = new Chessman(this);
        white[3].type = "kingW";
        white[3].setAttributes();
        white[3].board = new BoardRect[8][8];

        white[4] = new Chessman(this);
        white[4].type = "queenW";
        white[4].setAttributes();
        white[4].board = new BoardRect[8][8];

        white[5] = new Chessman(this);
        white[5].type = "bishopW";
        white[5].setAttributes();
        white[5].board = new BoardRect[8][8];

        white[6] = new Chessman(this);
        white[6].type = "knightW";
        white[6].setAttributes();
        white[6].board = new BoardRect[8][8];

        white[7] = new Chessman(this);
        white[7].type = "rookW";
        white[7].setAttributes();
        white[7].board = new BoardRect[8][8];

        for (int i=8;i<16;i++){
            white[i] = new Chessman(this);
            white[i].type = "pawnW";
            white[i].setAttributes();
            white[i].board = new BoardRect[8][8];
            white[i].pawnFirstMove = true;
        }


        for (int k=0;k<white.length;k++){

            BoardRect[][] copyBoard = new BoardRect[board.length][];
            for(int i = 0; i < board.length; i++)
            {
                copyBoard[i] = new BoardRect[board[i].length];
                for (int j = 0; j < board[i].length; j++)
                {
                    copyBoard[i][j] = board[i][j];
                }
            }

            white[k].board = copyBoard;
        }

        white = setupPiecesPos(white,"up");

        black = new Chessman[16];

        black[0] = new Chessman(this);
        black[0].type = "rookB";
        black[0].setAttributes();
        black[0].board = new BoardRect[8][8];

        black[1] = new Chessman(this);
        black[1].type = "knightB";
        black[1].setAttributes();
        black[1].board = new BoardRect[8][8];

        black[2] = new Chessman(this);
        black[2].type = "bishopB";
        black[2].setAttributes();
        black[2].board = new BoardRect[8][8];

        black[3] = new Chessman(this);
        black[3].type = "kingB";
        black[3].setAttributes();
        black[3].board = new BoardRect[8][8];

        black[4] = new Chessman(this);
        black[4].type = "queenB";
        black[4].setAttributes();
        black[4].board = new BoardRect[8][8];;

        black[5] = new Chessman(this);
        black[5].type = "bishopB";
        black[5].setAttributes();
        black[5].board = new BoardRect[8][8];

        black[6] = new Chessman(this);
        black[6].type = "knightB";
        black[6].setAttributes();
        black[6].board = new BoardRect[8][8];

        black[7] = new Chessman(this);
        black[7].type = "rookB";
        black[7].setAttributes();
        black[7].board = new BoardRect[8][8];

        for (int i=8;i<16;i++){
            black[i] = new Chessman(this);
            black[i].type = "pawnB";
            black[i].setAttributes();
            black[i].board = new BoardRect[8][8];
            black[i].pawnFirstMove = true;
        }

        for (int k=0;k<black.length;k++){

            BoardRect[][] copyBoard = new BoardRect[board.length][];
            for(int i = 0; i < board.length; i++)
            {
                copyBoard[i] = new BoardRect[board[i].length];
                for (int j = 0; j < board[i].length; j++)
                {
                    copyBoard[i][j] = board[i][j];
                }
            }

            black[k].board = copyBoard;
        }

        black = setupPiecesPos(black,"down");
    }

    public Chessman[] setupPiecesPos(Chessman[] pieces,String side){
        if (side.equals("up")){
            int ctr = 0;
            for (int i=0;i<pieces.length;i++){

                if (pieces[i] != null){
                    if (pieces[i].type.contains("pawn")){
                        pieces[i].posX = ctr*120;
                        pieces[i].posY = 120;
                        pieces[i].solidArea = new Rectangle(pieces[i].posX,pieces[i].posY,pieces[i].width,pieces[i].height);
                        ctr++;
                    }
                    else{
                        pieces[i].posX = i*120;
                        pieces[i].posY = 0;
                        pieces[i].solidArea = new Rectangle(pieces[i].posX,pieces[i].posY,pieces[i].width,pieces[i].height);
                    }
                }
            }
        }

        if (side.equals("down")){
            int ctr = 0;
            for (int i=0;i<pieces.length;i++){

                if (pieces[i] != null)
                {
                    if (pieces[i].type.contains("pawn")){
                        pieces[i].posX = ctr*120;
                        pieces[i].posY = 720;
                        pieces[i].solidArea = new Rectangle(pieces[i].posX,pieces[i].posY,pieces[i].width,pieces[i].height);
                        ctr++;
                    }
                    else{
                        pieces[i].posX = i*120;
                        pieces[i].posY = 840;
                        pieces[i].solidArea = new Rectangle(pieces[i].posX,pieces[i].posY,pieces[i].width,pieces[i].height);
                    }
                }
            }
        }
        return pieces;
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start(); // this calls method 'run'
    }

    public void run(){
        while(true){
            update();

            repaint();
        }
    }

    public void update(){
        /*whiteCheck = checkCheckWhite();

        blackCheck = checkCheckBlack();*/

        // after the move, if there is a checking situation, reset the piece position to previous pos and give playerTurn to same side
        /*if (whiteCheck){
            if (whiteLastPiecePlayed != null){
                whiteLastPiecePlayed.posX = whiteLastPiecePlayed.lastX;
                whiteLastPiecePlayed.posY = whiteLastPiecePlayed.lastY;
                playerTurn = "white";
            }
        }
        else{
            whiteLastPiecePlayed = null;
        }*/

        // after the move, if there is a checking situation, reset the piece position to previous pos and give playerTurn to same side
        if (blackCheck){
            if (blackLastPiecePlayed != null){
                blackLastPiecePlayed.posX = blackLastPiecePlayed.lastX;
                blackLastPiecePlayed.posY = blackLastPiecePlayed.lastY;
                playerTurn = "black";
            }
        }
        else{
            blackLastPiecePlayed = null;
        }


        if (playerTurn.equals("white")){
            calculateWhiteMovement();

            if (whiteSelectedPieceChange){
                resetAllPieceIndexBoard();
                whiteSelectedPieceChange = false;
            }
        }

        else if (playerTurn.equals("black")){
            calculateBlackMovement();

            if (blackSelectedPieceChange){
                resetAllPieceIndexBoard();
                blackSelectedPieceChange = false;
            }
        }

        if (previousTurn.equals("white")){
            resetAllPieceIndexBoard();
            whiteSelectedPieceChange = false;
            previousTurn = "";
        }

        else if (previousTurn.equals("black")){
            resetAllPieceIndexBoard();
            blackSelectedPieceChange = false;
            previousTurn = "";
        }

        // update board / piece containment
        for (int i=0;i<board.length;i++){
            for (int j=0;j<board[0].length;j++){

                for (int k=0;k<white.length;k++){
                    if (white[k] != null){
                        if (white[k].solidArea.intersects(board[i][j])){
                            board[i][j].chessPieceContained = white[k];
                        }
                        else if (!white[k].solidArea.intersects(board[i][j])){
                            board[i][j].chessPieceContained = null;
                        }
                    }
                }

            }
        }

        for (int i=0;i<board.length;i++){
            for (int j=0;j<board[0].length;j++){

                for (int k=0;k<black.length;k++){
                    if (black[k] != null){
                        if (black[k].solidArea.intersects(board[i][j])){
                            board[i][j].chessPieceContained = black[k];
                        }
                        else if (!black[k].solidArea.intersects(board[i][j])){
                            board[i][j].chessPieceContained = null;
                        }
                    }
                }

            }
        }

        // update solid area of the white pieces
        for (int i=0;i<white.length;i++){
            if (white[i] != null){
                white[i].solidArea.x = white[i].posX;
                white[i].solidArea.y = white[i].posY;
            }
        }

        for (int i=0;i<black.length;i++){
            if (black[i] != null) {
                black[i].solidArea.x = black[i].posX;
                black[i].solidArea.y = black[i].posY;
            }
        }

        // update board piece containment (this is for at the start of the game)
        for (int k=0;k<white.length;k++){
            for (int i=0;i<chessBoardCol;i++){
                for (int j=0;j<chessBoardRow;j++){
                    if (white[k] != null){
                        if (white[k].solidArea.intersects(board[i][j])){
                            board[i][j].chessPieceContained = white[k];
                        }
                    }
                }
            }
        }

        for (int k=0;k<black.length;k++){
            for (int i=0;i<chessBoardCol;i++){
                for (int j=0;j<chessBoardRow;j++){
                    if (black[k] != null){
                        if (black[k].solidArea.intersects(board[i][j])){
                            board[i][j].chessPieceContained = black[k];
                        }
                    }
                }
            }
        }
    }

    public void resetAllPieceIndexBoard(){
        for (Chessman white:white){
            if (white != null){
                for (int i=0;i<white.board.length;i++){
                    for (int j=0;j<white.board[0].length;j++){
                        white.board[i][j].canMove = false;
                    }
                }
            }
        }
        for (Chessman black:black){
            if (black != null){
                for (int i=0;i<black.board.length;i++){
                    for (int j=0;j<black.board[0].length;j++){
                        black.board[i][j].canMove = false;
                    }
                }
            }
        }
    }

    public boolean checkCheckWhite(){

        for (int i=0;i<black.length;i++) {
            if (black[i] != null) {

                // for rook check
                if (black[i].type.equals("rookB")){
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardX = board[j][k].x;

                            int boardYUp = board[j][k].y;
                            int boardYDown = board[j][k].y;

                            int indexY = 0;

                            for (int a=0;a<board.length;a++){
                                for (int b=0;b<board[0].length;b++){
                                    if (black[i] == board[a][b].chessPieceContained){
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (boardX == black[i].posX){
                                for (int i1=indexY;i1<=7;i1++){
                                    if (board[j][i1].y == boardYUp){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i] && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYUp = boardYUp + 120;
                                }
                                for (int i1=indexY;i1>=0;i1--){
                                    if (board[j][i1].y == boardYDown){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i] && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYDown = boardYDown - 120;
                                }
                            }
                        }
                    }

                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardY = board[j][k].y;

                            int boardXLeft = board[j][k].x;
                            int boardXRight = board[j][k].x;

                            int indexX = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        break;
                                    }
                                }
                            }

                            if (boardY == black[i].posY){
                                for (int i1=indexX;i1>=0;i1--){
                                    if (board[i1][k].x == boardXLeft){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i] && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXLeft = boardXLeft - 120;
                                }

                                for (int i1=indexX;i1<=7;i1++){
                                    if (board[i1][k].x == boardXRight){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i] && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXRight = boardXRight + 120;
                                }
                            }
                        }
                    }
                }

                // for bishop check
                if (black[i].type.equals("bishopB")){

                    // for topLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained != null){
                                            if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Bishop Check!");
                                                return true;
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for topRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained != null){
                                            if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Bishop Check!");
                                                return true;
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for downRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained != null){
                                            if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Bishop Check!");
                                                return true;
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }

                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }

                    // for downLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            System.err.println("Bishop Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }
                }

                // for queen check

                if (black[i].type.equals("queenB")){

                    // rook-like movement
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardX = board[j][k].x;

                            int boardYUp = board[j][k].y;
                            int boardYDown = board[j][k].y;

                            int indexY = 0;

                            for (int a=0;a<board.length;a++){
                                for (int b=0;b<board[0].length;b++){
                                    if (black[i] == board[a][b].chessPieceContained){
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (boardX == black[i].posX){
                                for (int i1=indexY;i1<=7;i1++){
                                    if (board[j][i1].y == boardYUp){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i] && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYUp = boardYUp + 120;
                                }
                                for (int i1=indexY;i1>=0;i1--){
                                    if (board[j][i1].y == boardYDown){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != black[i] && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYDown = boardYDown - 120;
                                }
                            }
                        }
                    }

                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardY = board[j][k].y;

                            int boardXLeft = board[j][k].x;
                            int boardXRight = board[j][k].x;

                            int indexX = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        break;
                                    }
                                }
                            }

                            if (boardY == black[i].posY){
                                for (int i1=indexX;i1>=0;i1--){
                                    if (board[i1][k].x == boardXLeft){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i] && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXLeft = boardXLeft - 120;
                                }

                                for (int i1=indexX;i1<=7;i1++){
                                    if (board[i1][k].x == boardXRight){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != black[i] && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXRight = boardXRight + 120;
                                }
                            }
                        }
                    }

                    // for bishop-like movement

                    // for topLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for topRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for downRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained != null){
                                            if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                break;
                                            }
                                        }
                                    }

                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }

                    // for downLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != black[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != black[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                            break;
                                        }
                                    }
                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }
                }
                if (black[i].type.equals("knightB") && (black[i] != null)){

                    // topLeft1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-2 < 0) && !(indexY-1 < 0)){
                                if (board[indexX-2][indexY-1].chessPieceContained != null && board[indexX-2][indexY-1].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-2][indexY-1].chessPieceContained != null && board[indexX-2][indexY-1].chessPieceContained != black[i] && !board[indexX-2][indexY-1].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // topLeft2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-1 < 0) && !(indexY-2 < 0)){
                                if (board[indexX-1][indexY-2].chessPieceContained != null && board[indexX-1][indexY-2].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-1][indexY-2].chessPieceContained != null && board[indexX-1][indexY-2].chessPieceContained != black[i] && !board[indexX-1][indexY-2].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // topRight1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+2 > 7) && !(indexY-1 < 0)){
                                if (board[indexX+2][indexY-1].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+2][indexY-1].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained != black[i] && !board[indexX+2][indexY-1].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // topRight2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+1 > 7) && !(indexY-2 < 0)){
                                if (board[indexX+1][indexY-2].chessPieceContained != null && board[indexX+1][indexY-2].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+1][indexY-2].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained != black[i] && !board[indexX+1][indexY-2].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // downLeft1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-2 < 0) && !(indexY+1 > 7)){
                                if (board[indexX-2][indexY+1].chessPieceContained != null && board[indexX-2][indexY+1].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-2][indexY+1].chessPieceContained != null && board[indexX-2][indexY+1].chessPieceContained != black[i] && !board[indexX-2][indexY+1].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // downLeft2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-1 < 0) && !(indexY+2 > 7)){
                                if (board[indexX-1][indexY+2].chessPieceContained != null && board[indexX-1][indexY+2].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-1][indexY+2].chessPieceContained != null && board[indexX-1][indexY+2].chessPieceContained != black[i] && !board[indexX-1][indexY+2].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // downRight1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+2 > 7) && !(indexY+1 > 7)){
                                if (board[indexX+2][indexY+1].chessPieceContained != null && board[indexX+2][indexY+1].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+2][indexY+1].chessPieceContained != null && board[indexX+2][indexY+1].chessPieceContained != black[i] && !board[indexX+2][indexY+1].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }

                    // downRight2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (black[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+1 > 7) && !(indexY+2 > 7)){
                                if (board[indexX+1][indexY+2].chessPieceContained != null && board[indexX+1][indexY+2].chessPieceContained.type.equals("kingW")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+1][indexY+2].chessPieceContained != null && board[indexX+1][indexY+2].chessPieceContained != black[i] && !board[indexX+1][indexY+2].chessPieceContained.type.equals("kingW")){
                                    break;
                                }
                            }
                        }
                    }
                }

                if (black[i].type.equals("pawnB")){

                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardX = board[j][k].x;
                            int boardY = board[j][k].y;

                            if (black[i].pawnFirstMove){
                                if (boardX+120 == black[i].posX && boardY+120 == black[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingW")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != black[i] && !board[j][k].chessPieceContained.type.equals("kingW")){
                                        break;
                                    }
                                }
                                if (boardX-120 == black[i].posX && boardY+120 == black[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingW")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != black[i] && !board[j][k].chessPieceContained.type.equals("kingW")){
                                        break;
                                    }
                                }
                            }

                            else{
                                if (boardX+120 == black[i].posX && boardY+120 == black[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingW")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != black[i] && !board[j][k].chessPieceContained.type.equals("kingW")){
                                        break;
                                    }
                                }
                                if (boardX-120 == black[i].posX && boardY+120 == black[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingW")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != black[i] && !board[j][k].chessPieceContained.type.equals("kingW")){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean checkCheckBlack(){

        for (int i=0;i<white.length;i++) {
            if (white[i] != null) {

                // for rook check
                if (white[i].type.equals("rookW")){
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardX = board[j][k].x;

                            int boardYUp = board[j][k].y;
                            int boardYDown = board[j][k].y;

                            int indexY = 0;

                            for (int a=0;a<board.length;a++){
                                for (int b=0;b<board[0].length;b++){
                                    if (white[i] == board[a][b].chessPieceContained){
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (boardX == white[i].posX){
                                for (int i1=indexY;i1<=7;i1++){
                                    if (board[j][i1].y == boardYUp){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i] && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYUp = boardYUp + 120;
                                }
                                for (int i1=indexY;i1>=0;i1--){
                                    if (board[j][i1].y == boardYDown){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i] && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYDown = boardYDown - 120;
                                }
                            }
                        }
                    }

                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardY = board[j][k].y;

                            int boardXLeft = board[j][k].x;
                            int boardXRight = board[j][k].x;

                            int indexX = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        break;
                                    }
                                }
                            }

                            if (boardY == white[i].posY){
                                for (int i1=indexX;i1>=0;i1--){
                                    if (board[i1][k].x == boardXLeft){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i] && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXLeft = boardXLeft - 120;
                                }

                                for (int i1=indexX;i1<=7;i1++){
                                    if (board[i1][k].x == boardXRight){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Rook Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i] && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXRight = boardXRight + 120;
                                }
                            }
                        }
                    }
                }

                // for bishop check
                if (white[i].type.equals("bishopW")){

                    // for topLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Bishop Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for topRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Bishop Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for downRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained != null){
                                            if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Bishop Check!");
                                                return true;
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }

                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }

                    // for downLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Bishop Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }
                }

                // for queen check

                if (white[i].type.equals("queenW")){

                    // rook-like movement
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardX = board[j][k].x;

                            int boardYUp = board[j][k].y;
                            int boardYDown = board[j][k].y;

                            int indexY = 0;

                            for (int a=0;a<board.length;a++){
                                for (int b=0;b<board[0].length;b++){
                                    if (white[i] == board[a][b].chessPieceContained){
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (boardX == white[i].posX){
                                for (int i1=indexY;i1<=7;i1++){
                                    if (board[j][i1].y == boardYUp){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i] && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYUp = boardYUp + 120;
                                }
                                for (int i1=indexY;i1>=0;i1--){
                                    if (board[j][i1].y == boardYDown){
                                        if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i]){
                                            if (board[j][i1].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained != white[i] && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardYDown = boardYDown - 120;
                                }
                            }
                        }
                    }

                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardY = board[j][k].y;

                            int boardXLeft = board[j][k].x;
                            int boardXRight = board[j][k].x;

                            int indexX = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        break;
                                    }
                                }
                            }

                            if (boardY == white[i].posY){
                                for (int i1=indexX;i1>=0;i1--){
                                    if (board[i1][k].x == boardXLeft){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i] && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXLeft = boardXLeft - 120;
                                }

                                for (int i1=indexX;i1<=7;i1++){
                                    if (board[i1][k].x == boardXRight){
                                        if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i]){
                                            if (board[i1][k].chessPieceContained.type.equals("kingB")){
                                                System.err.println("Queen Check!");
                                                return true;
                                            }
                                            else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained != white[i] && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                break;
                                            }
                                        }
                                    }
                                    boardXRight = boardXRight + 120;
                                }
                            }
                        }
                    }

                    // for bishop-like movement

                    // for topLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for topRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr >= 0){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr - 1;
                                }
                            }
                        }
                    }

                    // for downRight side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1<=7;i1++){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }

                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }

                    // for downLeft side
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;

                            int yAxisCtr = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        yAxisCtr = b;
                                        break;
                                    }
                                }
                            }

                            for (int i1=indexX;i1>=0;i1--){
                                if (yAxisCtr <= 7){
                                    if (board[i1][yAxisCtr].chessPieceContained != null && (board[i1][yAxisCtr].chessPieceContained != white[i])){
                                        if (board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            System.err.println("Queen Check!");
                                            return true;
                                        }
                                        else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained != white[i] && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                            break;
                                        }
                                    }
                                    yAxisCtr = yAxisCtr + 1;
                                }
                            }
                        }
                    }
                }
                if (white[i].type.equals("knightW")){

                    // topLeft1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-2 < 0) && !(indexY-1 < 0)){
                                if (board[indexX-2][indexY-1].chessPieceContained != null && board[indexX-2][indexY-1].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-2][indexY-1].chessPieceContained != null && board[indexX-2][indexY-1].chessPieceContained != white[i] && !board[indexX-2][indexY-1].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // topLeft2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-1 < 0) && !(indexY-2 < 0)){
                                if (board[indexX-1][indexY-2].chessPieceContained != null && board[indexX-1][indexY-2].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-1][indexY-2].chessPieceContained != null && board[indexX-1][indexY-2].chessPieceContained != white[i] && !board[indexX-1][indexY-2].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // topRight1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+2 > 7) && !(indexY-1 < 0)){
                                if (board[indexX+2][indexY-1].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+2][indexY-1].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained != white[i] && !board[indexX+2][indexY-1].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // topRight2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+1 > 7) && !(indexY-2 < 0)){
                                if (board[indexX+1][indexY-2].chessPieceContained != null && board[indexX+1][indexY-2].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+1][indexY-2].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained != white[i] && !board[indexX+1][indexY-2].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // downLeft1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-2 < 0) && !(indexY+1 > 7)){
                                if (board[indexX-2][indexY+1].chessPieceContained != null && board[indexX-2][indexY+1].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-2][indexY+1].chessPieceContained != null && board[indexX-2][indexY+1].chessPieceContained != white[i] && !board[indexX-2][indexY+1].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // downLeft2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX-1 < 0) && !(indexY+2 > 7)){
                                if (board[indexX-1][indexY+2].chessPieceContained != null && board[indexX-1][indexY+2].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX-1][indexY+2].chessPieceContained != null && board[indexX-1][indexY+2].chessPieceContained != white[i] && !board[indexX-1][indexY+2].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // downRight1
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+2 > 7) && !(indexY+1 > 7)){
                                if (board[indexX+2][indexY+1].chessPieceContained != null && board[indexX+2][indexY+1].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+2][indexY+1].chessPieceContained != null && board[indexX+2][indexY+1].chessPieceContained != white[i] && !board[indexX+2][indexY+1].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }

                    // downRight2
                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int indexX = 0;
                            int indexY = 0;

                            for (int a=0;a<board.length;a++) {
                                for (int b = 0; b < board[0].length; b++) {
                                    if (white[i] == board[a][b].chessPieceContained) {
                                        indexX = a;
                                        indexY = b;
                                        break;
                                    }
                                }
                            }

                            if (!(indexX+1 > 7) && !(indexY+2 > 7)){
                                if (board[indexX+1][indexY+2].chessPieceContained != null && board[indexX+1][indexY+2].chessPieceContained.type.equals("kingB")){
                                    System.err.println("Knight Check!");
                                    return true;
                                }
                                else if (board[indexX+1][indexY+2].chessPieceContained != null && board[indexX+1][indexY+2].chessPieceContained != white[i] && !board[indexX+1][indexY+2].chessPieceContained.type.equals("kingB")){
                                    break;
                                }
                            }
                        }
                    }
                }

                if (white[i].type.equals("pawnW")){

                    for (int j=0;j<board.length;j++){
                        for (int k=0;k<board[0].length;k++){
                            int boardX = board[j][k].x;
                            int boardY = board[j][k].y;

                            if (white[i].pawnFirstMove){
                                if (boardX-120 == white[i].posX && boardY-120 == white[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingB")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != white[i] && !board[j][k].chessPieceContained.type.equals("kingB")){
                                        break;
                                    }
                                }
                                if (boardX+120 == white[i].posX && boardY-120 == white[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingB")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != white[i] && !board[j][k].chessPieceContained.type.equals("kingB")){
                                        break;
                                    }
                                }
                            }

                            else{
                                if (boardX-120 == white[i].posX && boardY-120 == white[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingB")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != white[i] && !board[j][k].chessPieceContained.type.equals("kingB")){
                                        break;
                                    }
                                }
                                if (boardX+120 == white[i].posX && boardY-120 == white[i].posY){
                                    if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.equals("kingB")){
                                        System.err.println("Pawn Check!");
                                        return true;
                                    }
                                    else if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained != white[i] && !board[j][k].chessPieceContained.type.equals("kingB")){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void calculateWhiteMovement(){
        if (playerTurn.equals("white")){

            for (int i=0;i<white.length;i++){
                if (white[i] != null){

                    if (white[i].selected){

                        if (white[i].type.equals("pawnW")){

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardX = board[j][k].x;
                                    int boardY = board[j][k].y;

                                    if (white[i].pawnFirstMove){
                                        if (boardX == white[i].posX && boardY-120 == white[i].posY){
                                            if (board[j][k].chessPieceContained == null){
                                                white[i].board[j][k].canMove = true;
                                            }
                                            if (board[j][k].chessPieceContained != null){
                                                break;
                                            }
                                        }
                                        if (boardX == white[i].posX && boardY-240 == white[i].posY){
                                            if (board[j][k].chessPieceContained == null){
                                                white[i].board[j][k].canMove = true;
                                            }
                                            if (board[j][k].chessPieceContained != null){
                                                break;
                                            }
                                        }
                                        if (boardX-120 == white[i].posX && boardY-120 == white[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("B") && !board[j][k].chessPieceContained.type.equals("kingB")){
                                                white[i].board[j][k].canMove = true;
                                            }
                                        }
                                        if (boardX+120 == white[i].posX && boardY-120 == white[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("B") && !board[j][k].chessPieceContained.type.equals("kingB")){
                                                white[i].board[j][k].canMove = true;
                                            }
                                        }
                                    }

                                    else{
                                        if (boardX == white[i].posX && boardY-120 == white[i].posY){
                                            if (board[j][k].chessPieceContained == null){
                                                white[i].board[j][k].canMove = true;
                                            }
                                        }
                                        if (boardX-120 == white[i].posX && boardY-120 == white[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("B") && !board[j][k].chessPieceContained.type.equals("kingB")){
                                                white[i].board[j][k].canMove = true;
                                            }
                                        }
                                        if (boardX+120 == white[i].posX && boardY-120 == white[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("B") && !board[j][k].chessPieceContained.type.equals("kingB")){
                                                white[i].board[j][k].canMove = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (white[i].type.equals("rookW")){

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardX = board[j][k].x;

                                    int boardYUp = board[j][k].y;
                                    int boardYDown = board[j][k].y;

                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++){
                                        for (int b=0;b<board[0].length;b++){
                                            if (white[i] == board[a][b].chessPieceContained){
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardX == white[i].posX){
                                        for (int i1=indexY;i1<=7;i1++){
                                            if (board[j][i1].y == boardYUp){
                                                if ((board[j][i1].chessPieceContained == white[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != white[i]){
                                                        white[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                // if there is a non null spot and it contains a black piece, then i,t can move there, therefore eat it
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("B") && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYUp = boardYUp + 120;
                                        }
                                        for (int i1=indexY;i1>=0;i1--){
                                            if (board[j][i1].y == boardYDown){
                                                if ((board[j][i1].chessPieceContained == white[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != white[i]){
                                                        white[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("B") && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYDown = boardYDown - 120;
                                        }
                                    }
                                }
                            }

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardY = board[j][k].y;

                                    int boardXLeft = board[j][k].x;
                                    int boardXRight = board[j][k].x;

                                    int indexX = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardY == white[i].posY){
                                        for (int i1=indexX;i1>=0;i1--){
                                            if (board[i1][k].x == boardXLeft){
                                                if ((board[i1][k].chessPieceContained == white[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != white[i]){
                                                        white[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("B") && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXLeft = boardXLeft - 120;
                                        }

                                        for (int i1=indexX;i1<=7;i1++){
                                            if (board[i1][k].x == boardXRight){
                                                if ((board[i1][k].chessPieceContained == white[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != white[i]){
                                                        white[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("B") && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXRight = boardXRight + 120;
                                        }
                                    }
                                }
                            }
                        }

                        if (white[i].type.equals("bishopW")){

                            // for topLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for topRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for downRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    // for topRight side
                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }

                            // for downLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    // for topRight side
                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }
                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }
                        }

                        if (white[i].type.equals("queenW")){

                            // for rook-like movement
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardX = board[j][k].x;

                                    int boardYUp = board[j][k].y;
                                    int boardYDown = board[j][k].y;

                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++){
                                        for (int b=0;b<board[0].length;b++){
                                            if (white[i] == board[a][b].chessPieceContained){
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardX == white[i].posX){
                                        for (int i1=indexY;i1<=7;i1++){
                                            if (board[j][i1].y == boardYUp){
                                                if ((board[j][i1].chessPieceContained == white[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != white[i]){
                                                        white[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                // if there is a non null spot and it contains a black piece, then i,t can move there, therefore eat it
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("B") && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYUp = boardYUp + 120;
                                        }
                                        for (int i1=indexY;i1>=0;i1--){
                                            if (board[j][i1].y == boardYDown){
                                                if ((board[j][i1].chessPieceContained == white[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != white[i]){
                                                        white[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("B") && !board[j][i1].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYDown = boardYDown - 120;
                                        }
                                    }
                                }
                            }

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardY = board[j][k].y;

                                    int boardXLeft = board[j][k].x;
                                    int boardXRight = board[j][k].x;

                                    int indexX = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardY == white[i].posY){
                                        for (int i1=indexX;i1>=0;i1--){
                                            if (board[i1][k].x == boardXLeft){
                                                if ((board[i1][k].chessPieceContained == white[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != white[i]){
                                                        white[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("B") && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXLeft = boardXLeft - 120;
                                        }

                                        for (int i1=indexX;i1<=7;i1++){
                                            if (board[i1][k].x == boardXRight){
                                                if ((board[i1][k].chessPieceContained == white[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != white[i]){
                                                        white[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("B") && !board[i1][k].chessPieceContained.type.equals("kingB")){
                                                    white[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXRight = boardXRight + 120;
                                        }
                                    }
                                }
                            }

                            // for bishop-like movement

                            // for topLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for topRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for downRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    // for topRight side
                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }

                            // for downLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    // for topRight side
                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == white[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != white[i]){
                                                    white[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("B") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingB")){
                                                white[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }
                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }
                        }

                        if (white[i].type.equals("kingW")){
                            // downSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexY != 7){
                                        if ((board[indexX][indexY+1].chessPieceContained == white[i]) || board[indexX][indexY+1].chessPieceContained == null){
                                            if (board[indexX][indexY+1].chessPieceContained != white[i]){
                                                white[i].board[indexX][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX][indexY+1].chessPieceContained != null && board[indexX][indexY+1].chessPieceContained.type.contains("B") && !board[indexX][indexY+1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // upperSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexY != 0){
                                        if ((board[indexX][indexY-1].chessPieceContained == white[i]) || board[indexX][indexY-1].chessPieceContained == null){
                                            if (board[indexX][indexY-1].chessPieceContained != white[i]){
                                                white[i].board[indexX][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX][indexY-1].chessPieceContained != null && board[indexX][indexY-1].chessPieceContained.type.contains("B") && !board[indexX][indexY-1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // leftSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 0){
                                        if ((board[indexX-1][indexY].chessPieceContained == white[i]) || board[indexX-1][indexY].chessPieceContained == null){
                                            if (board[indexX-1][indexY].chessPieceContained != white[i]){
                                                white[i].board[indexX-1][indexY].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY].chessPieceContained != null && board[indexX-1][indexY].chessPieceContained.type.contains("B") && !board[indexX-1][indexY].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-1][indexY].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // rightSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 7){
                                        if ((board[indexX+1][indexY].chessPieceContained == white[i]) || board[indexX+1][indexY].chessPieceContained == null){
                                            if (board[indexX+1][indexY].chessPieceContained != white[i]){
                                                white[i].board[indexX+1][indexY].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY].chessPieceContained != null && board[indexX+1][indexY].chessPieceContained.type.contains("B") && !board[indexX+1][indexY].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+1][indexY].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // upperLeftSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 0 && indexY != 0){
                                        if ((board[indexX-1][indexY-1].chessPieceContained == white[i]) || board[indexX-1][indexY-1].chessPieceContained == null){
                                            if (board[indexX-1][indexY-1].chessPieceContained != white[i]){
                                                white[i].board[indexX-1][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY-1].chessPieceContained != null && board[indexX-1][indexY-1].chessPieceContained.type.contains("B") && !board[indexX-1][indexY-1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-1][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // upperRightSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 7 && indexY != 0){
                                        if ((board[indexX+1][indexY-1].chessPieceContained == white[i]) || board[indexX+1][indexY-1].chessPieceContained == null){
                                            if (board[indexX+1][indexY-1].chessPieceContained != white[i]){
                                                white[i].board[indexX+1][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY-1].chessPieceContained != null && board[indexX+1][indexY-1].chessPieceContained.type.contains("B") && !board[indexX+1][indexY-1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+1][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downLeftSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 0 && indexY != 7){
                                        if ((board[indexX-1][indexY+1].chessPieceContained == white[i]) || board[indexX-1][indexY+1].chessPieceContained == null){
                                            if (board[indexX-1][indexY+1].chessPieceContained != white[i]){
                                                white[i].board[indexX-1][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY+1].chessPieceContained != null && board[indexX-1][indexY+1].chessPieceContained.type.contains("B") && !board[indexX-1][indexY+1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-1][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downRightSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 7 && indexY != 7){
                                        if ((board[indexX+1][indexY+1].chessPieceContained == white[i]) || board[indexX+1][indexY+1].chessPieceContained == null){
                                            if (board[indexX+1][indexY+1].chessPieceContained != white[i]){
                                                white[i].board[indexX+1][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY+1].chessPieceContained != null && board[indexX+1][indexY+1].chessPieceContained.type.contains("B") && !board[indexX+1][indexY+1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+1][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (white[i].type.equals("knightW")){

                            // topLeft1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-2 < 0) && !(indexY-1 < 0)){
                                        if ((board[indexX-2][indexY-1].chessPieceContained == white[i]) || board[indexX-2][indexY-1].chessPieceContained == null){
                                            if (board[indexX-2][indexY-1].chessPieceContained != white[i]){
                                                white[i].board[indexX-2][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-2][indexY-1].chessPieceContained != null && board[indexX-2][indexY-1].chessPieceContained.type.contains("B") && !board[indexX-2][indexY-1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-2][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // topLeft2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-1 < 0) && !(indexY-2 < 0)){
                                        if ((board[indexX-1][indexY-2].chessPieceContained == white[i]) || board[indexX-1][indexY-2].chessPieceContained == null){
                                            if (board[indexX-1][indexY-2].chessPieceContained != white[i]){
                                                white[i].board[indexX-1][indexY-2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY-2].chessPieceContained != null && board[indexX-1][indexY-2].chessPieceContained.type.contains("B") && !board[indexX-1][indexY-2].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-1][indexY-2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // topRight1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+2 > 7) && !(indexY-1 < 0)){
                                        if ((board[indexX+2][indexY-1].chessPieceContained == white[i]) || board[indexX+2][indexY-1].chessPieceContained == null){
                                            if (board[indexX+2][indexY-1].chessPieceContained != white[i]){
                                                white[i].board[indexX+2][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+2][indexY-1].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained.type.contains("B")&& !board[indexX+2][indexY-1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+2][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // topRight2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+1 > 7) && !(indexY-2 < 0)){
                                        if ((board[indexX+1][indexY-2].chessPieceContained == white[i]) || board[indexX+1][indexY-2].chessPieceContained == null){
                                            if (board[indexX+1][indexY-2].chessPieceContained != white[i]){
                                                white[i].board[indexX+1][indexY-2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY-2].chessPieceContained != null && board[indexX+1][indexY-2].chessPieceContained.type.contains("B") && !board[indexX+1][indexY-2].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+1][indexY-2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downLeft1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-2 < 0) && !(indexY+1 > 7)){
                                        if ((board[indexX-2][indexY+1].chessPieceContained == white[i]) || board[indexX-2][indexY+1].chessPieceContained == null){
                                            if (board[indexX-2][indexY+1].chessPieceContained != white[i]){
                                                white[i].board[indexX-2][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-2][indexY+1].chessPieceContained != null && board[indexX-2][indexY+1].chessPieceContained.type.contains("B") && !board[indexX-2][indexY+1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-2][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downLeft2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-1 < 0) && !(indexY+2 > 7)){
                                        if ((board[indexX-1][indexY+2].chessPieceContained == white[i]) || board[indexX-1][indexY+2].chessPieceContained == null){
                                            if (board[indexX-1][indexY+2].chessPieceContained != white[i]){
                                                white[i].board[indexX-1][indexY+2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY+2].chessPieceContained != null && board[indexX-1][indexY+2].chessPieceContained.type.contains("B") && !board[indexX-1][indexY+2].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX-1][indexY+2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downRight1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+2 > 7) && !(indexY+1 > 7)){
                                        if ((board[indexX+2][indexY+1].chessPieceContained == white[i]) || board[indexX+2][indexY+1].chessPieceContained == null){
                                            if (board[indexX+2][indexY+1].chessPieceContained != white[i]){
                                                white[i].board[indexX+2][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+2][indexY+1].chessPieceContained != null && board[indexX+2][indexY+1].chessPieceContained.type.contains("B") && !board[indexX+2][indexY+1].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+2][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downRight2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (white[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+1 > 7) && !(indexY+2 > 7)){
                                        if ((board[indexX+1][indexY+2].chessPieceContained == white[i]) || board[indexX+1][indexY+2].chessPieceContained == null){
                                            if (board[indexX+1][indexY+2].chessPieceContained != white[i]){
                                                white[i].board[indexX+1][indexY+2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY+2].chessPieceContained != null && board[indexX+1][indexY+2].chessPieceContained.type.contains("B") && !board[indexX+1][indexY+2].chessPieceContained.type.equals("kingB")){
                                            white[i].board[indexX+1][indexY+2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
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

    public void calculateBlackMovement(){
        if (playerTurn.equals("black")){

            for (int i=0;i<black.length;i++){
                if (black[i] != null){

                    if (black[i].selected){

                        if (black[i].type.equals("pawnB")){

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardX = board[j][k].x;
                                    int boardY = board[j][k].y;

                                    if (black[i].pawnFirstMove){
                                        if (boardX == black[i].posX && boardY+120 == black[i].posY){
                                            if (board[j][k].chessPieceContained == null){
                                                black[i].board[j][k].canMove = true;
                                            }
                                            if (board[j][k].chessPieceContained != null){
                                                break;
                                            }
                                        }
                                        if (boardX == black[i].posX && boardY+240 == black[i].posY){
                                            if (board[j][k].chessPieceContained == null){
                                                black[i].board[j][k].canMove = true;
                                            }
                                            if (board[j][k].chessPieceContained != null){
                                                break;
                                            }
                                        }
                                        if (boardX+120 == black[i].posX && boardY+120 == black[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("W") && !board[j][k].chessPieceContained.type.equals("kingW")){
                                                black[i].board[j][k].canMove = true;
                                            }
                                        }
                                        if (boardX-120 == black[i].posX && boardY+120 == black[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("W") && !board[j][k].chessPieceContained.type.equals("kingW")){
                                                black[i].board[j][k].canMove = true;
                                            }
                                        }
                                    }

                                    else{
                                        if (boardX == black[i].posX && boardY+120 == black[i].posY){
                                            if (board[j][k].chessPieceContained == null){
                                                black[i].board[j][k].canMove = true;
                                            }
                                        }
                                        if (boardX+120 == black[i].posX && boardY+120 == black[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("W") && !board[j][k].chessPieceContained.type.equals("kingW")){
                                                black[i].board[j][k].canMove = true;
                                            }
                                        }
                                        if (boardX-120 == black[i].posX && boardY+120 == black[i].posY){
                                            if (board[j][k].chessPieceContained != null && board[j][k].chessPieceContained.type.contains("W") && !board[j][k].chessPieceContained.type.equals("kingW")){
                                                black[i].board[j][k].canMove = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (black[i].type.equals("rookB")){

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardX = board[j][k].x;

                                    int boardYUp = board[j][k].y;
                                    int boardYDown = board[j][k].y;

                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++){
                                        for (int b=0;b<board[0].length;b++){
                                            if (black[i] == board[a][b].chessPieceContained){
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardX == black[i].posX){
                                        for (int i1=indexY;i1<=7;i1++){
                                            if (board[j][i1].y == boardYUp){
                                                if ((board[j][i1].chessPieceContained == black[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != black[i]){
                                                        black[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                // if there is a non null spot and it contains a black piece, then i,t can move there, therefore eat it
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("W") && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYUp = boardYUp + 120;
                                        }
                                        for (int i1=indexY;i1>=0;i1--){
                                            if (board[j][i1].y == boardYDown){
                                                if ((board[j][i1].chessPieceContained == black[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != black[i]){
                                                        black[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("W") && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYDown = boardYDown - 120;
                                        }
                                    }
                                }
                            }

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardY = board[j][k].y;

                                    int boardXLeft = board[j][k].x;
                                    int boardXRight = board[j][k].x;

                                    int indexX = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardY == black[i].posY){
                                        for (int i1=indexX;i1>=0;i1--){
                                            if (board[i1][k].x == boardXLeft){
                                                if ((board[i1][k].chessPieceContained == black[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != black[i]){
                                                        black[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("W") && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXLeft = boardXLeft - 120;
                                        }

                                        for (int i1=indexX;i1<=7;i1++){
                                            if (board[i1][k].x == boardXRight){
                                                if ((board[i1][k].chessPieceContained == black[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != black[i]){
                                                        black[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("W") && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXRight = boardXRight + 120;
                                        }
                                    }
                                }
                            }
                        }

                        if (black[i].type.equals("bishopB")){

                            // for topLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for topRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for downRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }

                            // for downLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }
                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }
                        }

                        if (black[i].type.equals("queenB")){

                            // for rook-like movement
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardX = board[j][k].x;

                                    int boardYUp = board[j][k].y;
                                    int boardYDown = board[j][k].y;

                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++){
                                        for (int b=0;b<board[0].length;b++){
                                            if (black[i] == board[a][b].chessPieceContained){
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardX == black[i].posX){
                                        for (int i1=indexY;i1<=7;i1++){
                                            if (board[j][i1].y == boardYUp){
                                                if ((board[j][i1].chessPieceContained == black[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != black[i]){
                                                        black[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                // if there is a non null spot and it contains a black piece, then i,t can move there, therefore eat it
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("W") && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYUp = boardYUp + 120;
                                        }
                                        for (int i1=indexY;i1>=0;i1--){
                                            if (board[j][i1].y == boardYDown){
                                                if ((board[j][i1].chessPieceContained == black[i]) || board[j][i1].chessPieceContained == null){
                                                    if (board[j][i1].chessPieceContained != black[i]){
                                                        black[i].board[j][i1].canMove = true;
                                                    }
                                                }
                                                else if (board[j][i1].chessPieceContained != null && board[j][i1].chessPieceContained.type.contains("W") && !board[j][i1].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[j][i1].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardYDown = boardYDown - 120;
                                        }
                                    }
                                }
                            }

                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int boardY = board[j][k].y;

                                    int boardXLeft = board[j][k].x;
                                    int boardXRight = board[j][k].x;

                                    int indexX = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                break;
                                            }
                                        }
                                    }

                                    if (boardY == black[i].posY){
                                        for (int i1=indexX;i1>=0;i1--){
                                            if (board[i1][k].x == boardXLeft){
                                                if ((board[i1][k].chessPieceContained == black[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != black[i]){
                                                        black[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("W") && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXLeft = boardXLeft - 120;
                                        }

                                        for (int i1=indexX;i1<=7;i1++){
                                            if (board[i1][k].x == boardXRight){
                                                if ((board[i1][k].chessPieceContained == black[i]) || board[i1][k].chessPieceContained == null){
                                                    if (board[i1][k].chessPieceContained != black[i]){
                                                        black[i].board[i1][k].canMove = true;
                                                    }
                                                }
                                                else if (board[i1][k].chessPieceContained != null && board[i1][k].chessPieceContained.type.contains("W") && !board[i1][k].chessPieceContained.type.equals("kingW")){
                                                    black[i].board[i1][k].canMove = true;
                                                    break;
                                                }
                                                else{
                                                    break;
                                                }
                                            }
                                            boardXRight = boardXRight + 120;
                                        }
                                    }
                                }
                            }

                            // for bishop-like movement

                            // for topLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for topRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr >= 0){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr - 1;
                                        }
                                    }
                                }
                            }

                            // for downRight side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    // for topRight side
                                    for (int i1=indexX;i1<=7;i1++){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }

                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }

                            // for downLeft side
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;

                                    int yAxisCtr = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                yAxisCtr = b;
                                                break;
                                            }
                                        }
                                    }

                                    // for topRight side
                                    for (int i1=indexX;i1>=0;i1--){
                                        if (yAxisCtr <= 7){
                                            if ((board[i1][yAxisCtr].chessPieceContained == black[i]) || board[i1][yAxisCtr].chessPieceContained == null){
                                                if (board[i1][yAxisCtr].chessPieceContained != black[i]){
                                                    black[i].board[i1][yAxisCtr].canMove = true;
                                                }
                                            }
                                            else if (board[i1][yAxisCtr].chessPieceContained != null && board[i1][yAxisCtr].chessPieceContained.type.contains("W") && !board[i1][yAxisCtr].chessPieceContained.type.equals("kingW")){
                                                black[i].board[i1][yAxisCtr].canMove = true;
                                                break;
                                            }
                                            else{
                                                break;
                                            }
                                            yAxisCtr = yAxisCtr + 1;
                                        }
                                    }
                                }
                            }
                        }

                        if (black[i].type.equals("kingB")){
                            // downSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexY != 7){
                                        if ((board[indexX][indexY+1].chessPieceContained == black[i]) || board[indexX][indexY+1].chessPieceContained == null){
                                            if (board[indexX][indexY+1].chessPieceContained != black[i]){
                                                black[i].board[indexX][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX][indexY+1].chessPieceContained != null && board[indexX][indexY+1].chessPieceContained.type.contains("W") && !board[indexX][indexY+1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // upperSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexY != 0){
                                        if ((board[indexX][indexY-1].chessPieceContained == black[i]) || board[indexX][indexY-1].chessPieceContained == null){
                                            if (board[indexX][indexY-1].chessPieceContained != black[i]){
                                                black[i].board[indexX][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX][indexY-1].chessPieceContained != null && board[indexX][indexY-1].chessPieceContained.type.contains("W") && !board[indexX][indexY-1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // leftSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 0){
                                        if ((board[indexX-1][indexY].chessPieceContained == black[i]) || board[indexX-1][indexY].chessPieceContained == null){
                                            if (board[indexX-1][indexY].chessPieceContained != black[i]){
                                                black[i].board[indexX-1][indexY].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY].chessPieceContained != null && board[indexX-1][indexY].chessPieceContained.type.contains("W") && !board[indexX-1][indexY].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-1][indexY].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // rightSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 7){
                                        if ((board[indexX+1][indexY].chessPieceContained == black[i]) || board[indexX+1][indexY].chessPieceContained == null){
                                            if (board[indexX+1][indexY].chessPieceContained != black[i]){
                                                black[i].board[indexX+1][indexY].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY].chessPieceContained != null && board[indexX+1][indexY].chessPieceContained.type.contains("W") && !board[indexX+1][indexY].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+1][indexY].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // upperLeftSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 0 && indexY != 0){
                                        if ((board[indexX-1][indexY-1].chessPieceContained == black[i]) || board[indexX-1][indexY-1].chessPieceContained == null){
                                            if (board[indexX-1][indexY-1].chessPieceContained != black[i]){
                                                black[i].board[indexX-1][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY-1].chessPieceContained != null && board[indexX-1][indexY-1].chessPieceContained.type.contains("W") && !board[indexX-1][indexY-1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-1][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // upperRightSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 7 && indexY != 0){
                                        if ((board[indexX+1][indexY-1].chessPieceContained == black[i]) || board[indexX+1][indexY-1].chessPieceContained == null){
                                            if (board[indexX+1][indexY-1].chessPieceContained != black[i]){
                                                black[i].board[indexX+1][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY-1].chessPieceContained != null && board[indexX+1][indexY-1].chessPieceContained.type.contains("W") && !board[indexX+1][indexY-1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+1][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downLeftSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 0 && indexY != 7){
                                        if ((board[indexX-1][indexY+1].chessPieceContained == black[i]) || board[indexX-1][indexY+1].chessPieceContained == null){
                                            if (board[indexX-1][indexY+1].chessPieceContained != black[i]){
                                                black[i].board[indexX-1][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY+1].chessPieceContained != null && board[indexX-1][indexY+1].chessPieceContained.type.contains("W") && !board[indexX-1][indexY+1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-1][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downRightSide
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (indexX != 7 && indexY != 7){
                                        if ((board[indexX+1][indexY+1].chessPieceContained == black[i]) || board[indexX+1][indexY+1].chessPieceContained == null){
                                            if (board[indexX+1][indexY+1].chessPieceContained != black[i]){
                                                black[i].board[indexX+1][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY+1].chessPieceContained != null && board[indexX+1][indexY+1].chessPieceContained.type.contains("W") && !board[indexX+1][indexY+1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+1][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (black[i].type.equals("knightB")){

                            // topLeft1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-2 < 0) && !(indexY-1 < 0)){
                                        if ((board[indexX-2][indexY-1].chessPieceContained == black[i]) || board[indexX-2][indexY-1].chessPieceContained == null){
                                            if (board[indexX-2][indexY-1].chessPieceContained != black[i]){
                                                black[i].board[indexX-2][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-2][indexY-1].chessPieceContained != null && board[indexX-2][indexY-1].chessPieceContained.type.contains("W") && !board[indexX-2][indexY-1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-2][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // topLeft2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-1 < 0) && !(indexY-2 < 0)){
                                        if ((board[indexX-1][indexY-2].chessPieceContained == black[i]) || board[indexX-1][indexY-2].chessPieceContained == null){
                                            if (board[indexX-1][indexY-2].chessPieceContained != black[i]){
                                                black[i].board[indexX-1][indexY-2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY-2].chessPieceContained != null && board[indexX-1][indexY-2].chessPieceContained.type.contains("W") && !board[indexX-1][indexY-2].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-1][indexY-2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // topRight1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+2 > 7) && !(indexY-1 < 0)){
                                        if ((board[indexX+2][indexY-1].chessPieceContained == black[i]) || board[indexX+2][indexY-1].chessPieceContained == null){
                                            if (board[indexX+2][indexY-1].chessPieceContained != black[i]){
                                                black[i].board[indexX+2][indexY-1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+2][indexY-1].chessPieceContained != null && board[indexX+2][indexY-1].chessPieceContained.type.contains("W") && !board[indexX+2][indexY-1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+2][indexY-1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // topRight2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+1 > 7) && !(indexY-2 < 0)){
                                        if ((board[indexX+1][indexY-2].chessPieceContained == black[i]) || board[indexX+1][indexY-2].chessPieceContained == null){
                                            if (board[indexX+1][indexY-2].chessPieceContained != black[i]){
                                                black[i].board[indexX+1][indexY-2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY-2].chessPieceContained != null && board[indexX+1][indexY-2].chessPieceContained.type.contains("W") && !board[indexX+1][indexY-2].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+1][indexY-2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downLeft1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-2 < 0) && !(indexY+1 > 7)){
                                        if ((board[indexX-2][indexY+1].chessPieceContained == black[i]) || board[indexX-2][indexY+1].chessPieceContained == null){
                                            if (board[indexX-2][indexY+1].chessPieceContained != black[i]){
                                                black[i].board[indexX-2][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-2][indexY+1].chessPieceContained != null && board[indexX-2][indexY+1].chessPieceContained.type.contains("W") && !board[indexX-2][indexY+1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-2][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downLeft2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX-1 < 0) && !(indexY+2 > 7)){
                                        if ((board[indexX-1][indexY+2].chessPieceContained == black[i]) || board[indexX-1][indexY+2].chessPieceContained == null){
                                            if (board[indexX-1][indexY+2].chessPieceContained != black[i]){
                                                black[i].board[indexX-1][indexY+2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX-1][indexY+2].chessPieceContained != null && board[indexX-1][indexY+2].chessPieceContained.type.contains("W") && !board[indexX-1][indexY+2].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX-1][indexY+2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downRight1
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+2 > 7) && !(indexY+1 > 7)){
                                        if ((board[indexX+2][indexY+1].chessPieceContained == black[i]) || board[indexX+2][indexY+1].chessPieceContained == null){
                                            if (board[indexX+2][indexY+1].chessPieceContained != black[i]){
                                                black[i].board[indexX+2][indexY+1].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+2][indexY+1].chessPieceContained != null && board[indexX+2][indexY+1].chessPieceContained.type.contains("W") && !board[indexX+2][indexY+1].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+2][indexY+1].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }
                            }

                            // downRight2
                            for (int j=0;j<board.length;j++){
                                for (int k=0;k<board[0].length;k++){
                                    int indexX = 0;
                                    int indexY = 0;

                                    for (int a=0;a<board.length;a++) {
                                        for (int b = 0; b < board[0].length; b++) {
                                            if (black[i] == board[a][b].chessPieceContained) {
                                                indexX = a;
                                                indexY = b;
                                                break;
                                            }
                                        }
                                    }

                                    if (!(indexX+1 > 7) && !(indexY+2 > 7)){
                                        if ((board[indexX+1][indexY+2].chessPieceContained == black[i]) || board[indexX+1][indexY+2].chessPieceContained == null){
                                            if (board[indexX+1][indexY+2].chessPieceContained != black[i]){
                                                black[i].board[indexX+1][indexY+2].canMove = true;
                                            }
                                        }
                                        else if (board[indexX+1][indexY+2].chessPieceContained != null && board[indexX+1][indexY+2].chessPieceContained.type.contains("W") && !board[indexX+1][indexY+2].chessPieceContained.type.equals("kingW")){
                                            black[i].board[indexX+1][indexY+2].canMove = true;
                                            break;
                                        }
                                        else{
                                            break;
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        drawChessBoard(g2);

        for (int i=0;i<white.length;i++){
            if (white[i] != null){
                white[i].draw(g2);
            }
        }

        for (int i=0;i<black.length;i++){
            if (black[i] != null){
                black[i].draw(g2);
            }
        }

        // for debug, is the spot full or not
        /*for (int i=0;i<chessBoardCol;i++){
            for (int j=0;j<chessBoardRow;j++){
                g2.setColor(Color.red);
                g2.drawString(board[i][j].containsPiece(),i*tileSize+60,j*tileSize+60);
            }
        }
        */

        g2.dispose(); // dispose of this graphics context and release any system resources that it is using.
    }

    public void drawChessBoard(Graphics2D g2){
        int col = 0;
        int row = 0;
        int colorCtr = 0;
        for (int i=col;i<chessBoardCol;i++){
            for (int j=row;j<chessBoardRow;j++){
                if (colorCtr % 2 == 0){
                    g2.setColor(new Color(170,170,170));
                }
                else {
                    g2.setColor(new Color(100,40,0));
                }
                g2.fillRect(i*tileSize,j*tileSize,tileSize,tileSize);
                colorCtr++;
            }
            colorCtr++;
        }
    }

    public BufferedImage scaleImage(BufferedImage original,int width,int height){
        // to scale the image beforehand
        BufferedImage scaledImage = new BufferedImage(width,height,original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original,0,0,width,height,null);
        g2.dispose();

        return scaledImage;
    }
}