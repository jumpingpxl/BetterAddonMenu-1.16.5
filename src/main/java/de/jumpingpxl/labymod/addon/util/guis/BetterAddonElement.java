package de.jumpingpxl.labymod.addon.util.guis;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import joptsimple.internal.Strings;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class BetterAddonElement {

	private static final Minecraft MINECRAFT = Minecraft.getInstance();
	private static final FontRenderer FONT_RENDERER = MINECRAFT.fontRenderer;
	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();

	private final BetterAddonGui betterAddonGui;
	private final AddonInfo addonInfo;
	private final ScreenArea elementArea;
	private final ScreenArea iconArea;
	private final ScreenArea nameArea;
	private final ScreenArea descriptionArea;
	private LabyModAddon labyModAddon;
	private String[] description;
	private boolean visible;
	private int width;

	protected BetterAddonElement(BetterAddonGui betterAddonGui, AddonInfo addonInfo) {
		this.betterAddonGui = betterAddonGui;
		this.addonInfo = addonInfo;

		elementArea = new ScreenArea();
		iconArea = new ScreenArea();
		nameArea = new ScreenArea();
		descriptionArea = new ScreenArea();
		visible = true;
	}

	public String render(MatrixStack matrixStack, int mouseX, int mouseY, int x, int y, int maxX,
	                     int maxY, boolean selected, ScreenArea area) {
		elementArea.setX(x).setY(y).setMaxX(maxX).setMaxY(maxY);
		int height = maxY - y;
		int width = maxX - x;
		if (selected) {
			DRAW_UTILS.drawRect(matrixStack, x, y, maxX, maxY, Color.BLACK.getRGB());
			DRAW_UTILS.drawRectBorder(matrixStack, x - 1, y - 1, maxX + 1, maxY + 1,
					Color.WHITE.getRGB(),
					1);
		}

		int elementY = y + 2;
		drawIcon(matrixStack, x + 1, y + 1, height - 2);
		String addonName = trimStringToWidth(addonInfo.getName(), 1.0D, width - height);
		nameArea.setX(x + height + 1)
				.setY(elementY)
				.setWidth(FONT_RENDERER.getStringWidth(addonName))
				.setHeight(FONT_RENDERER.FONT_HEIGHT);
		//			nameArea.renderDebug(matrixStack);
		String addonColor = "§f";
		if (addonInfo instanceof OnlineAddonInfo) {
			OnlineAddonInfo onlineAddonInfo = (OnlineAddonInfo) addonInfo;
			if (onlineAddonInfo.isVerified()) {
				addonColor = "§e";
				int featuredX = x - 1;
				int featuredY = y - 1;
				Minecraft.getInstance().getTextureManager().bindTexture(ModTextures.MISC_FEATURED);
				GL11.glPushMatrix();
				GL11.glTranslatef(featuredX + (float) 4.5, featuredY + (float) 4.5, 0);
				GL11.glRotatef(40, 0, 0, 1);
				GL11.glTranslatef(-(featuredX + (float) 4.5), -(featuredY + (float) 4.5), 0);
				DRAW_UTILS.drawTexture(matrixStack, featuredX, featuredY, 0.0D, 0.0D, 255.0D, 255.0D, 9.0D,
						9.0D);
				GL11.glPopMatrix();
				Minecraft.getInstance().getTextureManager().deleteTexture(ModTextures.MISC_FEATURED);
			}
		}

		if (AddonLoader.hasInstalled(addonInfo)) {
			addonColor = "§a";
		}

		DRAW_UTILS.drawString(matrixStack, addonColor + addonName, nameArea.getX(), nameArea.getY());
		elementY += 9;
		DRAW_UTILS.drawString(matrixStack, "§a" + trimStringToWidth(
				"by " + addonInfo.getAuthor() + " §8- " + AddonCategory.getById(addonInfo.getCategory())
						.getName(), 0.5D, width - height), nameArea.getX(), elementY, 0.5);

		descriptionArea.setX(nameArea.getX()).setY(elementY + 5).setWidth(width - height - 4);

		if (Objects.isNull(description) || this.width != width) {
			this.width = width;
			this.description = trimStringToNewLine(addonInfo.getDescription(), 0.7,
					descriptionArea.getWidth());
		}

		int lineCount = 0;
		int maxLineWidth = 0;
		for (String line : description) {
			if (lineCount >= 2 && description.length > 3) {
				line += " §f...";
			}

			int lineWidth = (int) (MINECRAFT.fontRenderer.getStringWidth(line) * 0.7);
			if (lineWidth > maxLineWidth) {
				maxLineWidth = lineWidth;
			}

			DRAW_UTILS.drawString(matrixStack, "§7" + line, descriptionArea.getX(),
					descriptionArea.getY() + 7 * lineCount, 0.7F);
			lineCount++;
			if (lineCount >= 3) {
				break;
			}
		}

		descriptionArea.setWidth(maxLineWidth).setHeight(7 * lineCount);
		if (nameArea.renderTooltip(mouseX, mouseY)) {
			return "§a" + addonInfo.getName() + " §7by " + addonInfo.getAuthor();
		} else if ((lineCount > 2 && description.length > 3) && descriptionArea.renderTooltip(mouseX,
				mouseY)) {
			return "§7" + Strings.join(description, "\n§7");
		}

		return null;
	}

	public void renderSelectedAddon(MatrixStack matrixStack, int mouseX, int mouseY, int width,
	                                int height, ScreenArea addonArea) {
		drawIcon(matrixStack, addonArea.getX(), addonArea.getY() - 64, 64);
		if (Objects.isNull(labyModAddon)) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(addonArea.getX(), addonArea.getY(), 0);
		labyModAddon.onRenderPreview(matrixStack, mouseX - addonArea.getX(), mouseY - addonArea.getY(),
				0);
		GL11.glPopMatrix();
	}

	public void renderAddonSettings(MatrixStack matrixStack, int mouseX, int mouseY,
	                                ScreenArea addonArea, Scrollbar scrollbar) {
		if (!AddonLoader.hasInstalled(addonInfo)) {
			DRAW_UTILS.drawCenteredString(matrixStack, "§cThis Addon isn't installed",
					addonArea.getX() + addonArea.getWidth() / 2D,
					addonArea.getY() + addonArea.getHeight() / 2D - MINECRAFT.fontRenderer.FONT_HEIGHT * 2,
					2D);
			return;
		}

		if (Objects.isNull(labyModAddon)) {
			labyModAddon = AddonLoader.getInstalledAddonByInfo(addonInfo);
		}

		if (Objects.isNull(labyModAddon.getSubSettings()) || labyModAddon.getSubSettings().isEmpty()) {
			DRAW_UTILS.drawCenteredString(matrixStack, "§cThis Addon has no settings",
					addonArea.getX() + addonArea.getWidth() / 2D,
					addonArea.getY() + addonArea.getHeight() / 2D - MINECRAFT.fontRenderer.FONT_HEIGHT * 2,
					2D);
			return;
		}

		int totalEntryHeight = 0;
		int settingX = addonArea.getX() + addonArea.getWidth() / 2 - 120;
		int settingY = (int) (addonArea.getY() + 5 + scrollbar.getScrollY());
		int settingMaxX = addonArea.getX() + addonArea.getWidth() / 2 + 120;

		for (int layer = 0; layer < 3; layer++) {
			totalEntryHeight = 0;
			for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
				if (subSetting instanceof DropDownElement) {
					if (layer == 2) {
						subSetting.draw(matrixStack, settingX, settingY + totalEntryHeight, settingMaxX,
								settingY + totalEntryHeight + subSetting.getEntryHeight(), mouseX, mouseY);
					}
				} else if (subSetting instanceof ColorPickerCheckBoxBulkElement) {
					if (layer == 1) {
						subSetting.draw(matrixStack, settingX, settingY + totalEntryHeight, settingMaxX,
								settingY + totalEntryHeight + subSetting.getEntryHeight(), mouseX, mouseY);
					}
				} else if (layer == 0) {
					subSetting.draw(matrixStack, settingX, settingY + totalEntryHeight, settingMaxX,
							settingY + totalEntryHeight + subSetting.getEntryHeight(), mouseX, mouseY);
				}

				totalEntryHeight += subSetting.getEntryHeight() + 2;
			}
		}


/*		Map<SettingsElement, ScreenArea> renderAfter = Maps.newHashMap();
		for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
			if (subSetting instanceof DropDownElement
					|| subSetting instanceof ColorPickerCheckBoxBulkElement) {
				renderAfter.put(subSetting, new ScreenArea().setX(settingX)
						.setY(settingY + totalEntryHeight)
						.setMaxX(settingMaxX)
						.setMaxY(settingY + totalEntryHeight + subSetting.getEntryHeight()));
			} else {
				subSetting.draw(matrixStack, settingX, settingY + totalEntryHeight, settingMaxX,
						settingY + totalEntryHeight + subSetting.getEntryHeight(), mouseX, mouseY);
			}

			totalEntryHeight += subSetting.getEntryHeight() + 2;
		} */

/*		for (Map.Entry<SettingsElement, ScreenArea> subSettingEntry : renderAfter.entrySet()) {
			ScreenArea area = subSettingEntry.getValue();
			subSettingEntry.getKey().draw(matrixStack, area.getX(), area.getY(), area.getMaxX(),
					area.getMaxY(), mouse X, mouseY);
		} */

		double subSettingSize = labyModAddon.getSubSettings().size();
		scrollbar.setEntryHeight(totalEntryHeight / subSettingSize + 1.5);
		scrollbar.update((int) subSettingSize);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		return elementArea.isMouseOver(mouseX, mouseY) && visible;
	}

	public void mouseClickedSettings(int mouseX, int mouseY, int button) {
		if (!isSubSettingAvailable()) {
			return;
		}

		for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
			if (subSetting instanceof DropDownElement) {
				((DropDownElement<?>) subSetting).onClickDropDown(mouseX, mouseY, button);
			} else if (subSetting instanceof ColorPickerCheckBoxBulkElement) {
				((ColorPickerCheckBoxBulkElement) subSetting).onClickBulkEntry(mouseX, mouseY, button);
			} else {
				subSetting.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	public void mouseClickedPreview(int mouseX, int mouseY, int button) {
		if (Objects.nonNull(labyModAddon)) {
			labyModAddon.onMouseClickedPreview(mouseX, mouseY, button);
		}
	}

	public void mouseReleaseSettings(int mouseX, int mouseY, int button) {
		if (!isSubSettingAvailable()) {
			return;
		}

		for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
			subSetting.mouseRelease(mouseX, mouseY, button);
		}
	}

	public void mouseClickMoveSettings(int mouseX, int mouseY, int button) {
		if (!isSubSettingAvailable()) {
			return;
		}

		for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
			subSetting.mouseClickMove(mouseX, mouseY, button);
		}
	}

	public void charTypedSettings(char codePoint, int modifiers) {
		if (!isSubSettingAvailable()) {
			return;
		}

		for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
			subSetting.charTyped(codePoint, modifiers);
		}
	}

	public boolean keyPressedSettings(int keyCode, int scanCode, int modifiers) {
		boolean cancel = false;
		if (isSubSettingAvailable()) {
			for (SettingsElement subSetting : labyModAddon.getSubSettings()) {
				if (subSetting instanceof KeyElement) {
					KeyElement keyElement = (KeyElement) subSetting;
					if (keyElement.getTextField().isFocused()) {
						cancel = true;
					}
				}

				subSetting.keyPressed(keyCode, scanCode, modifiers);
			}
		}

		return cancel;
	}

	private boolean isSubSettingAvailable() {
		return Objects.nonNull(labyModAddon) && Objects.nonNull(labyModAddon.getSubSettings())
				&& !labyModAddon.getSubSettings().isEmpty();
	}

	private void drawIcon(MatrixStack matrixStack, int x, int y, int size) {
		iconArea.setX(x).setY(y).setWidth(size).setHeight(size);
		if (addonInfo.getImageURL() == null) {
			MINECRAFT.getTextureManager().bindTexture(ModTextures.MISC_HEAD_QUESTION);
			DRAW_UTILS.drawTexture(matrixStack, x, y, 256.0D, 256.0D, size, size);
		} else {
			DRAW_UTILS.drawImageUrl(matrixStack, addonInfo.getImageURL(), x, y, 256.0D, 256.0D, size,
					size);
		}
	}

	private String trimStringToWidth(String text, double size, int maxWidth) {
		StringBuilder stringBuilder = new StringBuilder();
		double currentWidth = 0D;
		for (char c : text.toCharArray()) {
			double charWidth = MINECRAFT.fontRenderer.getStringWidth(String.valueOf(c)) * size;
			if (currentWidth + charWidth < maxWidth) {
				stringBuilder.append(c);
				currentWidth += charWidth;
			} else {
				stringBuilder.append("..");
				break;
			}
		}

		return stringBuilder.toString();
	}

	private String[] trimStringToNewLine(String text, double size, int maxWidth) {
		List<String> trimmedStrings = Lists.newArrayList();
		StringBuilder stringBuilder = new StringBuilder();
		int lineWidth = 0;

		for (String word : text.split(" ")) {
			double wordWidth = MINECRAFT.fontRenderer.getStringWidth(" " + word) * size;
			if ((lineWidth + wordWidth) > maxWidth) {
				trimmedStrings.add(stringBuilder.length() == 0 ? "" : stringBuilder.substring(1));
				stringBuilder = new StringBuilder();
				lineWidth = 0;
			}

			stringBuilder.append(" ").append(word);
			lineWidth += wordWidth;
		}

		trimmedStrings.add(stringBuilder.substring(1));
		return trimmedStrings.toArray(new String[]{});
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public AddonInfo getAddonInfo() {
		return addonInfo;
	}

	public boolean isOnlineAddon() {
		return addonInfo instanceof OnlineAddonInfo;
	}

	public OnlineAddonInfo getOnlineAddonInfo() {
		return (OnlineAddonInfo) addonInfo;
	}
}
