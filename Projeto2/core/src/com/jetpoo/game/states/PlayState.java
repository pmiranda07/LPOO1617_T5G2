package com.jetpoo.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jetpoo.game.JetPoo;
import com.jetpoo.game.actors.HeavyGuy;
import com.jetpoo.game.actors.Hero;
import com.jetpoo.game.actors.NormalGuy;
import com.jetpoo.game.actors.Obstacle;
import com.jetpoo.game.actors.PowerUp;
import com.jetpoo.game.useful.Animation;

import java.util.Random;
import java.util.Vector;


public class PlayState extends State{

    private enum Condition {
        running, falling, acelerating;
    }

    public static final int GRAVITY = 10;
    private int speed;
    private int counter = 0;
    private int score;
    private boolean game_pause;
    private boolean screenTouched;
    private Condition condition;
    private Random randomGenerator;
    private float msg_time = 0;

    /*
    * ACTORS
    * */
    private Hero hero;
    private Vector<PowerUp> powerUps;
    private Vector<Obstacle> lasers;

    /*
    * TEXTURES
    * */
    private Animation runningAnimation;
    private Animation aceleratingAnimation;
    private Texture fallingAnimation;
    private TextureRegion actual;
    private Texture ground;
    private Texture ceiling;
    private Texture bottom;
    private Animation laserAnimation;
    private Animation numbers;
    private Texture score_board;
    private Animation powerup;
    private Texture msg;
    private Texture msg_heavy;
    private Texture msg_score;
    private Texture msg_speed;

    /**
     * Sounds
     */
    private Music aceleratingSound;
    private Sound powerUpSound;
    private Music music;


    /*
    * Ground, ceiling and bottom
    **/
    private Rectangle ground_bounds;
    private Rectangle ceiling_bounds;
    private Vector2 groundPos1, groundPos2;
    private Vector2 bottomPos1, bottomPos2;

    /**
     * PlayState contructor
     *
     * @param gsm game state manager
     * @param game the actual game
     */
    public PlayState(GameStateManager gsm, JetPoo game) {
        super(gsm, game);

        start();
    }

    /**
     * Start the play state
     */
    private void start(){
        getAssets();
        initVariables();
        initTouchListener();

    }

    /**
     * Get all assets needed from Asset Manager
     */
    private void getAssets(){
        ground = game.getAssetManager().get("background/ground.png", Texture.class);
        bottom = game.getAssetManager().get("background/Menu_bg1.png", Texture.class);
        ceiling = game.getAssetManager().get("background/ceiling.png", Texture.class);
        Texture tmp = game.getAssetManager().get("character/Character-run.png", Texture.class);
        runningAnimation = new Animation(new TextureRegion(tmp), 6, 0.9f);
        tmp = game.getAssetManager().get("character/Character-acelerating.png", Texture.class);
        aceleratingAnimation = new Animation(new TextureRegion(tmp), 7, 0.5f );
        fallingAnimation = game.getAssetManager().get("character/Character-falling.png", Texture.class);
        tmp = game.getAssetManager().get("others/laser.png", Texture.class);
        laserAnimation = new Animation(new TextureRegion(tmp), 7, 1 );
        tmp = game.getAssetManager().get("others/numbers.png", Texture.class);
        numbers = new Animation(new TextureRegion(tmp), 10, 1 );
        score_board = game.getAssetManager().get("others/score_board.png", Texture.class);
        aceleratingSound = game.getAssetManager().get("sounds/sound.ogg", Music.class);
        powerUpSound = game.getAssetManager().get("sounds/powerup.ogg", Sound.class);
        music = game.getAssetManager().get("sounds/music_level.ogg", Music.class);
        tmp = game.getAssetManager().get("others/coin.png", Texture.class);
        powerup = new Animation(new TextureRegion(tmp), 4, 1 );
        msg_heavy = game.getAssetManager().get("messages/msg_heavy.png", Texture.class);
        msg_score = game.getAssetManager().get("messages/msg_score.png", Texture.class);
        msg_speed = game.getAssetManager().get("messages/msg_speed.png", Texture.class);

    }

    /**
     * Init all variables from PlayState
     */
    private void initVariables(){
        cam.setToOrtho(false, JetPoo.WIDTH, JetPoo.HEIGHT);
        speed = 100;
        score = 0;
        game_pause = false;
        hero = new NormalGuy(100,64);
        screenTouched = false;
        condition = Condition.running;
        lasers = new Vector<Obstacle>();
        actual = runningAnimation.getFrame(0);

        ceiling_bounds = new Rectangle(0,(JetPoo.HEIGHT-128), JetPoo.WIDTH, 128);
        ground_bounds = new Rectangle(0,0, JetPoo.WIDTH, 64);
        groundPos1 = new Vector2(0, 0);
        groundPos2 = new Vector2(ground.getWidth(), 0);

        bottomPos1 = new Vector2(0,0);
        bottomPos2 = new Vector2(bottom.getWidth(),0);

        powerUps = new Vector<PowerUp>();
        randomGenerator = new Random();

        music.setLooping(true);
        music.play();
        music.setVolume(0.30f);

    }

    /**
     * Init Input adapter
     */
    private void initTouchListener(){
        Gdx.input.setInputProcessor(new InputAdapter(){

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                screenTouched = true;
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                screenTouched = false;
                return true;
            }

        });

    }

    /**
     * Handle input
     */
    public void handleInput(){
        if (screenTouched){
            hero.jump();
            if (!aceleratingSound.isPlaying()){
                aceleratingSound.play();
                aceleratingSound.setLooping(true);
            }

        }
        else {
            hero.setAcelerating(false);
            aceleratingSound.stop();

        }
    }

    /**
     * Test collisions
     */
    public void testCollisions(){
        hero.collideGround(ground_bounds);
        hero.collideCeiling(ceiling_bounds);
    }

    /**
     * Update Hero animation texture
     * @param dt time since last update
     */
    void updateHeroTexture(float dt){
        Condition a;

        if (hero.isOntheGround()){
            a = Condition.running;
        }
        else if (hero.isAcelerating()){
            a = Condition.acelerating;
        }
        else a = Condition.falling;

        switch (a){
            case running:
                if (condition == a)
                {
                    runningAnimation.update(dt);
                    actual = runningAnimation.getFrame();
                }
                else {
                    runningAnimation.reset();
                    actual = runningAnimation.getFrame();
                }
                break;

            case falling:
                actual = new TextureRegion(fallingAnimation);
                break;

            case acelerating:
                if (condition == a)
                {
                    aceleratingAnimation.update(dt);
                    actual = aceleratingAnimation.getFrame();
                }
                else {
                    aceleratingAnimation.reset();
                    actual = aceleratingAnimation.getFrame();
                }
                break;
        }

        condition = a;

    }

    /**
     * Update PlayState
     *
     * @param dt time since last update
     */
    @Override
    public void update(float dt) {

        if(!game_pause && dt < 1) {
            handleInput();
            updateScene(dt);

            hero.update(dt);
            fixbug();
            if (hero instanceof HeavyGuy){
                if (hero.getCounter() <= 0){
                    hero = new NormalGuy(hero.getX(), hero.getY());
                }
            }
            runningAnimation.update(dt);
            testCollisions();
            updateHeroTexture(dt);
            msg_time -= dt;
        }


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        sb.draw(bottom, bottomPos1.x, bottomPos1.y);
        sb.draw(bottom, bottomPos2.x, bottomPos2.y);

        sb.draw(ground, groundPos1.x, - ground.getHeight()/2);
        sb.draw(ground, groundPos2.x, - ground.getHeight()/2);
        sb.draw(ceiling, groundPos1.x, JetPoo.HEIGHT-ceiling.getHeight());
        sb.draw(ceiling, groundPos2.x, JetPoo.HEIGHT-ceiling.getHeight());

        displayScore(sb);


        for(int i=0; i < lasers.size(); i++){
            sb.draw(laserAnimation.getFrame(), lasers.get(i).getX(),lasers.get(i).getY(), lasers.get(i).getWidth(), lasers.get(i).getHeight());
        }


        if (condition == Condition.acelerating){
            sb.draw(actual, hero.getX()-10, hero.getY()-8, 120, 110);

        }
        else sb.draw(actual, hero.getX(), hero.getY(), 100, 100);

        for (int i = 0; i < powerUps.size(); i++){
            sb.draw(powerup.getFrame(), powerUps.get(i).getX(), powerUps.get(i).getY(), 50, 50);
        }

        if (msg_time > 0){
            sb.draw(msg, 350, JetPoo.HEIGHT - 130, 450, 150);
        }


        sb.end();
    }
    
    private void updateScene(float dt){
        counter += speed * dt;
        moveSceen(dt, ground, groundPos1, groundPos2);
        moveSceen(dt, bottom, bottomPos1, bottomPos2);
        obstaclesFactory(dt);
        powerUpFactory(dt);

    }

    private void moveSceen(float dt, Texture text, Vector2 v1, Vector2 v2){

        if (v1.x <= - text.getWidth())
        {
            v1.x = v2.x + text.getWidth()/2;
        }
        else if (v2.x <= - text.getWidth())
        {
            v2.x =v1.x+ text.getWidth()/2;
        }

        v1.x -= speed * dt;
        v2.x -= speed*dt;

    }

    private void obstaclesFactory(float dt){

        boolean increase = false;
        for (int i= 0; i < lasers.size(); i++){
            if (lasers.get(i).getX() < -100){
                lasers.remove(i);
                score += 1;
                increase = true;
            }
            else {
                lasers.get(i).update(speed * dt);
                if (hero.collideLaser(lasers.get(i))){
                    gsm.set(new GameOverState(gsm, game, score));
                }
            }
        }

        if (counter > 500){
            lasers.add(new Obstacle(JetPoo.WIDTH + 10));
            counter = 0;
        }

        if (increase)
            speed += 10;
        laserAnimation.update(dt);
    }

    private void powerUpFactory(float dt){

        for(int i=0; i < powerUps.size(); i++){
            powerUps.get(i).update(speed * dt);

            if (hero.catchPowerUp( powerUps.get(i)))
            {
                int type = powerUps.get(i).getType();

                switch (type){
                    case 0:
                        hero = new HeavyGuy(hero.getX(), hero.getY());
                        msg = msg_heavy;
                        break;
                    case 1:
                        score += 10;
                        msg = msg_score;
                        break;
                    case 2:
                        if (speed > 30)
                            speed -= 20;
                        msg = msg_speed;
                        break;
                }
                msg_time = 3;
                powerUpSound.play();

                powerUps.remove(i);
            }
            else if (powerUps.get(i).getX() < 100){
                powerUps.remove(i);
            }

        }
        int rand1;

        if (counter > 400){
            rand1 = randomGenerator.nextInt(100);
        }
        else rand1 = randomGenerator.nextInt(2000);

        if (rand1 == 0){
            PowerUp a = new PowerUp();
            if (powerUps.size() < 1)
                powerUps.add(a);
        }

        powerup.update(dt);

    }

    private void displayScore(SpriteBatch sb){

        /*
        * Mostra números de até 4 digitos
        * */
        int space = 10;
        int number = score;

        sb.draw(score_board, 14 , JetPoo.HEIGHT - 102, 242, 100);

        for(int i=0; i < 4; i++ ){

            switch (number % 10){
                case 0:
                    sb.draw(numbers.getFrame(0), 20 +(3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 1:
                    sb.draw(numbers.getFrame(1), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 2:
                    sb.draw(numbers.getFrame(2), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 3:
                    sb.draw(numbers.getFrame(3), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 4:
                    sb.draw(numbers.getFrame(4), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 5:
                    sb.draw(numbers.getFrame(5), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 6:
                    sb.draw(numbers.getFrame(6), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 7:
                    sb.draw(numbers.getFrame(7), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 8:
                    sb.draw(numbers.getFrame(8), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
                case 9:
                    sb.draw(numbers.getFrame(9), 20 + (3-i)*(space+50) , JetPoo.HEIGHT - 90, 50, 80);
                    break;
            }
            number /= 10;

        }


    }

    private void fixbug(){
        if (hero.getY() < 60){
            hero.setPosition(new Vector2(100, 63));
            hero.setVelocity(new Vector2(0, 0));

        }

    }
}
