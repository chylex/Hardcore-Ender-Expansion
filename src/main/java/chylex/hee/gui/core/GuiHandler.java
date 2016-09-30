package chylex.hee.gui.core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import chylex.hee.gui.*;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.tileentity.TileEntityAccumulationTable;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import chylex.hee.tileentity.TileEntityExperienceTable;
import chylex.hee.tileentity.TileEntityExtractionTable;
import chylex.hee.tileentity.TileEntityLootChest;
import cpw.mods.fml.common.network.IGuiHandler;

public final class GuiHandler implements IGuiHandler{
	public static GuiHandler instance = new GuiHandler();
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		Pos pos = Pos.at(x, y, z);
		
		switch(id){
			case 0: return pos.castTileEntity(world, TileEntityEnhancedBrewingStand.class).map(tile -> new ContainerEnhancedBrewingStand(player.inventory, tile)).orElse(null);
			case 1: return new ContainerAmuletOfRecovery(player);
			case 2: return pos.castTileEntity(world, TileEntityDecompositionTable.class).map(tile -> new ContainerDecompositionTable(player.inventory, tile)).orElse(null);
			case 3: return pos.castTileEntity(world, TileEntityExtractionTable.class).map(tile -> new ContainerExtractionTable(player.inventory, tile)).orElse(null);
			case 4: return new ContainerEndPowderEnhancements(player.inventory, y == -1 ? null : (IEnhanceableTile)pos.getTileEntity(world));
			case 5: return new ContainerCharmPouch(player);
			case 6: return pos.castTileEntity(world, TileEntityLootChest.class).map(tile -> new ContainerLootChest(player.inventory, tile.getInventoryFor(player))).orElse(null);
			case 7: return pos.castTileEntity(world, TileEntityExperienceTable.class).map(tile -> new ContainerExperienceTable(player.inventory, tile)).orElse(null);
			case 8: return new ContainerVoidPortalTokens(player, Pos.at(x, y, z));
			case 9: return pos.castTileEntity(world, TileEntityAccumulationTable.class).map(tile -> new ContainerAccumulationTable(player.inventory, tile)).orElse(null);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		Pos pos = Pos.at(x, y, z);
		
		switch(id){
			case 0: return pos.castTileEntity(world, TileEntityEnhancedBrewingStand.class).map(tile -> new GuiEnhancedBrewingStand(player.inventory, tile)).orElse(null);
			case 1: return new GuiAmuletOfRecovery(player);
			case 2: return pos.castTileEntity(world, TileEntityDecompositionTable.class).map(tile -> new GuiDecompositionTable(player.inventory, tile)).orElse(null);
			case 3: return pos.castTileEntity(world, TileEntityExtractionTable.class).map(tile -> new GuiExtractionTable(player.inventory, tile)).orElse(null);
			case 4: return y == -1 ? new GuiEndPowderEnhancements(player.inventory) : new GuiEndPowderEnhancements(player.inventory, (IEnhanceableTile)world.getTileEntity(x, y, z));
			case 5: return new GuiCharmPouch(player);
			case 6: return pos.castTileEntity(world, TileEntityLootChest.class).map(tile -> new GuiLootChest(player.inventory, tile.getInventoryFor(player))).orElse(null);
			case 7: return pos.castTileEntity(world, TileEntityExperienceTable.class).map(tile -> new GuiExperienceTable(player.inventory, tile)).orElse(null);
			case 8: return new GuiVoidPortalTokens(player, Pos.at(x, y, z));
			case 9: return pos.castTileEntity(world, TileEntityAccumulationTable.class).map(tile -> new GuiAccumulationTable(player.inventory, tile)).orElse(null);
		}
		
		return null;
	}
	
	private GuiHandler(){}
}
