package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {

    // Animation variables
    private Image frame1Sprite;
    private Image frame2Sprite;
    private int animationFrame = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 5; // Change frame every 5 game cycles (faster for explosions)
    private int totalFrames = 0;
    private static final int EXPLOSION_DURATION = 20; // Total frames explosion lasts

    public Explosion(int x, int y) {

        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;

        // Load both explosion animation frames
        var iiFrame1 = new ImageIcon(IMG_EXPLOSION_FRAME1);
        var iiFrame2 = new ImageIcon(IMG_EXPLOSION_FRAME2);

        // Scale both images to use the global scaling factor
        frame1Sprite = iiFrame1.getImage().getScaledInstance(
            iiFrame1.getIconWidth() * SCALE_FACTOR,
            iiFrame1.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
            
        frame2Sprite = iiFrame2.getImage().getScaledInstance(
            iiFrame2.getIconWidth() * SCALE_FACTOR,
            iiFrame2.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);

        // Set initial image to first frame
        setImage(frame1Sprite);
    }

    @Override
    public void act() {
        // Update explosion animation and duration
        totalFrames++;
        
        // Animate between frames
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationFrame = (animationFrame + 1) % 2; // Alternate between 0 and 1
            animationCounter = 0;
            
            // Update the sprite based on current animation frame
            setImage(animationFrame == 0 ? frame1Sprite : frame2Sprite);
        }
        
        // Check if explosion should end
        if (totalFrames >= EXPLOSION_DURATION) {
            setVisible(false);
        }
    }
    
    public void act(int direction) {
        // This method is kept for compatibility but calls the main act method
        act();
    }
}
