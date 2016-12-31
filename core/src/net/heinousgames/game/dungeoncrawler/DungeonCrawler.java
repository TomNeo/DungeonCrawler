package net.heinousgames.game.dungeoncrawler;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Main Class running the methods needed to play the game. The order of the methods in this
 * class is reflected in how they are ordered in the "render()" method.
 * @author Steve Hanus - 4-13-2014
 *
 */
public class DungeonCrawler implements ApplicationListener {
	public boolean dead = false;

	private enum PlayerState {
		MOVING_LEFT, MOVING_RIGHT, MOVING_UP, MOVING_DOWN;
	}

	private Stage framedStage;
	private Stage bigStage;
	private Stage uiStage;
	private Camera uiCamera;

	private Image enabledDownImg, enabledLeftImg, enabledRightImg, enabledUpImg;

	private Texture enabledDown, enabledUp, enabledLeft, enabledRight;

	private PlayerState playerState;

	private static float PLAYER_SPEED = 4f;

	private TiledMap map;
	private OrthographicCamera camera, bigCamera;
	private TextureRegion playerTexture, openSpotTexture, ghostTexture, exitTexture;
	public Player player;
	private boolean keyFound, ghostFound, ringFound, isMoving;
	private int currentX, currentY;
	public Music theme;
	public Sound scream;

	public HeinousRenderer renderer;
	public MapBuffer MapLoader;
	public Map<String, Boolean> nextMoves;

	private Vector3 touchPos;

	static class Player {
		final Vector2 pos = new Vector2();
		public int cash = 0;
	}

	@Override
	public void create() {

		enabledDown = new Texture("gfx/controls/flatDarkDown.png");
		enabledUp = new Texture("gfx/controls/flatDarkUp.png");
		enabledLeft = new Texture("gfx/controls/flatDarkLeft.png");
		enabledRight = new Texture("gfx/controls/flatDarkRight.png");

		enabledUpImg = new Image(enabledUp);
		enabledLeftImg = new Image(enabledLeft);
		enabledRightImg = new Image(enabledRight);
		enabledDownImg = new Image(enabledDown);

		enabledDownImg.setPosition(200, 120);
		enabledUpImg.setPosition(200, 280);
		enabledLeftImg.setPosition(120, 200);
		enabledRightImg.setPosition(280, 200);

		enabledDownImg.setSize(120, 120);
		enabledRightImg.setSize(120, 120);
		enabledLeftImg.setSize(120, 120);
		enabledUpImg.setSize(120, 120);

		enabledUpImg.setName("up");
		enabledDownImg.setName("down");
		enabledLeftImg.setName("left");
		enabledRightImg.setName("right");

		uiCamera = new OrthographicCamera(1920, 1080);
		uiCamera.position.x = 960;
		uiCamera.position.y = 540;
		uiStage = new Stage(new ScreenViewport());
		uiStage.getViewport().setCamera(uiCamera);

		uiStage.addActor(enabledLeftImg);
		uiStage.addActor(enabledDownImg);
		uiStage.addActor(enabledUpImg);
		uiStage.addActor(enabledRightImg);

		// player class defined above
		player = new Player();
		// put him in the bottom corner
		player.pos.set(0,0);
		MapLoader = new MapBuffer(this, new VillageMap1(this));//LevelThree(this));
		MapLoader.initialLoad();
		renderer = new HeinousRenderer(this, MapLoader, 1/120f);

		nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);

		// zelda theme, must change
		theme = Gdx.audio.newMusic(Gdx.files.internal("sfx/church.mp3"));
		theme.setLooping(true);
		//theme.play();

		// pretty sure this scream was free online for when ghost is found
		scream = Gdx.audio.newSound(Gdx.files.internal("sfx/scream.mp3"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 16, 9);//MapLoader.getCurrentMap().getX(), MapLoader.getCurrentMap().getY());
		camera.position.x = player.pos.x;
		camera.position.y = player.pos.y;

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
	public Map<String, Boolean> checkNearbyTilesForMovement(float x, float y) {
		System.out.println("x: " + x + "  y: " + y);
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

	private void updatePlayer(float deltaTime) {

		if (isMoving) {
			if (playerState == PlayerState.MOVING_DOWN) {
				player.pos.y -= (PLAYER_SPEED * deltaTime);
				if (player.pos.y <= currentY - 1) {
					player.pos.y = currentY - 1;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			} else if (playerState == PlayerState.MOVING_UP) {
				player.pos.y += (PLAYER_SPEED * deltaTime);
				if (player.pos.y >= currentY + 1) {
					player.pos.y = currentY + 1;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			} else if (playerState == PlayerState.MOVING_LEFT) {
				player.pos.x -= (PLAYER_SPEED * deltaTime);
				if (player.pos.x <= currentX - 1) {
					player.pos.x = currentX - 1;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			} else if (playerState == PlayerState.MOVING_RIGHT) {
				player.pos.x += (PLAYER_SPEED * deltaTime);
				if (player.pos.x >= currentX + 1) {
					player.pos.x = currentX + 1;
					playerState = null;
					isMoving = false;
					nextMoves = checkNearbyTilesForMovement(player.pos.x, player.pos.y);
				}
			}
		} else {
			if (Gdx.input.isTouched()) {
				currentX = (int)player.pos.x;
				currentY = (int)player.pos.y;
				touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				uiCamera.unproject(touchPos);

				Actor hitActor = uiStage.hit(touchPos.x, touchPos.y, false);

				if (hitActor != null) {
					if (hitActor.getName().equals("up") && nextMoves.get("up")) {
						System.out.println("UP");
						playerState = PlayerState.MOVING_UP;
						isMoving = true;
					} else if (hitActor.getName().equals("down") && nextMoves.get("down")) {
						System.out.println("DOWN");
						playerState = PlayerState.MOVING_DOWN;
						isMoving = true;
					} else if (hitActor.getName().equals("left") && nextMoves.get("left")) {
						System.out.println("LEFT");
						playerState = PlayerState.MOVING_LEFT;
						isMoving = true;
					} else if (hitActor.getName().equals("right") && nextMoves.get("right")) {
						System.out.println("RIGHT");
						playerState = PlayerState.MOVING_RIGHT;
						isMoving = true;
					}
				}
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
		MapLoader.update(deltaTime);

		camera.position.x = player.pos.x;
		camera.position.y = player.pos.y;
		camera.update();
		renderer.setView(camera);
		renderer.HeinousRender();

		uiCamera.update();
		uiStage.draw();
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
