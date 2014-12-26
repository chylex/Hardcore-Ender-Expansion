package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAccumulationTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/accumulation_table.png");

	public GuiAccumulationTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		super(new ContainerDecompositionTable(inv,tile),tile);
		setupEnergyStorage(96,27);
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
