package gdd.scene;

import gdd.Game;
import static gdd.Global.*;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import gdd.AudioPlayer;

public class BossScene extends JPanel {
    private final Game game;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Image bossImage;
    private Image bossBulletImage;
    private Player player;
    private List<Shot> playerShots = new ArrayList<>();
    private List<BossBullet> bossBullets = new ArrayList<>();
    private Timer timer;
    private boolean inGame = true;
    private String message = "";
    private int bossX, bossY, bossDX;
    private int bossHealth = 300;
    private int bossShootCounter = 0;
    private static final int BOSS_WIDTH = 160;
    private static final int BOSS_HEIGHT = 120;
    private static final int BOSS_SPEED = 4;
    private static final int BOSS_SHOOT_DELAY = 40; // frames
    private Random rand = new Random();
    private int transitionFrames = 120; // 2 seconds at 60fps
    private boolean transitionDone = false;
    private AudioPlayer audioPlayer;
    private int playerShotCooldown = 0;
    private int frame = 0;
    
    // Explosion animation fields
    private Image explosionImage;
    private boolean playerExploding = false;
    private int playerExplosionFrame = 0;
    private int playerExplosionTimer = 0;

    public BossScene(Game game, Player player) {
        this.game = game;
        this.player = player;
        loadBossImage();
        loadBossBulletImage();
        loadExplosionFrames();
        setFocusable(true);
        setBackground(Color.black);
        initEntities();
        addKeyListener(new TAdapter());
        requestFocusInWindow();
        playAudio();
        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
    }

    private void loadBossImage() {
        var ii = new ImageIcon("src/images/boss/boss_shoot.png");
        bossImage = ii.getImage();
    }
    private void loadBossBulletImage() {
        var ii = new ImageIcon("src/images/boss/boss_bullet.png");
        bossBulletImage = ii.getImage();
    }
    private void loadExplosionFrames() {
        var ii = new ImageIcon(IMG_EXPLOSION);
        explosionImage = ii.getImage();
    }
    private void initEntities() {
        // Use the player passed from Scene1 (keeps speed and bullet upgrades)
        player.setX(BOARD_WIDTH / 2 - 30);
        player.setY(540); // Match Scene1 Y position
        
        bossX = BOARD_WIDTH / 2 - BOSS_WIDTH / 2;
        bossY = 80;
        bossDX = BOSS_SPEED;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        requestFocusInWindow();
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Draw starfield background
        drawStarfield(g);

        // Draw boss
        if (bossHealth > 0 && transitionDone) {
            g.drawImage(bossImage, bossX, bossY, BOSS_WIDTH, BOSS_HEIGHT, this);
            // Boss health bar
            g.setColor(Color.red);
            g.fillRect(bossX, bossY - 20, (int)(BOSS_WIDTH * (bossHealth / 300.0)), 10);
            g.setColor(Color.white);
            g.drawRect(bossX, bossY - 20, BOSS_WIDTH, 10);
        }

        // Draw player
        if (playerExploding) {
            // Draw explosion animation
            g.drawImage(explosionImage, player.getX(), player.getY(), this);
        } else if (player.isVisible() && player.shouldDraw()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        // Draw player shots
        g.setColor(Color.yellow);
        for (Shot shot : playerShots) {
            if (shot.isVisible())
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }

        // Draw boss bullets
        for (BossBullet b : bossBullets) {
            if (b.visible)
                g.drawImage(bossBulletImage, b.x, b.y, 24, 32, this);
        }

        // Draw HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Boss HP: " + bossHealth, 10, 30);
        g.drawString("Lives: " + player.getLives(), 10, 50);
        g.drawString("Speed: " + ((player.getSpeed() - 2) / 2 + 1) + "/4", 10, 70);
        g.drawString("Multi-Shot: " + player.getMultiShotLevel() + "/4", 10, 90);

        // Transition message
        if (!transitionDone && inGame) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String msg = "Get Ready!";
            int msgW = g.getFontMetrics().stringWidth(msg);
            g.setColor(Color.cyan);
            g.drawString(msg, (d.width - msgW) / 2, d.height / 2);
        }

        // Win/lose message
        if (!inGame) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            int msgW = g.getFontMetrics().stringWidth(message);
            g.setColor(Color.green);
            g.drawString(message, (d.width - msgW) / 2, d.height / 2);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        if (!inGame) return;
        if (!transitionDone) {
            transitionFrames--;
            if (transitionFrames <= 0) transitionDone = true;
            return;
        }
        
        // Handle player explosion animation
        if (playerExploding) {
            playerExplosionTimer++;
            if (playerExplosionTimer >= 30) { // 30 frames = 0.5 seconds per frame
                playerExplosionFrame++;
                playerExplosionTimer = 0;
                if (playerExplosionFrame >= 2) {
                    // Explosion animation complete, end game
                    inGame = false;
                    message = "Game Over";
                    timer.stop();
                    stopAudio();
                    return;
                }
            }
            return; // Don't update anything else during explosion
        }
        frame++;
        boolean explosionSoundPlayedThisFrame = false;
        if (playerShotCooldown > 0) playerShotCooldown--;
        player.updatePowerups();
        // Boss movement
        bossX += bossDX;
        if (bossX < 0 || bossX + BOSS_WIDTH > BOARD_WIDTH) {
            bossDX = -bossDX;
            bossX += bossDX;
        }
        // Boss shooting - more unpredictable patterns
        bossShootCounter++;
        if (bossShootCounter >= BOSS_SHOOT_DELAY) {
            bossShootCounter = 0;
            
            // Random shooting pattern
            int pattern = rand.nextInt(4);
            switch (pattern) {
                case 0: // Single shot
                    int bulletX = bossX + BOSS_WIDTH / 2 - 12;
                    bossBullets.add(new BossBullet(bulletX, bossY + BOSS_HEIGHT));
                    break;
                case 1: // Spread shot (3 bullets)
                    bossBullets.add(new BossBullet(bossX + 20, bossY + BOSS_HEIGHT));
                    bossBullets.add(new BossBullet(bossX + BOSS_WIDTH / 2 - 12, bossY + BOSS_HEIGHT));
                    bossBullets.add(new BossBullet(bossX + BOSS_WIDTH - 32, bossY + BOSS_HEIGHT));
                    break;
                case 2: // Rapid fire (2 bullets close together)
                    bossBullets.add(new BossBullet(bossX + BOSS_WIDTH / 2 - 12, bossY + BOSS_HEIGHT));
                    bossShootCounter = -10; // Shoot again soon
                    break;
                case 3: // Side shots (2 bullets from edges)
                    bossBullets.add(new BossBullet(bossX + 10, bossY + BOSS_HEIGHT));
                    bossBullets.add(new BossBullet(bossX + BOSS_WIDTH - 26, bossY + BOSS_HEIGHT));
                    break;
            }
        }
        // Update boss bullets
        List<BossBullet> toRemove = new ArrayList<>();
        for (BossBullet b : bossBullets) {
            if (b.visible) {
                b.move();
                // Collision with player
                if (b.getBounds().intersects(player.getX(), player.getY(), 48, 48) && !player.isInvulnerable()) {
                    handlePlayerHit();
                    b.visible = false;
                }
                if (b.y > BOARD_HEIGHT || b.x < 0 || b.x > BOARD_WIDTH) b.visible = false;
            } else {
                toRemove.add(b);
            }
        }
        bossBullets.removeAll(toRemove);
        // Update player shots
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : playerShots) {
            if (shot.isVisible()) {
                shot.setY(shot.getY() - 16);
                // Collision with boss
                if (bossHealth > 0 && shot.getX() + 16 > bossX && shot.getX() < bossX + BOSS_WIDTH &&
                    shot.getY() < bossY + BOSS_HEIGHT && shot.getY() > bossY) {
                    bossHealth--;
                    // Play explosion sound
                    if (bossHealth > 0 && !explosionSoundPlayedThisFrame) {
                        try { new AudioPlayer(AUDIO_EXPLOSION).play(); } catch (Exception ex) {}
                        explosionSoundPlayedThisFrame = true;
                    }
                    shot.die();
                    if (bossHealth <= 0) {
                        inGame = false;
                        message = "You Win!";
                        timer.stop();
                        stopAudio();
                        // TODO: Replace with real win audio
                        try { new AudioPlayer(AUDIO_GAME_OVER).play(); } catch (Exception ex) {}
                    }
                }
                if (shot.getY() < 0) shot.die();
            } else {
                shotsToRemove.add(shot);
            }
        }
        playerShots.removeAll(shotsToRemove);
        // Player invulnerability update
        player.updateInvulnerability();
        player.act();
    }

    private void playAudio() {
        try {
            audioPlayer = new AudioPlayer(AUDIO_BOSS_SCENE);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing boss audio: " + e.getMessage());
        }
    }
    private void stopAudio() {
        try {
            if (audioPlayer != null) audioPlayer.stop();
        } catch (Exception e) {
            System.err.println("Error stopping boss audio: " + e.getMessage());
        }
    }
    
    private void handlePlayerHit() {
        // Reduce lives
        int currentLives = player.getLives();
        player.setLives(currentLives - 1);
        
        // Play explosion sound for all player hits
        try { new AudioPlayer(AUDIO_EXPLOSION).play(); } catch (Exception ex) {}
        
        if (currentLives <= 1) {
            // Last life lost - start explosion animation
            playerExploding = true;
            playerExplosionFrame = 0;
            playerExplosionTimer = 0;
            // Play game over sound
            try { new AudioPlayer(AUDIO_GAME_OVER).play(); } catch (Exception ex) {}
        } else {
            // Still have lives - make player temporarily invulnerable
            player.startInvulnerability(90);
        }
    }

    private void drawStarfield(Graphics g) {
        // Create a beautiful starfield background
        for (int i = 0; i < 50; i++) {
            int x = (i * 37) % BOARD_WIDTH; // Distribute stars across screen
            int y = (i * 23 + frame / 2) % BOARD_HEIGHT; // Stars move slowly
            int twinklePhase = (frame + i * 7) % 120;
            float twinkleIntensity = 0.3f + 0.7f * (float)Math.abs(Math.sin(twinklePhase * 0.1));
            
            // Different star sizes and colors
            if (i % 5 == 0) {
                // Bright white stars
                g.setColor(new Color(255, 255, 255, (int)(255 * twinkleIntensity)));
                g.fillOval(x, y, 4, 4);
            } else if (i % 3 == 0) {
                // Light blue stars
                g.setColor(new Color(200, 220, 255, (int)(200 * twinkleIntensity)));
                g.fillOval(x, y, 3, 3);
            } else {
                // Yellow/white stars
                g.setColor(new Color(255, 255, 200, (int)(180 * twinkleIntensity)));
                g.fillOval(x, y, 2, 2);
            }
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!inGame || !transitionDone) return;
            player.keyPressed(e);
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE && playerShotCooldown == 0) {
                if (playerShots.size() < 4) {
                    int level = player.getMultiShotLevel();
                    int baseX = player.getX() + (player.getImage().getWidth(null) / 2) - 12;
                    int y0 = player.getY();
                    int spread = 24;
                    int startOffset = -((level - 1) * spread) / 2;
                    for (int i = 0; i < level; i++) {
                        int offset = startOffset + i * spread;
                        Shot shot = new Shot(baseX + offset, y0);
                        playerShots.add(shot);
                    }
                    // Play gun sound
                    try { new AudioPlayer(AUDIO_GUN).play(); } catch (Exception ex) {}
                    playerShotCooldown = 10;
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    // Enhanced boss bullet class with different speeds and patterns
    private static class BossBullet {
        int x, y;
        int dx, dy; // Movement direction
        boolean visible = true;
        int speed;
        
        BossBullet(int x, int y) {
            this.x = x;
            this.y = y;
            this.speed = 6 + (int)(Math.random() * 4); // Random speed between 6-9
            
            // Balanced random movement pattern (left or right)
            double angle = (Math.random() - 0.5) * Math.PI / 3; // -30 to +30 degrees
            this.dx = (int)(Math.sin(angle) * speed);
            this.dy = speed; // Always move down
        }
        
        void move() {
            x += dx;
            y += dy;
        }
        
        java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle(x, y, 24, 32);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stopAudio();
    }
} 