package de.jumpingpxl.labymod.addon.util.guis;

public enum AddonCategory {

	UNKNOWN(0, "Unknown", '4'),
	GUI(1, "GUI", '3'),
	TOOLS(2, "Tools", '2'),
	SERVER_SUPPORT(3, "ServerSupport", 'c'),
	GRAPHICS(4, "Graphics", 'd');

	private static final AddonCategory[] VALUES = values();

	private final int id;
	private final String name;
	private final char color;

	AddonCategory(int id, String name, char color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	public static AddonCategory getById(int id) {
		for (AddonCategory addonCategory : VALUES) {
			if (addonCategory.id == id) {
				return addonCategory;
			}
		}

		return UNKNOWN;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return "§" + color + name;
	}

	public char getColor() {
		return color;
	}
}
