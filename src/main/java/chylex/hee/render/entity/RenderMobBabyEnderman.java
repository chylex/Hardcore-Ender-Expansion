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
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobBabyEnderman extends AbstractRenderMobEnderman{
	public RenderMobBabyEnderman(){}

	@Override
	public void renderEnderman(IEndermanRenderer enderman, double x, double y, double z, float yaw, float partialTickTime){
		endermanModel.isCarrying = enderman.isCarrying();
		
		GL.pushMatrix();
		GL.translate(x, y, z);
		GL.scale(0.48F, 0.48F, 0.48F);
		GL.translate(-x, -y, -z);
		GL.disableCullFace();
		GL.setDepthFunc(GL.LEQUAL);
		superDoRender(enderman, x, y, z, yaw, partialTickTime);
		GL.enableCullFace();
		GL.popMatrix();
	}

	@Override
	protected void renderEquippedItems(IEndermanRenderer enderman, float partialTickTime){
		ItemStack is = enderman.getCarrying();
		if (is == null)return;
		
		Item item = is.getItem();
		Block block = Block.getBlockFromItem(item);
		
		if (block != Blocks.bedrock){
			GL.enableRescaleNormal();
			GL.pushMatrix();
			GL.translate(0F, 0.6875F, -0.75F);
			GL.rotate(20F, 1F, 0F, 0F);
			GL.rotate(45F, 0F, 1F, 0F);
			GL.scale(-0.5F, -0.5F, 0.5F);
			int brightness = ((Entity)enderman).getBrightnessForRender(partialTickTime);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness%65536F, brightness/65536F);
			GL.color(1F, 1F, 1F, 1F);
			
			if (is.getItemSpriteNumber() == 0){
				bindTexture(TextureMap.locationBlocksTexture);
				field_147909_c.renderBlockAsItem(block, is.getItemDamage(), 1F);
			}
			else{
				GL.pushMatrix();
				GL.translate(0.2F, -0.3F, -0.3F);
				renderManager.itemRenderer.renderItem((EntityLivingBase)enderman, is, 0, ItemRenderType.ENTITY);
				GL.popMatrix();
			}
			
			GL.popMatrix();
			GL.disableRescaleNormal();
		}
	}
}