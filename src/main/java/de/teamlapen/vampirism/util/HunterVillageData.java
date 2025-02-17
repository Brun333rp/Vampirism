package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class HunterVillageData implements IVillageFactionData {
    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLUE_BANNER);
        CompoundNBT compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListNBT listNBT = new BannerPattern.Builder()
                .addPattern(BannerPattern.STRIPE_SMALL, DyeColor.BLACK)
                .addPattern(BannerPattern.STRIPE_CENTER, DyeColor.BLACK)
                .addPattern(BannerPattern.BORDER, DyeColor.WHITE)
                .addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
                .addPattern(BannerPattern.CURLY_BORDER, DyeColor.BLACK)
                .addPattern(BannerPattern.STRAIGHT_CROSS, DyeColor.WHITE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ADDITIONAL);
        itemStack.setHoverName(new TranslationTextComponent("block.minecraft.ominous_banner").withStyle(TextFormatting.GOLD));
        return itemStack;
    }
    private final ItemStack banner = createBanner();
    private List<CaptureEntityEntry> captureEntityEntries;

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return this.banner.copy();
    }

    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        if (this.captureEntityEntries == null) {
            this.captureEntityEntries = Lists.newArrayList(new CaptureEntityEntry(ModEntities.hunter, 10)/*, new CaptureEntityEntry(ModEntities.advanced_hunter, 2)*/);
        }
        return this.captureEntityEntries;
    }

    @Override
    public VillagerProfession getFactionVillageProfession() {
        return ModVillage.hunter_expert;
    }

    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return HunterBaseEntity.class;
    }

    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return ModEntities.task_master_hunter;
    }

    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(ModBlocks.totem_top_vampirism_hunter, ModBlocks.totem_top_vampirism_hunter_crafted);
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.matches(this.banner, stack);
    }
}
