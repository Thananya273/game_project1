package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.util.Random;

public class Alien1 extends Enemy {

    private Bomb bomb;
    // Zigzag movement fields
    private int startX = 0;
    private double phaseOffset = 0;
    private static final int ZIGZAG_AMPLITUDE = 40;
    private static final double ZIGZAG_FREQUENCY = 18.0;
    private static final Random rand = new Random();

    public Alien1(int x, int y) {
        super(x, y);
        this.startX = x;
        this.phaseOffset = rand.nextInt(360); // random phase offset in degrees
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        // Smooth sine wave zigzag
        this.y++;
        double radians = ((this.y + phaseOffset) / ZIGZAG_FREQUENCY);
        this.x = (int)(startX + ZIGZAG_AMPLITUDE * Math.sin(radians));
        // Call the parent's animation method
        super.act();
    }

    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }
        
        @Override
        public void act() {
            // Bomb doesn't need animation
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
}
