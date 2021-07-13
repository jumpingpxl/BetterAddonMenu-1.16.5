package de.jumpingpxl.labymod.addon.util;

import com.google.gson.JsonObject;
import de.jumpingpxl.labymod.addon.BetterAddonMenu;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.ColorPicker;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

import java.awt.*;
import java.util.List;

public class Settings {

	private final BetterAddonMenu betterAddonMenu;

	public Settings(BetterAddonMenu betterAddonMenu) {
		this.betterAddonMenu = betterAddonMenu;
	}

	public void loadConfig() {

	}

	public void fillSettings(List<SettingsElement> subSettings) {
		subSettings.add(new HeaderElement("§eAddon v" + BetterAddonMenu.VERSION));

		subSettings.add(
				new KeyElement("§cLol", "keyelement", new ControlElement.IconData(Material.REDSTONE)));
		ColorPickerCheckBoxBulkElement bulkElement = new ColorPickerCheckBoxBulkElement(
				"Background colors");
		ColorPicker pickerBackgroundColorTop = new ColorPicker("BG Top", Color.WHITE, null, 0, 0, 0,
				0);
		pickerBackgroundColorTop.setHasAdvanced(true);
		bulkElement.addColorPicker(pickerBackgroundColorTop);
		ColorPicker pickerBackgroundColorBottom = new ColorPicker("BG Bottom", Color.BLACK, null, 0, 0,
				0, 0);
		pickerBackgroundColorBottom.setHasAdvanced(true);
		bulkElement.addColorPicker(pickerBackgroundColorBottom);
		subSettings.add(bulkElement);
		bulkElement = new ColorPickerCheckBoxBulkElement("Button colors");
		ColorPicker pickerButtonColor = new ColorPicker("Button", Color.RED, null, 0, 0, 0, 0);
		pickerButtonColor.setHasAdvanced(true);
		bulkElement.addColorPicker(pickerButtonColor);
		ColorPicker pickerButtonColorHover = new ColorPicker("Hover", Color.BLUE, null, 0, 0, 0, 0);
		pickerButtonColorHover.setHasAdvanced(true);
		bulkElement.addColorPicker(pickerButtonColorHover);
		ColorPicker pickerButtonColorOutline = new ColorPicker("Outline", Color.GREEN, null, 0, 0, 0,
				0);
		pickerButtonColorOutline.setHasAdvanced(true);
		bulkElement.addColorPicker(pickerButtonColorOutline);
		CheckBox checkBoxMCStyle = new CheckBox("MC Style", CheckBox.EnumCheckBoxValue.ENABLED, null
				, 0,
				0, 0, 0);
		bulkElement.addCheckbox(checkBoxMCStyle);
		subSettings.add(bulkElement);

		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
		subSettings.add(new BooleanElement("§ctest", new ControlElement.IconData(Material.GRASS)));
	}

	private JsonObject getConfig() {
		return betterAddonMenu.getConfig();
	}

	private void saveConfig() {
		betterAddonMenu.saveConfig();
	}
}
