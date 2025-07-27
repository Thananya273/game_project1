package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.powerup.MultiShot;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Enemy;
import gdd.sprite.EnemyShot;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<EnemyShot> enemyShots;
    private Player player;
    // private Shot shot;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;
    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    private boolean bossTransitioning = false;
    private int bossTransitionFrames = 0;

    private int playerShotCooldown = 0;
    private boolean playerExploding = false;
    private int playerExplosionFrame = 0;
    private int playerExplosionTimer = 0;

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
    }

    private void initAudio() {
        try {
            audioPlayer = new AudioPlayer(AUDIO_SCENE1);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        try {
            String csvFile = "src/data/spawn_data.csv";
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line;
            boolean firstLine = true; // Skip header
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header line
                }
                
                String[] values = line.split(",");
                if (values.length >= 4) {
                    int frame = Integer.parseInt(values[0].trim());
                    String type = values[1].trim();
                    int x = Integer.parseInt(values[2].trim());
                    int y = Integer.parseInt(values[3].trim());
                    
                    spawnMap.put(frame, new SpawnDetails(type, x, y));
                }
            }
            br.close();
            System.out.println("Loaded " + spawnMap.size() + " spawn entries from CSV");
            
        } catch (IOException e) {
            System.err.println("Error loading spawn data from CSV: " + e.getMessage());
            // Fallback to hardcoded data if CSV fails
            loadFallbackSpawnData();
        }
    }
    
    private void loadFallbackSpawnData() {
        // Fallback data if CSV loading fails
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", 100, 0));
        spawnMap.put(200, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(300, new SpawnDetails("Alien1", 300, 0));
        spawnMap.put(400, new SpawnDetails("Alien1", 400, 0));
        spawnMap.put(500, new SpawnDetails("Alien1", 100, 0));
        spawnMap.put(600, new SpawnDetails("Alien2", 150, 0));
        spawnMap.put(650, new SpawnDetails("Alien2", 300, 0));
        spawnMap.put(800, new SpawnDetails("PowerUp-MultiShot", 250, 0));
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }
    
    public Player getPlayer() {
        return player;
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        enemyShots = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        // shot = new Shot();
    }

    private void drawMap(Graphics g) {
        // Draw scrolling starfield background

        // Calculate smooth scrolling offset (1 pixel per frame)
        int scrollOffset = (frame) % BLOCKHEIGHT;

        // Calculate which rows to draw based on screen position
        int baseRow = (frame) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2; // +2 for smooth scrolling

        // Loop through rows that should be visible on screen
        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            // Calculate which MAP row to use (with wrapping)
            int mapRow = (baseRow + screenRow) % MAP.length;

            // Calculate Y position for this row
            // int y = (screenRow * BLOCKHEIGHT) - scrollOffset;
            int y = BOARD_HEIGHT - ( (screenRow * BLOCKHEIGHT) - scrollOffset );

            // Skip if row is completely off-screen
            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            // Draw each column in this row
            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    // Calculate X position
                    int x = col * BLOCKWIDTH;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Calculate twinkling effect based on frame
        int twinklePhase = (frame + x + y) % 120; // Different phase for each cluster
        float twinkleIntensity = 0.3f + 0.7f * (float)Math.abs(Math.sin(twinklePhase * 0.1));
        
        // Main star (larger, bright white)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.setColor(new Color(255, 255, 255, (int)(255 * twinkleIntensity)));
        g.fillOval(centerX - 3, centerY - 3, 6, 6);
        
        // Medium stars (white/light blue)
        g.setColor(new Color(200, 220, 255, (int)(200 * twinkleIntensity)));
        g.fillOval(centerX - 18, centerY - 12, 4, 4);
        g.fillOval(centerX + 14, centerY - 10, 4, 4);
        g.fillOval(centerX - 10, centerY + 15, 4, 4);
        g.fillOval(centerX + 12, centerY + 18, 4, 4);
        
        // Small stars (yellow/white)
        g.setColor(new Color(255, 255, 200, (int)(180 * twinkleIntensity)));
        g.fillOval(centerX - 25, centerY + 8, 3, 3);
        g.fillOval(centerX + 20, centerY - 18, 3, 3);
        g.fillOval(centerX - 8, centerY - 22, 3, 3);
        g.fillOval(centerX + 10, centerY + 25, 3, 3);
        
        // Tiny stars (white, subtle)
        g.setColor(new Color(255, 255, 255, (int)(120 * twinkleIntensity)));
        g.fillOval(centerX - 30, centerY - 5, 2, 2);
        g.fillOval(centerX + 25, centerY + 12, 2, 2);
        g.fillOval(centerX - 15, centerY - 28, 2, 2);
        g.fillOval(centerX + 18, centerY + 30, 2, 2);
        g.fillOval(centerX - 5, centerY + 32, 2, 2);
        g.fillOval(centerX + 8, centerY - 30, 2, 2);
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (playerExploding) {
            // Draw explosion animation
            var ii = new ImageIcon(IMG_EXPLOSION);
            g.drawImage(ii.getImage(), player.getX(), player.getY(), this);
        } else if (player.isVisible() && player.shouldDraw()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }
    
    private void drawEnemyShots(Graphics g) {

        for (EnemyShot enemyShot : enemyShots) {

            if (enemyShot.isVisible()) {
                g.drawImage(enemyShot.getImage(), enemyShot.getX(), enemyShot.getY(), this);
            }
        }
    }
    
    private void drawHUD(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Lives
        g.drawString("Lives: " + player.getLives(), 10, 30);
        
        // Score
        g.drawString("Score: " + player.getScore() + "/9900", 10, 50);
        
        // Speed Level (as fraction)
        int speedLevel = (player.getSpeed() - 2) / 2 + 1; // Convert speed 2,4,6,8 to 1,2,3,4
        g.drawString("Speed: " + speedLevel + "/4", 10, 70);
        
        // Multi-Shot Level
        g.drawString("Multi-Shot: " + player.getMultiShotLevel() + "/4", 10, 90);
    }
    
    private void handlePlayerHit() {
        // Reduce lives
        int currentLives = player.getLives();
        player.setLives(currentLives - 1);
        
        // Create explosion at player position
        explosions.add(new Explosion(player.getX(), player.getY()));
        
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
            // Still have lives - respawn player
            respawnPlayer();
        }
    }
    
    private void respawnPlayer() {
        // Respawn player at the exact same position every time
        player.setX(270); // Same as START_X from Player class
        player.setY(540); // Same as START_Y from Player class
        
        // Reset player movement
        player.keyReleased(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT));
        player.keyReleased(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT));
        
        // Make player temporarily invulnerable (2 seconds at 60fps = 120 frames)
        player.startInvulnerability(120);
    }

    private void drawBombing(Graphics g) {

        for (Enemy enemy : enemies) {
            var bomb = enemy.getBomb();
            if (!bomb.isDestroyed()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);

        g.setColor(Color.green);

        if (inGame) {
            if (bossTransitioning) {
                // Draw everything frozen
                drawMap(g);
                drawExplosions(g);
                drawPowreUps(g);
                drawAliens(g);
                drawPlayer(g);
                drawShot(g);
                drawEnemyShots(g);
                drawBombing(g);
                drawHUD(g);
                // Draw transition message
                g.setFont(new Font("Arial", Font.BOLD, 48));
                String msg = "Boss Approaching!";
                int msgW = g.getFontMetrics().stringWidth(msg);
                g.setColor(Color.cyan);
                g.drawString(msg, (d.width - msgW) / 2, d.height / 2);
                Toolkit.getDefaultToolkit().sync();
                return;
            }
            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawEnemyShots(g);
            drawBombing(g);
            drawHUD(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            // Game over - show message like BossScene
            g.setFont(new Font("Arial", Font.BOLD, 48));
            int msgW = g.getFontMetrics().stringWidth(message);
            g.setColor(Color.green);
            g.drawString(message, (d.width - msgW) / 2, d.height / 2);
            
           }

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {

        if (bossTransitioning) {
            bossTransitionFrames--;
            if (bossTransitionFrames <= 0) {
                timer.stop();
                game.loadBossScene();
            }
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
                    timer.stop();
                    return;
                }
            }
            return; // Don't update anything else during explosion
        }
        
        boolean explosionSoundPlayedThisFrame = false;
        if (playerShotCooldown > 0) playerShotCooldown--;
        player.updatePowerups();

        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                case "Alien2":
                    Enemy enemy2 = new Alien2(sd.x, sd.y);
                    enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-MultiShot":
                    // Handle multi-shot item spawn
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                default:
                    System.out.println("Unknown spawn type: " + sd.type);
                    break;
            }
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();
        player.updateInvulnerability();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                    // Play powerup collection sound
                    try { new AudioPlayer(AUDIO_POWERUP).play(); } catch (Exception ex) {}
                }
            }
        }

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
                
                // Check collision with player
                if (enemy.collidesWith(player) && !player.isInvulnerable()) {
                    // Handle player hit (same as getting shot)
                    handlePlayerHit();
                    // Destroy the enemy that hit the player
                    enemy.setDying(true);
                    explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                    deaths++;
                    player.addScore(100); // Still get points for destroying the enemy
                    // Play explosion sound
                    if (!explosionSoundPlayedThisFrame) {
                        try { new AudioPlayer(AUDIO_EXPLOSION).play(); } catch (Exception ex) {}
                        explosionSoundPlayedThisFrame = true;
                    }
                }
                
                // Check if enemy should shoot
                if (enemy.shouldShoot()) {
                    enemyShots.add(enemy.createShot());
                }
            }
        }
        
        // Enemy shots
        List<EnemyShot> enemyShotsToRemove = new ArrayList<>();
        for (EnemyShot enemyShot : enemyShots) {
            if (enemyShot.isVisible()) {
                enemyShot.act();
                
                // Check collision with player
                if (enemyShot.collidesWith(player) && !player.isInvulnerable()) {
                    // Handle player hit
                    handlePlayerHit();
                    enemyShot.die();
                    enemyShotsToRemove.add(enemyShot);
                }
            } else {
                enemyShotsToRemove.add(enemyShot);
            }
        }
        enemyShots.removeAll(enemyShotsToRemove);
        
        // Explosions
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                explosion.act();
            }
        }

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + enemy.getImage().getWidth(null))
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + enemy.getImage().getHeight(null))) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        player.addScore(100); // Add 100 points for each enemy destroyed
                        // Play explosion sound
                        if (!explosionSoundPlayedThisFrame) {
                            try { new AudioPlayer(AUDIO_EXPLOSION).play(); } catch (Exception ex) {}
                            explosionSoundPlayedThisFrame = true;
                        }
                        // Start boss transition if score threshold reached
                        if (player.getScore() >= BOSS_SCORE && inGame && !bossTransitioning) {
                            bossTransitioning = true;
                            bossTransitionFrames = 120; // 2 seconds
                        }
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                int y = shot.getY();
                // y -= 4;
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // enemies
        // for (Enemy enemy : enemies) {
        //     int x = enemy.getX();
        //     if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        //         direction = -1;
        //         for (Enemy e2 : enemies) {
        //             e2.setY(e2.getY() + GO_DOWN);
        //         }
        //     }
        //     if (x <= BORDER_LEFT && direction != 1) {
        //         direction = 1;
        //         for (Enemy e : enemies) {
        //             e.setY(e.getY() + GO_DOWN);
        //         }
        //     }
        // }
        // for (Enemy enemy : enemies) {
        //     if (enemy.isVisible()) {
        //         int y = enemy.getY();
        //         if (y > GROUND - ALIEN_HEIGHT) {
        //             inGame = false;
        //             message = "Invasion!";
        //         }
        //         enemy.act(direction);
        //     }
        // }
        // Bomb logic
        for (Enemy enemy : enemies) {
            // Update bomb regardless of enemy visibility (bombs continue falling after enemy dies)
            enemy.updateBomb();
            
            if (enemy.isVisible()) {
                // Randomly drop a bomb
                if (enemy.getBomb().isDestroyed() && randomizer.nextInt(180) == 0) {
                    enemy.dropBomb();
                }
            }
            
            // Bomb-player collision (check for all bombs, even from dead enemies)
            var bomb = enemy.getBomb();
            if (!bomb.isDestroyed() && bomb.getX() + 16 > player.getX() && bomb.getX() < player.getX() + 48 &&
                bomb.getY() + 16 > player.getY() && bomb.getY() < player.getY() + 48 && !player.isInvulnerable()) {
                handlePlayerHit();
                try { new AudioPlayer(AUDIO_EXPLOSION).play(); } catch (Exception ex) {}
                bomb.setDestroyed(true);
            }
        }
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame && playerShotCooldown == 0) {
                if (shots.size() < 4) {
                    int level = player.getMultiShotLevel();
                    int baseX = x + (player.getImage().getWidth(null) / 2) - 12;
                    int y0 = y;
                    int spread = 24;
                    int startOffset = -((level - 1) * spread) / 2;
                    for (int i = 0; i < level; i++) {
                        int offset = startOffset + i * spread;
                        Shot shot = new Shot(baseX + offset, y0);
                        shots.add(shot);
                    }
                    // Play gun sound
                    try { new AudioPlayer(AUDIO_GUN).play(); } catch (Exception ex) {}
                    playerShotCooldown = 10;
                }
            }

        }
    }
}
