package fr.iglee42.createcasing;

import com.simibubi.create.Create;
import fr.iglee42.createcasing.ponder.ModPonderTags;
import fr.iglee42.createcasing.ponder.PonderIndex;
import fr.iglee42.createcasing.registries.ModBlocks;
import fr.iglee42.createcasing.registries.ModPartialModels;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.core.registries.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.renderer.RenderType;

public class CreateCasingClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("createcasing-fabric");

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GLASS_SHAFT.get(), RenderType.cutoutMipped());
		CreateCasing.init();
		ModPonderTags.register();
		PonderIndex.register();
		Create.REGISTRATE.addRegisterCallback(Registries.BLOCK, ModBlocks::registerEncasedShafts);
	}

	public static void onCtorClient() {
		ModPartialModels.init();
	}
}