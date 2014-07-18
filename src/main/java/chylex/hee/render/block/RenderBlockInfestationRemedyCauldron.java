package chylex.hee.render.block;
/*import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import chylex.hee.block.BlockInfestationRemedyCauldron;
import chylex.hee.block.BlockInfestationRemedyCauldron.CauldronState;
import chylex.hee.block.BlockList;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockInfestationRemedyCauldron implements ISimpleBlockRenderingHandler{
	private static final Map<Integer,ColorTransition> transitionData = new HashMap<>();
	private static long lastUpdateTime;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer){}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		renderer.renderStandardBlock(Blocks.cauldron,x,y,z);

		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(Blocks.cauldron.getMixedBrightnessForBlock(world,x,y,z));

		int color = Blocks.cauldron.colorMultiplier(world,x,y,z);
		float red = (color>>16&255)/255F;
		float green = (color>>8&255)/255F;
		float blue = (color&255)/255F;

		if (EntityRenderer.anaglyphEnable){
			float[] newColors = new float[]{ (red*30F+green*59F+blue*11F)/100F, (red*30F+green*70F)/100F, (red*30F+blue*70F)/100F };
			red = newColors[0];
			green = newColors[1];
			blue = newColors[2];
		}

		tessellator.setColorOpaque_F(red,green,blue);
		IIcon innerSide = Blocks.cauldron.getBlockTextureFromSide(2),innerBottom = BlockCauldron.getCauldronIcon("inner");

		renderer.renderFaceXPos(Blocks.cauldron,(x-1F+0.125F),y,z,innerSide);
		renderer.renderFaceXNeg(Blocks.cauldron,(x+1F-0.125F),y,z,innerSide);
		renderer.renderFaceZPos(Blocks.cauldron,x,y,(z-1F+0.125F),innerSide);
		renderer.renderFaceZNeg(Blocks.cauldron,x,y,(z+1F-0.125F),innerSide);
		
		renderer.renderFaceYPos(Blocks.cauldron,x,(y-1F+0.25F),z,innerBottom);
		renderer.renderFaceYNeg(Blocks.cauldron,x,(y+1F-0.75F),z,innerBottom);
		
		int meta = world.getBlockMetadata(x,y,z),locHash = 100000*x+256*z+y;
		CauldronState state = CauldronState.getByMeta(meta);
		
		if (Math.abs(lastUpdateTime-System.currentTimeMillis()) > 2000)transitionData.clear();
		lastUpdateTime = System.currentTimeMillis();
		
		ColorTransition transition = transitionData.get(locHash);
		if (transition == null || Math.abs(transition.lastUpdateTime-System.currentTimeMillis()) > 2000){
			transition = meta < 3?new ColorTransition(0.2891F,0.3789F,1F):new ColorTransition(state.color[0],state.color[1],state.color[2]);
			transitionData.put(locHash,transition);
		}
		
		transition.lastUpdateTime = lastUpdateTime;
		
		if (transition.factor == 0F && (transition.currentRed != state.color[0] || transition.currentGreen != state.color[1] || transition.currentBlue != state.color[2])){
			transition.targetRed = state.color[0];
			transition.targetGreen = state.color[1];
			transition.targetBlue = state.color[2];
			transition.factor += 0.001F;
		}
		else if (transition.factor > 0F && (transition.factor += 0.075F) >= 1F){
			transition.factor = 0F;
			transition.currentRed = transition.targetRed;
			transition.currentGreen = transition.targetGreen;
			transition.currentBlue = transition.targetBlue;
		}

		IIcon waterIcon = ((BlockInfestationRemedyCauldron)BlockList.infestation_cauldron).waterGray;
		float[] finalColor = transition.getColor();
		tessellator.setColorOpaque_F(finalColor[0],finalColor[1],finalColor[2]);
		renderer.renderFaceYPos(Blocks.cauldron,x,(y-1F+(6F+state.getLiquidAmount()*3F)/16F),z,waterIcon);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int renderType){
		return false;
	}

	@Override
	public int getRenderId(){
		return ModCommonProxy.renderIdInfestationRemedyCauldron;
	}
	
	private class ColorTransition{
		private float currentRed,currentGreen,currentBlue,
					  targetRed,targetGreen,targetBlue,
					  factor;
		private long lastUpdateTime;
		
		public ColorTransition(float red, float green, float blue){
			this.currentRed = this.targetRed = red;
			this.currentGreen = this.targetGreen = green;
			this.currentBlue = this.targetBlue = blue;
			this.factor = 0F;
			this.lastUpdateTime = System.currentTimeMillis();
		}
		
		public float[] getColor(){
			return new float[]{
				targetRed*factor+currentRed*(1F-factor),
				targetGreen*factor+currentGreen*(1F-factor),
				targetBlue*factor+currentBlue*(1F-factor)
			};
		}
	}
}
*/