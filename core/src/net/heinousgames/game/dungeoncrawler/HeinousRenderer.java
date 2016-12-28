package net.heinousgames.game.dungeoncrawler;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.Map;

/**
 * Created by User on 12/10/2016.
 */

public class HeinousRenderer extends OrthogonalTiledMapRenderer {

    private DungeonCrawler game;
    private MapBuffer mapBuffer;

    private TextureRegion playerTexture, openSpotTexture;

    public HeinousRenderer(DungeonCrawler game,  TiledMap inputMap) {
        super(inputMap);
        this.game = game;
        playerTexture = new TextureRegion(new Texture("gfx/player3.png"));
        openSpotTexture = new TextureRegion(new Texture("gfx/arrow3.png"));
    }


    public HeinousRenderer(DungeonCrawler game, MapBuffer mapBuffer, float unitScale){
        super(mapBuffer.getCurrentMap().getMap(),unitScale);
        this.mapBuffer = mapBuffer;
        this.game = game;
        playerTexture = new TextureRegion(new Texture("gfx/player3.png"));
        openSpotTexture = new TextureRegion(new Texture("gfx/arrow3.png"));
    }


    /**
     * draw player... duh
     */
    private void renderPlayer() {
        if(!game.dead) {
            batch.draw(playerTexture, game.player.pos.x, game.player.pos.y, 1, 1);
        }
    }

    public void HeinousRender(Map<String, Boolean> availableMoves){
        this.map = mapBuffer.getCurrentMap().map;
        render();
        batch.begin();
        mapBuffer.getCurrentMap().renderBackground((SpriteBatch)batch);
        renderPlayer();
        mapBuffer.getCurrentMap().renderForeground((SpriteBatch)batch);
        placeOpenSpots(availableMoves);
        batch.end();
    }

    private void placeOpenSpots(Map<String, Boolean> available) {
        if(!game.dead) {
            if (available.get("left")) {
                batch.draw(openSpotTexture, game.player.pos.x - 1, game.player.pos.y, 1, 1);
            }
            if (available.get("right")) {
                batch.draw(openSpotTexture, game.player.pos.x + 1, game.player.pos.y, 1, 1);
            }
            if (available.get("up")) {
                batch.draw(openSpotTexture, game.player.pos.x, game.player.pos.y + 1, 1, 1);
            }
            if (available.get("down")) {
                batch.draw(openSpotTexture, game.player.pos.x, game.player.pos.y - 1, 1, 1);
            }
        }
    }

}
