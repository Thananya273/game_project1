package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;

    private Rectangle bounds = new Rectangle(175,135,17,32);
    
    // Directional sprites
    private Image leftSprite;
    private Image leftSprite2;
    private Image rightSprite;
    private Image rightSprite2;
    private Image stationarySprite;
    
    // Animation variables
    private int animationFrame = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 6; // Change frame every 6 game cycles
    private boolean isAnimating = false;
    private int lastDirection = 0; // 0 = stationary, -1 = left, 1 = right
    
    // Key state tracking
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        // Load all directional sprites
        var iiStationary = new ImageIcon(IMG_PLAYER);
        var iiLeft = new ImageIcon(IMG_PLAYER_LEFT);
        var iiLeft2 = new ImageIcon(IMG_PLAYER_LEFT2);
        var iiRight = new ImageIcon(IMG_PLAYER_RIGHT);
        var iiRight2 = new ImageIcon(IMG_PLAYER_RIGHT2);

        // Scale all images to use the global scaling factor
        stationarySprite = iiStationary.getImage().getScaledInstance(
            iiStationary.getIconWidth() * SCALE_FACTOR,
            iiStationary.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
            
        leftSprite = iiLeft.getImage().getScaledInstance(
            iiLeft.getIconWidth() * SCALE_FACTOR,
            iiLeft.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
            
        leftSprite2 = iiLeft2.getImage().getScaledInstance(
            iiLeft2.getIconWidth() * SCALE_FACTOR,
            iiLeft2.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
            
        rightSprite = iiRight.getImage().getScaledInstance(
            iiRight.getIconWidth() * SCALE_FACTOR,
            iiRight.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
            
        rightSprite2 = iiRight2.getImage().getScaledInstance(
            iiRight2.getIconWidth() * SCALE_FACTOR,
            iiRight2.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);

        // Set initial image to stationary
        setImage(stationarySprite);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public void act() {
        x += dx;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }
        
        // Update animation
        updateAnimation();
    }
    
    private void updateAnimation() {
        // Check if direction changed
        int currentDirection = Integer.compare(dx, 0);
        
        if (currentDirection != 0) {
            // Player is moving
            if (currentDirection != lastDirection) {
                // Direction changed, show first frame immediately
                animationFrame = 0;
                animationCounter = 0;
                isAnimating = true;
                lastDirection = currentDirection;
                updateSprite();
            } else {
                // Same direction, transition to second frame after delay
                animationCounter++;
                if (animationCounter >= ANIMATION_SPEED && animationFrame == 0) {
                    animationFrame = 1; // Switch to second frame (held state)
                    updateSprite();
                }
            }
        } else {
            // Player is stationary
            if (lastDirection != 0) {
                stopAnimation();
                lastDirection = 0;
            }
        }
    }
    
    private void updateSprite() {
        if (dx < 0) {
            // Moving left
            setImage(animationFrame == 0 ? leftSprite : leftSprite2);
        } else if (dx > 0) {
            // Moving right
            setImage(animationFrame == 0 ? rightSprite : rightSprite2);
        }
    }
    
    private void startAnimation() {
        isAnimating = true;
        animationFrame = 0;
        animationCounter = 0;
        updateSprite(); // Show first frame immediately
    }
    
    private void stopAnimation() {
        isAnimating = false;
        animationFrame = 0;
        animationCounter = 0;
        setImage(stationarySprite);
    }
    
    private void updateMovement() {
        // If both keys are pressed or neither key is pressed, stop movement
        if ((leftKeyPressed && rightKeyPressed) || (!leftKeyPressed && !rightKeyPressed)) {
            dx = 0;
        } else if (leftKeyPressed) {
            dx = -currentSpeed;
        } else if (rightKeyPressed) {
            dx = currentSpeed;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            leftKeyPressed = true;
        }

        if (key == KeyEvent.VK_RIGHT) {
            rightKeyPressed = true;
        }
        
        updateMovement();
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            leftKeyPressed = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            rightKeyPressed = false;
        }
        
        updateMovement();
    }
}
