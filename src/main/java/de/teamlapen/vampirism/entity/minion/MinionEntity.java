package de.teamlapen.vampirism.entity.minion;

import com.mojang.authlib.GameProfile;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


public abstract class MinionEntity extends VampirismEntity implements IPlayerOverlay {
    private final static Logger LOGGER = LogManager.getLogger();

    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    protected static final DataParameter<Optional<UUID>> LORD_ID = EntityDataManager.createKey(MinionEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    /**
     * Only available server side.
     * Should be available on world join
     */
    @Nullable
    protected PlayerMinionController playerMinionController;

    @Nullable
    private GameProfile skinProfile;

    /**
     * Only valid if playerMinionController !=null
     */
    private int minionId;
    /**
     * Only valid if playerMinionController !=null
     */
    private int token;
    /**
     * Only valid and nonnull if playerMinionController !=null
     */
    private MinionData minionData;

    @Nullable
    @Override
    public GameProfile getOverlayPlayerProfile() {
        if (skinProfile == null) {
            skinProfile = this.getLordID().map(this.world::getPlayerByUuid).map(PlayerEntity::getGameProfile).orElse(null);

        }
        return skinProfile;
    }

    protected MinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    public void claimMinionSlot(int id, @Nonnull PlayerMinionController controller) {
        assert minionId == 0;
        controller.claimMinionSlot(id).ifPresent(token -> {
            playerMinionController = controller;
            minionId = id;
            this.token = token;
            getDataManager().set(LORD_ID, Optional.of(playerMinionController.getUUID()));
        });
    }

    @Nonnull
    public Optional<ILordPlayer> getLordOpt() {
        return Optional.ofNullable(getLord());
    }

    public float getScale() {
        return 0.8f;
    }

    @Nonnull
    @Override
    public EntitySize getSize(@Nonnull Pose p_213305_1_) {
        return super.getSize(p_213305_1_).scale(getScale());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote && !this.isValid() && this.isAlive()) {
            LOGGER.warn("Minion without lord.");
            this.remove();
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (playerMinionController != null) {
            this.minionData = playerMinionController.checkoutMinion(this.minionId, this.token, this);
            if (minionData == null) {
                this.playerMinionController = null;
            }
        }
    }

    @Override
    public void onDeath(@Nonnull DamageSource p_70645_1_) {
        super.onDeath(p_70645_1_);
        if (this.playerMinionController != null) {
            this.playerMinionController.markDeadAndReleaseMinionSlot(minionId, token);
            this.playerMinionController = null;
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        UUID id = nbt.hasUniqueId("lord") ? nbt.getUniqueId("lord") : null;
        if (id != null && world instanceof ServerWorld) {
            this.playerMinionController = MinionWorldData.getData((ServerWorld) this.world).getController(id);
            if (this.playerMinionController == null) {
                LOGGER.warn("Cannot get PlayerMinionController for {}", id);
            } else {
                this.minionId = nbt.getInt("minion_id");
                this.token = nbt.getInt("minion_token");
                this.getDataManager().set(LORD_ID, Optional.of(id));
            }
        }
    }

    /**
     * DON't use
     * Called to remove entity from world on call from lord.
     * Does checkin minion
     */
    @Deprecated
    public void recallMinion() {
        this.remove();
    }

    @Override
    public void remove(boolean p_remove_1_) {
        super.remove(p_remove_1_);
        if (playerMinionController != null) {
            playerMinionController.checkInMinion(this.minionId, this.token);
            this.minionData = null;
            this.playerMinionController = null;
        }
    }

    @Nullable
    protected ILordPlayer getLord() {
        return this.getLordID().map(this.world::getPlayerByUuid).filter(PlayerEntity::isAlive).map(FactionPlayerHandler::get).orElse(null);
    }

    protected Optional<UUID> getLordID() {
        return this.getDataManager().get(LORD_ID);
    }


    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(VReference.sunDamage).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(LORD_ID, Optional.empty());
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (isValid()) {
            this.getLordID().ifPresent(id -> nbt.putUniqueId("lord", id));
            nbt.putInt("minion_id", minionId);
            nbt.putInt("minion_token", token);
        }
    }

    protected boolean isValid() {
        return this.playerMinionController != null;
    }
}
