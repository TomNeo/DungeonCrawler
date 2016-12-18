package net.heinousgames.game.dungeoncrawler;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by User on 12/10/2016.
 */

public class LevelThree extends HeinousMap {

    private DungeonCrawler game;
    private boolean keyFound, ghostFound, ringFound;
    private TextureRegion ghostTexture, exitTexture;
    private boolean justDied = false;
    private float deathLength = 0f;
    private Vector2[] darkTiles;
    private Vector2[] collidables;

    public LevelThree(DungeonCrawler game) {
        super(new TmxMapLoader().load("levels/steves3.tmx"));
        setX(16);
        setY(9);
        this.game = game;
        ghostTexture = new TextureRegion(new Texture("gfx/ghost.png"), 0, 0, 320, 479);
        exitTexture = new TextureRegion(new Texture("levels/exit.png"), 0, 0, 32, 32);
        darkTiles = loadDarkTiles();
        //collidables = loadCollidables();
    }

    private Vector2[] loadCollidables() {
        // Collidable Objects (unvisited tiles)
        TiledMapTileLayer layer = (TiledMapTileLayer)this.getMap().getLayers().get(1);
        layer.setVisible(false);

        ArrayList<Vector2> returnedList = new ArrayList();

        for( int x = 0; x < layer.getWidth(); x++){
            for(int y = 0 ; y < layer.getHeight(); y++){
                if(layer.getCell(x,y) != null){
                    returnedList.add(new Vector2(x,y));
                }
            }
        }
        return returnedList.toArray(new Vector2[returnedList.size()]);
    }

    private  Vector2[] loadDarkTiles() {
        // black layer (unvisited tiles)
        TiledMapTileLayer layer = (TiledMapTileLayer)this.getMap().getLayers().get(4);
        layer.setVisible(false);

        ArrayList<Vector2> returnedList = new ArrayList();

        for( int x = 0; x < layer.getWidth(); x++){
            for(int y = 0 ; y < layer.getHeight(); y++){
                if(layer.getCell(x,y) != null){
                    returnedList.add(new Vector2(x,y));
                }
            }
        }
        return returnedList.toArray(new Vector2[returnedList.size()]);
    }

    public LevelThree(TiledMap input) {
        super(input);
    }


    @Override
    public void renderBackground(SpriteBatch batch) {
        //renderCollidableTiles(batch);
        renderDarkTiles(batch);
    }

    private void renderDarkTiles(SpriteBatch batch) {
        TiledMapTileLayer darkLayer = (TiledMapTileLayer)this.getMap().getLayers().get(4);
        for(int i = 0; i < darkTiles.length; i++){
            if(darkTiles[i]!=null){
                batch.draw(darkLayer.getCell((int)darkTiles[i].x,(int)darkTiles[i].y).getTile().getTextureRegion().getTexture(),darkTiles[i].x,darkTiles[i].y, 1, 1);
            }
        }
    }

    private void renderCollidableTiles(SpriteBatch batch) {
        TiledMapTileLayer collidableLayer = (TiledMapTileLayer)this.getMap().getLayers().get(1);
        for(int i = 0; i < collidables.length; i++){
            if(collidables[i]!=null){
                batch.draw(collidableLayer.getCell((int)collidables[i].x,(int)collidables[i].y).getTile().getTextureRegion().getTexture(),collidables[i].x,collidables[i].y, 1, 1);
            }
        }
    }

    @Override
    public void renderForeground(SpriteBatch batch) {
        renderExit(batch, keyFound);
        renderDeadGhost(batch, ghostFound);
    }

    @Override
    public void update(float deltaTime) {

        if(justDied){
            if(deathLength > 2f){
                game.scream.stop();
                game.theme.play();
                this.reset();
            }else{
                deathLength = deathLength + deltaTime;
            }
        }
        clearTile();
        if (game.dead && !justDied){
            game.theme.stop();
            game.scream.play();
            justDied = true;
        }
    }

    @Override
    public void reset() {
        game.player.pos.x = 0;
        game.player.pos.y = 0;
        game.nextMoves = game.checkNearbyTilesForMovement(game.player.pos.x,game.player.pos.y);
        justDied = false;
        deathLength = 0;
        game.dead = false;
        ghostFound = false;
        keyFound = false;
        darkTiles = loadDarkTiles();
        collidables = loadCollidables();
    }

    /**
     * Clears tiles after visiting them. Includes black tiles and tiles with items
     */
    private void clearTile() {
        /*
        TiledMapTileLayer collidableLayer = (TiledMapTileLayer)this.getMap().getLayers().get(1);
        for(int i = 0; i < collidables.length; i++){
            if(collidables[i] != null && collidables[i].x == game.player.pos.x && collidables[i].y == game.player.pos.y){
                if (collidableLayer.getCell((int)game.player.pos.x, (int)game.player.pos.y).getTile().getProperties().containsKey("monster")) {
                    // boolean is submitted to render method to draw the ghost full screen
                    ghostFound = true;
                    game.dead = true;
                } else if (collidableLayer.getCell((int)game.player.pos.x, (int)game.player.pos.y).getTile().getProperties().containsKey("key")) {
                    // boolean is submitted to render method to tell it to draw the exit stairs
                    keyFound = true;
                } else if (collidableLayer.getCell((int)game.player.pos.x, (int)game.player.pos.y).getTile().getProperties().containsKey("ring")) {
                    // boolean is submitted to render method to tell it to add cash
                    ringFound = true;
                    if (ringFound) {
                        ringFound = false;
                        game.player.cash += 5;
                    }
                }
                collidables[i] = null;
            }
        }

*/
        for(int i = 0; i < darkTiles.length; i++){
            if(darkTiles[i] != null && darkTiles[i].x == game.player.pos.x && darkTiles[i].y == game.player.pos.y){
                darkTiles[i] = null;
            }
        }
    }


    /**
     * Determines when it should or should not draw the exit stairs image
     * @param drawExit - the decider on whether the texture is drawn or not
     */
    private void renderExit(SpriteBatch batch, boolean drawExit) {
        if (drawExit) {
            TiledMapTileLayer layer = (TiledMapTileLayer)this.getMap().getLayers().get(1);
            for (int i = 0; i < getX(); i++) {
                for (int j = 0; j < getY(); j++) {
                    if (layer.getCell(i, j) != null) {
                        if (layer.getCell(i, j).getTile().getProperties().containsKey("exit"))
                            batch.draw(exitTexture, i, j, 1, 1);
                    }
                }
            }
        }
    }

    /**
     * Determines whether or not the ghost should appear the scare the player
     * @param visitedGhost - the decider on whether the texture is drawn or not
     */
    private void renderDeadGhost(SpriteBatch batch, boolean visitedGhost) {
        if (visitedGhost) {
            game.renderer.getBatch().draw(ghostTexture, 0, 0, getX(), getY());
            //visitedGhost = false;
        }
    }
}
