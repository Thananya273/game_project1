package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    private Bomb bomb;
    
    // Animation variables
    protected Image frame1Sprite;
    protected Image frame2Sprite;
    protected int animationFrame = 0;
    protected int animationCounter = 0;
    protected static final int ANIMATION_SPEED = 10; // Change frame every 10 game cycles
    
    // Shooting variables
    protected int shotCounter = 0;
    protected static final int SHOT_DELAY = 120; // Frames between shots (2 seconds at 60fps)
    protected static final int SHOT_CHANCE = 5; // 1 in 5 chance to shoot when timer is ready

    public Enemy(int x, int y) {

        initEnemy(x, y);
        bomb = new Bomb(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        // bomb = new Bomb(x, y);

        // Load both animation frames
        var iiFrame1 = new ImageIcon(IMG_ENEMY);
        var iiFrame2 = new ImageIcon(IMG_ENEMY1_FRAME2);

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
    public void act() {
        // This method is called by the game loop for animation
        updateAnimation();
    }
    
    public void act(int direction) {
        this.x += direction;
        updateAnimation();
    }
    
    private void updateAnimation() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationFrame = (animationFrame + 1) % 2; // Alternate between 0 and 1
            animationCounter = 0;
            
            // Update the sprite based on current animation frame
            setImage(animationFrame == 0 ? frame1Sprite : frame2Sprite);
        }
    }
    
    public boolean shouldShoot() {
        shotCounter++;
        if (shotCounter >= SHOT_DELAY) {
            shotCounter = 0;
            // Random chance to shoot
            return (int)(Math.random() * SHOT_CHANCE) == 0;
        }
        return false;
    }
    
    public EnemyShot createShot() {
        // Create a shot at the enemy's position
        return new EnemyShot(this.x + (getImage().getWidth(null) / 2), this.y + getImage().getHeight(null));
    }

    public Bomb getBomb() {
        return bomb;
    }
    public void dropBomb() {
        if (bomb.isDestroyed()) {
            bomb.setDestroyed(false);
            bomb.setX(this.x + getImage().getWidth(null) / 2 - 8);
            bomb.setY(this.y + getImage().getHeight(null));
        }
    }
    public void updateBomb() {
        if (!bomb.isDestroyed()) {
            bomb.setY(bomb.getY() + 4);
            if (bomb.getY() > BOARD_HEIGHT) {
                bomb.setDestroyed(true);
            }
        }
    }

    public class Bomb extends Sprite {
        private boolean destroyed;
        public Bomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }
        @Override
        public void act() {}
        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }
        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
