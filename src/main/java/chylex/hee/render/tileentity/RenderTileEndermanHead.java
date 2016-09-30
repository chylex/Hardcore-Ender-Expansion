package chylex.hee.render.tileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import chylex.hee.system.abstractions.GL;
import chylex.hee.tileentity.TileEntityEndermanHead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEndermanHead extends TileEntitySpecialRenderer{
	private static final ResourceLocation endermanHead = new ResourceLocation("hardcoreenderexpansion:textures/entity/enderman_head.png");
	private static final ModelSkeletonHead skullModel = new ModelSkeletonHead(0, 0, 64, 32);
	
	public static void renderSkull(int meta, float rot){
		GL.pushMatrix();
		GL.disableCullFace();
		
		if (meta != 1){
			switch(meta){
				case 2:
					GL.translate(0.5F, 0.25F, 0.74F);
					break;
				case 3:
					GL.translate(0.5F, 0.25F, 0.26F);
					rot = 180F;
					break;
				case 4:
					GL.translate(0.74F, 0.25F, 0.5F);
					rot = 270F;
					break;
				case 5:
				default:
					GL.translate(0.26F, 0.25F, 0.5F);
					rot = 90F;
			}
		}
		else GL.translate(0.5F, 0F, 0.5F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(endermanHead);
		GL.enableRescaleNormal();
		GL.scale(-1F, -1F, 1F);
		GL.enableAlphaTest();
		skullModel.render(null, 0F, 0F, 0F, rot, 0F, 0.0625F);
		GL.popMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime){
		GL.pushMatrix();
		GL.translate(x, y, z);
		renderSkull(((TileEntityEndermanHead)tile).getMetaClient(), ((TileEntityEndermanHead)tile).getRotationClient()*360F/16F);
		GL.popMatrix();
	}
}
