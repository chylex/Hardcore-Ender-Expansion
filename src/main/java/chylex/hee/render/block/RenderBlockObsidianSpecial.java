package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.BlockRenderHelper;
import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockObsidianSpecial implements ISimpleBlockRenderingHandler{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer){
		BlockRenderHelper.renderInventoryBlock(block,metadata,renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		int meta = world.getBlockMetadata(x,y,z);

		if (meta == 3)renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateTop = renderer.uvRotateBottom = 1;
		else if (meta == 4)renderer.uvRotateSouth = renderer.uvRotateNorth = 1;

		boolean flag = renderer.renderStandardBlock(block,x,y,z);
		renderer.uvRotateSouth = renderer.uvRotateEast = renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = renderer.uvRotateTop = renderer.uvRotateBottom = 0;
		return flag;
	}

	@Override
	public boolean shouldRender3DInInventory(int renderType){
		return true;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdObsidianSpecial;
	}
}
