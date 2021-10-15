package net.mehvahdjukaar.selene.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.selene.map.CustomDecoration;
import net.mehvahdjukaar.selene.map.CustomDecorationHolder;
import net.mehvahdjukaar.selene.map.client.MapDecorationRenderHandler;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public abstract class MapItemRendererMixin {


    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    private void render(PoseStack poseStack, MultiBufferSource buffer, int mapId, MapItemSavedData mapData , boolean isOnFrame, int light, CallbackInfo ci) {
        if (mapData instanceof CustomDecorationHolder) {
            int index = mapData.decorations.size();
            for (CustomDecoration decoration : ((CustomDecorationHolder) mapData).getCustomDecorations().values()){

                if(MapDecorationRenderHandler.render(decoration,matrixStack,buffer,mapData,isOnFrame,light,index)) index++;
            }
        }
    }

}