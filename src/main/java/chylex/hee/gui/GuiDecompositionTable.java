package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDecompositionTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/decomposition_table.png");

	public GuiDecompositionTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		super(new ContainerDecompositionTable(inv,tile),tile);
	}
	
	@Override
	protected int getEnergyBarX(){
		return 57;
	}

	@Override
	protected int getEnergyBarY(){
		return 34;
	}

	@Override
	protected int getEnergyIconX(){
		return 19;
	}

	@Override
	protected int getEnergyIconY(){
		return 37;
	}

	@Override
	protected int getStardustTextX(){
		return 34;
	}

	@Override
	protected int getStardustTextY(){
		return 33;
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
