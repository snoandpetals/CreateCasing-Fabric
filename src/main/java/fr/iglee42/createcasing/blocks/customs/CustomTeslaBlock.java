package fr.iglee42.createcasing.blocks.customs;

import com.mrh0.createaddition.shapes.CAShapes;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import fr.iglee42.createcasing.blockEntities.CustomTeslaBlockEntity;
import fr.iglee42.createcasing.compat.createaddition.register.ModAdditionBlockEntities;
import fr.iglee42.createcasing.compat.createaddition.register.ModAdditionBlocks;
import io.github.fabricators_of_create.porting_lib.block.ConnectableRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class CustomTeslaBlock extends Block implements IBE<CustomTeslaBlockEntity>, IWrenchable, ConnectableRedstoneBlock {
    public CustomTeslaBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    public static final VoxelShaper TESLA_COIL_SHAPE = CAShapes.shape(0, 0, 0, 16, 10, 16).add(1, 0, 1, 15, 12, 15).forDirectional();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TESLA_COIL_SHAPE.get(state.getValue(FACING).getOpposite());
    }

    @Override
    public Class<CustomTeslaBlockEntity> getBlockEntityClass() {
        return CustomTeslaBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CustomTeslaBlockEntity> getBlockEntityType() {
        return ModAdditionBlockEntities.CUSTOM_TESLA.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModAdditionBlockEntities.CUSTOM_TESLA.create(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return this.defaultBlockState().setValue(FACING, c.isSecondaryUseActive() ? c.getClickedFace() : c.getClickedFace().getOpposite());
    }

    public void setPowered(Level world, BlockPos pos, boolean powered) {
        world.setBlock(pos, defaultBlockState().setValue(FACING, world.getBlockState(pos).getValue(FACING)).setValue(POWERED, powered), 3);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult p_60508_) {
        Direction facing = blockState.getValue(FACING);

        if (player.getItemInHand(hand).is(AllBlocks.BRASS_CASING.get().asItem())) {
            world.setBlockAndUpdate(blockPos, CABlocks.TESLA_COIL.getDefaultState().setValue(FACING, facing));
            return InteractionResult.SUCCESS;
        } else if (player.getItemInHand(hand).is(AllBlocks.ANDESITE_CASING.get().asItem())) {
            world.setBlockAndUpdate(blockPos, ModAdditionBlocks.ANDESITE_TESLA.getDefaultState().setValue(FACING, facing));
            return InteractionResult.SUCCESS;
        } else if (player.getItemInHand(hand).is(AllBlocks.COPPER_CASING.get().asItem())) {
            world.setBlockAndUpdate(blockPos, ModAdditionBlocks.COPPER_TESLA.getDefaultState().setValue(FACING, facing));
            return InteractionResult.SUCCESS;
        } else if (player.getItemInHand(hand).is(AllBlocks.RAILWAY_CASING.get().asItem())) {
            world.setBlockAndUpdate(blockPos, ModAdditionBlocks.RAILWAY_TESLA.getDefaultState().setValue(FACING, facing));
            return InteractionResult.SUCCESS;
        }else if (player.getItemInHand(hand).is(AllBlocks.INDUSTRIAL_IRON_BLOCK.get().asItem())) {
            world.setBlockAndUpdate(blockPos, ModAdditionBlocks.INDUSTRIAL_IRON_TESLA.getDefaultState().setValue(FACING, facing));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
