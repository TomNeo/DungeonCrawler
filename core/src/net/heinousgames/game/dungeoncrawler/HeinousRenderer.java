package net.heinousgames.game.dungeoncrawler;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by User on 12/10/2016.
 */

public class HeinousRenderer extends OrthogonalTiledMapRenderer {

    private DungeonCrawler game;
    private MapBuffer mapBuffer;

    private TextureRegion playerTexture;


    public HeinousRenderer(DungeonCrawler game, MapBuffer mapBuffer, float unitScale){
        super(mapBuffer.getCurrentMap().getMap(),unitScale);
        this.mapBuffer = mapBuffer;
        this.game = game;
        playerTexture = new TextureRegion(new Texture("gfx/player3.png"));
    }


    /**
     * draw player... duh
     */
    private void renderPlayer() {
        if (!game.dead) {
            batch.draw(playerTexture, game.player.pos.x, game.player.pos.y, 1, 1);
        }
    }

    public void setMap(TiledMap value){
        map = value;
    }

    public void HeinousRender(/*Map<String, Boolean> availableMoves*/){
        render();
        batch.begin();
        mapBuffer.getCurrentMap().renderBackground((SpriteBatch)batch);
        renderPlayer();
        mapBuffer.getCurrentMap().renderForeground((SpriteBatch)batch);
        batch.end();
    }

}
