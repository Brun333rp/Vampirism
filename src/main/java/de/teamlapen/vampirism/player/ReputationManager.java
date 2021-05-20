package de.teamlapen.vampirism.player;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.IReputationManager;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class ReputationManager implements IReputationManager {

    private final IFactionPlayer<?> factionPlayer;
    private int reputation;
    private Reputation reputationLevel = Reputation.NEUTRAL;
    private boolean dirty;

    public ReputationManager(IFactionPlayer<?> factionPlayer) {
        this.factionPlayer = factionPlayer;
    }

    @Nonnull
    @Override
    public Reputation getReputationLevel() {
        return reputationLevel;
    }

    @Override
    public int getReputation() {
        return reputation;
    }

    private void updateLevel() {
        Reputation newReputation = Reputation.HATED;
        for (Reputation value : Reputation.values()) {
            if (value.getReputationReq() > this.reputation) {
                break;
            }else {
                newReputation = value;
            }
        }
        if (this.reputationLevel != newReputation) {
            this.reputationLevel = newReputation;
            this.dirty = true;
        }
    }

    public void setReputation(Reputation rep) {
        this.reputationLevel = rep;
        this.reputation = reputationLevel.getReputationReq();
        this.dirty = true;
    }

    public void setReputation(int rep) {
        this.reputation = rep;
        this.updateLevel();
    }

    public void addReputation(int reputation) {
        this.reputation += reputation;
        this.updateLevel();
    }

    public boolean tick() {
        long gameTime = this.factionPlayer.getRepresentingPlayer().world.getGameTime();
        switch (reputationLevel){
            case HATED:
            case HOSTILE:
            case UNFRIENDLY:
                if (gameTime % 1200 == 0) {
                    this.reputation++;
                }
        }

        if (gameTime % 6000 == 0) {
            updateLevel();
            return true;
        }
        if (this.dirty) {
            this.dirty = false;
            return true;
        }
        return false;
    }

    public CompoundNBT writeNBT(CompoundNBT nbt) {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("reputation", this.reputation);
        nbt.put("reputationManager", compound);
        return nbt;
    }

    public void readNBT(CompoundNBT nbt) {
        if (nbt.contains("reputationManager")) {
            CompoundNBT compound = nbt.getCompound("reputationManager");
            this.reputation = compound.getInt("reputation");
            this.updateLevel();
        }
    }


}
