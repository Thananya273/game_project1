package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites
    public static final int ENEMY_SCALE_FACTOR = 4; // Scaling factor for enemies (can be adjusted)

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 99999;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 20; // Doubled from 10
    public static final int BOSS_SCORE = 9900;

    // Audio file paths
    public static final String AUDIO_TITLE = "src/audio/title.wav";
    public static final String AUDIO_SCENE1 = "src/audio/scene1.wav";
    public static final String AUDIO_BOSS_SCENE = "src/audio/bossScene.wav";
    public static final String AUDIO_EXPLOSION = "src/audio/explosion.wav";
    public static final String AUDIO_GAME_OVER = "src/audio/gameover.wav";
    public static final String AUDIO_GUN = "src/audio/gun.wav";
    public static final String AUDIO_POWERUP = "src/audio/gameover.wav"; // Placeholder - replace with actual powerup sound

    // Images
    public static final String IMG_ENEMY = "src/images/enemy1/enemy1_1.png";
    public static final String IMG_ENEMY1_FRAME2 = "src/images/enemy1/enemy1_2.png";
    public static final String IMG_ENEMY2_FRAME1 = "src/images/enemy2/enemy2_1.png";
    public static final String IMG_ENEMY2_FRAME2 = "src/images/enemy2/enemy2_2.png";
    public static final String IMG_ENEMY2_SHOT = "src/images/enemy2/enemy2_shot.png";
    public static final String IMG_PLAYER = "src/images/player/stationery.png";
    public static final String IMG_PLAYER_LEFT = "src/images/player/left.png";
    public static final String IMG_PLAYER_LEFT2 = "src/images/player/left2.png";
    public static final String IMG_PLAYER_RIGHT = "src/images/player/right.png";
    public static final String IMG_PLAYER_RIGHT2 = "src/images/player/right2.png";
    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_EXPLOSION_FRAME1 = "src/images/explosion/explosion1.png";
    public static final String IMG_EXPLOSION_FRAME2 = "src/images/explosion/explosion2.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";
}
