package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    public Alien2(int x, int y) {
        super(x, y);
        initAlien2(x, y);
    }

    private void initAlien2(int x, int y) {
        this.x = x;
        this.y = y;

        // Load both enemy2 animation frames
        var iiFrame1 = new ImageIcon(IMG_ENEMY2_FRAME1);
        var iiFrame2 = new ImageIcon(IMG_ENEMY2_FRAME2);

        // Scale both images to use the enemy-specific scaling factor
        frame1Sprite = iiFrame1.getImage().getScaledInstance(
            iiFrame1.getIconWidth() * ENEMY_SCALE_FACTOR,
            iiFrame1.getIconHeight() * ENEMY_SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
            
        frame2Sprite = iiFrame2.getImage().getScaledInstance(
            iiFrame2.getIconWidth() * ENEMY_SCALE_FACTOR,
            iiFrame2.getIconHeight() * ENEMY_SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);

        // Set initial image to first frame
        setImage(frame1Sprite);
    }

    @Override
    public void act(int direction) {
        // Alien2 moves differently - maybe faster or in a different pattern
        this.y += 2; // Move down faster than Alien1
        // Call the parent's animation method
        super.act();
    }
} 