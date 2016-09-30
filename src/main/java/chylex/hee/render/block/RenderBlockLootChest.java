package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.GL;
import chylex.hee.tileentity.TileEntityLootChest;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockLootChest implements ISimpleBlockRenderingHandler{
	private final TileEntityLootChest chestRenderer = new TileEntityLootChest();
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer){
		GL.rotate(90F, 0F, 1F, 0F);
		GL.translate(-0.5F, -0.5F, -0.5F);
		TileEntityRendererDispatcher.instance.renderTileEntityAt(chestRenderer, 0D, 0D, 0D, 0F);
		GL.enableRescaleNormal();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId){
		return true;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdLootChest;
	}
}
