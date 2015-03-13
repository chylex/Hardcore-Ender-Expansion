package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.tileentity.TileEntityExtractionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiExtractionTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/extraction_table.png");
	
	public GuiExtractionTable(InventoryPlayer inv, TileEntityExtractionTable tile){
		super(new ContainerExtractionTable(inv,tile),tile);
		setupProgressBar(64,34);
		setupEnergyIcon(36,37);
		setupEnergyStorage(96,17);
		setupStardustText(40,53);
	}
	
	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
