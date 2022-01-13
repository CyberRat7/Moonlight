package net.mehvahdjukaar.selene.mixins;

import net.mehvahdjukaar.selene.villager_ai.VillagerAIManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    public VillagerMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = ("registerBrainGoals"), at = @At("RETURN"))
    protected void reg(Brain<Villager> pVillagerBrain, CallbackInfo ci) {
        VillagerAIManager.onRegisterBrainGoals(pVillagerBrain, this);
    }

}