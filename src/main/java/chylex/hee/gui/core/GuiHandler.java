package chylex.hee.gui.core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import chylex.hee.gui.ContainerAccumulationTable;
import chylex.hee.gui.ContainerCharmPouch;
import chylex.hee.gui.ContainerDecompositionTable;
import chylex.hee.gui.ContainerEndPowderEnhancements;
import chylex.hee.gui.ContainerEnhancedBrewingStand;
import chylex.hee.gui.ContainerExperienceTable;
import chylex.hee.gui.ContainerExtractionTable;
import chylex.hee.gui.ContainerVoidChest;
import chylex.hee.gui.GuiAccumulationTable;
import chylex.hee.gui.GuiCharmPouch;
import chylex.hee.gui.GuiDecompositionTable;
import chylex.hee.gui.GuiEndPowderEnhancements;
import chylex.hee.gui.GuiEnhancedBrewingStand;
import chylex.hee.gui.GuiExperienceTable;
import chylex.hee.gui.GuiExtractionTable;
import chylex.hee.gui.GuiTransportBeacon;
import chylex.hee.gui.GuiVoidChest;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.voidchest.PlayerVoidChest;
import chylex.hee.tileentity.TileEntityAccumulationTable;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import chylex.hee.tileentity.TileEntityExperienceTable;
import chylex.hee.tileentity.TileEntityExtractionTable;
import chylex.hee.tileentity.TileEntityVoidChest;
import cpw.mods.fml.common.network.IGuiHandler;

public final class GuiHandler implements IGuiHandler{
	public static GuiHandler instance = new GuiHandler();
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		switch(id){
			case 0: return new ContainerEnhancedBrewingStand(player.inventory,(TileEntityEnhancedBrewingStand)world.getTileEntity(x,y,z));
			case 2: return new ContainerDecompositionTable(player.inventory,(TileEntityDecompositionTable)world.getTileEntity(x,y,z));
			case 3: return new ContainerExtractionTable(player.inventory,(TileEntityExtractionTable)world.getTileEntity(x,y,z));
			case 4: return new ContainerEndPowderEnhancements(player.inventory,y == -1 ? null : (IEnhanceableTile)world.getTileEntity(x,y,z));
			case 5: return new ContainerCharmPouch(player);
			case 6: return new ContainerVoidChest(player.inventory,PlayerVoidChest.getInventory(player).setChest((TileEntityVoidChest)world.getTileEntity(x,y,z)));
			case 7: return new ContainerExperienceTable(player.inventory,(TileEntityExperienceTable)world.getTileEntity(x,y,z));
			case 9: return new ContainerAccumulationTable(player.inventory,(TileEntityAccumulationTable)world.getTileEntity(x,y,z));
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		switch(id){
			case 0: return new GuiEnhancedBrewingStand(player.inventory,(TileEntityEnhancedBrewingStand)world.getTileEntity(x,y,z));
			case 2: return new GuiDecompositionTable(player.inventory,(TileEntityDecompositionTable)world.getTileEntity(x,y,z));
			case 3: return new GuiExtractionTable(player.inventory,(TileEntityExtractionTable)world.getTileEntity(x,y,z));
			case 4: return y == -1 ? new GuiEndPowderEnhancements(player.inventory) : new GuiEndPowderEnhancements(player.inventory,(IEnhanceableTile)world.getTileEntity(x,y,z));
			case 5: return new GuiCharmPouch(player);
			case 6: return new GuiVoidChest(player.inventory,PlayerVoidChest.getInventory(player).setChest((TileEntityVoidChest)world.getTileEntity(x,y,z)));
			case 7: return new GuiExperienceTable(player.inventory,(TileEntityExperienceTable)world.getTileEntity(x,y,z));
			case 8: return new GuiTransportBeacon(x,y,z);
			case 9: return new GuiAccumulationTable(player.inventory,(TileEntityAccumulationTable)world.getTileEntity(x,y,z));
		}
		
		return null;
	}
	
	private GuiHandler(){}
}
