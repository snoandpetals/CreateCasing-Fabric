package fr.iglee42.createcasing.blocks.customs;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.createcasing.registries.ModBlockEntities;
import fr.iglee42.createcasing.items.CustomVerticalGearboxItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;

import java.util.Arrays;
import java.util.List;


/*
This class is a copy from the original class GearboxBlock
 */
public class CustomGearboxBlock extends RotatedPillarKineticBlock implements IBE<GearboxBlockEntity> {

	private final ItemEntry<CustomVerticalGearboxItem> verticalItem;

	public CustomGearboxBlock(Properties properties , ItemEntry<CustomVerticalGearboxItem> verticalItem) {
		super(properties);
		this.verticalItem = verticalItem;
	}

	public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
		return ModBlockEntities.GEARBOX.get();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		if (state.getValue(AXIS).isVertical())
			return super.getDrops(state, builder);
		return Arrays.asList(new ItemStack(verticalItem.get()));
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world,BlockPos pos, BlockState state) {
		if (state.getValue(AXIS).isVertical())
			return super.getCloneItemStack(world, pos, state);
		return new ItemStack(verticalItem.get());
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(AXIS, Direction.Axis.Y);
	}

	// IRotate:

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() != state.getValue(AXIS);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(AXIS);
	}

	@Override
	public Class<GearboxBlockEntity> getBlockEntityClass() {
		return GearboxBlockEntity.class;
	}

}