package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemKnowledgeNote;
import chylex.hee.item.ItemList;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.world.loot.IItemPostProcessor;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryContent{
	private enum SmallRoom{
		EMPTY, CLUSTER, ENDER_CHEST, FLOWER_POTS, LOOT_CHEST, FLOOR_DESIGN
	}
	
	private static final WeightedList<ObjectWeightPair<SmallRoom>> smallRoomList = new WeightedList<>(
		ObjectWeightPair.of(SmallRoom.LOOT_CHEST, 18),
		ObjectWeightPair.of(SmallRoom.FLOOR_DESIGN, 16),
		ObjectWeightPair.of(SmallRoom.CLUSTER, 15),
		ObjectWeightPair.of(SmallRoom.FLOWER_POTS, 14),
		ObjectWeightPair.of(SmallRoom.EMPTY, 11),
		ObjectWeightPair.of(SmallRoom.ENDER_CHEST, 9)
	);
	
	public static void populateSmallRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		SmallRoom design = smallRoomList.getRandomItem(rand).getObject();
		
		switch(design){
			case FLOWER_POTS:
				world.setBlock(x,y+1,z,BlockList.death_flower_pot,rand.nextInt(4) == 0 ? 15 : rand.nextInt(15));
				
				for(int extra = rand.nextInt(2+rand.nextInt(5)), xx, zz; extra > 0; extra--){
					xx = x+rand.nextInt(3)-rand.nextInt(3);
					zz = z+rand.nextInt(3)-rand.nextInt(3);
					
					if (world.isAir(xx,y+1,zz)){
						if (rand.nextInt(5) != 0){
							boolean adj = false;
							
							for(int dir = 0; dir < 4; dir++){
								if (world.getBlock(xx+Direction.offsetX[dir],y+1,zz+Direction.offsetZ[dir]) == BlockList.death_flower_pot){
									adj = true;
									break;
								}
							}
							
							if (adj)continue;
						}
						
						world.setBlock(xx,y+1,zz,BlockList.death_flower_pot,rand.nextInt(4) == 0 ? 15 : rand.nextInt(15));
					}
				}
				
				break;
				
			case CLUSTER:
				world.setBlock(x,y+1,z,BlockList.laboratory_floor);
				world.setBlock(x,y+2,z,BlockList.energy_cluster);
				
				for(int a = 0; a < 3; a++){
					for(int b = 0; b < 2; b++){
						if (world.getBlock(x-4+8*b,y+2,z-1+a) == BlockList.laboratory_glass)world.setBlock(x-4+8*b,y+2,z-1+a,BlockList.laboratory_obsidian);
						if (world.getBlock(x-1+a,y+2,z-4+8*b) == BlockList.laboratory_glass)world.setBlock(x-1+a,y+2,z-4+8*b,BlockList.laboratory_obsidian);
					}
				}
				
				break;
				
			case ENDER_CHEST:
				world.setBlock(x,y+1,z,Blocks.ender_chest,rand.nextInt(4));
				break;
				
			case LOOT_CHEST:
				world.setBlock(x,y+1,z,Blocks.chest,rand.nextInt(4));
				world.setTileEntityGenerator(x,y+1,z,"LabSmallChest",new ITileEntityGenerator(){
					@Override
					public void onTileEntityRequested(String key, TileEntity tile, Random rand){
						TileEntityChest chest = (TileEntityChest)tile;
						
						for(int a = 0; a < 3+rand.nextInt(8-rand.nextInt(3)); a++){
							chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),smallChestLoot.generateIS(rand));
						}
					}
				});
				
				Block floorDecoration = rand.nextInt(11) == 0 ? Blocks.lapis_block : BlockList.laboratory_obsidian;
				
				for(int a = 0; a < 2; a++){
					for(int b = 0; b < 2; b++){
						world.setBlock(x-1+2*a,y,z-2+4*b,floorDecoration);
						world.setBlock(x-2+4*a,y,z-1+2*b,floorDecoration);
					}
				}
				
				break;
				
			case FLOOR_DESIGN:
				switch(rand.nextInt(3)){
					case 0:
						world.setBlock(x,y,z,BlockList.laboratory_obsidian);
						
						for(int a = 0; a < 3; a++){
							for(int b = 0; b < 2; b++){
								world.setBlock(x-2+4*b,y,z-1+a,BlockList.laboratory_obsidian);
								world.setBlock(x-1+a,y,z-2+4*b,BlockList.laboratory_obsidian);
							}
						}
						
						break;
						
					case 1:
						for(int a = 0; a < 2; a++){
							for(int b = 0; b < 2; b++){
								world.setBlock(x-1+2*a,y,z-2+4*b,BlockList.laboratory_obsidian);
								world.setBlock(x-2+4*a,y,z-1+2*b,BlockList.laboratory_obsidian);
							}
						}
						
						break;
						
					case 2:
						world.setBlock(x,y,z,BlockList.laboratory_obsidian);
						
						for(int a = 0; a < 2; a++){
							world.setBlock(x-2+4*a,y,z,BlockList.laboratory_obsidian);
							world.setBlock(x,y,z-2+4*a,BlockList.laboratory_obsidian);
						}
						
						break;
				}
				
				break;
				
			default:
		}
	}
	
	private enum LargeRoom{
		EMPTY, ENCASED_ENDIUM, LOOT_CHESTS, SPHALERITE_IRON_BLOCK
	}
	
	private static final WeightedList<ObjectWeightPair<LargeRoom>> largeRoomList = new WeightedList<>(
		ObjectWeightPair.of(LargeRoom.ENCASED_ENDIUM, 25),
		ObjectWeightPair.of(LargeRoom.LOOT_CHESTS, 25),
		ObjectWeightPair.of(LargeRoom.EMPTY, 18),
		ObjectWeightPair.of(LargeRoom.SPHALERITE_IRON_BLOCK, 15)
	);
	
	public static void populateLargeRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		LargeRoom design = largeRoomList.getRandomItem(rand).getObject();
		
		switch(design){
			case ENCASED_ENDIUM:
				world.setBlock(x,y+2,z,BlockList.endium_block);
				
				for(int py = 0; py < 3; py++){
					for(int px = 0; px < 3; px++){
						for(int pz = 0; pz < 3; pz++){
							if (world.isAir(x-1+px,y+1+py,z-1+pz))world.setBlock(x-1+px,y+1+py,z-1+pz,BlockList.laboratory_glass);
						}
					}
				}
				
				break;
				
			case LOOT_CHESTS:
				for(int attempt = 0, placed = 0, dir; attempt < 30 && placed < 4; attempt++){
					dir = rand.nextInt(4);
					
					if (world.isAir(x+Direction.offsetX[dir]*4,y+1,z+Direction.offsetZ[dir]*4) && !world.isAir(x+Direction.offsetX[dir]*5,y+1,z+Direction.offsetZ[dir]*5)){
						world.setBlock(x+Direction.offsetX[dir]*4,y+1,z+Direction.offsetZ[dir]*4,Blocks.chest,dir);
						world.setTileEntityGenerator(x+Direction.offsetX[dir]*4,y+1,z+Direction.offsetZ[dir]*4,"LabLargeChest",new ITileEntityGenerator(){
							@Override
							public void onTileEntityRequested(String key, TileEntity tile, Random rand){
								TileEntityChest chest = (TileEntityChest)tile;
								
								for(int a = 0; a < 3+rand.nextInt(6+rand.nextInt(3)); a++){
									chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),largeChestLoot.generateIS(rand));
								}
							}
						});
						
						for(int a = 0; a < 3; a++){
							world.setBlock(x+Direction.offsetX[dir]*5+Direction.offsetZ[dir]*(a-1),y+2,z+Direction.offsetZ[dir]*5+Direction.offsetX[dir]*(a-1),BlockList.laboratory_obsidian);
						}
						
						++placed;
					}
					
					if (placed > 0 && rand.nextInt(5) == 0)break;
					else if (placed > 1 && rand.nextInt(4) == 0)break;
				}
				
				break;
				
			case SPHALERITE_IRON_BLOCK:
				world.setBlock(x,y+1,z,BlockList.laboratory_obsidian);
				world.setBlock(x,y+2,z,Blocks.iron_block);
				world.setBlock(x,y+3,z,BlockList.laboratory_obsidian);
				
				for(int a = 0; a < 4; a++){
					world.setBlock(x+Direction.offsetX[a],y+1,z+Direction.offsetZ[a],BlockList.sphalerite);
					world.setBlock(x+Direction.offsetX[a],y+2,z+Direction.offsetZ[a],BlockList.laboratory_obsidian);
					world.setBlock(x+Direction.offsetX[a],y+3,z+Direction.offsetZ[a],BlockList.sphalerite);
				}
				
				for(int a = 0; a < 2; a++){
					for(int b = 0; b < 2; b++){
						world.setBlock(x-1+2*a,y+1,z-1+2*b,BlockList.laboratory_obsidian);
						world.setBlock(x-1+2*a,y+2,z-1+2*b,BlockList.sphalerite);
						world.setBlock(x-1+2*a,y+3,z-1+2*b,BlockList.laboratory_obsidian);
					}
				}
				
				break;
				
			default:
		}
	}
	
	private static final WeightedLootList smallChestLoot = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.end_powder).setAmount(2,6).setWeight(22),
		new LootItemStack(ItemList.stardust).setAmount(1,5).setWeight(18),
		new LootItemStack(ItemList.knowledge_note).setWeight(15),
		new LootItemStack(Items.dye).setAmount(1,3).setDamage(4).setWeight(10),
		new LootItemStack(Items.emerald).setAmount(1,3).setWeight(10)
	}).addItemPostProcessor(new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			if (is.getItem() == ItemList.knowledge_note)ItemKnowledgeNote.setRandomNote(is,rand,4);
			return is;
		}
	});
	
	private static final WeightedLootList largeChestLoot = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.end_powder).setAmount(3,7).setWeight(25),
		new LootItemStack(ItemList.obsidian_fragment).setAmount(1,4).setWeight(22),
		new LootItemStack(ItemList.endium_ingot).setAmount(1,2).setWeight(17),
		new LootItemStack(ItemList.auricion).setAmount(1,2).setWeight(15),
		new LootItemStack(ItemList.knowledge_note).setWeight(10),
		new LootItemStack(Items.iron_ingot).setAmount(2,5).setWeight(8),
		new LootItemStack(Items.gold_ingot).setAmount(2,4).setWeight(7)
	}).addItemPostProcessor(new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			if (is.getItem() == ItemList.knowledge_note)ItemKnowledgeNote.setRandomNote(is,rand,6);
			return is;
		}
	});
	
	private LaboratoryContent(){}
}
