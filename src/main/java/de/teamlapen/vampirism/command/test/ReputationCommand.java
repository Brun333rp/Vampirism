package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IReputationManager;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.command.EnumArgument;
import org.apache.commons.lang3.tuple.Pair;

public class ReputationCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("reputation")
                .then(Commands.literal("amount")
                        .executes(context -> printReputation(context.getSource(), context.getSource().asPlayer())))
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("level")
                        .then(Commands.argument("level", EnumArgument.enumArgument(IReputationManager.Reputation.class))
                                .executes(context -> setReputationLevel(context.getSource().asPlayer(), context.getArgument("level", IReputationManager.Reputation.class)))))
                .then(Commands.literal("value")
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> setReputation(context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "amount")))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> increaseReputation(context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int setReputationLevel(ServerPlayerEntity asPlayer, IReputationManager.Reputation level) {
        FactionPlayerHandler.getOpt(asPlayer).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(p -> p).ifPresent(fp ->fp.getReputationManager().setReputation(level));
        return 0;
    }

    private static int setReputation(ServerPlayerEntity asPlayer, int level) {
        FactionPlayerHandler.getOpt(asPlayer).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(p -> p).ifPresent(fp ->fp.getReputationManager().setReputation(level));
        return 0;
    }

    private static int increaseReputation(ServerPlayerEntity asPlayer, int level) {
        FactionPlayerHandler.getOpt(asPlayer).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(p -> p).ifPresent(fp ->fp.getReputationManager().addReputation(level));
        return 0;
    }

    private static int printReputation(CommandSource source, ServerPlayerEntity asPlayer) {
        Pair<Integer, IReputationManager.Reputation> pair = FactionPlayerHandler.getOpt(asPlayer).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(p -> p).map(fp -> Pair.of(fp.getReputationManager().getReputation(), fp.getReputationManager().getReputationLevel())).orElse(Pair.of(0, IReputationManager.Reputation.NEUTRAL));
        source.sendFeedback(new TranslationTextComponent("command.vampirism.test.reputation",pair.getValue().getComponent(), pair.getKey()), false);
        return 0;
    }


}
