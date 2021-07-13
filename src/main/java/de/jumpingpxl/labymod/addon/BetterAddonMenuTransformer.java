package de.jumpingpxl.labymod.addon;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

public class BetterAddonMenuTransformer extends AddonTransformer {

	@Override
	public void registerTransformers() {
		this.registerTransformer(TransformerType.VANILLA, "betteraddonmenu.mixin.json");
	}
}