package fr.iglee42.createcasing.compat.createaddition.register;


import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.gearbox.GearboxInstance;
import com.simibubi.create.content.kinetics.gearbox.GearboxRenderer;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerRenderer;
import com.simibubi.create.content.kinetics.mixer.MixerInstance;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogInstance;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftInstance;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.iglee42.createcasing.blockEntities.*;
import fr.iglee42.createcasing.blockEntities.instances.*;
import fr.iglee42.createcasing.blockEntities.renderers.CreativeCogwheelRenderer;
import fr.iglee42.createcasing.blockEntities.renderers.CustomEncasedShaftRenderer;
import fr.iglee42.createcasing.blockEntities.renderers.CustomMixerRenderer;
import fr.iglee42.createcasing.blockEntities.renderers.CustomPressRenderer;
import fr.iglee42.createcasing.blockEntities.BrassShaftBlockEntity;
import fr.iglee42.createcasing.registries.ModBlocks;

import static fr.iglee42.createcasing.CreateCasing.REGISTRATE;

public class ModAdditionBlockEntities {
    public static final BlockEntityEntry<CustomTeslaBlockEntity> CUSTOM_TESLA = REGISTRATE
            .blockEntity("custom_tesla", CustomTeslaBlockEntity::new)
            .validBlocks(ModAdditionBlocks.ANDESITE_TESLA,ModAdditionBlocks.COPPER_TESLA,ModAdditionBlocks.RAILWAY_TESLA,ModAdditionBlocks.INDUSTRIAL_IRON_TESLA)
            .register();

    public static void register() {}
}
