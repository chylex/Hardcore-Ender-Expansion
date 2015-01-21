package chylex.hee.render.entity.layer;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerDragonDeath implements LayerRenderer{
	private static final Random rand = new Random(432L);
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime, float entityTickTime, float rotationYaw, float rotationPitch, float unitSize){
		EntityDragon dragon = (EntityDragon)entity;
		
		if (dragon.deathTicks > 0){
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer renderer = tessellator.getWorldRenderer();
			RenderHelper.disableStandardItemLighting();
			
			float deathTime = (dragon.deathTicks+partialTickTime)/200F;
			float f8 = 0F;
			if (deathTime > 0.8F)f8 = (deathTime-0.8F)/0.2F;
			
			rand.setSeed(432L);
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(7425);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770,1);
			GlStateManager.disableAlpha();
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F,-1F,-2F);

			for(int i = 0; i < (deathTime+deathTime*deathTime)/2F*60F; ++i){
				GlStateManager.rotate(rand.nextFloat()*360F,1F,0F,0F);
				GlStateManager.rotate(rand.nextFloat()*360F,0F,1F,0F);
				GlStateManager.rotate(rand.nextFloat()*360F,0F,0F,1F);
				GlStateManager.rotate(rand.nextFloat()*360F,1F,0F,0F);
				GlStateManager.rotate(rand.nextFloat()*360F,0F,1F,0F);
				GlStateManager.rotate(rand.nextFloat()*360F+deathTime*90F,0F,0F,1F);
				renderer.startDrawing(6);
				float f9 = rand.nextFloat()*20F+5F+f8*10F;
				float f10 = rand.nextFloat()*2F+1F+f8*2F;
				renderer.setColorRGBA_I(16777215,(int)(255F*(1F-f8)));
				renderer.addVertex(0D,0D,0D);
				renderer.setColorRGBA_I(16711935,0);
				renderer.addVertex(-0.866D*f10,f9,-0.5F*f10);
				renderer.addVertex(0.866D*f10,f9,-0.5F*f10);
				renderer.addVertex(0D,f9,f10);
				renderer.addVertex(-0.866D*f10,f9,-0.5F*f10);
				tessellator.draw();
			}

			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.shadeModel(7424);
			GlStateManager.color(1F,1F,1F,1F);
			GlStateManager.enableTexture2D();
			GlStateManager.enableAlpha();
			RenderHelper.enableStandardItemLighting();
		}
	}

	@Override
	public boolean shouldCombineTextures(){
		return false;
	}
}
