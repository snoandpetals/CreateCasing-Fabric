package fr.iglee42.createcasing;

import com.simibubi.create.Create;
import fr.iglee42.createcasing.registries.ModBlocks;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.registries.Registries;

public class CreateCasingServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Create.REGISTRATE.addRegisterCallback(Registries.BLOCK, ModBlocks::registerEncasedShafts);
    };
}
