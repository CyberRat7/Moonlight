package net.mehvahdjukaar.moonlight.api.map;

import net.mehvahdjukaar.moonlight.api.map.markers.MapBlockMarker;
import net.mehvahdjukaar.moonlight.api.map.type.CustomDecorationType;
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapDataRegistry {

    public static final ResourceKey<Registry<MapDecorationType<?, ?>>> REGISTRY_KEY = MapDataInternal.KEY;

    /**
     * Registers a custom data type to be stored in map data. Type will provide its onw data implementation
     **/
    public static <T extends CustomMapData> CustomMapData.Type<T> registerCustomMapSavedData(CustomMapData.Type<T> type) {
        return MapDataInternal.registerCustomMapSavedData(type);
    }

    public static <T extends CustomMapData> CustomMapData.Type<T> registerCustomMapSavedData(ResourceLocation id, Function<CompoundTag, T> factory) {
        return registerCustomMapSavedData(new CustomMapData.Type<>(id, factory));
    }

    /**
     * Call before mod setup. Register a code defined map marker type. You will still need to add a related json file
     */
    public static <T extends CustomDecorationType<?, ?>> T registerCustomType(T decorationType) {
       return MapDataInternal.registerCustomType(decorationType);
    }

    /**
     * Use to add non-permanent decoration like player icon, only visible to player holding the map.
     * Called by the client every time map marker would change.
     * This means that for moving ones you should manage this yourself with a client tick event
     * @param event callback
     */
    public static void addDynamicClientMarkersEvent(BiFunction<Integer, MapItemSavedData, Set<MapBlockMarker<?>>> event){
        MapDataInternal.addDynamicClientMarkersEvent(event);
    }

    /**
     * Use to add non-permanent per-player decoration like player icon, only visible to player holding the map.
     * Called by server every couple minutes and synced to the client.
     * Whether these will be saved or not is up to the marker provided
     * @param event callback
     */
    public static void addDynamicServerMarkersEvent(TriFunction<Player, Integer, MapItemSavedData, Set<MapBlockMarker<?>>> event){
        MapDataInternal.addDynamicServerMarkersEvent(event);
    }

    public static CustomDecorationType<?, ?> getCustomType(ResourceLocation resourceLocation) {
        return MapDataInternal.getCustomType(resourceLocation);
    }

    public static MapDecorationType<?, ?> getAssociatedType(Holder<Structure> structure) {
        return MapDataInternal.getAssociatedType(structure);
    }

    public static Registry<MapDecorationType<?, ?>> getRegistry(RegistryAccess registryAccess) {
        return MapDataInternal.getRegistry(registryAccess);
    }

    public static MapDecorationType<?, ?> get(ResourceLocation id) {
        return MapDataInternal.get(id);
    }

    public static Optional<MapDecorationType<?, ?>> getOptional(ResourceLocation id) {
        return MapDataInternal.getOptional(id);
    }

}