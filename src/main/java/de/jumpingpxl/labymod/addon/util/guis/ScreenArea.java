package de.jumpingpxl.labymod.addon.util.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

import java.awt.*;

public class ScreenArea {

	private int x;
	private int y;
	private int maxX;
	private int maxY;
	private int mouseOverTicks;

	public ScreenArea() {
		this.x = 0;
		this.y = 0;
		this.maxX = 0;
		this.maxY = 0;
		this.mouseOverTicks = 0;
	}

	public void renderDebug(MatrixStack matrixStack) {
		AbstractGui.fill(matrixStack, getX(), getY(), getMaxX(), getMaxY(), Color.PINK.getRGB());
	}

	public boolean renderTooltip(int mouseX, int mouseY) {
		if (isMouseOver(mouseX, mouseY)) {
			if (mouseOverTicks < 15) {
				mouseOverTicks++;
			} else {
				return true;
			}
		} else if (mouseOverTicks != 0) {
			mouseOverTicks = 0;
		}

		return false;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return isMouseOver((int) mouseX, (int) mouseY);
	}

	public int getX() {
		return x;
	}

	public ScreenArea setX(int x) {
		this.x = x;
		return this;
	}

	public int getY() {
		return y;
	}

	public ScreenArea setY(int y) {
		this.y = y;
		return this;
	}

	public int getMaxX() {
		return maxX;
	}

	public ScreenArea setMaxX(int maxX) {
		this.maxX = maxX;
		return this;
	}

	public int getMaxY() {
		return maxY;
	}

	public ScreenArea setMaxY(int maxY) {
		this.maxY = maxY;
		return this;
	}

	public int getWidth() {
		return maxX - x;
	}

	public ScreenArea setWidth(int width) {
		maxX = x + width;
		return this;
	}

	public int getHeight() {
		return maxY - y;
	}

	public ScreenArea setHeight(int height) {
		maxY = y + height;
		return this;
	}
}
