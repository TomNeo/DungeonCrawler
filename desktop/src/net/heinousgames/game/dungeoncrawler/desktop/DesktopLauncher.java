package net.heinousgames.game.dungeoncrawler.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.heinousgames.game.dungeoncrawler.DungeonCrawler;
import net.heinousgames.game.dungeoncrawler.TestMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//new LwjglApplication(new DungeonCrawler(), config);

		new LwjglApplication(new TestMain(), config);
	}
}
