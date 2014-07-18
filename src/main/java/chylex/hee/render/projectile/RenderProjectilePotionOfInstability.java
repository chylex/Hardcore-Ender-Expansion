package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.item.ItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectilePotionOfInstability extends Render{
    @Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F,0.5F,0.5F);
		
		GL11.glRotatef(180F-renderManager.playerViewY,0F,1F,0F);
		GL11.glRotatef(-renderManager.playerViewX,1F,0F,0F);
		
		bindEntityTexture(entity);
		renderIcon(Tessellator.instance,ItemList.potion_of_instability.getIconFromDamage(1));
		renderIcon(Tessellator.instance,ItemPotion.func_94589_d("bottle_splash")); // OBFUSCATED get potion icon
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationItemsTexture;
	}

	private void renderIcon(Tessellator tessellator, IIcon icon){
		float minU = icon.getMinU(),maxU = icon.getMaxU(),
			  minV = icon.getMinV(),maxV = icon.getMaxV();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F,1F,0F);
		tessellator.addVertexWithUV((-0.5F),(-0.25F),0D,minU,maxV);
		tessellator.addVertexWithUV((1F-0.5F),(-0.25F),0D,maxU,maxV);
		tessellator.addVertexWithUV((1F-0.5F),(1F-0.25F),0D,maxU,minV);
		tessellator.addVertexWithUV((-0.5F),(1F-0.25F),0D,minU,minV);
		tessellator.draw();
	}
}