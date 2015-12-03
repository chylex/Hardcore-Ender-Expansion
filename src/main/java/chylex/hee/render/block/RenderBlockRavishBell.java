package chylex.hee.render.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import chylex.hee.proxy.ModClientProxy;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockRavishBell implements ISimpleBlockRenderingHandler{
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

		long offsetSeed = (x*3129871L)^z*116129781L^y;
		offsetSeed = offsetSeed*offsetSeed*42317861L+offsetSeed*11L;
		
		Random rand = ModClientProxy.seedableRand;
		rand.setSeed(offsetSeed);
		
		drawCrossedSquares(block,-1,posX,posY,posZ,0b1111,renderer);

		int type;
		
		switch(rand.nextInt(7)){
			case 0: // all four sides
				drawCrossedSquares(block,2,posX,posY,posZ,0b1111,renderer);
				break;
			
			case 1: case 2: // two sides
				drawCrossedSquares(block,2,posX,posY,posZ,rand.nextBoolean() ? 0b0011 : 0b1100,renderer);
				break;
				
			case 3: case 4: // one side
				type = rand.nextInt(2);
				drawCrossedSquares(block,type,posX,posY,posZ,0b0001,renderer);
				drawCrossedSquares(block,1-type,posX,posY,posZ,0b0010,renderer);
				break;
				
			case 5: case 6: // one side
				type = rand.nextInt(2);
				drawCrossedSquares(block,type,posX,posY,posZ,0b0100,renderer);
				drawCrossedSquares(block,1-type,posX,posY,posZ,0b1000,renderer);
				break;
		}
		
		return true;
	}

	private void drawCrossedSquares(Block block, int meta, double x, double y, double z, int sides, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;
		IIcon icon = renderer.getBlockIconFromSideAndMetadata(block,0,meta);
		
		double iconU1 = icon.getMinU();
		double iconV1 = icon.getMinV();
		double iconU2 = icon.getMaxU();
		double iconV2 = icon.getMaxV();
		double minX = x+0.05D;
		double maxX = x+0.95D;
		double minZ = z+0.05D;
		double maxZ = z+0.95D;
		
		if ((sides&0b0001) != 0){
			tessellator.addVertexWithUV(minX,y+1F,minZ,iconU1,iconV1);
			tessellator.addVertexWithUV(minX,y,minZ,iconU1,iconV2);
			tessellator.addVertexWithUV(maxX,y,maxZ,iconU2,iconV2);
			tessellator.addVertexWithUV(maxX,y+1F,maxZ,iconU2,iconV1);
		}

		if ((sides&0b0010) != 0){
			tessellator.addVertexWithUV(maxX,y+1F,maxZ,iconU1,iconV1);
			tessellator.addVertexWithUV(maxX,y,maxZ,iconU1,iconV2);
			tessellator.addVertexWithUV(minX,y,minZ,iconU2,iconV2);
			tessellator.addVertexWithUV(minX,y+1F,minZ,iconU2,iconV1);
		}
		
		if ((sides&0b0100) != 0){
			tessellator.addVertexWithUV(minX,y+1F,maxZ,iconU1,iconV1);
			tessellator.addVertexWithUV(minX,y,maxZ,iconU1,iconV2);
			tessellator.addVertexWithUV(maxX,y,minZ,iconU2,iconV2);
			tessellator.addVertexWithUV(maxX,y+1F,minZ,iconU2,iconV1);
		}
		
		if ((sides&0b1000) != 0){
			tessellator.addVertexWithUV(maxX,y+1F,minZ,iconU1,iconV1);
			tessellator.addVertexWithUV(maxX,y,minZ,iconU1,iconV2);
			tessellator.addVertexWithUV(minX,y,maxZ,iconU2,iconV2);
			tessellator.addVertexWithUV(minX,y+1F,maxZ,iconU2,iconV1);
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int renderType){
		return false;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdRavishBell;
	}
}
