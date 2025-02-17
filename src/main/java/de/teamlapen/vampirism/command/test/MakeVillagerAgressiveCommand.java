package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class MakeVillagerAgressiveCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("makeVillagerAgressive")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return makeVillagerAgressive(context.getSource().getPlayerOrException());
                });
    }

    private static int makeVillagerAgressive(ServerPlayerEntity asPlayer) {
        List<VillagerEntity> l = asPlayer.getCommandSenderWorld().getEntitiesOfClass(VillagerEntity.class, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (VillagerEntity v : l) {
            if (v instanceof IFactionEntity) continue;
            TotemTileEntity.makeAgressive(v);
        }
        return 0;
    }
}
