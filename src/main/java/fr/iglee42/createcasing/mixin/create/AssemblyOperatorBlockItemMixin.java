package fr.iglee42.createcasing.mixin.create;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import fr.iglee42.createcasing.blocks.customs.CustomDepotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AssemblyOperatorBlockItem.class,remap = false)
public class AssemblyOperatorBlockItemMixin {

    @Inject(method = "operatesOn",at = @At("HEAD"),cancellable = true)
    private void inject(BlockState placedOnState, CallbackInfoReturnable<Boolean> cir){
        if (placedOnState.getBlock() instanceof CustomDepotBlock) cir.setReturnValue(true);
    }
}
