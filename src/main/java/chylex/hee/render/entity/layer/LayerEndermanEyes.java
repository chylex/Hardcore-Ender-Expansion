package chylex.hee.render.entity.layer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.render.entity.RenderMobParalyzedEnderman;

@SideOnly(Side.CLIENT)
public class LayerEndermanEyes implements LayerRenderer{
	private static final ResourceLocation texture = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	private static final ResourceLocation textureParalyzed = new ResourceLocation("hardcoreenderexpansion:textures/entity/enderman_eyes_brainless.png");
	private final RenderLiving renderer;
	private final boolean isParalyzed;
	
	public LayerEndermanEyes(RenderLiving renderer){
		this.renderer = renderer;
		isParalyzed = renderer instanceof RenderMobParalyzedEnderman;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime, float entityTickTime, float rotationYaw, float rotationPitch, float unitSize){
		renderer.bindTexture(isParalyzed ? textureParalyzed : texture);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(1,1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!entity.isInvisible());
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,61680%65536,61680/65536);
		GlStateManager.enableLighting();
		GlStateManager.color(1F,1F,1F,1F);
		renderer.getMainModel().render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitSize);
		renderer.func_177105_a((EntityLiving)entity,partialTickTime);
		
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

	@Override
	public boolean shouldCombineTextures(){
		return false;
	}
}
