package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import chylex.hee.block.BlockList;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.BlockRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockTransportBeacon implements ISimpleBlockRenderingHandler{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer){
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(BlockList.laboratory_glass));
		renderer.setRenderBounds(0D,0D,0D,1D,1D,1D);
		BlockRenderHelper.renderBlockAsItem(renderer,block,metadata);
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(Blocks.obsidian));
		renderer.setRenderBounds(0.125D,0.00625D,0.125D,0.875D,0.1875D,0.875D);
		BlockRenderHelper.renderBlockAsItem(renderer,block,metadata);
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(Blocks.beacon));
		renderer.setRenderBounds(0.1875D,0.1875D,0.1875D,0.8125D,0.875D,0.8125D);
		BlockRenderHelper.renderBlockAsItem(renderer,block,metadata);
		renderer.setRenderBounds(0D,0D,0D,1D,1D,1D);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(BlockList.laboratory_glass));
		renderer.setRenderBounds(0D,0D,0D,1D,1D,1D);
		renderer.renderStandardBlock(block,x,y,z);
		renderer.renderAllFaces = true;
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(Blocks.obsidian));
		renderer.setRenderBounds(0.125D,0.00625D,0.125D,0.875D,0.1875D,0.875D);
		renderer.renderStandardBlock(block,x,y,z);
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(Blocks.beacon));
		renderer.setRenderBounds(0.1875D,0.1875D,0.1875D,0.8125D,0.875D,0.8125D);
		renderer.renderStandardBlock(block,x,y,z);
		renderer.renderAllFaces = false;
		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId){
		return true;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdTransportBeacon;
	}
}
