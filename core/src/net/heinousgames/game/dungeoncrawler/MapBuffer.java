package net.heinousgames.game.dungeoncrawler;

import java.util.ArrayList;

/**
 * Created by User on 12/10/2016.
 */

public class MapBuffer {

    private HeinousMap current;
    private DungeonCrawler game;
    public ArrayList<HeinousMap> loaded = new ArrayList<HeinousMap>();

    public MapBuffer(DungeonCrawler game, HeinousMap initialMap){
        this.game = game;
        current = initialMap;
        loaded.add(current);
    }

    public void moveThroughDoor(DoorLink door){
        if(!door.finalMap.equals(door.initialMap)){
            changeMap(door.finalMap);
        }
        game.player.pos.x = door.x2;
        game.player.pos.y = door.y2;
        game.nextMoves = game.checkNearbyTilesForMovement(game.player.pos.x,game.player.pos.y);
    }

    public void changeMap(HeinousMap nextMap){
        current = nextMap;
    }

    public HeinousMap getCurrentMap(){
        return current;
    }

    public void update(float deltaTime){
        current.update(deltaTime);
        for (DoorLink door : current.doors){
            if(game.player.pos.x==door.x1&&game.player.pos.y==door.y1){
                moveThroughDoor(door);
                return;
            }
        }
    }

    public void initialLoad(){
        current.onLoad();
    }

}
