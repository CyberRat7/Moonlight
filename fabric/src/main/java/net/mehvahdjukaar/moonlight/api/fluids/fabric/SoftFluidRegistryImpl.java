package net.mehvahdjukaar.moonlight.api.fluids.fabric;

import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.mixins.fabric.MappedRegistryAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.checkerframework.checker.units.qual.K;

import java.util.HashMap;
import java.util.Map;

public class SoftFluidRegistryImpl {

    public static final ResourceKey<Registry<SoftFluid>> KEY = ResourceKey.createRegistryKey(
            Moonlight.res("moonlight/soft_fluids"));

    public static void init() {
    }

    public static ResourceKey<Registry<SoftFluid>> getRegistryKey() {
        return KEY;
    }


    public static Registry<SoftFluid> REG;

    private static final Map<Fluid, SoftFluid> FLUID_MAP = new HashMap<>();
    private static final Map<Item,SoftFluid> ITEM_MAP = new HashMap<>();


    public static void addExistingVanillaFluids() {
        //only runs on the first object
        var fluidMap = getFluidsMap();
        MappedRegistry<SoftFluid> reg = (MappedRegistry<SoftFluid>) SoftFluidRegistry.getDataPackRegistry();
        ((MappedRegistryAccessor)reg).setFrozen(false);
        for (Fluid f : Registry.FLUID) {
            try {
                if (f == null) continue;
                if (f instanceof FlowingFluid flowingFluid && flowingFluid.getSource() != f) continue;
                if (f == Fluids.EMPTY) continue;
                //if fluid map contains fluid it means that another equivalent fluid has already been registered
                if (fluidMap.containsKey(f)) continue;
                //is not equivalent: create new SoftFluid from forge fluid

                SoftFluid sf = (new SoftFluid.Builder(f)).build();
                //calling vanilla register function because calling that deferred register or forge registry now does nothing
                //cope
                //SOFT_FLUIDS.get().register(sf.getRegistryName(),sf);
                Registry.register(reg, Utils.getID(f), sf);
                fluidMap.put(f, sf);

            } catch (Exception ignored) {
            }
        }
        //adds empty fluid
        //Registry.register(reg, Moonlight.res("empty"), SoftFluidRegistry.EMPTY);
        reg.freeze();
    }

    public static Map<Fluid, SoftFluid> getFluidsMap() {
        return FLUID_MAP;
    }

    public static Map<Item, SoftFluid> getItemsMap() {
        return ITEM_MAP;
    }


    public static Holder<? extends SoftFluid> getDefaultValue(Registry<SoftFluid> reg) {
        return BuiltinRegistries.register(reg, ResourceKey.create(KEY, Moonlight.res("empty")), SoftFluidRegistry.EMPTY);
    }


}
