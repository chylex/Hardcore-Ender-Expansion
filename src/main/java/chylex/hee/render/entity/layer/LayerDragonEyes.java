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

@SideOnly(Side.CLIENT)
public class LayerDragonEyes implements LayerRenderer{
	private static final ResourceLocation texture = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	private final RenderLiving renderer;
	
	public LayerDragonEyes(RenderLiving renderer){
		this.renderer = renderer;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime, float entityTickTime, float rotationYaw, float rotationPitch, float unitSize){
		renderer.bindTexture(texture);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(1,1);
        GlStateManager.disableLighting();
        GlStateManager.depthFunc(514);
        
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,61680%65536,61680/65536);
        GlStateManager.enableLighting();
        GlStateManager.color(1F,1F,1F,1F);
        renderer.getMainModel().render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitSize);
		renderer.func_177105_a((EntityLiving)entity,partialTickTime);
		
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthFunc(515);
	}

	@Override
	public boolean shouldCombineTextures(){
		return false;
	}
}
