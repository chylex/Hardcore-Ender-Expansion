package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.tileentity.TileEntityExperienceTable;

@SideOnly(Side.CLIENT)
public class GuiExperienceTable extends GuiAbstractTable{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/experience_table.png");

	public GuiExperienceTable(InventoryPlayer inv, TileEntityExperienceTable tile){
		super(new ContainerExperienceTable(inv,tile),tile);
		setupProgressBar(75,34);
		setupEnergyIcon(37,37);
		setupStardustText(52,53);
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return guiResource;
	}
}
