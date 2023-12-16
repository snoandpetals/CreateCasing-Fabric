package fr.iglee42.createcasing.blocks.customs;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.depot.SharedDepotBlockMethods;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import fr.iglee42.createcasing.registries.ModBlockEntities;
import fr.iglee42.createcasing.registries.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomDepotBlock extends Block implements IBE<DepotBlockEntity>, IWrenchable, ProperWaterloggedBlock {

	public CustomDepotBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(WATERLOGGED));
	}
	
	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}
	
	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
		LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		updateWater(pLevel, pState, pCurrentPos);
		return pState;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return withWater(super.getStateForPlacement(pContext), pContext);
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_,
		CollisionContext p_220053_4_) {
		return AllShapes.CASING_13PX.get(Direction.UP);
	}

	@Override
	public Class<DepotBlockEntity> getBlockEntityClass() {
		return DepotBlockEntity.class;
	}
	
	@Override
	public BlockEntityType<? extends DepotBlockEntity> getBlockEntityType() {
		return ModBlockEntities.DEPOT.get();
	}
	
	@Override
	public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult p_60508_) {


		if (player.getItemInHand(hand).is(AllBlocks.ANDESITE_CASING.get().asItem())) {
			world.setBlockAndUpdate(blockPos, AllBlocks.DEPOT.getDefaultState());
			return InteractionResult.SUCCESS;
		} else if (player.getItemInHand(hand).is(AllBlocks.BRASS_CASING.get().asItem())) {
			if (ModBlocks.COPPER_DEPOT.has(blockState) || ModBlocks.RAILWAY_DEPOT.has(blockState)  || ModBlocks.INDUSTRIAL_IRON_DEPOT.has(blockState))
				world.setBlockAndUpdate(blockPos, ModBlocks.BRASS_DEPOT.getDefaultState());
			return InteractionResult.SUCCESS;
		} else if (player.getItemInHand(hand).is(AllBlocks.COPPER_CASING.get().asItem())) {
			if (ModBlocks.BRASS_DEPOT.has(blockState)||ModBlocks.RAILWAY_DEPOT.has(blockState)  || ModBlocks.INDUSTRIAL_IRON_DEPOT.has(blockState) )
				world.setBlockAndUpdate(blockPos, ModBlocks.COPPER_DEPOT.getDefaultState());
			return InteractionResult.SUCCESS;
		} else if (player.getItemInHand(hand).is(AllBlocks.RAILWAY_CASING.get().asItem())) {
			if (ModBlocks.BRASS_DEPOT.has(blockState)||ModBlocks.COPPER_DEPOT.has(blockState) || ModBlocks.INDUSTRIAL_IRON_DEPOT.has(blockState))
				world.setBlockAndUpdate(blockPos, ModBlocks.RAILWAY_DEPOT.getDefaultState());
			return InteractionResult.SUCCESS;
		}else if (player.getItemInHand(hand).is(AllBlocks.INDUSTRIAL_IRON_BLOCK.get().asItem())) {
			if (ModBlocks.BRASS_DEPOT.has(blockState)||ModBlocks.COPPER_DEPOT.has(blockState) || ModBlocks.RAILWAY_DEPOT.has(blockState))
				world.setBlockAndUpdate(blockPos, ModBlocks.INDUSTRIAL_IRON_DEPOT.getDefaultState());
			return InteractionResult.SUCCESS;
		}
		return SharedDepotBlockMethods.onUse(blockState, world, blockPos, player, hand, p_60508_);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		IBE.onRemove(state, worldIn, pos, newState);
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
		SharedDepotBlockMethods.onLanded(worldIn, entityIn);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return SharedDepotBlockMethods.getComparatorInputOverride(blockState, worldIn, pos);
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

}
