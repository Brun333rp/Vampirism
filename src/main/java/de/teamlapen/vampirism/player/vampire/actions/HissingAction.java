package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class HissingAction extends DefaultVampireAction {

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaHissingCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaHissingEnabled.get();
    }

    @Override
    protected boolean activate(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().playNotifySound(ModSounds.entity_vampire_scream, SoundCategory.PLAYERS, 1, 1);
        vampire.getRepresentingPlayer().getCommandSenderWorld().getLoadedEntitiesOfClass(MobEntity.class, new AxisAlignedBB(vampire.getRepresentingPlayer().blockPosition()).inflate(10, 10, 10)).forEach(e -> {
            if (e.getTarget() == vampire.getRepresentingPlayer()) {
                e.targetSelector.getRunningGoals().filter(g -> g.getGoal() instanceof TargetGoal).forEach(PrioritizedGoal::stop);
                e.setTarget(null);
            }
        });
        return true;
    }
}
