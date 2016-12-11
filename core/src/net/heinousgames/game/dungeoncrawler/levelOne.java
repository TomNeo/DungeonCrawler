package net.heinousgames.game.dungeoncrawler;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by User on 12/10/2016.
 */

public class LevelOne extends HeinousMap {

    private DungeonCrawler game;
    private boolean keyFound, ghostFound, ringFound;
    private TextureRegion ghostTexture, exitTexture;
    private boolean justDied = false;

    public LevelOne(DungeonCrawler game) {
        super(new TmxMapLoader().load("levels/latest.tmx"));
        setX(10);
        setY(15);
        this.game = game;

        ghostTexture = new TextureRegion(new Texture("gfx/ghost.png"), 0, 0, 320, 479);
        exitTexture = new TextureRegion(new Texture("levels/exit.png"), 0, 0, 32, 32);
    }

    @Override
    public void renderBackground(SpriteBatch batch) {
    }

    @Override
    public void renderForeground(SpriteBatch batch) {
        renderExit(batch, keyFound);
        renderDeadGhost(batch, ghostFound);
    }

    @Override
    public void update(DungeonCrawler.Player player) {

        clearTile(player);
        if (game.dead && !justDied){
            game.theme.stop();
            game.scream.play();
            justDied = true;
        }
    }

    /**
     * Clears tiles after visiting them. Includes black tiles and tiles with items
     */
    private void clearTile(DungeonCrawler.Player player) {
        // black layer (unvisited tiles)
        TiledMapTileLayer layer = (TiledMapTileLayer)this.getMap().getLayers().get(3);

        // layer that has ghosts, rings, and key
        TiledMapTileLayer layer2 = (TiledMapTileLayer)this.getMap().getLayers().get(1);

        // clear the black tile so it appears you visited this spot
        if (layer.getCell((int)player.pos.x, (int)player.pos.y) != null) {
            layer.setCell((int)player.pos.x, (int)player.pos.y, null);
        }

        // ghost, ring, key layer
        if (layer2.getCell((int)player.pos.x, (int)player.pos.y) != null) {
            if (layer2.getCell((int)player.pos.x, (int)player.pos.y).getTile().getProperties().containsKey("monster")) {
                // boolean is submitted to render method to draw the ghost full screen
                ghostFound = true;
                game.dead = true;
            } else if (layer2.getCell((int)player.pos.x, (int)player.pos.y).getTile().getProperties().containsKey("key")) {
                // boolean is submitted to render method to tell it to draw the exit stairs
                keyFound = true;
                layer.setCell((int)player.pos.x, (int)player.pos.y, null);
            } else if (layer2.getCell((int)player.pos.x, (int)player.pos.y).getTile().getProperties().containsKey("ring")) {
                // boolean is submitted to render method to tell it to add cash
                ringFound = true;
                if (ringFound) {
                    ringFound = false;
                    player.cash += 5;
                }
                // clear the tile from both the black layer and the layer that has the item
                layer.setCell((int)player.pos.x, (int)player.pos.y, null);
                layer2.setCell((int)player.pos.x, (int)player.pos.y, null);
            }
        }
    }


    /**
     * Determines when it should or should not draw the exit stairs image
     * @param drawExit - the decider on whether the texture is drawn or not
     */
    private void renderExit(SpriteBatch batch, boolean drawExit) {
        if (drawExit) {
            TiledMapTileLayer layer = (TiledMapTileLayer)this.getMap().getLayers().get(4);
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
