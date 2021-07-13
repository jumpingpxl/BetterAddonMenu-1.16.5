package de.jumpingpxl.labymod.addon;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.jumpingpxl.labymod.addon.listener.ScreenOpenListener;
import de.jumpingpxl.labymod.addon.util.Settings;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;

import java.util.List;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date
 * @project LabyMod-Addon: -1.16.5
 */

public class BetterAddonMenu extends LabyModAddon {

	public static final String VERSION = "1";

	private Settings settings;

	@Override
	public void onEnable() {
		settings = new Settings(this);
		getApi().getEventService().registerListener(new ScreenOpenListener());
	}

	@Override
	public void loadConfig() {
		settings.loadConfig();
	}

	@Override
	protected void fillSettings(List<SettingsElement> settingsElements) {
		settings.fillSettings(settingsElements);
	}

	@Override
	public void onRenderPreview(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		boolean hover = (mouseX > 5 && mouseX < 5 + 20 && mouseY > 6 && mouseY < 6 + 20);
		if (hover) {
			TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L,
					"§aTest microphone " + mouseX + ";" + mouseY);
			LabyMod.getInstance().getDrawUtils().drawHoveringText(stack, mouseX, mouseY, "§cTest");
		}

		LabyMod.getInstance().getDrawUtils().drawString(stack,
				ModColor.cl('c') + "Not connected to " + "voice chat " + mouseX + ";" + mouseY, 5, 5);
	}
}
