package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothingPantsModel extends VampirismArmorModel {

    private static ClothingPantsModel instance;

    public static ClothingPantsModel getInstance() {
        if (instance == null) {
            instance = new ClothingPantsModel();
        }
        return instance;
    }

    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer belt;

    public ClothingPantsModel() {
        super(32, 32);
        this.leftLeg = new ModelRenderer(this, 16, 0);
        this.leftLeg.setPos(-4F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.belt = new ModelRenderer(this, 4, 16);
        this.belt.setPos(0.0F, 0.0F, 0.0F);
        this.belt.addBox(-4.0F, 7.0F, -2.0F, 8.0F, 5.0F, 4.0F, 0.25F, 0.25F, 0.25F);
        this.rightLeg = new ModelRenderer(this, 0, 0);
        this.rightLeg.setPos(1.9F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, 0.25F, 0.25F);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyModels() {
        return ImmutableList.of(this.belt);
    }

    @Override
    protected Iterable<ModelRenderer> getLeftLegModels() {
        return ImmutableList.of(this.leftLeg);
    }

    @Override
    protected Iterable<ModelRenderer> getRightLegModels() {
        return ImmutableList.of(this.rightLeg);
    }
}
