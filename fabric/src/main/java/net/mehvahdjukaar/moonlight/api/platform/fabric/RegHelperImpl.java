package net.mehvahdjukaar.moonlight.api.platform.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.mehvahdjukaar.moonlight.api.client.fabric.IFabricMenuType;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.core.misc.AntiRepostWarning;
import net.mehvahdjukaar.moonlight.core.set.fabric.BlockSetInternalImpl;
import net.mehvahdjukaar.moonlight.fabric.FabricRecipeConditionManager;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RegHelperImpl {

    public static final Map<ResourceKey<? extends Registry<?>>, Map<String, RegistryQueue<?>>> REGISTRIES = new LinkedHashMap<>();

    public static final List<ResourceKey<? extends Registry<?>>> REG_PRIORITY = List.of(
            Registries.SOUND_EVENT, Registries.BLOCK, Registries.FLUID, Registries.PARTICLE_TYPE,
            Registries.ENTITY_TYPE, Registries.ITEM,
            Registries.BLOCK_ENTITY_TYPE, Registries.PLACEMENT_MODIFIER_TYPE, Registries.STRUCTURE_TYPE,
            Registries.STRUCTURE_PIECE, Registries.FEATURE, Registries.CONFIGURED_FEATURE,
            Registries.PLACED_FEATURE
    );

    //order is important here
    static {
        REG_PRIORITY.forEach(e -> REGISTRIES.put(e, new LinkedHashMap<>()));
    }

    //call from mod setup
    @ApiStatus.Internal
    public static void registerEntries() {
        for (var m : REGISTRIES.entrySet()) {
            m.getValue().values().forEach(RegistryQueue::initializeEntries);
            if (m.getKey() == Registries.BLOCK) {
                //dynamic block registration after all blocks
                BlockSetInternalImpl.registerEntries();
            }
        }
        //register entities attributes now
        ATTRIBUTE_REGISTRATIONS.forEach(e -> e.accept(FabricDefaultAttributeRegistry::register));
    }


    @SuppressWarnings("unchecked")
    public static <T, E extends T> RegSupplier<E> register(ResourceLocation name, Supplier<E> supplier,ResourceKey<? extends Registry<T>> reg) {
        if (supplier == null) {
            throw new IllegalArgumentException("Registry entry Supplier for " + name + " can't be null");
        }
        String modId = name.getNamespace();
        var m = REGISTRIES.computeIfAbsent(reg, h -> new LinkedHashMap<>());
        RegistryQueue<T> registry = (RegistryQueue<T>) m.computeIfAbsent(modId,
                c -> {
                    if(PlatformHelper.getPhysicalSide().isClient()) AntiRepostWarning.addMod(modId);

                    return new RegistryQueue<>(reg);
                });
        return registry.add(supplier, name);
    }

    public static <T, E extends T> RegSupplier<E> registerAsync(ResourceLocation name, Supplier<E> supplier, ResourceKey<? extends Registry<T>> regKey) {
        RegistryQueue.EntryWrapper<E, T> entry = new RegistryQueue.EntryWrapper<>(name, supplier, regKey);
        entry.initialize();
        return entry;
    }

    public static <T> void registerInBatch(ResourceKey<? extends Registry<T>> reg, Consumer<Registrator<T>> eventListener) {
        var m = REGISTRIES.computeIfAbsent(reg, h -> new LinkedHashMap<>());
        RegistryQueue<T> registry = (RegistryQueue<T>) m.computeIfAbsent("a", c -> new RegistryQueue<>(reg));
        registry.add(eventListener);
    }

    public static <C extends AbstractContainerMenu> RegSupplier<MenuType<C>> registerMenuType(
            ResourceLocation name,
            TriFunction<Integer, Inventory, FriendlyByteBuf, C> containerFactory) {
        return register(name, () -> IFabricMenuType.create(containerFactory::apply), Registries.MENU);
    }

    public static <T extends Entity> RegSupplier<EntityType<T>> registerEntityType(ResourceLocation name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange, int updateInterval) {
        Supplier<EntityType<T>> s = () -> EntityType.Builder.of(factory, category).sized(width, height).build(name.toString());
        return register(name, s, Registries.ENTITY_TYPE);
    }

    public static void registerItemBurnTime(Item item, int burnTime) {
        FuelRegistry.INSTANCE.add(item, burnTime);
    }

    public static void registerBlockFlammability(Block item, int fireSpread, int flammability) {
        FlammableBlockRegistry.getDefaultInstance().add(item, fireSpread, flammability);
    }

    public static void registerVillagerTrades(VillagerProfession profession, int level, Consumer<List<VillagerTrades.ItemListing>> factories) {
        FabricSetupCallbacks.COMMON_SETUP.add(() -> TradeOfferHelper.registerVillagerOffers(profession, level, factories));
    }

    public static void registerWanderingTraderTrades(int level, Consumer<List<VillagerTrades.ItemListing>> factories) {
        //this just runs immediately... needs to run on mod setup instead
        FabricSetupCallbacks.COMMON_SETUP.add(() -> TradeOfferHelper.registerWanderingTraderOffers(level, factories));
    }

    public static void addAttributeRegistration(Consumer<RegHelper.AttributeEvent> eventListener) {
        ATTRIBUTE_REGISTRATIONS.add(eventListener);
    }

    private static final List<Consumer<RegHelper.AttributeEvent>> ATTRIBUTE_REGISTRATIONS = new ArrayList<>();

    public static void addCommandRegistration(Consumer<CommandDispatcher<CommandSourceStack>> eventListener) {
        CommandRegistrationCallback.EVENT.register((d, s, b) -> eventListener.accept(d));
    }

    public static void registerSimpleRecipeCondition(ResourceLocation id, Predicate<String> predicate) {
        FabricRecipeConditionManager.registerSimple(id, predicate);
    }


}
