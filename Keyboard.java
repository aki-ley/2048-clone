//import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;

public class Keyboard {

    public static boolean[] pressed = new boolean[256];
    public static boolean[] prev = new boolean[256];

    private Keyboard() {    }

    public static void update() {
        for(int i = 0; i < 4; i++) {
            if(i == 0) prev[KeyEvent.VK_LEFT] = pressed[KeyEvent.VK_LEFT];
            if(i == 1) prev[KeyEvent.VK_RIGHT] = pressed[KeyEvent.VK_RIGHT];
            if(i == 2) prev[KeyEvent.VK_UP] = pressed[KeyEvent.VK_UP];
            if(i == 3) prev[KeyEvent.VK_DOWN] = pressed[KeyEvent.VK_DOWN];
        }
    }

    //this way of doing it accommofdates multiple pressed keys 
    //(not just one at a time)
    public static void keyPressed(KeyEvent e) {
        pressed[e.getKeyCode()] = true;
    }

    public static void keyReleased(KeyEvent e) {
        pressed[e.getKeyCode()] = false;
    }

    //prev is only false for a split second before updating
    public static boolean typed(int KeyEvent) {
        return !pressed[KeyEvent] && prev[KeyEvent];
    }

}
