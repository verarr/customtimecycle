package xyz.eclipseisoffline.customtimecycle;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;

public class PermissionsService {
    public static final String PERMISSION_ROOT = "timecycle";

    public static boolean hasPermission(@NotNull ServerPlayer player,
                                        @NotNull String permission,
                                        int defaultPermissionLevel) {
        if (player.hasPermissions(defaultPermissionLevel)) {
            return true;
        }

        String permissionNode = String.format("%s.%s", PERMISSION_ROOT, permission);

        return PermissionAPI.getRegisteredNodes().stream()
                .filter(node -> node.getType() == PermissionTypes.BOOLEAN)
                .filter(node -> node.getNodeName().equals(permissionNode))
                .anyMatch(node -> (boolean) node.getDefaultResolver().resolve(player, player.getUUID()));
    }

    public static boolean hasPermission(@NotNull CommandSourceStack commandSource,
                                        @NotNull String permission,
                                        int defaultPermissionLevel) {

        return commandSource.hasPermission(defaultPermissionLevel);
    }

    public static boolean sourceHasPermission(@NotNull CommandSourceStack commandSource,
                                              @NotNull String permission,
                                              int defaultPermissionLevel) {
        if (commandSource.getPlayer() != null) {
            return hasPermission(commandSource.getPlayer(), permission, defaultPermissionLevel);
        } else {
            return hasPermission(commandSource, permission, defaultPermissionLevel);
        }
    }
}