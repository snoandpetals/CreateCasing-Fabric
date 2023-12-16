package fr.iglee42.createcasing.blocks.customs;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.gui.ScreenOpener;
import fr.iglee42.createcasing.blockEntities.BrassShaftBlockEntity;
import fr.iglee42.createcasing.blockEntities.MetalShaftBlockEntity;
import fr.iglee42.createcasing.registries.ModBlockEntities;
import fr.iglee42.createcasing.registries.ModBlocks;
import fr.iglee42.createcasing.screen.BrassShaftScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MetalShaftBlock extends ShaftBlock {
    public MetalShaftBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntities.BRASS_SHAFT.get();
    }

}
