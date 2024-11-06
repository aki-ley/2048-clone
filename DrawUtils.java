import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

//utility function for centering text based on font size
public class DrawUtils {

    private DrawUtils() {}

    public static int getMessageWidth(String message, Font font, Graphics2D g) {
        g.setFont(font);
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(message, g);
        return(int)bounds.getWidth();
    }

    //java likes to automatically add padding so getting the height is a little harder
    public static int getMessageHeight(String message, Font font, Graphics2D g) {
        g.setFont(font);
        if(message.length() == 0) return 0;
        TextLayout tl = new TextLayout(message, font, g.getFontRenderContext());
        return(int)tl.getBounds().getHeight();
    }

}
