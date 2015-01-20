package chylex.hee.render.tileentity;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.tileentity.TileEntityVoidChest;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileVoidChest extends TileEntitySpecialRenderer{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/entity/tile/void_chest.png");
	private final ModelChest model = new ModelChest();
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime, int what){
		int meta = tile.hasWorldObj() ?	meta = tile.getBlockMetadata() : 0;
		TileEntityVoidChest chest = (TileEntityVoidChest)tile;
		
		bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F,1F,1F,1F);
		
		GL11.glTranslatef((float)x,(float)y+1F,(float)z+1F);
		GL11.glScalef(1F,-1F,-1F);
		GL11.glTranslatef(0.5F,0.5F,0.5F);
		GL11.glRotatef(meta == 2 ? 180 : meta == 4 ? 90 : meta == 5 ? -90 : 0,0F,1F,0F);
		GL11.glTranslatef(-0.5F,-0.5F,-0.5F);
		
		model.chestLid.rotateAngleX = -((1F-(float)Math.pow(1F-chest.lidAnim+(chest.prevLidAnim-chest.lidAnim)*partialTickTime,3))*(float)Math.PI*0.5F);
		model.renderAll();
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1F,1F,1F,1F);
	}
}
