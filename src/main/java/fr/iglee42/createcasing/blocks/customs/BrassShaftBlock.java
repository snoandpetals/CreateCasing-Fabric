package fr.iglee42.createcasing.blocks.customs;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.tterrag.registrate.fabric.EnvExecutor;
import fr.iglee42.createcasing.blockEntities.BrassShaftBlockEntity;
import fr.iglee42.createcasing.registries.ModBlockEntities;
import fr.iglee42.createcasing.screen.BrassShaftScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BrassShaftBlock extends MetalShaftBlock{
    public BrassShaftBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntities.BRASS_SHAFT.get();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        EnvExecutor.runWhenOn(EnvType.CLIENT,
                () -> () -> withBlockEntityDo(world, pos, be -> openScreen((BrassShaftBlockEntity) be,player)));
        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    protected void openScreen(BrassShaftBlockEntity be,Player player){
        if (!(player instanceof LocalPlayer))
            return;
        ScreenOpener.open(new BrassShaftScreen(be));
    }

    @Override
    public void neighborChanged(BlockState p_60509_, Level level, BlockPos pos, Block p_60512_, BlockPos p_60513_, boolean p_60514_) {
        if (level.hasNeighborSignal(pos)){
            RotationPropagator.handleAdded((Level) level,pos, (KineticBlockEntity) level.getBlockEntity(pos));
        }
        super.neighborChanged(p_60509_, level, pos, p_60512_, p_60513_, p_60514_);
    }
}
