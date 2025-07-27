package gdd.powerup;

import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {
    public MultiShot(int x, int y) {
        super(x, y);
        var ii = new ImageIcon("src/images/powerup-g.png");
        setImage(ii.getImage());
    }

    public void act() {
        // SpeedUp specific behavior can be added here
        // For now, it just moves down the screen
        this.y += 2; // Move down by 2 pixel each frame
    }

    @Override
    public void upgrade(Player player) {
        player.activateMultiShot(600); // 10 seconds at 60fps, increases level
        die();
    }
} 