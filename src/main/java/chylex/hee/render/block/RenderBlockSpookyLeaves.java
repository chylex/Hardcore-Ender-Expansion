package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import chylex.hee.proxy.ModCommonProxy;
import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockSpookyLeaves implements ISimpleBlockRenderingHandler{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer){}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(world,x,y,z));
		int colorMultiplier = block.colorMultiplier(world,x,y,z);
		float r = (colorMultiplier>>16&255)/255F,
			  g = (colorMultiplier>>8&255)/255F,
			  b = (colorMultiplier&255)/255F;

		if (EntityRenderer.anaglyphEnable){
			r = (r*30F+g*59F+b*11F)/100F;
			g = (r*30F+g*70F)/100F;
			b = (r*30F+b*70F)/100F;
		}

		tessellator.setColorOpaque_F(r,g,b);
		double posX = x, posY = y, posZ = z;

		long offsetSeed = (x*3129871)^z*116129781L^y;
		offsetSeed = offsetSeed*offsetSeed*42317861L+offsetSeed*11L;
		float scale = (float)(1.05D+(((offsetSeed>>16&15L)/15F)-0.5D)*0.08D);
		posX += (((offsetSeed>>16&15L)/15F)-0.5D)*0.2D;
		posY += (((offsetSeed>>20&15L)/15F)-1D)*0.15D;
		posZ += (((offsetSeed>>24&15L)/15F)-0.5D)*0.2D;

		drawCrossedSquares(block,world.getBlockMetadata(x,y,z),posX,posY,posZ,scale,renderer);

		return true;
	}

	private void drawCrossedSquares(Block block, int meta, double x, double y, double z, float scale, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;
		IIcon icon = renderer.getBlockIconFromSideAndMetadata(block,0,meta);

		long offsetSeed = (long)(x*3129871)^(long)z*116129781L^(long)y;
		offsetSeed = offsetSeed*offsetSeed*42317861L+offsetSeed*11L;
		double xOffset = (((offsetSeed>>16&15L)/15F)-0.5D)*0.1D;
		double zOffset = (((offsetSeed>>20&15L)/15F)-0.5D)*0.1D;

		double iconU1 = icon.getMinU();
		double iconV1 = icon.getMinV();
		double iconU2 = icon.getMaxU();
		double iconV2 = icon.getMaxV();
		double finalScale = 0.45D*scale;
		double minX = x+0.5D-finalScale;
		double maxX = x+0.5D+finalScale;
		double minZ = z+0.5D-finalScale;
		double maxZ = z+0.5D+finalScale;

		minX -= xOffset;
		maxX += xOffset;
		minZ -= zOffset;
		maxZ += zOffset;

		tessellator.addVertexWithUV(minX,y+scale,minZ,iconU1,iconV1);
		tessellator.addVertexWithUV(minX,y,minZ,iconU1,iconV2);
		tessellator.addVertexWithUV(maxX,y,maxZ,iconU2,iconV2);
		tessellator.addVertexWithUV(maxX,y+scale,maxZ,iconU2,iconV1);
		tessellator.addVertexWithUV(maxX,y+scale,maxZ,iconU1,iconV1);
		tessellator.addVertexWithUV(maxX,y,maxZ,iconU1,iconV2);
		tessellator.addVertexWithUV(minX,y,minZ,iconU2,iconV2);
		tessellator.addVertexWithUV(minX,y+scale,minZ,iconU2,iconV1);
		tessellator.addVertexWithUV(minX,y+scale,maxZ,iconU1,iconV1);
		tessellator.addVertexWithUV(minX,y,maxZ,iconU1,iconV2);
		tessellator.addVertexWithUV(maxX,y,minZ,iconU2,iconV2);
		tessellator.addVertexWithUV(maxX,y+scale,minZ,iconU2,iconV1);
		tessellator.addVertexWithUV(maxX,y+scale,minZ,iconU1,iconV1);
		tessellator.addVertexWithUV(maxX,y,minZ,iconU1,iconV2);
		tessellator.addVertexWithUV(minX,y,maxZ,iconU2,iconV2);
		tessellator.addVertexWithUV(minX,y+scale,maxZ,iconU2,iconV1);
	}

	@Override
	public boolean shouldRender3DInInventory(int renderType){
		return false;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdSpookyLeaves;
	}
}
