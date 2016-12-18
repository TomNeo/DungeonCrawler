package net.heinousgames.game.dungeoncrawler;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Created by User on 12/11/2016.
 */

public abstract class HeinousMap {

    public TiledMap map;
    private int x;
    private int y;

    public HeinousMap(TiledMap input){
        map = input;
    }

    public TiledMap getMap(){
        return map;
    }

    /*
     * Place to include in the levels things to render behind the player (but infront of the tile Layers)
     */
    public abstract void renderBackground(SpriteBatch batch);

    /*
     * Place to include in the levels things to render infront of the player
     */
    public abstract void renderForeground(SpriteBatch batch);

    public abstract void update(float deltaTime);

    public void setX(int input){
        x = input;
    }

    public void setY(int input){
        y = input;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public abstract void reset();
}
