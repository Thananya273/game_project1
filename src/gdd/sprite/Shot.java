package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 18;
    private static final int V_SPACE = 1;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        var ii = new ImageIcon(IMG_SHOT);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR, 
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(x);
        setY(y);
    }
    
    @Override
    public void act() {
        // Shot doesn't need animation
    }
}
