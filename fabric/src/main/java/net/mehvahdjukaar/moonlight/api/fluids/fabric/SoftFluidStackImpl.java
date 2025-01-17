package net.mehvahdjukaar.moonlight.api.fluids.fabric;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.util.PotionNBTHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoftFluidStackImpl extends SoftFluidStack{

    public SoftFluidStackImpl(Holder<SoftFluid> fluid, int count, CompoundTag tag) {
        super(fluid, count, tag);
    }

    public static SoftFluidStack of(Holder<SoftFluid> fluid, int count, @Nullable CompoundTag tag) {
        return new SoftFluidStackImpl(fluid, count, tag);
    }

    public static FluidVariant toFabricFluid(SoftFluidStack softFluid) {
        // tag stuff
        List<String> nbtKey = softFluid.fluid().getNbtKeyFromItem();
        CompoundTag tag = softFluid.getTag();
        CompoundTag newCom = new CompoundTag();
        if (tag != null && !tag.isEmpty()  && nbtKey != null) {
            for (String k : nbtKey) {

                Tag c = tag.get(k);
                if (c != null) {
                    newCom.put(k, c);
                }
            }
            if (newCom.isEmpty()) newCom = null;
        }
        Fluid vanillaFluid = softFluid.fluid().getVanillaFluid();
        return FluidVariant.of(vanillaFluid, newCom);
    }

    public static SoftFluidStack fromFabricFluid(FluidVariant fluidStack, int bottlesAmount) {
        return SoftFluidStack.fromFluid(fluidStack.getFluid(), bottlesAmount,
                fluidStack.hasNbt() ? fluidStack.getNbt().copy() : null);
    }

}
