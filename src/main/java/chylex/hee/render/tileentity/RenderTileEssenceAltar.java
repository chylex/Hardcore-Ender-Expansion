package chylex.hee.render.tileentity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.render.BlockRenderHelper;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEssenceAltar extends TileEntitySpecialRenderer{
	private static RenderBlocks blockRenderer;
	
	private RenderManager renderManager;

	private TileEntityEssenceAltar altar;
	private long lastRotationUpdateTime;
	private short glyphRot,requiredItemRot;
	private double viewRot;

	public RenderTileEssenceAltar(){
		renderManager = RenderManager.instance;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime){
		GL.pushMatrix();
		GL.translate((float)x,(float)y,(float)z);

		Tessellator tessellator = Tessellator.instance;
		GL.color(1F,1F,1F,1F);
		tessellator.setColorOpaque_F(1F,1F,1F);
		int l = 15728880;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,l%65536,l/65536);

		GL.pushMatrix();
		GL.translate(0.5F,1.25F,0.5F);
		GL.scale(0.5F,0.5F,0.5F);
		GL.enableRescaleNormal();
		
		altar = (TileEntityEssenceAltar)tile;
		viewRot = 180D+Math.toDegrees(Math.atan2(renderManager.viewerPosX-altar.xCoord-0.5D,renderManager.viewerPosZ-altar.zCoord-0.5D));

		long time = altar.getWorldObj().getTotalWorldTime();
		if (time != lastRotationUpdateTime){
			if (++requiredItemRot >= 360)requiredItemRot -= 360;
			if (++glyphRot >= 720)glyphRot -= 720;
			lastRotationUpdateTime = time;
		}

		renderAltar();

		GL.disableRescaleNormal();
		GL.popMatrix();
		GL.popMatrix();
	}

	private void renderAltar(){
		byte stage = altar.getStage();
		
		if (stage == TileEntityEssenceAltar.STAGE_WORKING){
			
			/*
			 * ESSENCE ICON AND AMOUNT
			 */
			
			startVerticalItem();
			renderItem(ItemList.essence,altar.getEssenceType().getItemDamage());
			endVerticalItem();
			
			GL.pushMatrix();
			GL.translate(0F,-0.8F,0F);

			GL.pushMatrix();
			GL.rotate(viewRot,0F,1F,0F);
			GL.translate(-0.5F,-0.25F,0F);
			
			String ns = String.valueOf(altar.getEssenceLevel());
			GL.translate(+0.4F*(ns.length()*0.5F)-0.2F,0F,0F);
			
			for(int a = 0; a < ns.length(); a++){
				GL.pushMatrix();
				GL.translate(-0.4F*a,0F,0F);
				renderItem(ItemList.special_effects,DragonUtil.tryParse(ns.substring(a,a+1),0));
				GL.popMatrix();
			}

			GL.popMatrix();
			GL.popMatrix();
			
			altar.getActionHandler().onRender();
		}
		else if (stage == TileEntityEssenceAltar.STAGE_HASTYPE){
			
			/*
			 * FLOATING ITEM
			 */
			
			ItemStack is = altar.getShowedRuneItem();
			if (is != null){
				GL.pushMatrix();

				if (is.getItemSpriteNumber() == 0 && BlockRenderHelper.shouldRenderBlockIn3D(Block.getBlockFromItem(is.getItem()))){
					GL.scale(0.7F,0.7F,0.7F);
					GL.rotate(requiredItemRot,0F,1F,0F);
					BlockRenderHelper.renderBlockAsItem(blockRenderer,Block.getBlockFromItem(is.getItem()),is.getItemDamage());
				}
				else{
					GL.scale(0.85F,0.85F,0.85F);
					GL.rotate(++requiredItemRot >= 360 ? requiredItemRot -= 360 : requiredItemRot,0F,1F,0F);
					GL.translate(-0.5F,-0.5F,0F);
					
					renderManager.renderEngine.bindTexture(is.getItemSpriteNumber() == 0?TextureMap.locationBlocksTexture:TextureMap.locationItemsTexture);
					IIcon icon = is.getItem().getIconFromDamage(is.getItemDamage());
					ItemRenderer.renderItemIn2D(Tessellator.instance,icon.getMaxU(),icon.getMinV(),icon.getMinU(),icon.getMaxV(),icon.getIconWidth(),icon.getIconHeight(),0.0625F);
				}
				
				GL.popMatrix();
			}
		}
		
		/*
		 * GLYPHS
		 */
		
		GL.pushMatrix();
		GL.translate(-0.5F,-1F,-0.5F);
		GL.scale(0.5F,0.02F,0.5F);
		GL.translate(1F,0F,1F);
		GL.rotate(45F+glyphRot*0.5F,0F,1F,0F);
		
		float[] glyphCols = altar.getEssenceType().glyphColors;
		double angpart = Math.PI/4D;
		byte index = altar.getRuneItemIndex();
		
		for(int a = 0; a < 8; a++){
			GL.pushMatrix();
			GL.translate(Math.cos(a*angpart)*0.8D,0F,Math.sin(a*angpart)*0.8D);
			GL.rotate(90F,1F,0F,0F);
			GL.rotate(45F*a+30,0F,0F,1F);
			
			if (index == -1)renderItem(ItemList.special_effects,ItemSpecialEffects.glyphIndex+a,glyphCols[0],glyphCols[1],glyphCols[2]);
			else if (index > a)renderItem(ItemList.special_effects,ItemSpecialEffects.glyphIndex+a,0.9725F,0.8265F,0.225F);
			else renderItem(ItemList.special_effects,ItemSpecialEffects.glyphIndex+a,1F,1F,1F);
			
			GL.popMatrix();
		}
		
		GL.popMatrix();
	}

	private void startVerticalItem(){
		GL.pushMatrix();
		GL.rotate(viewRot,0F,1F,0F);
		GL.translate(-0.5F,-0.25F,0F);
	}

	private void endVerticalItem(){
		GL.popMatrix();
	}

	private void renderItem(Item item, int damage){
		renderItem(item,damage,1F,1F,1F);
	}

	private void renderItem(Item item, int damage, float red, float green, float blue){
		renderManager.renderEngine.bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		GL.color(red,green,blue,1F);
		IIcon icon = item.getIconFromDamage(damage);
		ItemRenderer.renderItemIn2D(Tessellator.instance,icon.getMaxU(),icon.getMinV(),icon.getMinU(),icon.getMaxV(),icon.getIconWidth(),icon.getIconHeight(),0.0625F);
	}
	
	@Override
	public void func_147496_a(World world){ // OBFUSCATED create block renderer
		blockRenderer = new RenderBlocks(world);
	}
}
