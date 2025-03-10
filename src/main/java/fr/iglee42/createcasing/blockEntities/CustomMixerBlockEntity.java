package fr.iglee42.createcasing.blockEntities;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
//todo import fr.iglee42.createcasing.config.ModConfigs;
import fr.iglee42.createcasing.registries.ModBlocks;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;
import java.util.Optional;

import static com.simibubi.create.foundation.item.TooltipHelper.cutTextComponent;

public class CustomMixerBlockEntity extends BasinOperatingBlockEntity {

	private static final Object shapelessOrMixingRecipesKey = new Object();

	public int runningTicks;
	public int processingTicks;
	public boolean running;

	public CustomMixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public float getRenderedHeadOffset(float partialTicks) {
		int localTick;
		float offset = 0;
		if (running) {
			if (runningTicks < 20) {
				localTick = runningTicks;
				float num = (localTick + partialTicks) / 20f;
				num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
				offset = num - .5f;
			} else if (runningTicks <= 20) {
				offset = 1;
			} else {
				localTick = 40 - runningTicks;
				float num = (localTick - partialTicks) / 20f;
				num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
				offset = num - .5f;
			}
		}
		return offset + 7 / 16f;
	}

	public float getRenderedHeadRotationSpeed(float partialTicks) {
		float speed = getSpeed();
		if (running) {
			if (runningTicks < 15) {
				return speed;
			}
			if (runningTicks <= 20) {
				return speed * 2;
			}
			return speed;
		}
		return speed / 2;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		registerAwardables(behaviours, AllAdvancements.MIXER);
	}
	
	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		running = compound.getBoolean("Running");
		runningTicks = compound.getInt("Ticks");
		super.read(compound, clientPacket);

		if (clientPacket && hasLevel())
			getBasin().ifPresent(bte -> bte.setAreFluidsMoving(running && runningTicks <= 20));
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Running", running);
		compound.putInt("Ticks", runningTicks);
		super.write(compound, clientPacket);
	}

	@Override
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToTooltip(tooltip,isPlayerSneaking);
		// todo if (!ModConfigs.common().kinetics.shouldCustomMixerMixeFaster.get()) {
		if (!true) {
			if (ModBlocks.BRASS_MIXER.has(getBlockState()) || ModBlocks.COPPER_MIXER.has(getBlockState()) || ModBlocks.RAILWAY_MIXER.has(getBlockState())){
				Component spacing = IHaveGoggleInformation.componentSpacing;
				tooltip.add(spacing.plainCopy()
						.append(Component.translatable("tooltip.createcasing.mixermixenormaly.title"))
						.withStyle(ChatFormatting.GOLD));
				Component hint = Component.translatable("tooltip.createcasing.mixermixenormaly");
				List<Component> cutComponent = cutTextComponent(hint, TooltipHelper.Palette.GRAY_AND_WHITE);
				for (Component component : cutComponent)
					tooltip.add(spacing.plainCopy()
							.append(component));
			}
		}
		return true;
	}

	@Override
	public void tick() {
		super.tick();

		if (runningTicks >= 40) {
			running = false;
			runningTicks = 0;
			basinChecker.scheduleUpdate();
			return;
		}

		float speed = Math.abs(getSpeed());
		if (running && level != null) {
			if (level.isClientSide && runningTicks == 20)
				renderParticles();

			if ((!level.isClientSide || isVirtual()) && runningTicks == 20) {
				if (processingTicks < 0) {
					float recipeSpeed = 1;
					if (currentRecipe instanceof ProcessingRecipe) {
						int t = ((ProcessingRecipe<?>) currentRecipe).getProcessingDuration();
						if (t != 0)
							recipeSpeed = t / 100f;
					}

					processingTicks = Mth.clamp((Mth.log2((int) (512 / speed))) * Mth.ceil(recipeSpeed * 15) + 1, 1, 512);

					Optional<BasinBlockEntity> basin = getBasin();
					if (basin.isPresent()) {
						Couple<SmartFluidTankBehaviour> tanks = basin.get()
							.getTanks();
						if (!tanks.getFirst()
							.isEmpty()
							|| !tanks.getSecond()
								.isEmpty())
							level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
								SoundSource.BLOCKS, .75f, speed < 65 ? .75f : 1.5f);
					}

				} else {
					processingTicks--;
					if (currentRecipe != null) {
						//todo if (ModConfigs.common().kinetics.shouldCustomMixerMixeFaster.get()) {
						if (true) {

							if (ModBlocks.RAILWAY_MIXER.has(getBlockState())) {
								processingTicks--;
							} else if (ModBlocks.BRASS_MIXER.has(getBlockState())) {
								if (currentRecipe instanceof ShapelessRecipe)
									processingTicks--;
								else if (((ProcessingRecipe<?>) currentRecipe).getFluidIngredients().isEmpty())
									processingTicks--;
							} else if (ModBlocks.COPPER_MIXER.has(getBlockState()) && currentRecipe instanceof ProcessingRecipe<?> && (((ProcessingRecipe<?>) currentRecipe).getIngredients().isEmpty() || currentRecipe.getId().getPath().contains("potion_mixing"))) {
								processingTicks--;
							}
						}
					}
					if (processingTicks == 0) {
						runningTicks++;
						processingTicks = -1;
						applyBasinRecipe();
						sendData();
					}
				}
			}

			if (runningTicks != 20)
				runningTicks++;
		}
	}

	public void renderParticles() {
		Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent() || level == null)
			return;

		for (SmartInventory inv : basin.get().getInvs()) {
			for (int slot = 0; slot < inv.getSlots().size(); slot++) {
				ItemStack stackInSlot = inv.getItem(slot);
				if (stackInSlot.isEmpty())
					continue;
				ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
				spillParticle(data);
			}
		}

		for (SmartFluidTankBehaviour behaviour : basin.get()
			.getTanks()) {
			if (behaviour == null)
				continue;
			for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
				if (tankSegment.isEmpty(0))
					continue;
				spillParticle(FluidFX.getFluidParticle(tankSegment.getRenderedFluid()));
			}
		}
	}

	protected void spillParticle(ParticleOptions data) {
		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.25f);
		offset = VecHelper.rotate(offset, angle, Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y)
			.add(0, .25f, 0);
		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y - 1.75f, center.z, target.x, target.y, target.z);
	}

	@Override
	protected List<Recipe<?>> getMatchingRecipes() {
		List<Recipe<?>> matchingRecipes = super.getMatchingRecipes();

		if (!AllConfigs.server().recipes.allowBrewingInMixer.get())
			return matchingRecipes;
		
		Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent())
			return matchingRecipes;
		
		BasinBlockEntity basinTileEntity = basin.get();
		if (basin.isEmpty())
			return matchingRecipes;


		Storage<ItemVariant> availableItems = basinTileEntity
				.getItemStorage(null);
		if (availableItems == null)
			return matchingRecipes;

		try (Transaction t = TransferUtil.getTransaction()) {
			for (StorageView<ItemVariant> view : availableItems.nonEmptyViews()) {
				List<MixingRecipe> list = PotionMixingRecipes.BY_ITEM.get(view.getResource().getItem());
				if (list == null)
					continue;
				for (MixingRecipe mixingRecipe : list)
					if (matchBasinRecipe(mixingRecipe))
						matchingRecipes.add(mixingRecipe);
			}
		}

		return matchingRecipes;
	}

	@Override
	protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
		return ((r instanceof CraftingRecipe && !(r instanceof ShapedRecipe)
				 && AllConfigs.server().recipes.allowShapelessInMixer.get() && r.getIngredients()
				.size() > 1
				 && !MechanicalPressBlockEntity.canCompress(r)) && !AllRecipeTypes.shouldIgnoreInAutomation(r)
			|| r.getType() == AllRecipeTypes.MIXING.getType());
	}

	@Override
	public void startProcessingBasin() {
		if (running && runningTicks <= 20)
			return;
		super.startProcessingBasin();
		running = true;
		runningTicks = 0;
	}

	@Override
	public boolean continueWithPreviousRecipe() {
		runningTicks = 20;
		return true;
	}

	@Override
	protected void onBasinRemoved() {
		if (!running)
			return;
		runningTicks = 40;
		running = false;
	}

	@Override
	protected Object getRecipeCacheKey() {
		return shapelessOrMixingRecipesKey;
	}

	@Override
	protected boolean isRunning() {
		return running;
	}

	@Override
	protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
		return Optional.of(AllAdvancements.MIXER);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void tickAudio() {
		super.tickAudio();

		// SoundEvents.BLOCK_STONE_BREAK
		boolean slow = Math.abs(getSpeed()) < 65;
		if (slow && AnimationTickHolder.getTicks() % 2 == 0)
			return;
		if (runningTicks == 20)
			AllSoundEvents.MIXING.playAt(level, worldPosition, .75f, 1, true);
	}
}
