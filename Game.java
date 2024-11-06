import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Game extends JPanel implements KeyListener, Runnable {

    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 630;
    public static final Font main = new Font("Bebas Neue Regular", Font.PLAIN, 28);
    private Thread game;
    private boolean running;
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private GameBoard board;

    private long startTime;
    private long elapsed;
    private boolean set;

    public Game() {
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);

        board = new GameBoard(WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, HEIGHT - GameBoard.BOARD_HEIGHT - 10);
    }

    private void update() {
        /* 
        //testing
        if(Keyboard.pressed[KeyEvent.VK_SPACE]) {
            System.out.println("space");
        }
        if(Keyboard.typed(KeyEvent.VK_RIGHT)) {
            System.out.println("right!!!");
        } */

        board.update();

        Keyboard.update();
    } //as in fps, # times it will be called

    private void render() {
        Graphics2D g = (Graphics2D) image.getGraphics(); //image keeps track of everything drawn on the screen: the board and pieces
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        board.render(g);
        g.dispose();

        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
    }

    @Override
    public void run() {
        int fps = 0, updates = 0;
        long fpsTimer = System.currentTimeMillis();
        double nsPerUpdate = 1000000000.0 / 60; //nanosecs between updates

        //last update time in nanosecs
        double then = System.nanoTime();
        double unprocessed = 0; //how many updates needed in case rendering falls behind

        while(running) {

            //how many updates we need to do based on how much time has passed
            boolean shouldRender = false;
            double now = System.nanoTime();
            unprocessed += (now - then) / nsPerUpdate;
            then = now;

            //update queue
            while(unprocessed >= 1) {
                updates++;
                update();
                unprocessed--;
                shouldRender = true; //to lock into 60fps
            }

            //render
            if(shouldRender) {
                fps++;
                render();
                shouldRender = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace(); //print out error
                }
            }
        }

        //fps timer
        //prints out the seconds, how many updates and how many fps for debugging, normally wounlt have this
        if(System.currentTimeMillis() - fpsTimer > 1000) {
            System.out.printf("%d fps %d updates", fps, updates);
            System.out.println();
            fps = 0;
            updates = 0;
            fpsTimer += 1000;
        }

        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    public synchronized void start() {
        if(running) return;
        running = true;
        game = new Thread(this, "game");
        game.start();
    }

    public synchronized void stop() {
        if(!running) return;
        running = false;
        System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Keyboard.keyTyped(e);
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keyboard.keyPressed(e);
        //throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keyboard.keyReleased(e);
        //throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }



}
