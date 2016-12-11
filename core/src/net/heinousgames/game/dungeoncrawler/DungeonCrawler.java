package net.heinousgames.game.dungeoncrawler;

		import java.util.HashMap;
		import java.util.Map;

		import com.badlogic.gdx.ApplicationListener;
		import com.badlogic.gdx.Gdx;
		import com.badlogic.gdx.audio.Music;
		import com.badlogic.gdx.audio.Sound;
		import com.badlogic.gdx.graphics.GL20;
		import com.badlogic.gdx.graphics.OrthographicCamera;
		import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
		import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
		import com.badlogic.gdx.math.Vector2;
		import com.badlogic.gdx.math.Vector3;

/**
 * Main Class running the methods needed to play the game. The order of the methods in this
 * class is reflected in how they are ordered in the "render()" method.
 * @author Steve Hanus - 4-13-2014
 *
 */
public class DungeonCrawler implements ApplicationListener {
	public boolean dead = false;
	private OrthographicCamera camera;
	public Player player;
	public Music theme;
	public Sound scream;

	public HeinousRenderer renderer;
	private MapBuffer MapLoader;
	private Map<String, Boolean> nextMoves;

	static class Player {
		final Vector2 pos = new Vector2();
		public int cash = 0;
	}

	@Override
	public void create() {

		// player class defined above
		player = new Player();
		// put him in the bottom corner
		player.pos.set(0, 0);
		MapLoader = new MapBuffer(new LevelTwo(this));
		renderer = new HeinousRenderer(this, MapLoader, 1/16f);

		nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);

		// zelda theme, must change
		theme = Gdx.audio.newMusic(Gdx.files.internal("sfx/theme.mp3"));
		theme.setLooping(true);
		theme.play();

		// pretty sure this scream was free online for when ghost is found
		scream = Gdx.audio.newSound(Gdx.files.internal("sfx/scream.mp3"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, MapLoader.getCurrentMap().getX(), MapLoader.getCurrentMap().getY());
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
		TiledMapTileLayer layer = (TiledMapTileLayer)MapLoader.getCurrentMap().getMap().getLayers().get(0);

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

	private void updatePlayer() {
		Vector3 touchPos = new Vector3();
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if ((int) touchPos.x == player.pos.x + 1 && (int) touchPos.y == player.pos.y && nextMoves.get("right")) {
				player.pos.x += 1;
				nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
			} else if (Gdx.input.isTouched() && (int) touchPos.x == player.pos.x - 1 && (int) touchPos.y == player.pos.y && nextMoves.get("left")) {
				player.pos.x -= 1;
				nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
			} else if (Gdx.input.isTouched() && (int) touchPos.x == player.pos.x && (int) touchPos.y == player.pos.y + 1 && nextMoves.get("up")) {
				player.pos.y += 1;
				nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
			} else if (Gdx.input.isTouched() && (int) touchPos.x == player.pos.x && (int) touchPos.y == player.pos.y - 1 && nextMoves.get("down")) {
				player.pos.y -= 1;
				nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
			}
		}
	}

	@Override
	public void render() {
		// clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updatePlayer();
		MapLoader.getCurrentMap().update(player);

		camera.setToOrtho(false, MapLoader.getCurrentMap().getX(), MapLoader.getCurrentMap().getY());
		camera.update();
		renderer.setView(camera);
		renderer.HeinousRender(nextMoves);

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
