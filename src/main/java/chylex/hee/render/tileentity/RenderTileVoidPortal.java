package chylex.hee.render.tileentity;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockVoidPortal;
import chylex.hee.entity.technical.EntityTechnicalVoidPortal;
import chylex.hee.item.ItemPortalToken;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryEnvironment;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileVoidPortal extends RenderTilePortalBase{
	private final float[] color = new float[3];
	private float alpha, translation;
	private TerritoryEnvironment activeEnvironment;
	
	@Override
	protected void onRender(){
		EntityTechnicalVoidPortal data = BlockVoidPortal.getData(tile.getWorldObj(),tile.xCoord,tile.yCoord,tile.zCoord).orElse(null);
		alpha = data == null ? 0F : data.prevRenderAlpha+(data.renderAlpha-data.prevRenderAlpha)*ptt;
		translation = data == null ? 0F : data.prevRenderTranslation+(data.renderTranslation-data.prevRenderTranslation)*ptt;
		
		ItemStack tokenIS = data.getActiveToken();
		
		if (tokenIS != null){
			EndTerritory territory = ItemPortalToken.getTerritory(tokenIS);
			if (territory != null)activeEnvironment = territory.environment;
		}
	}
	
	@Override
	protected float getTranslation(){
		return tile.getBlockMetadata() == Meta.voidPortalReturn ? super.getTranslation() : translation;
	}
	
	@Override
	protected void generateColors(int layer){
		if (tile.getBlockMetadata() == Meta.voidPortalReturn){
			RenderTileEndPortal.setEndPortalColor(this,layer);
			return;
		}
		
		if (layer == 0){
			red = green = blue = 0F;
			return;
		}
		
		if (activeEnvironment != null){
			activeEnvironment.generatePortalColor(color,layer,rand);
		}
		
		red = color[0];
		green = color[1];
		blue = color[2];
		
		colorMp *= alpha;
	}
}
