package de.jumpingpxl.labymod.addon.listener;

import de.jumpingpxl.labymod.addon.util.guis.BetterAddonGui;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.gui.screen.ScreenOpenEvent;
import net.labymod.settings.LabyModAddonsGui;
import net.labymod.settings.LabyModModuleEditorGui;
import net.minecraft.client.gui.screen.Screen;

public class ScreenOpenListener {

	private Screen previousScreen;

	@Subscribe
	public void onScreenOpen(ScreenOpenEvent event) {
		Screen newScreen = event.getScreen();
		System.out.println(newScreen);
		LabyModAddonsGui.isRestartRequired();
		if (newScreen instanceof LabyModModuleEditorGui) {
			//if(newScreen instanceof ) {
			event.setScreen(new BetterAddonGui(previousScreen, newScreen));
		}

		previousScreen = newScreen;
	}
}
