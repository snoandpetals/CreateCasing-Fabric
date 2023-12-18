package fr.iglee42.createcasing.compat.createaddition.register;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.motor.CreativeMotorGenerator;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.content.redstone.displayLink.source.ItemNameDisplaySource;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.blocks.customs.*;
import fr.iglee42.createcasing.blocks.publics.PublicEncasedCogwheelBlock;
import fr.iglee42.createcasing.blocks.publics.PublicEncasedPipeBlock;
import fr.iglee42.createcasing.blocks.publics.PublicEncasedShaftBlock;
import fr.iglee42.createcasing.items.CustomVerticalGearboxItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static fr.iglee42.createcasing.CreateCasing.REGISTRATE;
import static net.minecraft.world.level.block.Blocks.GLASS;

public class ModAdditionBlocks {
    public static final BlockEntry<CustomTeslaBlock> ANDESITE_TESLA = createTesla("andesite");
    public static final BlockEntry<CustomTeslaBlock> COPPER_TESLA = createTesla("copper");
    public static final BlockEntry<CustomTeslaBlock> RAILWAY_TESLA = createTesla("railway");
    public static final BlockEntry<CustomTeslaBlock> INDUSTRIAL_IRON_TESLA = createTesla("industrial_iron");

    private static BlockEntry<CustomTeslaBlock> createTesla(String name){
        return Objects.equals(name, "andesite") || Objects.equals(name, "copper") || Objects.equals(name, "railway") ? REGISTRATE.block(name+"_tesla", CustomTeslaBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(MapColor.STONE))
                .properties(BlockBehaviour.Properties::noOcclusion)
                .transform(pickaxeOnly())
                .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.createcasing."+name+"_tesla"))
                .addLayer(() -> RenderType::cutoutMipped)
                .item(AssemblyOperatorBlockItem::new)
                .transform(customItemModel())
                .register()
                :
                REGISTRATE.block(name+"_tesla", CustomTeslaBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .properties(p -> p.mapColor(MapColor.STONE))
                        .properties(BlockBehaviour.Properties::noOcclusion)
                        .transform(pickaxeOnly())
                        .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.createcasing.custom_tesla"))
                        .addLayer(() -> RenderType::cutoutMipped)
                        .item(AssemblyOperatorBlockItem::new)
                        .transform(customItemModel())
                        .register();
    }
    public static void register() {
    }
}
