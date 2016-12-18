package net.heinousgames.game.dungeoncrawler;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Main Class running the methods needed to play the game. The order of the methods in this
 * class is reflected in how they are ordered in the "render()" method.
 * @author Steve Hanus - 4-13-2014
 *
 */
public class DungeonCrawler implements ApplicationListener {

	private enum PlayerState {
		MOVING_LEFT, MOVING_RIGHT, MOVING_UP, MOVING_DOWN;
	}

	private Stage framedStage;
	private Stage bigStage;

	private PlayerState playerState;

	private static float PLAYER_SPEED = 2f;

	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera, bigCamera;
	private TextureRegion playerTexture, openSpotTexture, ghostTexture, exitTexture;
	public Player player;
	private SpriteBatch batch;
	private boolean keyFound, ghostFound, ringFound, isMoving;
	private int x, y;
	public Music theme;
	public Sound scream;
	private int cash;
	public boolean dead = false;
	private long originalTime;

	public HeinousRenderer renderer2;
	private SpriteBatch batch2;
	private MapBuffer MapLoader;
	private Map<String, Boolean> nextMoves;

	private Vector3 touchPos;

	static class Player {
		final Vector2 pos = new Vector2();
		public int cash = 0;
	}

	static class Exit {
		final Vector2 pos = new Vector2();
	}

	@Override
	public void create() {

//		framedStage = new Stage(new ScreenViewport(camera));
//		bigStage = new Stage(new ScreenViewport(bigCamera));

		originalTime = System.currentTimeMillis();

		// player class defined above
		player = new Player();
		// put him in the bottom corner
		player.pos.set(12, 5);
		MapLoader = new MapBuffer(new levelOne(this));
		renderer2 = new HeinousRenderer(this, MapLoader, 1/120f);
		batch2 = (SpriteBatch)renderer2.getBatch();

		nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);

		// in-game currency
		cash = 0;

		// make true to displayer exit
		keyFound = false;

		// make true to increase in-game-currency
		ringFound = false;

		// zelda theme, must change
		theme = Gdx.audio.newMusic(Gdx.files.internal("sfx/theme.mp3"));
//		theme.setLooping(true);
//		theme.play();

		// pretty sure this scream was free online for when ghost is found
		scream = Gdx.audio.newSound(Gdx.files.internal("sfx/scream.mp3"));

		playerTexture = new TextureRegion(new Texture("gfx/player3.png"));
		openSpotTexture = new TextureRegion(new Texture("gfx/arrow3.png"));
		ghostTexture = new TextureRegion(new Texture("gfx/ghost.png"), 0, 0, 320, 479);
		exitTexture = new TextureRegion(new Texture("levels/exit.png"), 0, 0, 32, 32);

		// tiles are 16x16
		map = new TmxMapLoader().load("levels/steves3.tmx");
		//renderer = new OrthogonalTiledMapRenderer(map, 1/16f);
		//batch = (SpriteBatch)renderer.getBatch();
		batch = (SpriteBatch)renderer2.getBatch();
		// width and height of loaded map in tiles
		x = 16;
		y = 9;

		// create an orthographic camera, shows us 10x15 units of the world
		camera = new OrthographicCamera();
		bigCamera = new OrthographicCamera(1920, 1080);

//		framedStage.
		camera.setToOrtho(false, x, y);
		camera.position.x = player.pos.x;
		camera.position.y = player.pos.y;
		camera.update();

	}

	/**
	 * Use this method to see visitable tiles near the tile you submit
	 * (usually the player's current position)
	 * @param x - the x position of the tile you submit
	 * @param y - the y position of the tile you submit
	 * @return - a map data structure consisting of strings "left", "right", "up",
	 * 			 and "down" and the booleans stating if you can move in that direction
	 * 	 THIS METHOD LOOKS AT THE LOWEST LAYER FROM TILED AS THE "MOVEABLE" PATH
	 */
	private Map<String, Boolean> checkNearbyTilesForMovement(float x, float y) {
		Map<String, Boolean> returnedMap = new HashMap<String, Boolean>();
		TiledMapTileLayer layer = (TiledMapTileLayer)MapLoader.getCurrentMap().getMap().getLayers().get(1);

		// x+1 == right of the submitted position
		Cell cell = layer.getCell((int)x + 1, (int)y);
		if (cell != null) {
			returnedMap.put("right", true);
		} else {
			returnedMap.put("right", false);
		}

		// x-1 == left of the submitted position
		cell = layer.getCell((int)x-1, (int)y);
		if (cell != null) {
			returnedMap.put("left", true);
		} else {
			returnedMap.put("left", false);
		}
		// y+1 == above the submitted position
		cell = layer.getCell((int)x, (int)y+1);
		if (cell != null) {
			returnedMap.put("up", true);
		} else {
			returnedMap.put("up", false);
		}
		// y-1 == below the submitted position
		cell = layer.getCell((int)x, (int)y-1);
		if (cell != null) {
			returnedMap.put("down", true);
		} else {
			returnedMap.put("down", false);
		}

		return returnedMap;
	}

	private void updatePlayer(float deltaTime) {

		if (isMoving) {
			if (playerState == PlayerState.MOVING_DOWN && player.pos.y >= (int)touchPos.y) {
				player.pos.y -= (PLAYER_SPEED * deltaTime);
				if (player.pos.y <= (int)touchPos.y) {
					player.pos.y = (int)touchPos.y;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			} else if (playerState == PlayerState.MOVING_UP && player.pos.y <= (int)touchPos.y) {
				player.pos.y += (PLAYER_SPEED * deltaTime);
				if (player.pos.y >= (int)touchPos.y) {
					player.pos.y = (int)touchPos.y;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			} else if (playerState == PlayerState.MOVING_LEFT && player.pos.x >= (int)touchPos.x) {
				player.pos.x -= (PLAYER_SPEED * deltaTime);
				if (player.pos.x <= (int)touchPos.x) {
					player.pos.x = (int)touchPos.x;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			} else if (playerState == PlayerState.MOVING_RIGHT && player.pos.x <= (int)touchPos.x) {
				player.pos.x += (PLAYER_SPEED * deltaTime);
				if (player.pos.x >= (int)touchPos.x) {
					player.pos.x = (int)touchPos.x;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			}
		} else {
			if (Gdx.input.isTouched()) {
				touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);
				if ((int) touchPos.x == player.pos.x + 1 && nextMoves.get("right")) {
					playerState = PlayerState.MOVING_RIGHT;
					isMoving = true;
					originalTime = System.currentTimeMillis();
				} else if ((int) touchPos.x == player.pos.x - 1 && nextMoves.get("left")) {
					playerState = PlayerState.MOVING_LEFT;
					isMoving = true;
					originalTime = System.currentTimeMillis();
				} else if ((int) touchPos.y == player.pos.y + 1 && nextMoves.get("up")) {
					playerState = PlayerState.MOVING_UP;
					isMoving = true;
					originalTime = System.currentTimeMillis();
				} else if ((int) touchPos.y == player.pos.y - 1 && nextMoves.get("down")) {
					playerState = PlayerState.MOVING_DOWN;
					isMoving = true;
					originalTime = System.currentTimeMillis();
				}
			}
		}
	}

	/**
	 * Clears tiles after visiting them. Includes black tiles and tiles with items
	 */
	private void clearTile() {
		// black layer (unvisited tiles)
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(3);

		// layer that has ghosts, rings, and key
		TiledMapTileLayer layer2 = (TiledMapTileLayer)map.getLayers().get(1);

		// clear the black tile so it appears you visited this spot
		if (layer.getCell((int)player.pos.x, (int)player.pos.y) != null) {
			layer.setCell((int)player.pos.x, (int)player.pos.y, null);
		}

		// ghost, ring, key layer
		if (layer2.getCell((int)player.pos.x, (int)player.pos.y) != null) {
			if (layer2.getCell((int)player.pos.x, (int)player.pos.y).getTile().getProperties().containsKey("monster")) {
				// boolean is submitted to render method to draw the ghost full screen
				ghostFound = true;
			} else if (layer2.getCell((int)player.pos.x, (int)player.pos.y).getTile().getProperties().containsKey("key")) {
				// boolean is submitted to render method to tell it to draw the exit stairs
				keyFound = true;
				layer.setCell((int)player.pos.x, (int)player.pos.y, null);
			} else if (layer2.getCell((int)player.pos.x, (int)player.pos.y).getTile().getProperties().containsKey("ring")) {
				// boolean is submitted to render method to tell it to add cash
				ringFound = true;
				if (ringFound) {
					ringFound = false;
					cash += 5;
				}
				// clear the tile from both the black layer and the layer that has the item
				layer.setCell((int)player.pos.x, (int)player.pos.y, null);
				layer2.setCell((int)player.pos.x, (int)player.pos.y, null);
			}
		}

	}

	@Override
	public void render(){
		render(Gdx.graphics.getDeltaTime());
	}

	public void render(float deltaTime) {
		// clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updatePlayer(/*System.currentTimeMillis() - originalTime, */deltaTime);
		MapLoader.getCurrentMap().update(player);
		//clearTile();
		renderer2.setView(camera);
		renderer2.render();

		camera.position.x = player.pos.x;
		camera.position.y = player.pos.y;
		camera.update();
		//renderer.setView(camera);
		//renderer.render();
		renderer2.HeinousRender(nextMoves);

		//batch.begin();
		//renderPlayer();
		//renderExit(keyFound);
		//placeOpenSpots();
		//renderDeadGhost(ghostFound);
		//batch.end();
	}

	/**
	 * draw player... duh
	 */
	private void renderPlayer() {
		batch.draw(playerTexture, player.pos.x, player.pos.y, 1, 1);
	}

	/**
	 * draw the image on nearby spots indicating that this is a spot you can move to
	 */
	private void placeOpenSpots() {

		if (checkNearbyTilesForMovement(player.pos.x, player.pos.y).get("left")) {
			batch.draw(openSpotTexture, player.pos.x-1, player.pos.y, 1, 1);
		}

		if (checkNearbyTilesForMovement(player.pos.x, player.pos.y).get("right")) {
			batch.draw(openSpotTexture, player.pos.x+1, player.pos.y, 1, 1);
		}

		if (checkNearbyTilesForMovement(player.pos.x, player.pos.y).get("up")) {
			batch.draw(openSpotTexture, player.pos.x, player.pos.y+1, 1, 1);
		}

		if (checkNearbyTilesForMovement(player.pos.x, player.pos.y).get("down")) {
			batch.draw(openSpotTexture, player.pos.x, player.pos.y-1, 1, 1);
		}

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

	/**
	 * Determines when it should or should not draw the exit stairs image
	 * @param drawExit - the decider on whether the texture is drawn or not
	 */
	private void renderExit(boolean drawExit) {
		if (drawExit) {
			TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(4);
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
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
	private void renderDeadGhost(boolean visitedGhost) {
		if (visitedGhost) {
			theme.stop();
			scream.play();
			batch.draw(ghostTexture, 0, 0, x, y);
			visitedGhost = false;
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
