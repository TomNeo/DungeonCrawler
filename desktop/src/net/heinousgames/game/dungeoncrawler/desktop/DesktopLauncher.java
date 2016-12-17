package net.heinousgames.game.dungeoncrawler.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.heinousgames.game.dungeoncrawler.DungeonCrawler;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1740; //1920; // 960 // 240 // 48
		// 1740
		// 960
		config.height = 960; //1080; // 540 // 135 // 27
//		config.width = 1080;
//		config.height = 1920;
		new LwjglApplication(new DungeonCrawler(), config);

	}
}
