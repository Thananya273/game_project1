package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class EnemyShot extends Sprite {

    private int speed = 3; // Speed of enemy shots

    public EnemyShot(int x, int y) {
        initEnemyShot(x, y);
    }

    private void initEnemyShot(int x, int y) {
        this.x = x;
        this.y = y;

        // Load enemy shot image
        var ii = new ImageIcon(IMG_ENEMY2_SHOT); // Using enemy2_shot.png for all enemy shots

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(
            ii.getIconWidth() * SCALE_FACTOR,
            ii.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act() {
        // Move the shot downward
        this.y += speed;
        
        // Remove shot if it goes off screen
        if (this.y > BOARD_HEIGHT) {
            setVisible(false);
        }
    }
} 