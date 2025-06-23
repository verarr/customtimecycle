package xyz.eclipseisoffline.customtimecycle.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.eclipseisoffline.customtimecycle.TimeManager;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    private PlayerList playerList;

    @WrapMethod(method = "synchronizeTime")
    public void synchronizeTime(ServerLevel level, Operation<Void> original) {
        if (!TimeManager.getInstance(level).isNormalTimeRate()) {
            // Tell the clients not to update time locally when using custom time rate to prevent sync issues
            playerList.broadcastAll(new ClientboundSetTimePacket(level.getGameTime(), level.getDayTime(), false));
        } else {
            original.call(level);
        }
    }
}
