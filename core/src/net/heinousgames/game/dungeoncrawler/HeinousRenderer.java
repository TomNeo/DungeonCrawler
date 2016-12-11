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

    private TestMain.Player player;
    private MapBuffer mapBuffer;

    private TextureRegion playerTexture, openSpotTexture;

    public HeinousRenderer(TestMain.Player player,  TiledMap inputMap) {
        super(inputMap);
        this.player = player;
        playerTexture = new TextureRegion(new Texture("gfx/players.png"), 0, 0, 32, 32);
        openSpotTexture = new TextureRegion(new Texture("gfx/players.png"), 64, 0, 32, 32);
    }


    public HeinousRenderer(TestMain.Player player, MapBuffer mapBuffer, float unitScale){
        super(mapBuffer.getCurrentMap().getMap(),unitScale);
        this.mapBuffer = mapBuffer;
        this.player = player;
        playerTexture = new TextureRegion(new Texture("gfx/players.png"), 0, 0, 32, 32);
        openSpotTexture = new TextureRegion(new Texture("gfx/players.png"), 64, 0, 32, 32);
    }


    /**
     * draw player... duh
     */
    private void renderPlayer() {
        batch.draw(playerTexture, player.pos.x, player.pos.y, 1, 1);
    }

    public void HeinousRender(Map<String, Boolean> availableMoves){
        batch.begin();
        mapBuffer.getCurrentMap().renderBackground((SpriteBatch)batch);
        renderPlayer();
        mapBuffer.getCurrentMap().renderForeground((SpriteBatch)batch);
        placeOpenSpots(availableMoves);
        batch.end();
    }

    private void placeOpenSpots(Map<String, Boolean> available) {
        if (available.get("left")) {
            batch.draw(openSpotTexture, player.pos.x-1, player.pos.y, 1, 1);
        }
        if (available.get("right")) {
            batch.draw(openSpotTexture, player.pos.x+1, player.pos.y, 1, 1);
        }
        if (available.get("up")) {
            batch.draw(openSpotTexture, player.pos.x, player.pos.y+1, 1, 1);
        }
        if (available.get("down")) {
            batch.draw(openSpotTexture, player.pos.x, player.pos.y-1, 1, 1);
        }
    }

}
