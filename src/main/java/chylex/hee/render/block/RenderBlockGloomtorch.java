package chylex.hee.render.block;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockGloomtorch implements ISimpleBlockRenderingHandler{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer){}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		switch(CollectionUtil.get(Facing6.list, Pos.at(x, y, z).getMetadata(world)-1).orElse(Facing6.DOWN_NEGY)){
			default: break;
			
			case UP_POSY:
				renderer.uvRotateNorth = 3;
				renderer.uvRotateSouth = 3;
				renderer.uvRotateEast = 3;
				renderer.uvRotateWest = 3;
				break;
			
			case WEST_NEGX:
				renderer.uvRotateWest = 1;
				renderer.uvRotateEast = 2;
				renderer.uvRotateTop = 1;
				renderer.uvRotateBottom = 2;
				break;
				
			case EAST_POSX:
				renderer.uvRotateWest = 2;
				renderer.uvRotateEast = 1;
				renderer.uvRotateTop = 2;
				renderer.uvRotateBottom = 1;
				break;
				
			case NORTH_NEGZ:
				renderer.uvRotateNorth = 1;
				renderer.uvRotateSouth = 2;
				renderer.uvRotateTop = 3;
				renderer.uvRotateBottom = 3;
				break;
				
			case SOUTH_POSZ:
				renderer.uvRotateNorth = 2;
				renderer.uvRotateSouth = 1;
				break;
		}
		
		renderer.renderStandardBlock(block, x, y, z);
		renderer.uvRotateSouth = renderer.uvRotateEast = renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = renderer.uvRotateTop = renderer.uvRotateBottom = 0;
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId){
		return false;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdGloomtorch;
	}
}
