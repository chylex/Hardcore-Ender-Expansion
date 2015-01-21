package chylex.hee.render.entity.layer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.render.entity.AbstractRenderMobEnderman;

@SideOnly(Side.CLIENT)
public class LayerEndermanHeldItem implements LayerRenderer{
	private final AbstractRenderMobEnderman renderer;
	
	public LayerEndermanHeldItem(AbstractRenderMobEnderman renderer){
		this.renderer = renderer;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime, float entityTickTime, float rotationYaw, float rotationPitch, float unitSize){
		ItemStack carrying = ((IEndermanRenderer)entity).getCarrying();
		
		if (carrying != null){
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(0F,0.6875F,-0.75F);
			GL11.glRotatef(20F,1F,0F,0F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glScalef(-0.5F,-0.5F,0.5F);
			
			int brightness = entity.getBrightnessForRender(partialTickTime);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightness%65536F,brightness/65536F);
			GL11.glColor4f(1F,1F,1F,1F);
			renderer.bindTexture(TextureMap.locationBlocksTexture);
			renderItemStack(entity,carrying);
			
			GL11.glPopMatrix();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
	}
	
	private void renderItemStack(EntityLivingBase entity, ItemStack is){
		if (is.getItem() instanceof ItemBlock){
			BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
			blockRenderer.renderBlockBrightness(((ItemBlock)is.getItem()).block.getStateFromMeta(is.getItemDamage()),1F);
		}
		else{
			Minecraft.getMinecraft().getItemRenderer().renderItem(entity,is,ItemCameraTransforms.TransformType.THIRD_PERSON);
		}
	}

	@Override
	public boolean shouldCombineTextures(){
		return false;
	}
}
