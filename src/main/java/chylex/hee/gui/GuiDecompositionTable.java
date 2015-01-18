package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDecompositionTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/decomposition_table.png");

	public GuiDecompositionTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		super(new ContainerDecompositionTable(inv,tile),tile);
		setupProgressBar(57,34);
		setupEnergyIcon(19,37);
		setupStardustText(34,53);
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
