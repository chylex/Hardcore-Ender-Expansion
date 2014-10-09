package chylex.hee.render.entity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobBabyEnderman extends AbstractRenderMobEnderman{
	public RenderMobBabyEnderman(){}

	@Override
	public void renderEnderman(IEndermanRenderer enderman, double x, double y, double z, float yaw, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glScalef(0.48F,0.48F,0.48F);
		GL11.glTranslated(-x,-y,-z);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		superDoRender(enderman,x,y,z,yaw,partialTickTime);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	@Override
	protected void renderEquippedItems(IEndermanRenderer enderman, float partialTickTime){
		ItemStack is = enderman.getCarrying();
		if (is == null)return;
		
		Item item = is.getItem();
		Block block = Block.getBlockFromItem(item);
		
		if (block != Blocks.bedrock){
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(0F,0.6875F,-0.75F);
			GL11.glRotatef(20F,1F,0F,0F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glScalef(-0.5F,-0.5F,0.5F);
			int brightness = ((Entity)enderman).getBrightnessForRender(partialTickTime);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightness%65536F,brightness/65536F);
			GL11.glColor4f(1F,1F,1F,1F);
			
			if (is.getItemSpriteNumber() == 0){
				bindTexture(TextureMap.locationBlocksTexture);
				field_147909_c.renderBlockAsItem(block,is.getItemDamage(),1F);
			}
			else{
				GL11.glPushMatrix();
				GL11.glTranslatef(0.2F,-0.3F,-0.3F);
				renderManager.itemRenderer.renderItem((EntityLivingBase)enderman,is,0,ItemRenderType.ENTITY);
				GL11.glPopMatrix();
			}
			
			GL11.glPopMatrix();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
	}
}