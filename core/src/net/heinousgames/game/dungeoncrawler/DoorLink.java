package net.heinousgames.game.dungeoncrawler;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by User on 12/28/2016.
 */

public class DoorLink {

    public HeinousMap initialMap;
    public HeinousMap finalMap;
    public float x1;
    public float y1;
    public float x2;
    public float y2;


    DoorLink(HeinousMap fromMap, HeinousMap toMap, Vector2 initial, Vector2 resulting){
        this(fromMap,toMap,initial.x,initial.y,resulting.x,resulting.y);
    }

    DoorLink(HeinousMap fromMap, HeinousMap toMap, float x1, float y1, float x2, float y2){
        initialMap = fromMap;
        finalMap = toMap;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

}
