package net.mehvahdjukaar.moonlight.api.platform.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public abstract class ChannelHandler {

    @ExpectPlatform
    public static ChannelHandler createChannel(ResourceLocation channelMame) {
        throw new AssertionError();
    }

    protected final ResourceLocation channelName;

    public ChannelHandler(ResourceLocation channelName) {
        this.channelName = channelName;
    }

    public abstract <M extends Message> void register(
            NetworkDir direction,
            Class<M> messageClass,
            Function<FriendlyByteBuf, M> decoder);



    public interface Context {
        NetworkDir getDirection();

        Player getSender();
    }

    public enum NetworkDir {
        PLAY_TO_SERVER, PLAY_TO_CLIENT;
    }

    public abstract void sendToClientPlayer(ServerPlayer serverPlayer, Message message);

    public abstract void sendToAllClientPlayers(Message message);

    public abstract void sendToAllClientPlayersInRange(Level level, BlockPos pos, int radius, Message message);

    public abstract void sentToAllClientPlayersTrackingEntity(Entity target, Message message);

    public abstract void sendToServer(Message message);

}