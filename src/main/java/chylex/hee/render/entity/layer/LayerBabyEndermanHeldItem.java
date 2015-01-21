package chylex.hee.render.entity.layer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.render.entity.AbstractRenderMobEnderman;

@SideOnly(Side.CLIENT)
public class LayerBabyEndermanHeldItem implements LayerRenderer{
	private final AbstractRenderMobEnderman renderer;
	
	public LayerBabyEndermanHeldItem(AbstractRenderMobEnderman renderer){
		this.renderer = renderer;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime, float entityTickTime, float rotationYaw, float rotationPitch, float unitSize){
		ItemStack is = ((IEndermanRenderer)entity).getCarrying();
		if (is.getItem() == Item.getItemFromBlock(Blocks.bedrock))return;
		
		Item item = is.getItem();
		Block block = Block.getBlockFromItem(item);
		
		if (block != Blocks.bedrock){
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(0F,0.6875F,-0.75F);
			GL11.glRotatef(20F,1F,0F,0F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glScalef(-0.5F,-0.5F,0.5F);
			
			int brightness = entity.getBrightnessForRender(partialTickTime);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightness%65536F,brightness/65536F);
			GL11.glColor4f(1F,1F,1F,1F);
			renderItemStack(entity,is);
			
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
			GL11.glPushMatrix();
			GL11.glTranslatef(0.2F,-0.3F,-0.3F);
			Minecraft.getMinecraft().getItemRenderer().renderItem(entity,is,ItemCameraTransforms.TransformType.THIRD_PERSON);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures(){
		return false;
	}
}
