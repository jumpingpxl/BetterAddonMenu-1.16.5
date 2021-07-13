package de.jumpingpxl.labymod.addon.util.guis;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.jumpingpxl.labymod.addon.util.TextField;
import net.labymod.addon.online.AddonInfoManager;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.core.LabyModCore;
import net.labymod.core.WorldRendererAdapter;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;

import java.util.Objects;
import java.util.Set;

public class BetterAddonGui extends Screen {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();

	private final Screen previousScreen;
	private final Screen labyModAddonsGui;
	private final ScreenArea overViewArea;
	private final ScreenArea addonArea;
	private final Scrollbar overViewScrollbar;
	private final Scrollbar addonScrollbar;
	private final Set<BetterAddonElement> addonElements;
	private BetterAddonElement selectedAddon;
	private TextField textField;

	public BetterAddonGui(Screen previousScreen, Screen labyModAddonsGui) {
		super(StringTextComponent.EMPTY);
		this.previousScreen = previousScreen;
		this.labyModAddonsGui = labyModAddonsGui;

		overViewArea = new ScreenArea();
		addonArea = new ScreenArea();
		overViewScrollbar = new Scrollbar(0);
		addonScrollbar = new Scrollbar(0);
		addonElements = Sets.newHashSet();

		refreshAddons();
	}

	@Override
	protected void init() {
		labyModAddonsGui.init(minecraft, width, height);
		Tabs.initGui(labyModAddonsGui);
		adjustAreas();

/*		int buttonTextWidth = DRAW_UTILS.getFontRenderer().getStringWidth("Classic") + 10;
		addButton(new Button(width - buttonTextWidth - 2, 27, buttonTextWidth, 20,
				new StringTextComponent(classicView ? "Modern" : "Classic"), onPress -> {
			setClassicView(!classicView);
			onPress.setMessage(new StringTextComponent(classicView ? "Modern" : "Classic"));
		})); */
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (LabyMod.getInstance().isInGame()) {
			DRAW_UTILS.drawIngameBackground(matrixStack);
		} else {
			renderBackground(overViewArea.getX(), overViewArea.getY(), overViewArea.getMaxX(),
					overViewArea.getMaxY(), overViewScrollbar.getScrollY(), 32);
			renderBackground(addonArea.getX(), addonArea.getY(), addonArea.getMaxX(),
					addonArea.getMaxY(),
					addonScrollbar.getScrollY(), 32);
		}

		String hoverText = null;
		int centerX = width / 2;
		int addonY = (int) (60 + overViewScrollbar.getScrollY());
		for (BetterAddonElement addonElement : addonElements) {
			if (addonElement.isVisible()) {
				String elementText = addonElement.render(matrixStack, mouseX, mouseY,
						overViewArea.getX() + 10, addonY, overViewArea.getMaxX() - 10, addonY + 37,
						selectedAddon == addonElement, overViewArea);
				if (Objects.nonNull(elementText)) {
					hoverText = elementText;
				}

				addonY += 39;
			}
		}

		if (Objects.nonNull(selectedAddon)) {
			selectedAddon.renderAddonSettings(matrixStack, mouseX, mouseY, addonArea, addonScrollbar);
			drawAreaShadows(matrixStack);
			selectedAddon.renderSelectedAddon(matrixStack, mouseX, mouseY, width, height, addonArea);
		} else {
			drawAreaShadows(matrixStack);
		}

		RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
		Tabs.drawScreen(this, matrixStack, mouseX, mouseY, partialTicks);
		textField.render(matrixStack, mouseX, mouseY);
		overViewScrollbar.draw(mouseX, mouseY);
		addonScrollbar.draw(mouseX, mouseY);
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		if (Objects.nonNull(hoverText)) {
			DRAW_UTILS.drawHoveringText(matrixStack, mouseX, mouseY, hoverText.split("\n"));
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Tabs.mouseClicked(this);
		if (overViewArea.isMouseOver(mouseX, mouseY)) {
			for (BetterAddonElement addonElement : addonElements) {
				if (addonElement.mouseClicked((int) mouseX, (int) mouseY, button)) {
					selectedAddon = addonElement;
					addonScrollbar.reset();
					addonScrollbar.update(0);
					break;
				}
			}
		}

		if (Objects.nonNull(selectedAddon) && addonArea.isMouseOver(mouseX, mouseY)) {
			selectedAddon.mouseClickedSettings((int) mouseX, (int) mouseY, button);
			selectedAddon.mouseClickedPreview((int) mouseX - addonArea.getX(),
					(int) mouseY - addonArea.getY(), button);
		}

		textField.mouseClicked(mouseX, button);
		overViewScrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.CLICKED);
		addonScrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.CLICKED);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (overViewArea.isMouseOver(mouseX, mouseY)) {
			overViewScrollbar.mouseInput(delta);
		} else if (addonArea.isMouseOver(mouseX, mouseY)) {
			addonScrollbar.mouseInput(delta);
		}

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		overViewScrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.RELEASED);
		addonScrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.RELEASED);

		if (Objects.nonNull(selectedAddon) && addonArea.isMouseOver(mouseX, mouseY)) {
			selectedAddon.mouseReleaseSettings((int) mouseX, (int) mouseY, button);
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX,
	                            double dragY) {
		if (Objects.nonNull(selectedAddon) && addonArea.isMouseOver(mouseX, mouseY)) {
			selectedAddon.mouseClickMoveSettings((int) mouseX, (int) mouseY, button);
		}

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (Objects.nonNull(selectedAddon) && selectedAddon.keyPressedSettings(keyCode, scanCode,
				modifiers)) {
			return false;
		}

		textField.keyPressed(keyCode);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (Objects.nonNull(selectedAddon)) {
			selectedAddon.charTypedSettings(codePoint, modifiers);
		}

		textField.charTyped(codePoint);
		return false;
	}

	@Override
	public void tick() {
		textField.tick();
		super.tick();
	}

	private void drawAreaShadows(MatrixStack matrixStack) {
		DRAW_UTILS.drawGradientShadowTop(matrixStack, overViewArea.getY(), overViewArea.getX(),
				overViewArea.getMaxX());
		DRAW_UTILS.drawGradientShadowBottom(matrixStack, overViewArea.getMaxY(), overViewArea.getX(),
				overViewArea.getMaxX());

		DRAW_UTILS.drawGradientShadowLeft(overViewArea.getX(), overViewArea.getY(),
				overViewArea.getMaxY());
		DRAW_UTILS.drawGradientShadowRight(overViewArea.getMaxX(), overViewArea.getY(),
				overViewArea.getMaxY());

		DRAW_UTILS.drawGradientShadowTop(matrixStack, addonArea.getY(), addonArea.getX(),
				addonArea.getMaxX());
		DRAW_UTILS.drawGradientShadowBottom(matrixStack, addonArea.getMaxY(), addonArea.getX(),
				addonArea.getMaxX());
		DRAW_UTILS.drawGradientShadowLeft(addonArea.getX(), addonArea.getY(), addonArea.getMaxY());
		DRAW_UTILS.drawGradientShadowRight(addonArea.getMaxX(), addonArea.getY(), addonArea.getMaxY());

		renderBackground(addonArea.getX(), 0, addonArea.getMaxX(), addonArea.getY(), 0, 64);
		renderBackground(addonArea.getX(), addonArea.getMaxY(), addonArea.getMaxX(), height, 0, 64);

		renderBackground(0, 0, overViewArea.getX(), height, 0, 64);
		renderBackground(overViewArea.getX(), 0, overViewArea.getMaxX(), overViewArea.getY(), 0, 64);
		renderBackground(overViewArea.getX(), overViewArea.getMaxY(), overViewArea.getMaxX(), height
				, 0,
				64);
		renderBackground(overViewArea.getMaxX(), 0, addonArea.getX(), height, 0, 64);
		renderBackground(addonArea.getMaxX(), 0, width, height, 0, 64);
	}

	private void adjustAreas() {
		overViewArea.setX(5).setY(50).setMaxX(width / 2 - 55).setMaxY(height - 20);
		if (overViewArea.getWidth() > 220) {
			overViewArea.setWidth(220);
		}

		addonArea.setX(overViewArea.getMaxX() + 10).setY(100).setMaxX(width - 4).setMaxY(height - 20);

		int overViewPos = overViewArea.getMaxX();
		overViewScrollbar.setPosition(overViewPos - 5, overViewArea.getY(), overViewPos,
				overViewArea.getMaxY());
		overViewScrollbar.setSpeed(40);
		overViewScrollbar.setEntryHeight(39.5);
		overViewScrollbar.update(
				(int) addonElements.stream().filter(BetterAddonElement::isVisible).count());
		overViewScrollbar.init();

		addonScrollbar.setPosition(addonArea.getMaxX() - 5, addonArea.getY(), addonArea.getMaxX(),
				addonArea.getMaxY());
		addonScrollbar.setSpeed(20);
		addonScrollbar.setEntryHeight(22);
		addonScrollbar.init();

		String text = "";
		int cursorIndex = 0;
		boolean focused = false;
		if (Objects.nonNull(textField)) {
			text = textField.getText();
			cursorIndex = textField.getCursorIndex();
			focused = textField.isFocused();
		}

		textField = new TextField(minecraft.fontRenderer, overViewArea.getX(), 27,
				overViewArea.getWidth(), 20, this::searchAddons);
		textField.setText(text);
		textField.setCursorIndex(cursorIndex);
		textField.setFocused(focused);
		textField.setPlaceholder("Search...");
	}

	private void searchAddons(String query) {
		int visibleAddons = 0;
		query = query.toLowerCase();
		for (BetterAddonElement addonElement : addonElements) {
			AddonInfo addonInfo = addonElement.getAddonInfo();
			if (addonInfo.getName().toLowerCase().contains(query) || addonInfo.getAuthor()
					.toLowerCase()
					.contains(query) || addonInfo.getDescription().toLowerCase().contains(query)) {
				visibleAddons++;
				addonElement.setVisible(true);
			} else {
				addonElement.setVisible(false);
			}
		}

		overViewScrollbar.update(visibleAddons);
	}

	private void refreshAddons() {
		AddonInfoManager infoManager = AddonInfoManager.getInstance();
		for (AddonInfo addonInfo : infoManager.getAddonInfoList()) {
			BetterAddonElement addonElement = new BetterAddonElement(this, addonInfo);

			addonElements.add(addonElement);
			if (Objects.isNull(selectedAddon)) {
				selectedAddon = addonElement;
			}
		}
	}

	public void renderBackground(int x, int y, int maxX, int maxY, double scrolling,
	                             int brightness) {
		scrolling = (-scrolling);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
		minecraft.getTextureManager().bindTexture(
				LabyModCore.getRenderImplementation().getOptionsBackground());
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(x, maxY, 0.0D).tex(x / 32.0F, (maxY + scrolling) / 32.0F).color(brightness,
				brightness, brightness, 255).endVertex();
		worldrenderer.pos(maxX, maxY, 0.0D).tex(maxX / 32.0F, (maxY + scrolling) / 32.0F).color(
				brightness, brightness, brightness, 255).endVertex();
		worldrenderer.pos(maxX, y, 0.0D).tex(maxX / 32.0F, (y + scrolling) / 32.0F).color(brightness,
				brightness, brightness, 255).endVertex();
		worldrenderer.pos(x, y, 0.0D).tex(x / 32.0F, (y + scrolling) / 32.0F).color(brightness,
				brightness, brightness, 255).endVertex();
		tessellator.draw();
	}
}
