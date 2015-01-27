package chylex.hee.render.tileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.tileentity.TileEntityEndermanHead;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEndermanHead extends TileEntitySpecialRenderer{
	private static final ResourceLocation endermanHead = new ResourceLocation("hardcoreenderexpansion:textures/entity/enderman_head.png");
	private static final ModelSkeletonHead skullModel = new ModelSkeletonHead(0,0,64,32);
	
	public static void renderSkull(int meta, float rot){
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		if (meta != 1){
			switch(meta){
				case 2:
					GL11.glTranslatef(0.5F,0.25F,0.74F);
					break;
				case 3:
					GL11.glTranslatef(0.5F,0.25F,0.26F);
					rot = 180F;
					break;
				case 4:
					GL11.glTranslatef(0.74F,0.25F,0.5F);
					rot = 270F;
					break;
				case 5:
				default:
					GL11.glTranslatef(0.26F,0.25F,0.5F);
					rot = 90F;
			}
		}
		else GL11.glTranslatef(0.5F,0F,0.5F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(endermanHead);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(-1F,-1F,1F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		skullModel.render(null,0F,0F,0F,rot,0F,0.0625F);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime, int what){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		renderSkull(((TileEntityEndermanHead)tile).getMeta(),((TileEntityEndermanHead)tile).getRotation()*360F/16F);
		GL11.glPopMatrix();
	}
}
