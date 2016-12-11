package net.heinousgames.game.dungeoncrawler;

/**
 * Created by User on 12/10/2016.
 */

public class MapBuffer {

    private HeinousMap current;

    public MapBuffer(HeinousMap initialMap){
        current = initialMap;
    }

    public void changeMap(HeinousMap nextMap){
        current = nextMap;
    }

    public HeinousMap getCurrentMap(){
        return current;
    }

}
