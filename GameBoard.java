import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.event.KeyEvent;

//mastermind of game
public class GameBoard {

    public static final int ROWS = 4;
    public static final int COLS = 4;

    private final int startingTiles = 2;
    private Tile[][] board;
    private boolean dead;
    private boolean win;
    private BufferedImage gameBoard;
    private BufferedImage finalBoard; //final image, game board with tiles
    private int x;
    private int y;

    private static int SPACING = 10; //between tiles
    public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
    public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;

    private boolean hasStarted; //game start for timer

    public GameBoard(int x, int y) {
        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS];
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        createBoardImage();
        start();
    }

    private void createBoardImage() {
        Graphics2D g = (Graphics2D) gameBoard.getGraphics();
        g.setColor(new Color(0xbbada0));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(new Color(0xcdc1b4));

        for(int row = 0; row < ROWS; row++) {
            for(int col = 0; col < COLS; col++) {
                int x = SPACING + SPACING * col + Tile.WIDTH * col;
                int y = SPACING + SPACING * row + Tile.HEIGHT * row;
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
            }
        }
    }

    //spawning first 2 tiles
    private void start() {
        for(int i = 0; i < startingTiles; i++) {
            spawnRandom();
        }
        //spawn(0, 0, 2); //instead of random, just here for ref
    }

    //private void spawn(int row, int col, int value) {
    //    board[row][col] = new Tile(value, getTileX(col), getTileY(row));
    //}

    //pick a spot and decide between 2 or 4 to spawn
    private void spawnRandom() {
        Random random = new Random();
        boolean notValid = true;

        //while not valid is true (not 2 or 4) look for valid nums
        //loop to find location without existing tile
        while(notValid) {
            int location = random.nextInt(ROWS * COLS);
            int row = location / ROWS;
            int col = location % COLS;
            //converting 1d to 2d? num to row to col?
            Tile current = board[row][col];
            if(current == null) {
                int value = random.nextInt(10) < 9 ? 2 : 4;
                //condensed if statement: random int from 0-9, if less than 9 then 2, otherwise 4?
                //OH. okayyy. 90% chance of 2 spawning, 10% chance of 4
                Tile tile = new Tile(value, getTileX(col), getTileY(row));
                board[row][col] = tile;
                notValid = false;
            }
        }
    }

    public int getTileX(int col) {
        return SPACING + col * Tile.WIDTH + col * SPACING;
    } //similar to board width expression
    public int getTileY(int row) {
        return SPACING + row * Tile.HEIGHT + row * SPACING;
    }

    public void render(Graphics2D g) {
        Graphics2D g2d = (Graphics2D)finalBoard.getGraphics();
        g2d.drawImage(gameBoard, 0, 0, null);

        for(int row = 0; row < ROWS; row++) {
            for(int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if(current == null) continue; 
                current.render(g2d);
            }
        }

        g.drawImage(finalBoard, x, y, null); 
        g2d.dispose();
        //xy here are the global variables, not the same as in the createboardimage method
    }

    public void update() {
        checkKeys();

        //check if u win
        for(int row = 0; row < ROWS; row++) {
            for(int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if(current == null) continue; 
                current.update();
                resetPosition(current, row, col);
                if(current.getValue() == 2048) {
                    win = true;
                }
            }
        }
    }

    private void resetPosition(Tile current, int row, int col) {
        if(current == null) return;

        int x = getTileX(col);
        int y = getTileY(row);

        int distX = current.getX() - x;
        int distY = current.getY() - y;

        if(Math.abs(distX) < Tile.SLIDE_SPEED) {
            current.setX(current.getX() - distX);
        } //absolute value
        if(Math.abs(distY) < Tile.SLIDE_SPEED) {
            current.setY(current.getY() - distY);
        }

        if(distX < 0) { //moving left
            current.setX(current.getX() + Tile.SLIDE_SPEED);
        }
        if(distX > 0) { //changed order from the video
            current.setX(current.getX() - Tile.SLIDE_SPEED);
        }
        if(distY < 0) {
            current.setY(current.getY() + Tile.SLIDE_SPEED);
        }
        if(distY > 0) {
            current.setY(current.getY() - Tile.SLIDE_SPEED);
        }
    }

    private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction dir) {
        boolean canMove = false;

        Tile current = board[row][col];
        if(current == null) return false;
        boolean move = true;
        int newCol = col;
        int newRow = row;

        while(move) { //if combine or slide anywhere
            newCol += horizontalDirection;
            newRow += verticalDirection;
            if (checkOutOfBounds(dir, newRow, newCol)) break;
            if (board[newRow][newCol] == null) {
                board[newRow][newCol] = current;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                canMove = true;
            } else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) {
                board[newRow][newCol].setCanCombine(false);
                board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                board[newRow][newCol].setCombineAnimation(true);
                //add to score
            } else {
                move = false;
            }
        }
        return canMove;
    }

    private boolean checkOutOfBounds(Direction dir, int row, int col) {
        if(dir == Direction.LEFT) {
            return col < 0;
        } else if (dir == Direction.RIGHT) {
            return col > COLS - 1;
        } else if (dir==Direction.UP) {
            return row < 0;
        } else if (dir == Direction.DOWN) {
            return row > ROWS - 1;
        }
        return false;
    }

    private void moveTiles(Direction dir) {
        boolean canMove = false;
        int horizontalDirection = 0;
        int verticalDirection = 0;

        if (dir == Direction.LEFT) {
            horizontalDirection = -1; //v=moving negative on the x axis
            for(int row = 0; row < ROWS; row++) {
                for(int col = 0; col < COLS; col++) {
                    if(!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir); 
                        //becomes true or false based on last tile update, if any tile can move canMove = true
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if(dir == Direction.RIGHT) {
            horizontalDirection = 1; //up 1 on the x axis
            for(int row = 0; row < ROWS; row++) {
                for(int col = COLS - 1; col >= 0; col--) {
                    if(!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir); 
                        //becomes true or false based on last tile update, if any tile can move canMove = true
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    } //updating tiles from LEFT:
                }     // ex. 2 2 4 8 --> 0 4 4 8 CORRECT
            }         //     2 2 4 8 --> 0 0 0 16 WRONG
        } else if (dir == Direction.UP) {
            verticalDirection = -1; 
            for(int row = 0; row < ROWS; row++) {
                for(int col = 0; col < COLS; col++) {
                    if(!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir); 
                        //becomes true or false based on last tile update, if any tile can move canMove = true
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.DOWN) {
            verticalDirection = 1; 
            for(int row = ROWS - 1; row >= 0; row--) {
                for(int col = 0; col < COLS; col++) {
                    if(!canMove) {
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir); 
                        //becomes true or false based on last tile update, if any tile can move canMove = true
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else {
            System.out.println(dir + " is not a valid direction.");
        }

        for(int row = 0; row < ROWS; row++) {
            for(int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if(current == null) continue;
                current.setCanCombine(true);
            }
        }

        if(canMove) {
            spawnRandom();
            checkDead();
        }
    }

    private void checkDead() {
        for(int row = 0; row < ROWS; row++) {
            for(int col = 0; col < COLS; col++) {
                if(board[row][col] == null) return; //theres still spavce on the board
                if(checkSurroundingTiles(row, col, board[row][col])) {
                    return; //check if a tile can combine, break out of loop
                }
            }
        }

        dead = true; //if loop not broken then no more moves
        //setHighScore(true);
    }

    private boolean checkSurroundingTiles(int row, int col, Tile current) {
        if(row > 0) {
            Tile check = board[row - 1][col];
            if(check == null) return true; //tile can combine
            if(current.getValue() == check.getValue()) return true;
        }
        if(row < ROWS - 1) {
            Tile check = board[row + 1][col];
            if(check == null) return true;
            if(current.getValue() == check.getValue()) return true;
        }
        if(col > 0) {
            Tile check = board[row][col - 1];
            if(check == null) return true;
            if(current.getValue() == check.getValue()) return true;
        }
        if(col < COLS - 1) {
            Tile check = board[row][col + 1];
            if(check == null) return true;
            if(current.getValue() == check.getValue()) return true;
        } //all of THIS^^ can be condensed but uh
        return false; //tile CANT combine
    }

    private void checkKeys() {
        if(Keyboard.typed(KeyEvent.VK_LEFT)) {
            moveTiles(Direction.LEFT);
            if(!hasStarted) hasStarted = true;
        }
        if(Keyboard.typed(KeyEvent.VK_RIGHT)) {
            moveTiles(Direction.RIGHT);
            if(!hasStarted) hasStarted = true;
        }
        if(Keyboard.typed(KeyEvent.VK_UP)) {
            moveTiles(Direction.UP);
            if(!hasStarted) hasStarted = true;
        }
        if(Keyboard.typed(KeyEvent.VK_DOWN)) {
            moveTiles(Direction.DOWN);
            if(!hasStarted) hasStarted = true;
        }
    }

}
