package com.jetpoo.game.actors;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.jetpoo.game.JetPoo;

import java.util.Random;

/**
 * Represents a bonus for the hero. Can be other hero, an increase on score or a decrease on speed of the sceene
 *
 * Created by davidfalcao on 09/06/17.
 */

public class PowerUp {
    private Vector2 position;

    /*
    * Type 0: Change Hero to heavyGuy
    * Type 1: increase score by 10
    * Type 2: decrease velocity by 20
    **/
    private int type;
    private Circle bounds;
    private Random randomGenerator;

    /**
     * @brief PowerUp's constructor
     */
    public PowerUp() {
        randomGenerator = new Random();
        type = randomGenerator.nextInt(3);
        initPosition();
        bounds = new Circle(position.x, position.y, 25);
    }
    /**
     * @brief init PowerUp's Position
     */
    public void initPosition(){
        int ground = 64;
        int ceiling = 128;
        position = new Vector2();
        this.position.x = 1100;
        position.y = randomGenerator.nextInt(JetPoo.HEIGHT-ceiling-ground-50) + ground+25;
    }

    /**
     * @brief update PowerUp's Position
     * @param delta update difference
     */
    public void update(float delta){
        position.x -= delta;
        bounds.setPosition(position);
    }

    /**
     * @brief returns the PowerUp's bounds
     *
     * @return PowerUp's Bounds
     */
    public Circle getBounds() {
        return bounds;
    }
    /**
     * @brief returns the PowerUp's type
     *
     * @return PowerUp's Type
     */
    public int getType() {
        return type;
    }
    /**
     * @brief returns the PowerUp's x position
     * @return PowerUp's x position
     */
    public float getX(){
        return position.x - 25;
    }
    /**
     * @brief returns the PowerUp's y position
     * @return PowerUp's y position
     */
    public float getY(){
        return position.y - 25;
    }

    /**
     * @brief sets the PowerUp's Position
     * @param x PowerUp's Position
     * @param y PowerUp's Position
     */
    public void setPosition(int x, int y){
        position.x = x;
        position.y = y;
    }

}
