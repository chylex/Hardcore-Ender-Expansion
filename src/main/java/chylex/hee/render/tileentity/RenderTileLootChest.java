package chylex.hee.render.tileentity;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityLootChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileLootChest extends TileEntitySpecialRenderer{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/entity/tile/loot_chest.png");
	private final ModelChest model = new ModelChest();
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime){
		int meta = tile.hasWorldObj() ?	tile.getBlockMetadata() : 0;
		TileEntityLootChest chest = (TileEntityLootChest)tile;
		
		bindTexture(texture);
		GL.pushMatrix();
		GL.enableRescaleNormal();
		GL.color(1F, 1F, 1F, 1F);
		
		GL.translate(x, y+1D, z+1D);
		GL.scale(1F, -1F, -1F);
		GL.translate(0.5F, 0.5F, 0.5F);
		GL.rotate(meta == 2 ? 180 : meta == 4 ? 90 : meta == 5 ? -90 : 0, 0F, 1F, 0F);
		GL.translate(-0.5F, -0.5F, -0.5F);
		
		model.chestLid.rotateAngleX = Math.min(0F, -((1F-(float)Math.pow(1F-chest.lidAnim+(chest.prevLidAnim-chest.lidAnim)*partialTickTime, 3))*MathUtil.HALF_PI));
		model.renderAll();
		
		GL.disableRescaleNormal();
		GL.popMatrix();
		GL.color(1F, 1F, 1F, 1F);
	}
}
