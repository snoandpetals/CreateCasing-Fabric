package fr.iglee42.createcasing.mixin.create;

import com.simibubi.create.Create;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.registries.ModBlocks;
import net.minecraft.core.registries.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Create.class, remap = false)
public class CreateMixin {
    @Inject(method = "onInitialize", at = @At("HEAD"))
    private void inject(CallbackInfo info) {
        System.out.println("CREATE INIT");
    }
}
