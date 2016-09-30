package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.tileentity.TileEntityAccumulationTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAccumulationTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/accumulation_table.png");

	public GuiAccumulationTable(InventoryPlayer inv, TileEntityAccumulationTable tile){
		super(new ContainerAccumulationTable(inv, tile), tile);
		setupEnergyIcon(36, 37);
		setupEnergyStorage(51, 18);
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
