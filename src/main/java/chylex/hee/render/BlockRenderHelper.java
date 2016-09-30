package chylex.hee.render;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.render.tileentity.RenderTileEndermanHead;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BlockRenderHelper{
	public static void renderBlockAsItem(RenderBlocks renderer, Block block, int metadata){
		RenderManager.instance.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		
		if (block == BlockList.enderman_head){
			GL.translate(-0.5F, 0F, -0.5F);
			RenderTileEndermanHead.renderSkull(1, 0F);
		}
		else renderer.renderBlockAsItem(block, metadata, 1F);
	}
	
	public static boolean shouldRenderBlockIn3D(Block block){
		return block != Blocks.iron_bars;
	}
	
	public static void renderInventoryBlock(Block block, int metadata, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;
		GL.translate(-0.5F, -0.5F, -0.5F);
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
		
		GL.translate(0.5F, 0.5F, 0.5F);
	}
	
	private BlockRenderHelper(){}
}
