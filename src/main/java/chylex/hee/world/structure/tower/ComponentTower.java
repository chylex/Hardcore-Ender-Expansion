package chylex.hee.world.structure.tower;
import gnu.trove.list.array.TByteArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.item.ItemKnowledgeNote;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.WorldGenSavefile;
import chylex.hee.system.savedata.types.WorldGenSavefile.WorldGenElement;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.TileEntityEndermanHead;
import chylex.hee.tileentity.spawner.TowerEndermanSpawnerLogic;
import chylex.hee.world.loot.IItemPostProcessor;
import chylex.hee.world.loot.ItemUtil;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.ComponentLargeStructureWorld;
import chylex.hee.world.structure.util.Facing;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;

public class ComponentTower extends ComponentLargeStructureWorld implements ITileEntityGenerator{
	private static final byte roomHeight = 6;
	
	public static final WeightedLootList lootTower = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(Blocks.web).setWeight(220),
		new LootItemStack(ItemList.end_powder).setAmount(1,10).setWeight(212),
		new LootItemStack(Items.gold_nugget).setAmount(1,12).setWeight(190),
		new LootItemStack(ItemList.enhanced_ender_pearl).setAmount(1,5).setWeight(182),
		new LootItemStack(Items.experience_bottle).setAmount(1,8).setWeight(175),
		new LootItemStack(Items.ender_pearl).setAmount(1,6).setWeight(160),
		new LootItemStack(Items.emerald).setAmount(1,6).setWeight(152),
		new LootItemStack(ItemList.igneous_rock).setAmount(1,7).setWeight(140),
		new LootItemStack(Items.iron_ingot).setAmount(1,13).setWeight(136),
		new LootItemStack(ItemList.stardust).setAmount(3,11).setWeight(131),
		new LootItemStack(Items.gold_ingot).setAmount(1,11).setWeight(125),
		new LootItemStack(ItemList.knowledge_note).setWeight(122),
		new LootItemStack(BlockList.obsidian_special).setAmount(1,9).setDamage(0,2).setWeight(121),
		new LootItemStack(ItemList.adventurers_diary).setWeight(117),
		new LootItemStack(Blocks.yellow_flower).setAmount(1,7).setWeight(116),
		new LootItemStack(Blocks.red_flower).setAmount(1,5).setWeight(111),
		new LootItemStack(ItemList.biome_compass).setWeight(110),
		new LootItemStack(Items.golden_apple).setAmount(1,2).setWeight(109),
		new LootItemStack(BlockList.obsidian_special_glow).setAmount(1,8).setDamage(0,2).setWeight(104),
		new LootItemStack(Items.spawn_egg).setDamage(58).setAmount(1,4).setWeight(101),
		new LootItemStack(Blocks.brown_mushroom).setAmount(1,6).setWeight(92),
		new LootItemStack(Blocks.red_mushroom).setAmount(1,6).setWeight(90),
		new LootItemStack(Items.diamond).setAmount(1,5).setWeight(85),
		new LootItemStack(Items.compass).setWeight(75),
		new LootItemStack(Items.clock).setWeight(75),
		new LootItemStack(ItemList.music_disk).setDamage(0,ItemMusicDisk.getRecordCount()-1).setWeight(71),
		new LootItemStack(Items.water_bucket).setWeight(68),
		new LootItemStack(Blocks.chest).setWeight(60),
		new LootItemStack(Blocks.tnt).setAmount(1,8).setWeight(58),
		new LootItemStack(BlockList.enhanced_tnt).setAmount(1,4).setWeight(49),
		new LootItemStack(Blocks.grass).setAmount(1,6).setWeight(46),
		new LootItemStack(Blocks.mycelium).setAmount(1,6).setWeight(45),
		new LootItemStack(Items.cake).setWeight(37),
		new LootItemStack(Items.ender_eye).setAmount(1,2).setWeight(36),
		new LootItemStack(ItemList.enhanced_brewing_stand).setWeight(17),
		new LootItemStack(ItemList.temple_caller).setWeight(14),
		new LootItemStack(BlockList.essence_altar).setWeight(13),
		new LootItemStack(Items.golden_apple).setDamage(1).setWeight(4)
	}).addItemPostProcessor(new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			if (is.getItem() == ItemList.enhanced_ender_pearl){
				List<EnderPearlEnhancements> availableTypes = CollectionUtil.newList(EnderPearlEnhancements.values());
				
				for(int a = 0; a < 1+Math.abs(Math.round(rand.nextDouble()*rand.nextGaussian()*2.75D)); a++){
					is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
					if (availableTypes.isEmpty())break;
				}
			}
			else if (is.getItem() == Item.getItemFromBlock(BlockList.enhanced_tnt)){
				List<TNTEnhancements> availableTypes = CollectionUtil.newList(TNTEnhancements.values());
				
				for(int a = 0; a < 1+rand.nextInt(2)+Math.round(rand.nextDouble()*2D); a++){
					is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
					if (availableTypes.isEmpty())break;
				}
			}
			else if (is.getItem() == ItemList.knowledge_note){
				ItemKnowledgeNote.setRandomNote(is,rand,5);
			}
			else if (is.getItem() == Items.cake){
				ItemUtil.addLore(is,EnumChatFormatting.DARK_PURPLE.toString()+EnumChatFormatting.ITALIC+"Why are there just pieces of cake");
				ItemUtil.addLore(is,EnumChatFormatting.DARK_PURPLE.toString()+EnumChatFormatting.ITALIC+"lying all over the place?...");
			}
			
			return is;
		}
	});
	
	public static WeightedLootList lootFuel = new WeightedLootList(
		new LootItemStack(Items.coal).setAmount(1,16).setWeight(32),
		new LootItemStack(Items.coal).setDamage(1).setAmount(1,16).setWeight(30),
		new LootItemStack(ItemList.igneous_rock).setAmount(1,6).setWeight(20),
		new LootItemStack(Blocks.coal_block).setAmount(1,6).setWeight(10),
		new LootItemStack(Items.blaze_rod).setAmount(1,8).setWeight(18),
		new LootItemStack(Items.lava_bucket).setWeight(15),
		new LootItemStack(Blocks.sapling).setAmount(1,20).setDamage(0,3).setWeight(10),
		new LootItemStack(Items.stick).setAmount(1,32).setWeight(8)
	);
	
	private byte lastRoomUsed = -1;
	
	/**
	 * Required for reflection.
	 */
	public ComponentTower(){}
	
	protected ComponentTower(Random rand, int x, int z){
		super(rand,x,32,z,32,140,32);
		WorldDataHandler.<WorldGenSavefile>get(WorldGenSavefile.class).addElementAt(getStartX()>>4,getStartZ()>>4,WorldGenElement.DUNGEON_TOWER);
	}
	
	@Override
	protected int setupStructure(long seed){
		Random rand = new Random(((getStartX()/14)*185226L+(getStartZ()/14)*24071632895L)^seed);
		
		int centerX = sizeX/2, centerZ = sizeZ/2, islandY = 32+rand.nextInt(40), roomAmount = 4; // room amount maybe random?
		
		// ISLAND
		
		float rad = rand.nextFloat()*3.5F+9F;
		int ceilRad = MathUtil.ceil(rad);
		
		for(int a = centerX-ceilRad; a <= centerX+ceilRad; a++){
			for(int b = centerX-ceilRad; b <= centerX+ceilRad; b++){
				double distFromCenter = MathUtil.distance(a-centerX,b-centerX);
				
				if (distFromCenter > rad-rand.nextFloat()*1.1F+rand.nextFloat()*0.5F)continue;
				
				double distFactor = ceilRad*1.15D-distFromCenter;
				
				for(int spikeY = 0; spikeY <= distFactor*0.45D*Math.sqrt(distFactor*0.5D)+rand.nextDouble()*1.5D*distFactor; spikeY++){
					structure.setBlock(a,islandY-spikeY,b,Blocks.end_stone);
				}
			}
		}
		
		// WALL LAYOUT
		
		int topY = islandY+roomHeight*roomAmount;
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 5; b++){
				for(int y = islandY; y <= topY; y++){
					structure.setBlock(centerX-4+8*a,y,centerZ-2+b,BlockList.obsidian_special);
					structure.setBlock(centerX-2+b,y,centerZ-4+8*a,BlockList.obsidian_special);
				}
			}
			
			for(int b = 0; b < 2; b++){
				for(int y = islandY; y <= topY; y++){
					structure.setBlock(centerX-3+6*a,y,centerZ-3+6*b,BlockList.obsidian_special);
				}
			}
		}
		
		// ROOM CONTENT
		
		for(int a = 0, ladderMeta, yy; a < roomAmount; a++){
			yy = islandY+roomHeight*a;
			ladderMeta = getMetadataWithOffset(Blocks.ladder,a%2 == 0 ? 2 : 3);
			for(int py = yy+1; py <= yy+6; py++)structure.setBlock(centerX,py,centerZ-3+6*(a%2),Blocks.ladder,ladderMeta);
			
			generateWallDecorations(centerX,yy,centerZ);
			generateFloor(centerX,yy,centerZ,a);
			
			if (a < roomAmount-1)generateBasicRoom(centerX,yy+1,centerZ,a,rand);
			else generateChestRoom(centerX,yy+1,centerZ,a,rand);
		}
		
		// ENTRANCE
		
		for(int yy = islandY+1; yy <= islandY+5; yy++){
			for(int xx = centerX-1; xx <= centerX+1; xx++)structure.setBlock(xx,yy,centerZ+4,Blocks.air);
			for(int xx = centerX-2; xx <= centerX+2; xx++)structure.setBlock(xx,yy,centerZ+5,Blocks.air);
		}
		
		for(int xx = centerX-1; xx <= centerX+1; xx++){
			for(int zz = centerZ+3; zz <= centerZ+5; zz++)structure.setBlock(xx,islandY,zz,Blocks.obsidian);
			structure.setBlock(xx,islandY+5,centerZ+4,BlockList.obsidian_special,5);
		}

		for(int a = 0; a < 2; a++){
			for(int yy = islandY; yy <= islandY+5; yy++){
				structure.setBlock(centerX-2+4*a,yy,centerZ+5,BlockList.obsidian_special,2);
			}
			
			structure.setBlock(centerX-1+2*a,islandY+5,centerZ+5,BlockList.obsidian_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,a == 0 ? 1 : 0));
		}
		
		// TOP AREA
		
		for(int xx = centerX-3; xx <= centerX+3; xx++){
			for(int zz = centerZ-2; zz <= centerZ+2; zz++){
				structure.setBlock(xx,topY,zz,Blocks.obsidian);
			}
		}
		
		for(int a = 0; a < 2; a++){
			for(int xx = centerX-2; xx <= centerX+2; xx++)structure.setBlock(xx,topY,centerZ-3+6*a,Blocks.obsidian);
			
			for(int yy = topY-2; yy <= topY+5; yy++)structure.setBlock(centerX-5+10*a,yy,centerZ,Blocks.obsidian);
			for(int yy = topY-1; yy <= topY+8; yy++)structure.setBlock(centerX-6+12*a,yy,centerZ,Blocks.obsidian);
			for(int yy = topY; yy <= topY+10; yy++)structure.setBlock(centerX-7+14*a,yy,centerZ,Blocks.obsidian);
			for(int yy = topY+3; yy <= topY+12; yy++)structure.setBlock(centerX-8+16*a,yy,centerZ,Blocks.obsidian);
			
			for(int b = 0; b < 2; b++){
				for(int yy = topY-1; yy <= topY; yy++)structure.setBlock(centerX-5+10*a,yy,centerZ-1+2*b,Blocks.obsidian);
				for(int yy = topY; yy <= topY+4; yy++)structure.setBlock(centerX-6+12*a,yy,centerZ-1+2*b,Blocks.obsidian);
				for(int yy = topY+4; yy <= topY+8; yy++)structure.setBlock(centerX-7+14*a,yy,centerZ-1+2*b,Blocks.obsidian);
			}
			
			structure.setBlock(centerX-8+16*a,topY+13,centerZ,Blocks.glowstone,0,true);
			
			for(int yy = topY-2; yy <= topY+5; yy++)structure.setBlock(centerX,yy,centerZ-5+10*a,Blocks.obsidian);
			for(int yy = topY-1; yy <= topY+8; yy++)structure.setBlock(centerX,yy,centerZ-6+12*a,Blocks.obsidian);
			for(int yy = topY; yy <= topY+10; yy++)structure.setBlock(centerX,yy,centerZ-7+14*a,Blocks.obsidian);
			for(int yy = topY+3; yy <= topY+12; yy++)structure.setBlock(centerX,yy,centerZ-8+16*a,Blocks.obsidian);
			
			for(int b = 0; b < 2; b++){
				for(int yy = topY-1; yy <= topY; yy++)structure.setBlock(centerX-1+2*b,yy,centerZ-5+10*a,Blocks.obsidian);
				for(int yy = topY; yy <= topY+4; yy++)structure.setBlock(centerX-1+2*b,yy,centerZ-6+12*a,Blocks.obsidian);
				for(int yy = topY+4; yy <= topY+8; yy++)structure.setBlock(centerX-1+2*b,yy,centerZ-7+14*a,Blocks.obsidian);
			}
			
			structure.setBlock(centerX,topY+13,centerZ-8+16*a,Blocks.glowstone,0,true);
		}
		
		int zOffset = roomAmount%2 == 1 ? 2 : -2;
		structure.setBlock(centerX,topY,centerZ+(roomAmount%2 == 1 ? -3 : 3),Blocks.ladder,getMetadataWithOffset(Blocks.ladder,roomAmount%2 == 0 ? 3 : 2));
		structure.setBlock(centerX,topY+1,centerZ+zOffset,BlockList.obsidian_special_glow,1,true);
		
		EntityMiniBossEnderEye eye = new EntityMiniBossEnderEye(null,centerX+0.5D,topY+1.825D,centerZ+zOffset+0.5D);
		eye.setPositionAndRotation(eye.posX,eye.posY,eye.posZ,90*coordBaseMode,0);
		structure.addEntity(eye);
		
		return 0;
	}
	
	private void generateWallDecorations(int x, int y, int z){
		for(int a = 0; a < 2; a++){
			// outline
			for(int zz = z-2; zz <= z+2; zz++)structure.setBlock(x-5+10*a,y+6,zz,BlockList.obsidian_special,1);
			for(int xx = x-2; xx <= x+2; xx++)structure.setBlock(xx,y+6,z-5+10*a,BlockList.obsidian_special,1);
			
			for(int b = 0; b < 2; b++){
				structure.setBlock(x-4+8*a,y+6,z-3+6*b,BlockList.obsidian_special,1);
				structure.setBlock(x-3+6*a,y+6,z-4+8*b,BlockList.obsidian_special,1);
			}
			
			// pillars
			for(int yy = y+1; yy <= y+5; yy++){
				structure.setBlock(x-5+10*a,yy,z,BlockList.obsidian_special,2);
				structure.setBlock(x,yy,z-5+10*a,BlockList.obsidian_special,2);
			}
			
			// obsidian blocks and stairs + glowing obsidian
			for(int b = 0; b < 2; b++){
				structure.setBlock(x-4+8*a,y+3,z-1+2*b,BlockList.obsidian_special_glow,0,true);
				structure.setBlock(x-1+2*b,y+3,z-4+8*a,BlockList.obsidian_special_glow,0,true);
				
				for(int c = 0; c < 2; c++){
					structure.setBlock(x-5+10*a,y+1+4*c,z-1+2*b,Blocks.obsidian,0);
					structure.setBlock(x-5+10*a,y+1+4*c,z-2+4*b,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0 ? 3 : 2));
					structure.setBlock(x-5+10*a,y+2+2*c,z-1+2*b,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0 ? 3 : 2));
					
					structure.setBlock(x-1+2*b,y+1+4*c,z-5+10*a,Blocks.obsidian,0);
					structure.setBlock(x-2+4*b,y+1+4*c,z-5+10*a,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0 ? 0 : 1));
					structure.setBlock(x-1+2*b,y+2+2*c,z-5+10*a,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0 ? 0 : 1));
				}
			}
		}
	}
	
	private void generateFloor(int x, int y, int z, int roomNb){
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++){
				// 2x2 obsidian blocks on sides
				for(int xx = x-2+a*3; xx <= x-1+a*3; xx++){
					for(int zz = z-3+b*4; zz <= z-2+b*5; zz++){
						structure.setBlock(xx,y,zz,Blocks.obsidian);
					}
				}
				// 2x1 smooth obsidian blocks next to them
				for(int xx = x-2+a*3; xx <= x-1+a*3; xx++)structure.setBlock(xx,y,z-1+b*2,BlockList.obsidian_special);
				// 1x1 smooth obsidian corners
				for(int xx = x-3+a*6; xx <= x-3+a*6; xx++)structure.setBlock(xx,y,z-2+b*4,BlockList.obsidian_special);
			}
			// 1x2 smooth obsidian blocks in center [part 1]
			structure.setBlock(x,y,z-2+a*4,BlockList.obsidian_special,0);
			// 1x3 obsidian lines on sides
			for(int zz = z-1; zz <= z+1; zz++)structure.setBlock(x-3+a*6,y,zz,Blocks.obsidian);
			// 2x1 obsidian lines in center
			for(int xx = x-2+a*3; xx <= x-1+a*3; xx++)structure.setBlock(xx,y,z,Blocks.obsidian);
			// 1x1 obsidian dots in center
			for(int zz = z-1+a*2; zz <= z+a; zz++)structure.setBlock(x,y,zz,Blocks.obsidian);
		}
		structure.setBlock(x,y,z,BlockList.obsidian_special,6);
		// 1x2 smooth obsidian blocks in center [part 2]
		if (roomNb%2 == 0)structure.setBlock(x,y,z-3,BlockList.obsidian_special,0);
		else structure.setBlock(x,y,z+3,BlockList.obsidian_special,0);
	}
	
	private void generateBasicRoom(int x, int y, int z, int roomNb, Random rand){
		int n = roomNb == 0 ? rand.nextInt(8)+1 : rand.nextInt(9)+1; // lazy to prevent 9 from being the first room
		boolean isRoomEven = roomNb%2 == 0;
		
		if (n == lastRoomUsed)n = rand.nextInt(9)+1;
		lastRoomUsed = (byte)n;
		
		/*
		 * bookshelves on sides and ench. table in the center
		 */
		if (n == 1){
			structure.setBlock(x,y,z,Blocks.enchanting_table,0);
			int flowerType = rand.nextInt(3) == 0?15:rand.nextInt(5);

			for(int a = 0; a < 2; a++){
				structure.setBlock(x-3+a*6,y+4,z,BlockList.death_flower_pot,flowerType);
				structure.setBlock(x-2+a*4,y,z+(isRoomEven ? 3 : -3),BlockList.obsidian_special,0);
				spawnBrewingStand(x-2+a*4,y+1,z+(isRoomEven ? 3 : -3),rand);
				
				for(int b = 0; b < 2; b++){
					for(int yy = y; yy <= y+3; yy++)structure.setBlock(x-3+a*6,yy,z-1+b*2,Blocks.bookshelf);
					spawnEndermanHead(x-3+a*6,y+4,z-2+b*4,x-1+a*2,z);
					
					for(int c = 0; c < 2; c++)structure.setBlock(x-3+a*6,y+c*3,z+2-b*4,Blocks.oak_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(b == 1?Facing.NORTH_NEGZ:Facing.SOUTH_POSZ).getStairs()));
				}
				
				for(int b = 0; b < 4; b++)structure.setBlock(x-3+a*6,y+b,z,Blocks.oak_stairs,(b%2 == 1?4:0)+getMetadataWithOffset(Blocks.stone_stairs,(a == 1?Facing.EAST_POSX:Facing.WEST_NEGX).getStairs()));
			}
		}
		/*
		 * heads on dispensers with redstone on floor and spawner in the center
		 */
		else if (n == 2){
			spawnEndermanSpawner(x,y,z,2+roomNb);
			
			for(int a = 0; a < 2; a++){
				spawnDispenser(x-3+a*6,y,z,0,2,Facing.UP);
				spawnEndermanHead(x-3+a*6,y+1,z,x,z);
			}
			
			for(int a = 0,xx,zz; a < 6+rand.nextInt(10); a++){
				xx = x+rand.nextInt(7)-3;
				zz = z+rand.nextInt(7)-3;
				
				if (structure.getBlock(xx,y,zz).getMaterial() == Material.air && structure.getBlock(xx,y-1,zz).isNormalCube()){
					structure.setBlock(xx,y,zz,Blocks.redstone_wire,0);
				}
			}
		}
		/*
		 * heads on stairs, flower in center, spawners on sides
		 */
		else if (n == 3){
			spawnDispenser(x,y,z,0,2,Facing.UP);
			structure.setBlock(x,y+1,z,BlockList.death_flower_pot,rand.nextInt(3));
			
			for(int a = 0; a < 2; a++){
				spawnEndermanSpawner(x-3+a*6,y,z,1+roomNb);
				
				for(int b = 0; b < 2; b++){
					structure.setBlock(x-3+a*6,y+2,z-2+b*4,BlockList.obsidian_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 1 ? Facing.EAST_POSX : Facing.WEST_NEGX).getStairs()));
					spawnEndermanHead(x-3+a*6,y+3,z-2+b*4,x,z-2+b*4);
				}
			}
		}
		/*
		 * heads on dispensers, spawners between stairs in corners
		 */
		else if (n == 4){
			for(int a = 0; a < 2; a++){
				spawnDispenser(x-3+a*6,y,z,0,2,Facing.UP);
				spawnEndermanHead(x-3+a*6,y+1,z,x,z);
				
				for(int b = 0; b < 2; b++){
					spawnEndermanSpawner(x-3+a*6,y+2,z-2+b*4,1+roomNb);
					spawnEndermanSpawner(x-2+a*4,y+2,z-3+b*6,1+roomNb);
					
					for(int c = 0; c < 2; c++){
						for(int d = 0; d < 2; d++){
							structure.setBlock(x-3+a*6,y+c+3*d,z-2+b*4,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
							structure.setBlock(x-2+a*4,y+c+3*d,z-3+b*6,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
						}
					}
				}
			}
		}
		/*
		 * anvils and heads on sides, brewing stand on bookshelves in corners
		 */
		else if (n == 5){
			for(int a = 0; a < 2; a++){
				structure.setBlock(x-3+a*6,y,z,Blocks.double_stone_slab,0);
				spawnEndermanHead(x-3+a*6,y+1,z,x,z);
				
				for(int b = 0; b < 2; b++){
					spawnAnvil(x-3+a*6,y,z-1+b*2,Facing.NORTH_NEGZ,rand);
					
					structure.setBlock(x-3+a*6,y,z-2+b*4,Blocks.double_stone_slab,0);
					structure.setBlock(x-3+a*6,y+1,z-2+b*4,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 1 ? Facing.EAST_POSX : Facing.WEST_NEGX).getStairs()));
					for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x-3+a*6,yy,z-2+b*4,Blocks.nether_brick_fence);
					spawnEndermanSpawner(x-3+a*6,y+4,z-2+b*4,1+roomNb);
					
					structure.setBlock(x-1+a*2,y,z-3+b*6,Blocks.stone_brick_stairs,getMetadataWithOffset(Blocks.stone_stairs,(a == 1 ? Facing.EAST_POSX : Facing.WEST_NEGX).getStairs()));
					structure.setBlock(x-2+a*4,y,z-3+b*6,Blocks.bookshelf,0);
					spawnBrewingStand(x-2+a*4,y+1,z-3+b*6,rand);
					
				}
			}
		}
		/*
		 * flowers in corners, iron bars with spawner on ceiling
		 */
		else if (n == 6){
			spawnEndermanSpawner(x,y+4,z,2+roomNb);
			
			for(int a = 0; a < 2; a++){
				for(int b = 0; b < 2; b++){
					structure.setBlock(x-3+a*6,y+b,z,Blocks.nether_brick_stairs,b*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 1 ? Facing.EAST_POSX : Facing.WEST_NEGX).getStairs()));
					spawnDispenser(x-3+a*6,y,z-2+b*4,0,1,Facing.UP);
					structure.setBlock(x-3+a*6,y+1,z-2+b*4,BlockList.death_flower_pot,rand.nextInt(5));
				}
				
				structure.setBlock(x-3+a*6,y+4,z,BlockList.obsidian_special,1);
				for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x-3+a*6,yy,z,Blocks.iron_bars);
				for(int yy = y+3; yy <= y+4; yy++)structure.setBlock(x-2+a*4,yy,z,Blocks.iron_bars);
				structure.setBlock(x-1+a*2,y+5,z,Blocks.iron_bars,0);
			}
			
			/*int sign = isRoomEven?-1:1;
			
			structure.setBlock(x,y+4,z-3*sign,BlockList.obsidianSpecial,1);
			for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x,yy,z-3*sign,Blocks.iron_bars);
			for(int yy = y+3; yy <= y+4; yy++)structure.setBlock(x,yy,z-2*sign,Blocks.iron_bars);
			structure.setBlock(x,y+5,z-1*sign,Blocks.iron_bars,0);
			
			fillWithBlocks(world,bb,x,y+5,z+1*sign,x,y+4,z+2*sign,Blocks.iron_bars,Blocks.iron_bars,false);*/
			
			for(int a = 0,xx,zz; a < 5+rand.nextInt(8); a++){
				xx = rand.nextInt(7)-3;
				zz = rand.nextInt(7)-3;
				
				if (structure.getBlock(xx,y,zz).getMaterial() == Material.air && structure.getBlock(xx,y-1,zz).isNormalCube()){
					structure.setBlock(xx,y,zz,Blocks.redstone_wire,0);
				}
			}
		}
		/*
		 * beacons and shelves with stairs on sides
		 */
		else if (n == 7){
			int zOffset = isRoomEven ? -3 : 3;
			structure.setBlock(x,y+4,z-zOffset,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,Facing.SOUTH_POSZ.getStairs()));
			//for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x,yy,z+_z,Blocks.bookshelf);
			
			for(int a = 0; a < 2; a++){
				for(int yy = y+1; yy <= y+3; yy++)structure.setBlock(x-3+a*6,yy,z,Blocks.bookshelf);
				
				for(int b = 0; b < 2; b++){
					structure.setBlock(x-3+a*6,y+2,z-1+b*2,Blocks.bookshelf,0);
					structure.setBlock(x-1+b*2,y+2,z-3+a*6,Blocks.bookshelf,0);
					
					spawnEndermanSpawner(x-3+a*6,y,z-2+b*4,1+roomNb);
					spawnEndermanSpawner(x-2+a*4,y,z-3+b*6,1+roomNb);
					
					spawnAnvil(x-2+a*4,y,z-2+b*4,Facing.EAST_POSX,rand);
					structure.setBlock(x-2+a*4,y+1,z-2+b*4,Blocks.lit_redstone_lamp,0);
					structure.setBlock(x-2+a*4,y+2,z-2+b*4,Blocks.stone_slab,0);
					
					structure.setBlock(x-3+a*6,y+b*4,z,Blocks.stone_brick_stairs,b*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
					
					for(int c = 0; c < 2; c++){
						for(int d = 0; d < 2; d++){
							structure.setBlock(x-3+a*6,y+c+3*d,z-1+b*2,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
							structure.setBlock(x-1+a*2,y+c+3*d,z-3+b*6,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
						}
					}
				}
			}
		}
		/*
		 * maze
		 */
		else if (n == 8){
			int[][] pillars = new int[][]{
				new int[]{ -2, -1 }, new int[]{ 0, -2 }, new int[]{ 1, -2 }, new int[]{ 1, -3 }, new int[]{ -3, 2 },
				new int[]{ -1, 2 }, new int[]{ -1, 3 }, new int[]{ 0, 1 }, new int[]{ 3, 1 }
			};
			
			for(int[] xz:pillars){
				for(int yy = y; yy <= y+4; yy++)structure.setBlock(x+xz[0],yy,z+xz[1],BlockList.obsidian_special,2);
				if (rand.nextFloat() < 0.25F)spawnEndermanSpawner(x+xz[0],y+rand.nextInt(5),z+xz[1],1);
			}
			
			for(int yy = y; yy <= y+1; yy++)structure.setBlock(x+2,yy,z-1,BlockList.obsidian_special,2);
			spawnEndermanSpawner(x+2,y+2,z-1,2+roomNb);
			structure.setBlock(x+2,y+3,z-1,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.stone_stairs,Facing.SOUTH_POSZ.getStairs()));
			
			for(int attempt = 0,placed = 0,xx,zz; attempt < 8 && placed < rand.nextInt(4); attempt++){
				xx = x+rand.nextInt(7)-3;
				zz = z+rand.nextInt(7)-3;
				
				if (structure.isAir(xx,y,zz)){
					spawnEndermanSpawner(xx,y,zz,1);
					++placed;
				}
			}
			
			spawnChest(x+2,y,z-3,rand.nextBoolean(),3,5,Facing.SOUTH_POSZ);
		}
		/*
		 * ench. table in the center, bookshelves in corners, obsidian with spawners on sides
		 */
		else if (n == 9){
			structure.setBlock(x,y,z,Blocks.enchanting_table,0);
			for(int yy = y+3+(roomNb == 0 ? 1 : 0); yy <= y+4; yy++)structure.setBlock(x,yy,z+(isRoomEven ? 3 : -3),Blocks.obsidian);
			
			for(int a = 0; a < 2; a++){
				spawnEndermanSpawner(x-3+a*6,y+2,z,3+roomNb);
				structure.setBlock(x,y+4,z-2+a*4,BlockList.obsidian_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
				
				for(int b = 0; b < 2; b++){
					for(int yy = y+b*3; yy <= y+1+b*3; yy++)structure.setBlock(x-3+a*6,yy,z,Blocks.obsidian);
					structure.setBlock(x-2+4*a,y+4*b,z,BlockList.obsidian_stairs,4*b+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
					
					for(int yy = y; yy <= y+4; yy++)structure.setBlock(x-3+a*6,yy,z-2+b*4,Blocks.bookshelf);
					for(int yy = y; yy <= y+4; yy++)structure.setBlock(x-2+a*4,yy,z-3+b*6,Blocks.bookshelf);
					
					structure.setBlock(x-3+a*6,y,z-1+b*2,Blocks.oak_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
					structure.setBlock(x-1+a*2,y,z-3+b*6,Blocks.oak_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
					
					for(int c = 0; c < 2; c++){
						if (rand.nextFloat() < 0.42F){
							int xx = c == 0 ? x-3+a*6 : x-1+a*2, zz = c == 0 ? z-1+b*2 : z-3+b*6;
							
							if (rand.nextBoolean())structure.setBlock(xx,y+1,zz,BlockList.death_flower_pot,rand.nextInt(10) == 0 ? 15 : rand.nextInt(4));
							else spawnBrewingStand(xx,y+1,zz,rand);
						}
					}
				}
			}
		}
	}

	private void generateChestRoom(int x, int y, int z, int roomNb, Random rand){
		int n = rand.nextInt(3)+1;
		boolean isRoomEven = roomNb%2 == 0;
		
		/*
		 * chests between stairs with nether fences and hoppers
		 */
		if (n == 1){
			for(int a = 0; a < 2; a++){
				spawnChest(x-3+a*6,y,z,false,7,11,a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX);
				
				for(int b = 0; b < 2; b++){
					spawnEndermanSpawner(x-3+a*6,y,z-2+b*4,2);
					spawnEndermanSpawner(x-2+a*4,y,z-3+b*6,2);
					structure.setBlock(x-2+a*4,y,z-2+b*4,BlockList.obsidian_special,2);
					
					spawnEndermanHead(x-2+a*4,y+1,z-2+b*4,x,z);
					
					for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x-3+a*6,yy,z-2+b*4,Blocks.nether_brick_fence);
					structure.setBlock(x-3+a*6,y+4,z-2+b*4,Blocks.hopper,0);
					
					for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x-2+a*4,yy,z-3+b*6,Blocks.nether_brick_fence);
					structure.setBlock(x-2+a*4,y+4,z-3+b*6,Blocks.hopper,0);
					
					structure.setBlock(x-3+a*6,y,z-1+b*2,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
					structure.setBlock(x-3+a*6,y+1,z-2+b*4,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
					
					structure.setBlock(x-1+b*2,y,z-3+a*6,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
					structure.setBlock(x-2+b*4,y+1,z-3+a*6,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
				}
			}
		}
		/*
		 * lots of chests and furnaces with stair decorations
		 */
		else if (n == 2){
			for(int a = 0; a < 2; a++){
				spawnEndermanSpawner(x-3+a*6,y,z,6);
				for(int yy = y+1; yy <= y+3; yy++)structure.setBlock(x-3+a*6,yy,z,Blocks.nether_brick_fence);
				structure.setBlock(x-3+a*6,y+4,z,Blocks.hopper,0);
				
				for(int b = 0; b < 2; b++){
					for(int c = 0; c < 2; c++){
						spawnChest(x-3+a*6,y,z+(c+1)*(-1+b*2),true,0,rand.nextInt(5) <= 1 ? 4 : 2,a == 0 ? Facing.EAST_POSX : Facing.WEST_NEGX);
						spawnChest(x+(c+1)*(-1+b*2),y,z-3+a*6,true,0,rand.nextInt(5) <= 1 ? 4 : 2,a == 0 ? Facing.NORTH_NEGZ : Facing.SOUTH_POSZ);
						
						structure.setBlock(x-3+a*6,y+2+c*2,z-1+b*2,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
						structure.setBlock(x-3+a*6,y+2+c*2,z-2+b*4,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
						
						structure.setBlock(x-1+a*2,y+2+c*2,z-3+b*6,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0 ? Facing.SOUTH_POSZ : Facing.NORTH_NEGZ).getStairs()));
						structure.setBlock(x-2+a*4,y+2+c*2,z-3+b*6,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
					}
					
					structure.setBlock(x-2+a*4,y+4,z-2+b*4,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0 ? Facing.WEST_NEGX : Facing.EAST_POSX).getStairs()));
					
					spawnFurnace(x-3+a*6,y+3,z-2+b*4,0.35F,a == 0 ? Facing.EAST_POSX : Facing.WEST_NEGX,rand);
					spawnFurnace(x-2+a*4,y+3,z-3+b*6,0.35F,b == 0 ? Facing.NORTH_NEGZ : Facing.SOUTH_POSZ,rand);
				}
			}
			
			int zOffset = isRoomEven ? 3 : -3;
			for(int yy = y+2; yy <= y+3; yy++)structure.setBlock(x,yy,z+zOffset,Blocks.nether_brick_fence);
			structure.setBlock(x,y+4,z+zOffset,Blocks.hopper,0);
		}
		/*
		 * one chest with torches on nether fences in corners
		 */
		else if (n == 3){
			spawnChest(x,y,z,false,17,21,isRoomEven ? Facing.NORTH_NEGZ : Facing.SOUTH_POSZ);
			structure.setBlock(x,y+2,z,BlockList.obsidian_special_glow,2,true);
			spawnEndermanSpawner(x,y+3,z,7);
			
			for(int a = 0; a < 2; a++){
				structure.setBlock(x-1+a*2,y+3,z,Blocks.obsidian,0);
				structure.setBlock(x,y+3,z-1+a*2,Blocks.obsidian,0);
				
				structure.setBlock(x-2+a*4,y+4,z,Blocks.obsidian,0);
				structure.setBlock(x,y+4,z-2+a*4,Blocks.obsidian,0);
				
				for(int b = 0; b < 2; b++){
					structure.setBlock(x-1+a*2,y+4,z-1+b*2,Blocks.obsidian,0);
				
					structure.setBlock(x-3+a*6,y,z-2+b*4,Blocks.nether_brick_fence,0);
					structure.setBlock(x-3+a*6,y+1,z-2+b*4,Blocks.torch,5);
					
					structure.setBlock(x-2+a*4,y,z-3+b*6,Blocks.nether_brick_fence,0);
					structure.setBlock(x-2+a*4,y+1,z-3+b*6,Blocks.torch,5);
				}
			}
		}
	}
	
	/*
	 * SPAWN HELPERS
	 */
	
	private final int[] potionData = new int[]{
		0, 16, 8193, 8257, 8225, 8194, 8258, 8226, 8195, 8259, 8227, 8197, 8261, 8229, 8198, 8262, 8201,
		8265, 8233, 8206, 8270, 8196, 8260, 8228, 8200, 8264, 8232, 8202, 8266, 8234, 8204, 8268, 8236
	};
	
	private void spawnBrewingStand(int x, int y, int z, Random rand){
		structure.setBlock(x,y,z,rand.nextInt(100) == 0 ? BlockList.enhanced_brewing_stand : Blocks.brewing_stand,0);
		structure.setTileEntityGenerator(x,y,z,"BrewingStand",this);
	}
	
	private void spawnChest(int x, int y, int z, boolean isTrapped, int minItems, int maxItems, Facing facing){
		structure.setBlock(x,y,z,isTrapped ? Blocks.trapped_chest : Blocks.chest,getMetadataWithOffset(Blocks.chest,facing.get6Directional()));
		structure.setTileEntityGenerator(x,y,z,"Chest:"+minItems+":"+maxItems,this);
	}
	
	private void spawnDispenser(int x, int y, int z, int minItems, int maxItems, Facing facing){
		int meta = facing.get6Directional();
		if (facing != Facing.DOWN && facing != Facing.UP)meta = getMetadataWithOffset(Blocks.dispenser,meta);
		
		structure.setBlock(x,y,z,Blocks.dispenser,meta);
		structure.setTileEntityGenerator(x,y,z,"Dispenser:"+minItems+":"+maxItems+":"+meta,this);
	}
	
	private void spawnFurnace(int x, int y, int z, float fuelChance, Facing facing, Random rand){
		structure.setBlock(x,y,z,Blocks.furnace,getMetadataWithOffset(Blocks.pumpkin,facing.get6Directional()));
		if (rand.nextFloat() < fuelChance)structure.setTileEntityGenerator(x,y,z,"Furnace",this);
	}
	
	private void spawnEndermanHead(int x, int y, int z, Facing facing){
		structure.setBlock(x,y,z,BlockList.enderman_head,getMetadataWithOffset(Blocks.skull,facing.getSkull())); // may be broken
		structure.setTileEntityGenerator(x,y,z,"Skull",this);
	}
	
	private void spawnEndermanHead(int x, int y, int z, int pointingAtX, int pointingAtZ){
		structure.setBlock(x,y,z,BlockList.enderman_head,1);
		structure.setTileEntityGenerator(x,y,z,"Head:"+((byte)Math.floor(90D+MathUtil.toDeg(Math.atan2(pointingAtZ-z,pointingAtX-x))/22.5D)),this);
	}
	
	private void spawnEndermanSpawner(int x, int y, int z, int difficulty){
		structure.setBlock(x,y,z,BlockList.custom_spawner,0);
		structure.setTileEntityGenerator(x,y,z,"Spawner:"+difficulty,this);
	}
	
	private void spawnAnvil(int x, int y, int z, Facing facing, Random rand){
		int meta = facing.getAnvil();
		if (coordBaseMode == 1 || coordBaseMode == 3)meta = 1-meta;
		
		float broken = rand.nextFloat();
		if (broken < 0.25F)meta |= 0x8;
		else if (broken < 0.7F)meta |= 0x4;
		
		structure.setBlock(x,y,z,Blocks.anvil,meta);
	}
	
	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		if (key.startsWith("Chest:")){
			String[] split = StringUtils.split(key,":",3);
			int minItems = DragonUtil.tryParse(split[1],0), maxItems = DragonUtil.tryParse(split[2],0);
			
			TileEntityChest chest = (TileEntityChest)tile;
			for(int a = 0; a < rand.nextInt(maxItems-minItems+1)+minItems; a++)chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),lootTower.generateIS(rand));
		}
		else if (key.startsWith("Dispenser:")){
			String[] split = StringUtils.split(key,":",4);
			int minItems = DragonUtil.tryParse(split[1],0), maxItems = DragonUtil.tryParse(split[2],0);
			
			TileEntityDispenser dispenser = (TileEntityDispenser)tile;
			for(int a = 0; a < rand.nextInt(maxItems-minItems+1)+minItems; a++)dispenser.setInventorySlotContents(rand.nextInt(dispenser.getSizeInventory()),lootTower.generateIS(rand));
			
			tile.blockMetadata = DragonUtil.tryParse(split[3],0);
		}
		else if (key.startsWith("Spawner:")){
			int minY = tile.yCoord+32, difficulty = DragonUtil.tryParse(StringUtils.split(key,":",2)[1],0);
			
			List<Potion> availablePotions = CollectionUtil.newList(new Potion[]{
				Potion.damageBoost, /*Potion.invisibility, */Potion.moveSpeed, Potion.regeneration, Potion.resistance, Potion.fireResistance
			});
			
			List<PotionEffect> effects = new ArrayList<>();
			int amount = (int)Math.round(difficulty/2.7D)+rand.nextInt(1+MathUtil.ceil(difficulty/3D));
			
			for(int a = 0; a < amount; a++){
				Potion potion = availablePotions.get(rand.nextInt(availablePotions.size()));
				effects.add(new PotionEffect(potion.id,12000,Math.min(3,MathUtil.floor(difficulty/4.4D+rand.nextDouble()*(difficulty/3.8D)))));
				
				availablePotions.remove(potion);
				if (availablePotions.isEmpty())break;
			}
			
			MobSpawnerBaseLogic logic = ((TileEntityCustomSpawner)tile).getSpawnerLogic();
			if (logic instanceof TowerEndermanSpawnerLogic)((TowerEndermanSpawnerLogic)logic).setSpawnEffects(effects).setTestingY(minY,minY+roomHeight-2);
		}
		else if (key.startsWith("Head:")){
			((TileEntityEndermanHead)tile).setRotation(DragonUtil.tryParse(StringUtils.split(key,":",2)[1],0));
		}
		else if (key.equals("Head")){
			tile.blockMetadata = -1;
			tile.getBlockMetadata();
		}
		else if (key.equals("Furnace")){
			((TileEntityFurnace)tile).setInventorySlotContents(1,lootFuel.generateIS(rand));
		}
		else if (key.startsWith("BrewingStand")){
			TileEntityBrewingStand brewingStand = (TileEntityBrewingStand)tile;
			
			TByteArrayList slots = new TByteArrayList(3);
			for(byte a = 0; a < 3; a++)slots.add(a);
			
			float r = rand.nextFloat();
			int fill = r > 0.9F ? 3 : r > 0.6F ? 2 : r > 0.25F ? 1 : 0;
			
			for(int a = 0; a < fill; a++){
				byte slot = slots.get(rand.nextInt(slots.size()));
				slots.remove(slot);
				
				int data = potionData[rand.nextInt(potionData.length)];
				brewingStand.setInventorySlotContents(slot,new ItemStack(Items.potionitem,1,data > 16 && rand.nextInt(5) == 0 ? data|16384 : data));
			}
		}
	}
}