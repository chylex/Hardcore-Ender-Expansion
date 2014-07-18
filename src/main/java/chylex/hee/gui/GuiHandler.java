package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import chylex.hee.tileentity.TileEntityEnergyExtractionTable;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{
	public static GuiHandler instance = new GuiHandler();
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		switch(id){
			case 0: return new ContainerEnhancedBrewingStand(player.inventory,(TileEntityEnhancedBrewingStand)world.getTileEntity(x,y,z));
			case 2: return new ContainerDecompositionTable(player.inventory,(TileEntityDecompositionTable)world.getTileEntity(x,y,z));
			case 3: return new ContainerEnergyExtractionTable(player.inventory,(TileEntityEnergyExtractionTable)world.getTileEntity(x,y,z));
			case 4: return new ContainerEndPowderEnhancements(player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		switch(id){
			case 0: return new GuiEnhancedBrewingStand(player.inventory,(TileEntityEnhancedBrewingStand)world.getTileEntity(x,y,z));
			case 1: return new GuiKnowledgeBook(player.inventory.getCurrentItem());
			case 2: return new GuiDecompositionTable(player.inventory,(TileEntityDecompositionTable)world.getTileEntity(x,y,z));
			case 3: return new GuiEnergyExtractionTable(player.inventory,(TileEntityEnergyExtractionTable)world.getTileEntity(x,y,z));
			case 4: return new GuiEndPowderEnhancements(player.inventory);
		}
		return null;
	}
}
