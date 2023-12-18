package fr.iglee42.createcasing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.fabric.EnvExecutor;
import fr.iglee42.createcasing.compat.createaddition.CreateAdditionCompatInit;
import fr.iglee42.createcasing.compat.kubejs.KubeJSCompatInit;
import fr.iglee42.createcasing.registries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class CreateCasing implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MODID = "createcasing-fabric";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
	public static List<ItemLike> hidedItems = new ArrayList<>();

	static {
		REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item))));
	}

	@Override
	public void onInitialize() {
		System.out.println("CASING INIT");
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// todo WHY DON'T WOOD SHAFTS GET ENCASED?

		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> CreateCasingClient::onCtorClient);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> Create.REGISTRATE.addRegisterCallback(Registries.BLOCK, ModBlocks::registerEncasedShafts) );
		ServerWorldEvents.LOAD.register((server, level) -> Create.REGISTRATE.addRegisterCallback(Registries.BLOCK, ModBlocks::registerEncasedShafts));

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onPlayerRightClickOnBlock(world, hitResult.getBlockPos(), player.getItemInHand(hand)));
	}

	public static void init(){
		//ModConfigs.register();
		ModSounds.prepare();
		ModBlocks.register();
		ModBlocks.registerEncasedShafts();
		ModBlockEntities.register();
		//ModCreativeModeTabs.register();
		//ModPackets.registerPackets();
		if (FabricLoader.getInstance().isModLoaded("createaddition")) {
			CreateAdditionCompatInit.init();
		}

		REGISTRATE.register();

		if (FabricLoader.getInstance().isModLoaded("kubejs")) {
			KubeJSCompatInit.init();
		}
	}
	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(MODID, path);
	}

	public static boolean isExtendedCogsLoaded() {
		return FabricLoader.getInstance().isModLoaded("extendedgears");
	}

	public static void hideItem(ItemLike it) {
		hidedItems.add(it);
	}

	public InteractionResult onPlayerRightClickOnBlock(Level world, BlockPos pos, ItemStack stack) {
		if (stack.isEmpty()) return InteractionResult.PASS;
		if (AllBlocks.MECHANICAL_PRESS.has(world.getBlockState(pos))) {
			BlockState blockState = world.getBlockState(pos);
			Direction facing = blockState.getValue(HORIZONTAL_FACING);

			if (stack.is(AllBlocks.BRASS_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.BRASS_PRESS.getDefaultState().setValue(HORIZONTAL_FACING, facing));
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.COPPER_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.COPPER_PRESS.getDefaultState().setValue(HORIZONTAL_FACING, facing));
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.RAILWAY_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.RAILWAY_PRESS.getDefaultState().setValue(HORIZONTAL_FACING, facing));
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.INDUSTRIAL_IRON_BLOCK.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.INDUSTRIAL_IRON_PRESS.getDefaultState().setValue(HORIZONTAL_FACING, facing));
				return InteractionResult.SUCCESS;
			}
		} else if (AllBlocks.MECHANICAL_MIXER.has(world.getBlockState(pos))) {
			if (stack.is(AllBlocks.BRASS_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.BRASS_MIXER.getDefaultState());
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.COPPER_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.COPPER_MIXER.getDefaultState());
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.RAILWAY_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.RAILWAY_MIXER.getDefaultState());
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.INDUSTRIAL_IRON_BLOCK.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.INDUSTRIAL_IRON_MIXER.getDefaultState());
				return InteractionResult.SUCCESS;
			}
		} else if (AllBlocks.DEPOT.has(world.getBlockState(pos))) {
			if (stack.is(AllBlocks.BRASS_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.BRASS_DEPOT.getDefaultState());
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.COPPER_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.COPPER_DEPOT.getDefaultState());
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.RAILWAY_CASING.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.RAILWAY_DEPOT.getDefaultState());
				return InteractionResult.SUCCESS;
			} else if (stack.is(AllBlocks.INDUSTRIAL_IRON_BLOCK.get().asItem())) {
				world.setBlockAndUpdate(pos, ModBlocks.INDUSTRIAL_IRON_DEPOT.getDefaultState());
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
}