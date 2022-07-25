package net.mehvahdjukaar.moonlight.api.client.model.fabric;

import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//needed cause fabric
public class SlaveModel implements BakedModel {

    public static final SlaveModel INSTANCE = new SlaveModel();

    private CustomBakedModel father;

    @Nullable
    private ExtraModelData data;
    public void prepare(CustomBakedModel father, @Nullable ExtraModelData data){
        this.data = data;
        this.father = father;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return father.getBlockQuads(blockState, direction, randomSource, RenderType.cutout(), data);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return father.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return father.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return father.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return father.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return father.getBlockParticle(data);
    }

    @Override
    public ItemTransforms getTransforms() {
        return father.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return father.getOverrides();
    }
}
