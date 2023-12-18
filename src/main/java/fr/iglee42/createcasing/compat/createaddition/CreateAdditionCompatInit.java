package fr.iglee42.createcasing.compat.createaddition;

import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import fr.iglee42.createcasing.blocks.customs.CustomTeslaBlock;
import fr.iglee42.createcasing.compat.createaddition.register.ModAdditionBlockEntities;
import fr.iglee42.createcasing.compat.createaddition.register.ModAdditionBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class CreateAdditionCompatInit {
    public static void init() {
        ModAdditionBlockEntities.register();
        ModAdditionBlocks.register();
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onPlayerRightClickOnBlock(world, hitResult.getBlockPos(), player.getItemInHand(hand)));
    }
    public static InteractionResult onPlayerRightClickOnBlock(Level world, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) return InteractionResult.PASS;
        if (CABlocks.TESLA_COIL.has(world.getBlockState(pos))) {
            BlockState blockState = world.getBlockState(pos);
            Direction facing = blockState.getValue(TeslaCoilBlock.FACING);

            if (stack.is(AllBlocks.ANDESITE_CASING.get().asItem())) {
                world.setBlockAndUpdate(pos, ModAdditionBlocks.ANDESITE_TESLA.getDefaultState().setValue(CustomTeslaBlock.FACING, facing));
                return InteractionResult.SUCCESS;
            } else if (stack.is(AllBlocks.COPPER_CASING.get().asItem())) {
                world.setBlockAndUpdate(pos, ModAdditionBlocks.COPPER_TESLA.getDefaultState().setValue(CustomTeslaBlock.FACING, facing));
                return InteractionResult.SUCCESS;
            } else if (stack.is(AllBlocks.RAILWAY_CASING.get().asItem())) {
                world.setBlockAndUpdate(pos, ModAdditionBlocks.RAILWAY_TESLA.getDefaultState().setValue(CustomTeslaBlock.FACING, facing));
                return InteractionResult.SUCCESS;
            } else if (stack.is(AllBlocks.INDUSTRIAL_IRON_BLOCK.get().asItem())) {
                world.setBlockAndUpdate(pos, ModAdditionBlocks.INDUSTRIAL_IRON_TESLA.getDefaultState().setValue(CustomTeslaBlock.FACING, facing));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
