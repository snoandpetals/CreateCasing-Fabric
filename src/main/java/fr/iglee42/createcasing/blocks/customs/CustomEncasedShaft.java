package fr.iglee42.createcasing.blocks.customs;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import fr.iglee42.createcasing.blockEntities.CustomEncasedShaftBlockEntity;
import fr.iglee42.createcasing.registries.ModBlockEntities;
import fr.iglee42.createcasing.utils.DontShowInCreativeTab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

public class CustomEncasedShaft extends EncasedShaftBlock implements DontShowInCreativeTab {

    private Supplier<Block> shaft;

    public CustomEncasedShaft(Properties properties, Supplier<Block> casing, Supplier<? extends Block> shaft) {
        super(properties, casing);
        this.shaft = shaft::get;
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntities.CUSTOM_ENCASED_SHAFT.get();
    }

    public Supplier<Block> getShaft() {
        return shaft;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        context.getLevel()
                .levelEvent(2001, context.getClickedPos(), Block.getId(state));
        KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
                shaft.get().defaultBlockState()
                        .setValue(AXIS, state.getValue(AXIS)));
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world,BlockPos pos, BlockState state) {
        return this.getCasing().asItem().getDefaultInstance();
    }
}
