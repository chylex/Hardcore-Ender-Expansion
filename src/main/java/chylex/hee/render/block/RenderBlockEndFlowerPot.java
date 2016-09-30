package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import chylex.hee.init.BlockList;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockEndFlowerPot implements ISimpleBlockRenderingHandler{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer){}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		renderer.renderStandardBlock(block, x, y, z);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
		float f = 1F;
		int l = block.colorMultiplier(renderer.blockAccess, x, y, z);
		IIcon icon = renderer.getBlockIconFromSide(block, 0);
		float red = (l>>16&255)/255F;
		float green = (l>>8&255)/255F;
		float blue = (l&255)/255F;

		if (EntityRenderer.anaglyphEnable){
			float newRed = (red*30F+green*59F+blue*11F)/100F;
			float newGreen = (red*30F+green*70F)/100F;
			float newBlue = (red*30F+blue*70F)/100F;
			red = newRed;
			green = newGreen;
			blue = newBlue;
		}

		tessellator.setColorOpaque_F(f*red, f*green, f*blue);
		float renderOffset = 0.1865F;
		renderer.renderFaceXPos(block, (x-0.5F+renderOffset), y, z, icon);
		renderer.renderFaceXNeg(block, (x+0.5F-renderOffset), y, z, icon);
		renderer.renderFaceZPos(block, x, y, (z-0.5F+renderOffset), icon);
		renderer.renderFaceZNeg(block, x, y, (z+0.5F-renderOffset), icon);
		renderer.renderFaceYPos(block, x, (y-0.5F+renderOffset+0.1875F), z, renderer.getBlockIcon(Blocks.dirt));

		if (renderer.blockAccess.getBlockMetadata(x, y, z) < 15)renderer.renderBlockByRenderType(BlockList.death_flower, x, y, z);
		else renderer.drawCrossedSquares(BlockList.death_flower.getIcon(0, 15), x, y, z, 0.9F);
		
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int renderType){
		return false;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdFlowerPot;
	}
}
