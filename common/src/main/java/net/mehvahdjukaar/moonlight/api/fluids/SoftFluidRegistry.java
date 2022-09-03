package net.mehvahdjukaar.moonlight.api.fluids;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.moonlight.api.map.MapDecorationRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.client.SoftFluidClient;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundFinalizeFluidsMessage;
import net.mehvahdjukaar.moonlight.core.network.ModMessages;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//TODO: maybe split into api/core?
public class SoftFluidRegistry {

    public static final ResourceLocation EMPTY_ID = Moonlight.res("empty");

    public static SoftFluid getEmpty(){
        return get(EMPTY_ID);
    }

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceKey<Registry<SoftFluid>> getRegistryKey() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Map<Fluid, SoftFluid> getFluidsMap() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Map<Item, SoftFluid> getItemsMap() {
        throw new AssertionError();
    }

    public static Registry<SoftFluid> getDataPackRegistry() {
        return Utils.hackyGetRegistryAccess().registryOrThrow(getRegistryKey());
    }

    public static Collection<SoftFluid> getValues() {
        return getDataPackRegistry().stream().toList();
    }

    public static Set<Map.Entry<ResourceKey<SoftFluid>, SoftFluid>> getEntries() {
        return getDataPackRegistry().entrySet();
    }

    public static SoftFluid get(String id) {
        return get(new ResourceLocation(id));
    }


    @Nullable
    public static ResourceLocation getID(SoftFluid s) {
        return getDataPackRegistry().getKey(s);
    }

    /**
     * gets a soft fluid provided his registry id
     *
     * @param id fluid registry id
     * @return soft fluid. empty fluid if not found
     */
    public static SoftFluid get(ResourceLocation id) {
        String namespace = id.getNamespace();
        if (namespace.equals("selene") || namespace.equals("minecraft")) id = Moonlight.res(id.getPath()); //backwards compat
        // mc stuff has my id //TODO: split into diff folders for each modded fluid
        return getDataPackRegistry().get(id);
    }

    public static Optional<SoftFluid> getOptional(ResourceLocation id) {
        String namespace = id.getNamespace();
        if (namespace.equals("selene") || namespace.equals("minecraft")) id = Moonlight.res(id.getPath()); //backwards compat
        return getDataPackRegistry().getOptional(id);
    }

    /**
     * gets a soft fluid provided a forge fluid
     *
     * @param fluid equivalent forge fluid
     * @return soft fluid. empty fluid if not found
     */
    public static SoftFluid fromForgeFluid(Fluid fluid) {
        return getFluidsMap().getOrDefault(fluid, getEmpty());
    }

    /**
     * gets a soft fluid provided a bottle like item
     *
     * @param filledContainerItem item containing provided fluid
     * @return soft fluid. empty fluid if not found
     */
    @Nonnull
    public static SoftFluid fromItem(Item filledContainerItem) {
        return getItemsMap().getOrDefault(filledContainerItem, getEmpty());
    }


    private static void populateSlaveMaps() {
        var itemMap = getItemsMap();
        itemMap.clear();
        var fluidsMap = getFluidsMap();
        fluidsMap.clear();
        for (var s : getValues()) {
            if (PlatformHelper.isModLoaded(s.getFromMod())) {
                s.getEquivalentFluids().forEach(f -> fluidsMap.put(f, s));
                s.getContainerList().getPossibleFilled().forEach(i -> {
                    //don't associate water to potion bottle
                    if (i != Items.POTION || !(getID(s).toString().equals("minecraft:water"))) {
                        itemMap.put(i, s);
                    }
                });
            }
        }
    }

    //wtf is going on here

    //TODO: call these
    public static void postInitClient() {
        populateSlaveMaps();
        SoftFluidClient.refresh();
    }

    //on data load
    public static void onDataLoad() {
        populateSlaveMaps();
        //registers existing fluids. also update the salve maps
        registerExistingVanillaFluids();
        ModMessages.CHANNEL.sendToAllClientPlayers(new ClientBoundFinalizeFluidsMessage());
    }


    @ExpectPlatform
    private static void registerExistingVanillaFluids() {
        throw new AssertionError();
    }
}

