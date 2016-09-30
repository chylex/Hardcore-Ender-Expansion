package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockCrossedDecoration implements ISimpleBlockRenderingHandler{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer){}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		int colorMultiplier = block.colorMultiplier(world, x, y, z);
		float r = (colorMultiplier>>16&255)/255F,
			  g = (colorMultiplier>>8&255)/255F,
			  b = (colorMultiplier&255)/255F;

		if (EntityRenderer.anaglyphEnable){
			r = (r*30F+g*59F+b*11F)/100F;
			g = (r*30F+g*70F)/100F;
			b = (r*30F+b*70F)/100F;
		}

		tessellator.setColorOpaque_F(r, g, b);
		double posX = x, posY = y, posZ = z;

		long offsetSeed = (x*3129871L)^z*116129781L^y;
		offsetSeed = offsetSeed*offsetSeed*42317861L+offsetSeed*11L;
		
		int meta = world.getBlockMetadata(x, y, z);
		float scale = 1F, xzScaleMp = 0.45F;
		
		switch(meta){
			case BlockCrossedDecoration.dataInfestedGrass:
			case BlockCrossedDecoration.dataInfestedTallgrass:
			case BlockCrossedDecoration.dataInfestedFern:
			case BlockCrossedDecoration.dataThornBush:
				posX += (((offsetSeed>>16&15L)/15F)-0.5D)*0.5D;
				posY += (((offsetSeed>>20&15L)/15F)-1D)*0.2D;
				posZ += (((offsetSeed>>24&15L)/15F)-0.5D)*0.5D;
				break;
				
			case BlockCrossedDecoration.dataLilyFire:
				posX += (((offsetSeed>>16&15L)/15F)-0.5D)*0.3D;
				posZ += (((offsetSeed>>24&15L)/15F)-0.5D)*0.3D;
				break;
				
			case BlockCrossedDecoration.dataVioletMossTall:
			case BlockCrossedDecoration.dataVioletMossModerate:
			case BlockCrossedDecoration.dataVioletMossShort:
				scale = (float)(0.65D+(((offsetSeed>>8&15L)/15F)-0.5D)*0.1D);
				xzScaleMp = (float)(0.4D+(((offsetSeed>>8&15L)/15F)-0.5D)*0.15D);
				posX += (((offsetSeed>>16&15L)/15F)-0.5D)*0.6D;
				posY += (((offsetSeed>>20&15L)/15F)-1D)*0.1D;
				posZ += (((offsetSeed>>24&15L)/15F)-0.5D)*0.6D;
				break;
		}

		drawCrossedSquares(block, meta, posX, posY, posZ, scale, xzScaleMp, renderer);

		return true;
	}

	private void drawCrossedSquares(Block block, int meta, double x, double y, double z, float scale, float xzScaleMp, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;
		IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 0, meta);

		double iconU1 = icon.getMinU();
		double iconV1 = icon.getMinV();
		double iconU2 = icon.getMaxU();
		double iconV2 = icon.getMaxV();
		double finalScale = xzScaleMp*scale;
		double minX = x+0.5D-finalScale;
		double maxX = x+0.5D+finalScale;
		double minZ = z+0.5D-finalScale;
		double maxZ = z+0.5D+finalScale;

		tessellator.addVertexWithUV(minX, y+scale, minZ, iconU1, iconV1);
		tessellator.addVertexWithUV(minX, y, minZ, iconU1, iconV2);
		tessellator.addVertexWithUV(maxX, y, maxZ, iconU2, iconV2);
		tessellator.addVertexWithUV(maxX, y+scale, maxZ, iconU2, iconV1);
		tessellator.addVertexWithUV(maxX, y+scale, maxZ, iconU1, iconV1);
		tessellator.addVertexWithUV(maxX, y, maxZ, iconU1, iconV2);
		tessellator.addVertexWithUV(minX, y, minZ, iconU2, iconV2);
		tessellator.addVertexWithUV(minX, y+scale, minZ, iconU2, iconV1);
		tessellator.addVertexWithUV(minX, y+scale, maxZ, iconU1, iconV1);
		tessellator.addVertexWithUV(minX, y, maxZ, iconU1, iconV2);
		tessellator.addVertexWithUV(maxX, y, minZ, iconU2, iconV2);
		tessellator.addVertexWithUV(maxX, y+scale, minZ, iconU2, iconV1);
		tessellator.addVertexWithUV(maxX, y+scale, minZ, iconU1, iconV1);
		tessellator.addVertexWithUV(maxX, y, minZ, iconU1, iconV2);
		tessellator.addVertexWithUV(minX, y, maxZ, iconU2, iconV2);
		tessellator.addVertexWithUV(minX, y+scale, maxZ, iconU2, iconV1);
	}

	@Override
	public boolean shouldRender3DInInventory(int renderType){
		return false;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdCrossedDecoration;
	}
}
