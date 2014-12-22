package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.tileentity.TileEntityExperienceTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiExperienceTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/experience_table.png");

	public GuiExperienceTable(InventoryPlayer inv, TileEntityExperienceTable tile){
		super(new ContainerExperienceTable(inv,tile),tile);
	}
	
	@Override
	protected int getProgressBarX(){
		return 75;
	}

	@Override
	protected int getProgressBarY(){
		return 34;
	}

	@Override
	protected int getEnergyIconX(){
		return 37;
	}

	@Override
	protected int getEnergyIconY(){
		return 37;
	}

	@Override
	protected int getStardustTextX(){
		return 52;
	}

	@Override
	protected int getStardustTextY(){
		return 53;
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
