package de.teamlapen.vampirism.api.entity.player;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public interface IReputationManager {

    IReputationManager DUMMY = new IReputationManager() { //TODO 1.17 remove
        @Nonnull
        @Override
        public Reputation getReputationLevel() {
            return Reputation.NEUTRAL;
        }

        @Override
        public int getReputation() {
            return 0;
        }

        @Override
        public void setReputation(Reputation rep) {

        }

        @Override
        public void setReputation(int rep) {

        }

        @Override
        public void addReputation(int reputation) {

        }
    };

    @Nonnull
    Reputation getReputationLevel();

    int getReputation();

    public void setReputation(Reputation rep);

    public void setReputation(int rep);

    public void addReputation(int reputation);

    enum Reputation {
        HATED(-2000,"hated", TextFormatting.DARK_RED), HOSTILE(-1500, "hostile", TextFormatting.RED), UNFRIENDLY(-1000, "unfriendly", TextFormatting.GOLD), NEUTRAL(-500, "neutral", TextFormatting.YELLOW), FRIENDLY(500, "friendly", TextFormatting.GREEN), HONORED(1500, "honored", TextFormatting.DARK_GREEN), REVERED(2500, "revered", TextFormatting.LIGHT_PURPLE), EXALTED(3500, "exalted", TextFormatting.AQUA);

        private int reputationReq;
        private String name;
        private ITextComponent component;

        Reputation(int reputationReq, String name, TextFormatting nameFormat) {
            this.reputationReq = reputationReq;
            this.name = name;
            this.component = new TranslationTextComponent("reputation.vampirism." + name).mergeStyle(nameFormat);
        }

        public int getReputationReq() {
            return reputationReq;
        }

        public String getName() {
            return name;
        }

        public ITextComponent getComponent() {
            return component;
        }
    }
}
