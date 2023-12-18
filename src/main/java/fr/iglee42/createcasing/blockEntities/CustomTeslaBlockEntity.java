package fr.iglee42.createcasing.blockEntities;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBeltCallbacks;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilTileEntity;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CADamageTypes;
import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import fr.iglee42.createcasing.blocks.customs.CustomTeslaBlock;
import fr.iglee42.createcasing.compat.createaddition.register.ModAdditionBlocks;
import fr.iglee42.createcasing.registries.ModBlocks;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
public class CustomTeslaBlockEntity extends TeslaCoilTileEntity implements IHaveGoggleInformation {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<ChargingRecipe> recipeCache;

    private final ItemStackHandler inputInv;
    private int chargeAccumulator;

    protected ItemStack chargedStackCache;
    protected int poweredTimer = 0;

    public CustomTeslaBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        inputInv = new ItemStackHandler(1);
        recipeCache = Optional.empty();
    }

    public BeltProcessingBehaviour processingBehaviour;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        processingBehaviour =
                new BeltProcessingBehaviour(this).whenItemEnters((s, i) -> TeslaCoilBeltCallbacks.onItemReceived(s, i, this))
                        .whileItemHeld((s, i) -> TeslaCoilBeltCallbacks.whenItemHeld(s, i, this));
        behaviours.add(processingBehaviour);
    }

    @Override
    public boolean isEnergyInput(Direction side) {
        return side != getBlockState().getValue(CustomTeslaBlock.FACING).getOpposite();
    }

    @Override
    public boolean isEnergyOutput(Direction side) {
        return false;
    }

    @Override
    public long getConsumption() {
        return Config.TESLA_COIL_CHARGE_RATE.get();
    }

    @Override
    protected float getItemCharge(EnergyStorage energy) {
        if (energy == null)
            return 0f;
        return (float) energy.getAmount() / (float) energy.getCapacity();
    }

    @Override
    protected BeltProcessingBehaviour.ProcessingResult onCharge(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        BeltProcessingBehaviour.ProcessingResult res = chargeCompundAndStack(transported, handler);
        return res;
    }

    private void doDmg() {
        localEnergy.internalConsumeEnergy(Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get());
        BlockPos origin = getBlockPos().relative(getBlockState().getValue(CustomTeslaBlock.FACING).getOpposite());
        List<LivingEntity> ents = Objects.requireNonNull(getLevel()).getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(Config.TESLA_COIL_HURT_RANGE.get()));
        for(LivingEntity e : ents) {
            if(e == null) return;
            int dmg = Config.TESLA_COIL_HURT_DMG_MOB.get();
            int time = Config.TESLA_COIL_HURT_EFFECT_TIME_MOB.get();
            if(e instanceof Player) {
                dmg = Config.TESLA_COIL_HURT_DMG_PLAYER.get();
                time = Config.TESLA_COIL_HURT_EFFECT_TIME_PLAYER.get();
            }
            if(dmg > 0)
                e.hurt(CADamageTypes.TESLA_COIL.source(level), dmg);
            if(time > 0)
                e.addEffect(new MobEffectInstance(CAEffects.SHOCKING.get(), (int) time));
        }
    }

    int dmgTick = 0;
    int soundTimeout = 0;

    @Override
    public void tick() {
        assert level != null;
        if (level != null && level.isClientSide()) {
            if(isPoweredState() && soundTimeout++ > 20) {
                //level.playLocalSound(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 1f, 16f, false);
                soundTimeout = 0;
            }
            return;
        }
        int signal = Objects.requireNonNull(getLevel()).getBestNeighborSignal(getBlockPos());
        //System.out.println(signal + ":" + (energy.getEnergyStored() >= HURT_ENERGY_REQUIRED));
        if(signal > 0 && localEnergy.getAmount() >= Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get())
            poweredTimer = 10;

        dmgTick++;
        if((dmgTick%=Config.TESLA_COIL_HURT_FIRE_COOLDOWN.get()) == 0 && localEnergy.getAmount() >= Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get() && signal > 0)
            doDmg();

        if(poweredTimer > 0) {
            if(!isPoweredState()) {
                if (ModAdditionBlocks.RAILWAY_TESLA.has(getBlockState())) {
                    ModAdditionBlocks.RAILWAY_TESLA.get().setPowered(level, getBlockPos(), true);
                    System.out.println("YIPPEE 1");
                } else if (ModAdditionBlocks.COPPER_TESLA.has(getBlockState())) {
                    ModAdditionBlocks.COPPER_TESLA.get().setPowered(level, getBlockPos(), true);
                    System.out.println("YIPPEE 2");
                } else if (ModAdditionBlocks.ANDESITE_TESLA.has(getBlockState())) {
                    ModAdditionBlocks.ANDESITE_TESLA.get().setPowered(level, getBlockPos(), true);
                    System.out.println("YIPPEE 3");
                } else if (ModAdditionBlocks.INDUSTRIAL_IRON_TESLA.has(getBlockState())) {
                    ModAdditionBlocks.INDUSTRIAL_IRON_TESLA.get().setPowered(level, getBlockPos(), true);
                    System.out.println("YIPPEE 4");
                }
            }
            poweredTimer--;
        } else if(isPoweredState()){
            if (ModAdditionBlocks.RAILWAY_TESLA.has(getBlockState())) {
                ModAdditionBlocks.RAILWAY_TESLA.get().setPowered(level, getBlockPos(), false);
            } else if (ModAdditionBlocks.COPPER_TESLA.has(getBlockState())) {
                ModAdditionBlocks.COPPER_TESLA.get().setPowered(level, getBlockPos(), false);
            } else if (ModAdditionBlocks.ANDESITE_TESLA.has(getBlockState())) {
                ModAdditionBlocks.ANDESITE_TESLA.get().setPowered(level, getBlockPos(), false);
            } else if (ModAdditionBlocks.INDUSTRIAL_IRON_TESLA.has(getBlockState())) {
                ModAdditionBlocks.INDUSTRIAL_IRON_TESLA.get().setPowered(level, getBlockPos(), false);
            }
        }
    }

    @Override
    public boolean isPoweredState() {
        return getBlockState().getValue(CustomTeslaBlock.POWERED);
    }

    @Override
    protected BeltProcessingBehaviour.ProcessingResult chargeCompundAndStack(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        ItemStack stack = transported.stack;
        if(stack == null)
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        //first check if it can charge, if so then charge
        if(chargeStackCustom(stack, transported, handler)) {
            poweredTimer = 10;
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        //then check if it has recipe, then energize
        else if(chargeRecipeCustom(stack, transported, handler)) {
            poweredTimer = 10;
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        return BeltProcessingBehaviour.ProcessingResult.PASS;
    }
    protected final boolean chargeStackCustom(
            final ItemStack stack,
            final TransportedItemStack ignoredTransported,
            final TransportedItemStackHandlerBehaviour ignoredHandler
    ) {
        ContainerItemContext context = ContainerItemContext.withInitial(stack);
        final EnergyStorage es =  EnergyStorage.ITEM.find(stack, context);

        // False = Belt Moves
        // True = Belt Pause

        // check if energystorage exist on the depot/belt
        if (es == null)
        {
            return false;
        }
        else
        {
            long Energy = localEnergy.getAmount();
            final long toTransfer = Math.min(Energy,getConsumption());
            // check for valid transaction
            try (Transaction t = TransferUtil.getTransaction())
            {
                if (es.insert(1, t) != 1)
                    return false;
            }
            // check if the tesla coil has enough energy
            if(localEnergy.getAmount() < stack.getCount())
                return false;

            //
            try (Transaction t = TransferUtil.getTransaction())
            {
                int transferred = (int)localEnergy.internalConsumeEnergy((int)es.insert(toTransfer, t));
                if(transferred > 0)
                {
                    t.commit();
                }
                else
                {
                    t.abort();
                    return false;
                }
            }
            stack.setTag(context.getItemVariant().copyNbt());
            return true;
        }
    }

    private boolean chargeRecipeCustom(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if(!inputInv.getStackInSlot(0).is(stack.getItem())) {
            inputInv.setStackInSlot(0, stack);
            recipeCache = find(new RecipeWrapper(inputInv), Objects.requireNonNull(this.getLevel()));
            chargeAccumulator = 0;
        }
        if(recipeCache.isPresent()) {
            ChargingRecipe recipe = recipeCache.get();
            long energyRemoved = localEnergy.internalConsumeEnergy(Math.min( Config.TESLA_COIL_RECIPE_CHARGE_RATE.get(), recipe.getEnergy() - chargeAccumulator));
            chargeAccumulator += energyRemoved;
            if(chargeAccumulator >= recipe.getEnergy()) {
                TransportedItemStack remainingStack = transported.copy();
                TransportedItemStack result = transported.copy();
                result.stack = recipe.getResultItem(null).copy();
                remainingStack.stack.shrink(1);
                List<TransportedItemStack> outList = new ArrayList<>();
                outList.add(result);
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, remainingStack));
                chargeAccumulator = 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public Optional<ChargingRecipe> find(RecipeWrapper wrapper, Level world) {
        return world.getRecipeManager().getRecipeFor(ChargingRecipe.TYPE, wrapper, world);
    }
}
