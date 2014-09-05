package chylex.hee.world.structure.tower;
import gnu.trove.list.array.TByteArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.item.ItemKnowledgeFragment;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.mechanics.knowledge.util.FragmentWeightLists;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.TileEntityEndermanHead;
import chylex.hee.tileentity.spawner.TowerEndermanSpawnerLogic;
import chylex.hee.world.loot.IItemPostProcessor;
import chylex.hee.world.loot.ItemUtil;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.ComponentScatteredFeatureCustom;
import chylex.hee.world.structure.util.Facing;
import chylex.hee.world.structure.util.Offsets;

public class ComponentScatteredFeatureTower extends ComponentScatteredFeatureCustom{
	private static final Random spawnerRand = new Random();
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
		new LootItemStack(ItemList.knowledge_fragment).setWeight(122),
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
				List<EnderPearlEnhancements> availableTypes = new ArrayList<>(Arrays.asList(EnderPearlEnhancements.values()));
				
				for(int a = 0; a < 1+Math.abs(Math.round(rand.nextDouble()*rand.nextGaussian()*2.75D)); a++){
					is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
					if (availableTypes.isEmpty())break;
				}
			}
			else if (is.getItem() == Item.getItemFromBlock(BlockList.enhanced_tnt)){
				List<TNTEnhancements> availableTypes = new ArrayList<>(Arrays.asList(TNTEnhancements.values()));
				
				for(int a = 0; a < 1+rand.nextInt(2)+Math.round(rand.nextDouble()*2D); a++){
					is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
					if (availableTypes.isEmpty())break;
				}
			}
			else if (is.getItem() == ItemList.knowledge_fragment){
				ItemKnowledgeFragment.setRandomFragment(is,rand);
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
	
	private int startX,startZ;
	private byte lastRoomUsed = -1;
	
	/**
	 * Required for reflection.
	 */
	public ComponentScatteredFeatureTower(){}
	
	protected ComponentScatteredFeatureTower(Random rand, int x, int z){
		super(rand,x,32,z,32,140,32);
		startX = x;
		startZ = z;
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox bb){
		Random consistentRandom = new Random(((startX/14)*185226L+(startZ/14)*24071632895L)^world.getWorldInfo().getSeed());
		
		int centerX = sizeX/2, centerZ = sizeZ/2, islandY = 32+consistentRandom.nextInt(40), roomAmount = 4; // room amount maybe random?
		
		// ISLAND
		
		float rad = rand.nextFloat()*3.5F+9F;
		int ceilRad = (int)Math.ceil(rad);
		
		for(int a = centerX-ceilRad; a <= centerX+ceilRad; a++){
			for(int b = centerX-ceilRad; b <= centerX+ceilRad; b++){
				double distFromCenter = MathUtil.distance(a-centerX,b-centerX);
				
				if (distFromCenter > rad-rand.nextFloat()*1.1F+rand.nextFloat()*0.5F)continue;
				
				double distFactor = ceilRad*1.15D-distFromCenter;
				
				for(int spikeY = 0; spikeY <= distFactor*0.45D*Math.sqrt(distFactor*0.5D)+rand.nextDouble()*1.5D*distFactor; spikeY++){
					placeBlockAtCurrentPosition(world,Blocks.end_stone,0,a,islandY-spikeY,b,bb);
				}
			}
		}
		
		// WALL LAYOUT
		
		int topY = islandY+roomHeight*roomAmount;
		
		for(int a = 0; a < 2; a++){
			fillWithBlocks(world,bb,centerX-4+8*a,islandY,centerZ-2,centerX-4+8*a,topY,centerZ+2,BlockList.obsidian_special,BlockList.obsidian_special,false);
			fillWithBlocks(world,bb,centerX-2,islandY,centerZ-4+8*a,centerX+2,topY,centerZ-4+8*a,BlockList.obsidian_special,BlockList.obsidian_special,false);
			for(int b = 0; b < 2; b++){
				fillWithBlocks(world,bb,centerX-3+6*a,islandY,centerZ-3+6*b,centerX-3+6*a,topY,centerZ-3+6*b,BlockList.obsidian_special,BlockList.obsidian_special,false);
			}
		}
		
		// ROOM CONTENT
		
		for(int a = 0, ladderMeta, yy; a < roomAmount; a++){
			yy = islandY+roomHeight*a;
			ladderMeta = getMetadataWithOffset(Blocks.ladder,a%2 == 0?2:3);
			fillWithMetadataBlocks(world,bb,centerX,yy+1,centerZ-3+6*(a%2),centerX,yy+6,centerZ-3+6*(a%2),Blocks.ladder,ladderMeta,Blocks.ladder,ladderMeta,false);
			
			generateWallDecorations(world,bb,centerX,yy,centerZ);
			generateFloor(world,bb,centerX,yy,centerZ,a);
			
			if (a < roomAmount-1)generateBasicRoom(world,rand,bb,centerX,yy+1,centerZ,a);
			else generateChestRoom(world,rand,bb,centerX,yy+1,centerZ,a);
		}
		
		// ENTRANCE
		
		fillWithBlocks(world,bb,centerX-1,islandY+1,centerZ+4,centerX+1,islandY+5,centerZ+4,Blocks.air,Blocks.air,false);
		fillWithBlocks(world,bb,centerX-2,islandY+1,centerZ+5,centerX+2,islandY+5,centerZ+5,Blocks.air,Blocks.air,false);
		
		fillWithBlocks(world,bb,centerX-1,islandY,centerZ+3,centerX+1,islandY,centerZ+5,Blocks.obsidian,Blocks.obsidian,false);
		fillWithMetadataBlocks(world,bb,centerX-1,islandY+5,centerZ+4,centerX+1,islandY+5,centerZ+4,BlockList.obsidian_special,5,BlockList.obsidian_special,5,false);
		for(int a = 0; a < 2; a++){
			fillWithMetadataBlocks(world,bb,centerX-2+4*a,islandY,centerZ+5,centerX-2+4*a,islandY+5,centerZ+5,BlockList.obsidian_special,2,BlockList.obsidian_special,2,false);
			placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,a == 0?1:0),centerX-1+2*a,islandY+5,centerZ+5,bb);
		}
		
		// TOP AREA
		
		fillWithBlocks(world,bb,centerX-3,topY,centerZ-2,centerX+3,topY,centerZ+2,Blocks.obsidian,Blocks.obsidian,false);
		for(int a = 0; a < 2; a++){
			fillWithBlocks(world,bb,centerX-2,topY,centerZ-3+6*a,centerX+2,topY,centerZ-3+6*a,Blocks.obsidian,Blocks.obsidian,false);
			
			fillWithBlocks(world,bb,centerX-5+10*a,topY-2,centerZ,centerX-5+10*a,topY+5,centerZ,Blocks.obsidian,Blocks.obsidian,false);
			fillWithBlocks(world,bb,centerX-6+12*a,topY-1,centerZ,centerX-6+12*a,topY+8,centerZ,Blocks.obsidian,Blocks.obsidian,false);
			fillWithBlocks(world,bb,centerX-7+14*a,topY,centerZ,centerX-7+14*a,topY+10,centerZ,Blocks.obsidian,Blocks.obsidian,false);
			fillWithBlocks(world,bb,centerX-8+16*a,topY+3,centerZ,centerX-8+16*a,topY+12,centerZ,Blocks.obsidian,Blocks.obsidian,false);
			for(int b = 0; b < 2; b++){
				fillWithBlocks(world,bb,centerX-5+10*a,topY-1,centerZ-1+2*b,centerX-5+10*a,topY,centerZ-1+2*b,Blocks.obsidian,Blocks.obsidian,false);
				fillWithBlocks(world,bb,centerX-6+12*a,topY,centerZ-1+2*b,centerX-6+12*a,topY+4,centerZ-1+2*b,Blocks.obsidian,Blocks.obsidian,false);
				fillWithBlocks(world,bb,centerX-7+14*a,topY+4,centerZ-1+2*b,centerX-7+14*a,topY+8,centerZ-1+2*b,Blocks.obsidian,Blocks.obsidian,false);
			}
			placeBlockAndUpdate(Blocks.glowstone,0,centerX-8+16*a,topY+13,centerZ,world,bb);
			
			fillWithBlocks(world,bb,centerX,topY-2,centerZ-5+10*a,centerX,topY+5,centerZ-5+10*a,Blocks.obsidian,Blocks.obsidian,false);
			fillWithBlocks(world,bb,centerX,topY-1,centerZ-6+12*a,centerX,topY+8,centerZ-6+12*a,Blocks.obsidian,Blocks.obsidian,false);
			fillWithBlocks(world,bb,centerX,topY,centerZ-7+14*a,centerX,topY+10,centerZ-7+14*a,Blocks.obsidian,Blocks.obsidian,false);
			fillWithBlocks(world,bb,centerX,topY+3,centerZ-8+16*a,centerX,topY+12,centerZ-8+16*a,Blocks.obsidian,Blocks.obsidian,false);
			for(int b = 0; b < 2; b++){
				fillWithBlocks(world,bb,centerX-1+2*b,topY-1,centerZ-5+10*a,centerX-1+2*b,topY,centerZ-5+10*a,Blocks.obsidian,Blocks.obsidian,false);
				fillWithBlocks(world,bb,centerX-1+2*b,topY,centerZ-6+12*a,centerX-1+2*b,topY+4,centerZ-6+12*a,Blocks.obsidian,Blocks.obsidian,false);
				fillWithBlocks(world,bb,centerX-1+2*b,topY+4,centerZ-7+14*a,centerX-1+2*b,topY+8,centerZ-7+14*a,Blocks.obsidian,Blocks.obsidian,false);
			}
			placeBlockAndUpdate(Blocks.glowstone,0,centerX,topY+13,centerZ-8+16*a,world,bb);
		}
		
		int zOffset = roomAmount%2 == 1 ? 2 : -2;
		placeBlockAtCurrentPosition(world,Blocks.ladder,getMetadataWithOffset(Blocks.ladder,roomAmount%2 == 0?3:2),centerX,topY,centerZ+(roomAmount%2 == 1?-3:3),bb);
		placeBlockAndUpdate(BlockList.obsidian_special_glow,1,centerX,topY+1,centerZ+zOffset,world,bb);
		
		int xx = getXWithOffset(centerX,centerZ+zOffset), yy = getYWithOffset(topY+1), zz = getZWithOffset(centerX,centerZ+zOffset);
		if (bb.isVecInside(xx,yy,zz)){
			EntityMiniBossEnderEye eye = new EntityMiniBossEnderEye(world,xx+0.5D,yy+0.825D,zz+0.5D);
			eye.setPositionAndRotation(eye.posX,eye.posY,eye.posZ,90*coordBaseMode,0);
			world.spawnEntityInWorld(eye);
		}
		
		return true;
	}
	
	private void generateWallDecorations(World world, StructureBoundingBox bb, int x, int y, int z){
		for(int a = 0; a < 2; a++){
			// outline
			fillWithMetadataBlocks(world,bb,x-5+10*a,y+6,z-2,x-5+10*a,y+6,z+2,BlockList.obsidian_special,1,BlockList.obsidian_special,1,false);
			fillWithMetadataBlocks(world,bb,x-2,y+6,z-5+10*a,x+2,y+6,z-5+10*a,BlockList.obsidian_special,1,BlockList.obsidian_special,1,false);
			for(int b = 0; b < 2; b++){
				fillWithMetadataBlocks(world,bb,x-4+8*a,y+6,z-3+6*b,x-4+8*a,y+6,z-3+6*b,BlockList.obsidian_special,1,BlockList.obsidian_special,1,false);
				fillWithMetadataBlocks(world,bb,x-3+6*a,y+6,z-4+8*b,x-3+6*a,y+6,z-4+8*b,BlockList.obsidian_special,1,BlockList.obsidian_special,1,false);
			}
			
			// pillars
			fillWithMetadataBlocks(world,bb,x-5+10*a,y+1,z,x-5+10*a,y+5,z,BlockList.obsidian_special,2,BlockList.obsidian_special,2,false);
			fillWithMetadataBlocks(world,bb,x,y+1,z-5+10*a,x,y+5,z-5+10*a,BlockList.obsidian_special,2,BlockList.obsidian_special,2,false);
			
			// obsidian blocks and stairs + glowing obsidian
			for(int b = 0; b < 2; b++){
				placeBlockAndUpdate(BlockList.obsidian_special_glow,0,x-4+8*a,y+3,z-1+2*b,world,bb);
				placeBlockAndUpdate(BlockList.obsidian_special_glow,0,x-1+2*b,y+3,z-4+8*a,world,bb);
				
				for(int c = 0; c < 2; c++){
					placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x-5+10*a,y+1+4*c,z-1+2*b,bb);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0?3:2),x-5+10*a,y+1+4*c,z-2+4*b,bb);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0?3:2),x-5+10*a,y+2+2*c,z-1+2*b,bb);
					
					placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x-1+2*b,y+1+4*c,z-5+10*a,bb);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0?0:1),x-2+4*b,y+1+4*c,z-5+10*a,bb);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,b == 0?0:1),x-1+2*b,y+2+2*c,z-5+10*a,bb);
				}
			}
		}
	}
	
	private void generateFloor(World world, StructureBoundingBox bb, int x, int y, int z, int roomNb){
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++){
				// 2x2 obsidian blocks on sides
				fillWithBlocks(world,bb,x-2+a*3,y,z-3+b*4,x-1+a*3,y,z-2+b*5,Blocks.obsidian,Blocks.obsidian,false);
				// 2x1 smooth obsidian blocks next to them
				fillWithBlocks(world,bb,x-2+a*3,y,z-1+b*2,x-1+a*3,y,z-1+b*2,BlockList.obsidian_special,BlockList.obsidian_special,false);
				// 1x1 smooth obsidian corners
				fillWithBlocks(world,bb,x-3+a*6,y,z-2+b*4,x-3+a*6,y,z-2+b*4,BlockList.obsidian_special,BlockList.obsidian_special,false);
			}
			// 1x2 smooth obsidian blocks in center [part 1]
			placeBlockAtCurrentPosition(world,BlockList.obsidian_special,0,x,y,z-2+a*4,bb);
			// 1x3 obsidian lines on sides
			fillWithBlocks(world,bb,x-3+a*6,y,z-1,x-3+a*6,y,z+1,Blocks.obsidian,Blocks.obsidian,false);
			// 2x1 obsidian lines in center
			fillWithBlocks(world,bb,x-2+a*3,y,z,x-1+a*3,y,z,Blocks.obsidian,Blocks.obsidian,false);
			// 1x1 obsidian dots in center
			fillWithBlocks(world,bb,x,y,z-1+a*2,x,y,z+a,Blocks.obsidian,Blocks.obsidian,false);
		}
		placeBlockAtCurrentPosition(world,BlockList.obsidian_special,6,x,y,z,bb);
		// 1x2 smooth obsidian blocks in center [part 2]
		if (roomNb%2 == 0)placeBlockAtCurrentPosition(world,BlockList.obsidian_special,0,x,y,z-3,bb);
		else placeBlockAtCurrentPosition(world,BlockList.obsidian_special,0,x,y,z+3,bb);
	}
	
	private void generateBasicRoom(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, int roomNb){
		int n = roomNb == 0 ? rand.nextInt(8)+1 : rand.nextInt(9)+1; // lazy to prevent 9 from being the first room
		boolean isRoomEven = roomNb%2 == 0;
		
		if (n == lastRoomUsed)n = rand.nextInt(9)+1;
		lastRoomUsed = (byte)n;
		
		/*
		 * bookshelves on sides and ench. table in the center
		 */
		if (n == 1){
			placeBlockAtCurrentPosition(world,Blocks.enchanting_table,0,x,y,z,bb);
			int flowerType = rand.nextInt(3) == 0?15:rand.nextInt(5);

			for(int a = 0; a < 2; a++){
				placeBlockAtCurrentPosition(world,BlockList.death_flower_pot,flowerType,x-3+a*6,y+4,z,bb);
				placeBlockAtCurrentPosition(world,BlockList.obsidian_special,0,x-2+a*4,y,z+(isRoomEven?3:-3),bb);
				spawnBrewingStand(world,rand,bb,x-2+a*4,y+1,z+(isRoomEven?3:-3));
				
				for(int b = 0; b < 2; b++){
					fillWithBlocks(world,bb,x-3+a*6,y,z-1+b*2,x-3+a*6,y+3,z-1+b*2,Blocks.bookshelf,Blocks.bookshelf,false);
					spawnEndermanHead(world,rand,bb,x-3+a*6,y+4,z-2+b*4,x-1+a*2,z);
					
					for(int c = 0; c < 2; c++)placeBlockAtCurrentPosition(world,Blocks.oak_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(b == 1?Facing.NORTH_NEGZ:Facing.SOUTH_POSZ).getStairs()),x-3+a*6,y+c*3,z+2-b*4,bb);
				}
				
				for(int b = 0; b < 4; b++)placeBlockAtCurrentPosition(world,Blocks.oak_stairs,(b%2 == 1?4:0)+getMetadataWithOffset(Blocks.stone_stairs,(a == 1?Facing.EAST_POSX:Facing.WEST_NEGX).getStairs()),x-3+a*6,y+b,z,bb);
			}
		}
		/*
		 * heads on dispensers with redstone on floor and spawner in the center
		 */
		else if (n == 2){
			spawnEndermanSpawner(world,rand,bb,x,y,z,2+roomNb,32+y);
			
			for(int a = 0; a < 2; a++){
				spawnDispenser(world,rand,bb,x-3+a*6,y,z,0,2,Facing.UP);
				spawnEndermanHead(world,rand,bb,x-3+a*6,y+1,z,x,z);
			}
			
			for(int a = 0,xx,zz; a < 6+rand.nextInt(10); a++){
				xx = x+rand.nextInt(6)-3;
				zz = z+rand.nextInt(6)-3;
				if (getBlockAtCurrentPosition(world,xx,y,zz,bb).getMaterial() == Material.air && getBlockAtCurrentPosition(world,xx,y-1,zz,bb).isNormalCube(world,xx,y-1,zz)){
					placeBlockAtCurrentPosition(world,Blocks.redstone_wire,0,xx,y,zz,bb);
				}
			}
		}
		/*
		 * heads on stairs, flower in center, spawners on sides
		 */
		else if (n == 3){
			spawnDispenser(world,rand,bb,x,y,z,0,2,Facing.UP);
			placeBlockAtCurrentPosition(world,BlockList.death_flower_pot,rand.nextInt(3),x,y+1,z,bb);
			
			for(int a = 0; a < 2; a++){
				spawnEndermanSpawner(world,rand,bb,x-3+a*6,y,z,1+roomNb,32+y);
				
				for(int b = 0; b < 2; b++){
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 1?Facing.EAST_POSX:Facing.WEST_NEGX).getStairs()),x-3+a*6,y+2,z-2+b*4,bb);
					spawnEndermanHead(world,rand,bb,x-3+a*6,y+3,z-2+b*4,x,z-2+b*4);
				}
			}
		}
		/*
		 * heads on dispensers, spawners between stairs in corners
		 */
		else if (n == 4){
			for(int a = 0; a < 2; a++){
				spawnDispenser(world,rand,bb,x-3+a*6,y,z,0,2,Facing.UP);
				spawnEndermanHead(world,rand,bb,x-3+a*6,y+1,z,x,z);
				
				for(int b = 0; b < 2; b++){
					spawnEndermanSpawner(world,rand,bb,x-3+a*6,y+2,z-2+b*4,1+roomNb,32+y);
					spawnEndermanSpawner(world,rand,bb,x-2+a*4,y+2,z-3+b*6,1+roomNb,32+y);
					
					for(int c = 0; c < 2; c++){
						for(int d = 0; d < 2; d++){
							placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-3+a*6,y+c+3*d,z-2+b*4,bb);
							placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-2+a*4,y+c+3*d,z-3+b*6,bb);
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
				placeBlockAtCurrentPosition(world,Blocks.double_stone_slab,0,x-3+a*6,y,z,bb);
				spawnEndermanHead(world,rand,bb,x-3+a*6,y+1,z,x,z);
				
				for(int b = 0; b < 2; b++){
					spawnAnvil(world,rand,bb,x-3+a*6,y,z-1+b*2,Facing.NORTH_NEGZ);
					
					placeBlockAtCurrentPosition(world,Blocks.double_stone_slab,0,x-3+a*6,y,z-2+b*4,bb);
					placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 1?Facing.EAST_POSX:Facing.WEST_NEGX).getStairs()),x-3+a*6,y+1,z-2+b*4,bb);
					fillWithBlocks(world,bb,x-3+a*6,y+2,z-2+b*4,x-3+a*6,y+3,z-2+b*4,Blocks.nether_brick_fence,Blocks.nether_brick_fence,false);
					spawnEndermanSpawner(world,rand,bb,x-3+a*6,y+4,z-2+b*4,1+roomNb,32+y);
					
					placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,getMetadataWithOffset(Blocks.stone_stairs,(a == 1?Facing.EAST_POSX:Facing.WEST_NEGX).getStairs()),x-1+a*2,y,z-3+b*6,bb);
					placeBlockAtCurrentPosition(world,Blocks.bookshelf,0,x-2+a*4,y,z-3+b*6,bb);
					spawnBrewingStand(world,rand,bb,x-2+a*4,y+1,z-3+b*6);
					
				}
			}
		}
		/*
		 * flowers in corners, iron bars with spawner on ceiling
		 */
		else if (n == 6){
			spawnEndermanSpawner(world,rand,bb,x,y+4,z,2+roomNb,32+y);
			
			for(int a = 0; a < 2; a++){
				for(int b = 0; b < 2; b++){
					placeBlockAtCurrentPosition(world,Blocks.nether_brick_stairs,b*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 1?Facing.EAST_POSX:Facing.WEST_NEGX).getStairs()),x-3+a*6,y+b,z,bb);
					spawnDispenser(world,rand,bb,x-3+a*6,y,z-2+b*4,0,1,Facing.UP);
					placeBlockAtCurrentPosition(world,BlockList.death_flower_pot,rand.nextInt(5),x-3+a*6,y+1,z-2+b*4,bb);
				}
				
				placeBlockAtCurrentPosition(world,BlockList.obsidian_special,1,x-3+a*6,y+4,z,bb);
				fillWithBlocks(world,bb,x-3+a*6,y+2,z,x-3+a*6,y+3,z,Blocks.iron_bars,Blocks.iron_bars,false);
				fillWithBlocks(world,bb,x-2+a*4,y+3,z,x-2+a*4,y+4,z,Blocks.iron_bars,Blocks.iron_bars,false);
				placeBlockAtCurrentPosition(world,Blocks.iron_bars,0,x-1+a*2,y+5,z,bb);
			}
			
			/*int sign = isRoomEven?-1:1;
			
			placeBlockAtCurrentPosition(world,BlockList.obsidianSpecial,1,x,y+4,z-3*sign,bb);
			fillWithBlocks(world,bb,x,y+2,z-3*sign,x,y+3,z-3*sign,Blocks.iron_bars,Blocks.iron_bars,false);
			fillWithBlocks(world,bb,x,y+3,z-2*sign,x,y+4,z-2*sign,Blocks.iron_bars,Blocks.iron_bars,false);
			placeBlockAtCurrentPosition(world,Blocks.iron_bars,0,x,y+5,z-1*sign,bb);
			
			fillWithBlocks(world,bb,x,y+5,z+1*sign,x,y+4,z+2*sign,Blocks.iron_bars,Blocks.iron_bars,false);*/
			
			for(int a = 0,xx,zz; a < 5+rand.nextInt(8); a++){
				xx = rand.nextInt(6)-3;
				zz = rand.nextInt(6)-3;
				if (getBlockAtCurrentPosition(world,xx,y,zz,bb).getMaterial() == Material.air && getBlockAtCurrentPosition(world,xx,y-1,zz,bb).isNormalCube(world,xx,y-1,zz)){
					placeBlockAtCurrentPosition(world,Blocks.redstone_wire,0,xx,y,zz,bb);
				}
			}
		}
		/*
		 * beacons and shelves with stairs on sides
		 */
		else if (n == 7){
			int zOffset = isRoomEven ? -3 : 3;
			placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,Facing.SOUTH_POSZ.getStairs()),x,y+4,z-zOffset,bb);
			//fillWithBlocks(world,bb,x,y+2,z+_z,x,y+3,z+_z,Blocks.bookshelf,Blocks.bookshelf,false);
			
			for(int a = 0; a < 2; a++){
				fillWithBlocks(world,bb,x-3+a*6,y+1,z,x-3+a*6,y+3,z,Blocks.bookshelf,Blocks.bookshelf,false);
				
				for(int b = 0; b < 2; b++){
					placeBlockAtCurrentPosition(world,Blocks.bookshelf,0,x-3+a*6,y+2,z-1+b*2,bb);
					placeBlockAtCurrentPosition(world,Blocks.bookshelf,0,x-1+b*2,y+2,z-3+a*6,bb);
					
					spawnEndermanSpawner(world,rand,bb,x-3+a*6,y,z-2+b*4,1+roomNb,32+y);
					spawnEndermanSpawner(world,rand,bb,x-2+a*4,y,z-3+b*6,1+roomNb,32+y);
					
					spawnAnvil(world,rand,bb,x-2+a*4,y,z-2+b*4,Facing.EAST_POSX);
					placeBlockAtCurrentPosition(world,Blocks.lit_redstone_lamp,0,x-2+a*4,y+1,z-2+b*4,bb);
					placeBlockAtCurrentPosition(world,Blocks.stone_slab,0,x-2+a*4,y+2,z-2+b*4,bb);
					
					placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,b*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-3+a*6,y+b*4,z,bb);
					
					for(int c = 0; c < 2; c++){
						for(int d = 0; d < 2; d++){
							placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-3+a*6,y+c+3*d,z-1+b*2,bb);
							placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,c*4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-1+a*2,y+c+3*d,z-3+b*6,bb);
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
				fillWithMetadataBlocks(world,bb,x+xz[0],y,z+xz[1],x+xz[0],y+4,z+xz[1],BlockList.obsidian_special,2,BlockList.obsidian_special,2,false);
				if (rand.nextFloat() < 0.25F)spawnEndermanSpawner(world,rand,bb,x+xz[0],y+rand.nextInt(5),z+xz[1],1,32+y);
			}
			
			fillWithMetadataBlocks(world,bb,x+2,y,z-1,x+2,y+1,z-1,BlockList.obsidian_special,2,BlockList.obsidian_special,2,false);
			spawnEndermanSpawner(world,rand,bb,x+2,y+2,z-1,2+roomNb,32+y);
			placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.stone_stairs,Facing.SOUTH_POSZ.getStairs()),x+2,y+3,z-1,bb);
			
			for(int attempt = 0,placed = 0,xx,zz; attempt < 8 && placed < rand.nextInt(4); attempt++){
				xx = x+rand.nextInt(6)-3;
				zz = z+rand.nextInt(6)-3;
				if (getBlockAtCurrentPosition(world,xx,y,zz,bb).getMaterial() == Material.air){
					spawnEndermanSpawner(world,rand,bb,xx,y,zz,1,32+y);
					++placed;
				}
			}
			
			spawnChest(world,rand,bb,x+2,y,z-3,rand.nextBoolean(),3,5,Facing.SOUTH_POSZ);
		}
		/*
		 * ench. table in the center, bookshelves in corners, obsidian with spawners on sides
		 */
		else if (n == 9){
			placeBlockAtCurrentPosition(world,Blocks.enchanting_table,0,x,y,z,bb);
			fillWithBlocks(world,bb,x,y+3+(roomNb == 0?1:0),z+(isRoomEven?3:-3),x,y+4,z+(isRoomEven?3:-3),Blocks.obsidian,Blocks.obsidian,false);
			
			for(int a = 0; a < 2; a++){
				spawnEndermanSpawner(world,rand,bb,x-3+a*6,y+2,z,3+roomNb,32+y);
				placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x,y+4,z-2+a*4,bb);
				
				for(int b = 0; b < 2; b++){
					fillWithBlocks(world,bb,x-3+a*6,y+b*3,z,x-3+a*6,y+1+b*3,z,Blocks.obsidian,Blocks.obsidian,false);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,4*b+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-2+4*a,y+4*b,z,bb);
					
					fillWithBlocks(world,bb,x-3+a*6,y,z-2+b*4,x-3+a*6,y+4,z-2+b*4,Blocks.bookshelf,Blocks.bookshelf,false);
					fillWithBlocks(world,bb,x-2+a*4,y,z-3+b*6,x-2+a*4,y+4,z-3+b*6,Blocks.bookshelf,Blocks.bookshelf,false);
					
					placeBlockAtCurrentPosition(world,Blocks.oak_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-3+a*6,y,z-1+b*2,bb);
					placeBlockAtCurrentPosition(world,Blocks.oak_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-1+a*2,y,z-3+b*6,bb);
					for(int c = 0; c < 2; c++){
						if (rand.nextFloat() < 0.42F){
							int xx = c == 0?x-3+a*6:x-1+a*2,zz = c == 0?z-1+b*2:z-3+b*6;
							
							if (rand.nextBoolean())placeBlockAtCurrentPosition(world,BlockList.death_flower_pot,rand.nextInt(10) == 0?15:rand.nextInt(4),xx,y+1,zz,bb);
							else spawnBrewingStand(world,rand,bb,xx,y+1,zz);
						}
					}
				}
			}
		}
	}

	private void generateChestRoom(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, int roomNb){
		int n = rand.nextInt(3)+1;
		boolean isRoomEven = roomNb%2 == 0;
		
		/*
		 * chests between stairs with nether fences and hoppers
		 */
		if (n == 1){
			for(int a = 0; a < 2; a++){
				spawnChest(world,rand,bb,x-3+a*6,y,z,false,7,11,a == 0?Facing.WEST_NEGX:Facing.EAST_POSX);
				
				for(int b = 0; b < 2; b++){
					spawnEndermanSpawner(world,rand,bb,x-3+a*6,y,z-2+b*4,2,32+y);
					spawnEndermanSpawner(world,rand,bb,x-2+a*4,y,z-3+b*6,2,32+y);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_special,2,x-2+a*4,y,z-2+b*4,bb);
					
					spawnEndermanHead(world,rand,bb,x-2+a*4,y+1,z-2+b*4,x,z);
					
					fillWithBlocks(world,bb,x-3+a*6,y+2,z-2+b*4,x-3+a*6,y+3,z-2+b*4,Blocks.nether_brick_fence,Blocks.nether_brick_fence,false);
					placeBlockAtCurrentPosition(world,Blocks.hopper,0,x-3+a*6,y+4,z-2+b*4,bb);
					
					fillWithBlocks(world,bb,x-2+a*4,y+2,z-3+b*6,x-2+a*4,y+3,z-3+b*6,Blocks.nether_brick_fence,Blocks.nether_brick_fence,false);
					placeBlockAtCurrentPosition(world,Blocks.hopper,0,x-2+a*4,y+4,z-3+b*6,bb);
					
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-3+a*6,y,z-1+b*2,bb);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-3+a*6,y+1,z-2+b*4,bb);
					
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-1+b*2,y,z-3+a*6,bb);
					placeBlockAtCurrentPosition(world,BlockList.obsidian_stairs,getMetadataWithOffset(Blocks.oak_stairs,(b == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-2+b*4,y+1,z-3+a*6,bb);
				}
			}
		}
		/*
		 * lots of chests and furnaces with stair decorations
		 */
		else if (n == 2){
			for(int a = 0; a < 2; a++){
				spawnEndermanSpawner(world,rand,bb,x-3+a*6,y,z,6,32+y);
				fillWithBlocks(world,bb,x-3+a*6,y+1,z,x-3+a*6,y+3,z,Blocks.nether_brick_fence,Blocks.air,false);
				placeBlockAtCurrentPosition(world,Blocks.hopper,0,x-3+a*6,y+4,z,bb);
				
				for(int b = 0; b < 2; b++){
					for(int c = 0; c < 2; c++){
						spawnChest(world,rand,bb,x-3+a*6,y,z+(c+1)*(-1+b*2),true,0,rand.nextInt(5) <= 1?4:2,a == 0?Facing.EAST_POSX:Facing.WEST_NEGX);
						spawnChest(world,rand,bb,x+(c+1)*(-1+b*2),y,z-3+a*6,true,0,rand.nextInt(5) <= 1?4:2,a == 0?Facing.NORTH_NEGZ:Facing.SOUTH_POSZ);
						
						placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-3+a*6,y+2+c*2,z-1+b*2,bb);
						placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-3+a*6,y+2+c*2,z-2+b*4,bb);
						
						placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(b == 0?Facing.SOUTH_POSZ:Facing.NORTH_NEGZ).getStairs()),x-1+a*2,y+2+c*2,z-3+b*6,bb);
						placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-2+a*4,y+2+c*2,z-3+b*6,bb);
					}
					
					placeBlockAtCurrentPosition(world,Blocks.stone_brick_stairs,4+getMetadataWithOffset(Blocks.stone_stairs,(a == 0?Facing.WEST_NEGX:Facing.EAST_POSX).getStairs()),x-2+a*4,y+4,z-2+b*4,bb);
					
					spawnFurnace(world,rand,bb,x-3+a*6,y+3,z-2+b*4,0.35F,a == 0?Facing.EAST_POSX:Facing.WEST_NEGX);
					spawnFurnace(world,rand,bb,x-2+a*4,y+3,z-3+b*6,0.35F,b == 0?Facing.NORTH_NEGZ:Facing.SOUTH_POSZ);
				}
			}
			
			int zOffset = isRoomEven ? 3 : -3;
			fillWithBlocks(world,bb,x,y+2,z+zOffset,x,y+3,z+zOffset,Blocks.nether_brick_fence,Blocks.air,false);
			placeBlockAtCurrentPosition(world,Blocks.hopper,0,x,y+4,z+zOffset,bb);
		}
		/*
		 * one chest with torches on nether fences in corners
		 */
		else if (n == 3){
			spawnChest(world,rand,bb,x,y,z,false,17,21,isRoomEven?Facing.NORTH_NEGZ:Facing.SOUTH_POSZ);
			placeBlockAndUpdate(BlockList.obsidian_special_glow,2,x,y+2,z,world,bb);
			spawnEndermanSpawner(world,rand,bb,x,y+3,z,7,32+y);
			
			for(int a = 0; a < 2; a++){
				placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x-1+a*2,y+3,z,bb);
				placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x,y+3,z-1+a*2,bb);
				
				placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x-2+a*4,y+4,z,bb);
				placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x,y+4,z-2+a*4,bb);
				
				for(int b = 0; b < 2; b++){
					placeBlockAtCurrentPosition(world,Blocks.obsidian,0,x-1+a*2,y+4,z-1+b*2,bb);
				
					placeBlockAtCurrentPosition(world,Blocks.nether_brick_fence,0,x-3+a*6,y,z-2+b*4,bb);
					placeBlockAtCurrentPosition(world,Blocks.torch,5,x-3+a*6,y+1,z-2+b*4,bb);
					
					placeBlockAtCurrentPosition(world,Blocks.nether_brick_fence,0,x-2+a*4,y,z-3+b*6,bb);
					placeBlockAtCurrentPosition(world,Blocks.torch,5,x-2+a*4,y+1,z-3+b*6,bb);
				}
			}
		}
	}
	
	/*
	 * SPAWN HELPERS
	 */
	
	private Offsets getOffsets(int x, int y, int z, StructureBoundingBox bb){
		int xx = getXWithOffset(x,z), yy = getYWithOffset(y), zz = getZWithOffset(x,z);
		return new Offsets(xx,yy,zz,bb.isVecInside(xx,yy,zz));
	}
	
	private final int[] potionData = new int[]{
		0, 16, 8193, 8257, 8225, 8194, 8258, 8226, 8195, 8259, 8227, 8197, 8261, 8229, 8198, 8262, 8201,
		8265, 8233, 8206, 8270, 8196, 8260, 8228, 8200, 8264, 8232, 8202, 8266, 8234, 8204, 8268, 8236
	};
	
	private void spawnBrewingStand(World world, Random rand, StructureBoundingBox bb, int x, int y, int z){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		placeBlockAtCurrentPosition(world,rand.nextInt(100) == 0?BlockList.enhanced_brewing_stand:Blocks.brewing_stand,0,x,y,z,bb);
		
		float r = rand.nextFloat();
		int fill = r > 0.9F ? 3 : r > 0.6F ? 2 : r > 0.25F ? 1 : 0;
		if (fill == 0)return;
		
		TileEntityBrewingStand brewingStand = (TileEntityBrewingStand)world.getTileEntity(offsets.x,offsets.y,offsets.z);
		if (brewingStand == null)return;
		
		TByteArrayList slots = new TByteArrayList(3);
		for(byte a = 0; a < 3; a++)slots.add(a);
		
		for(int a = 0; a < fill; a++){
			byte slot = slots.get(rand.nextInt(slots.size()));
			slots.remove(slot);
			
			int data = potionData[rand.nextInt(potionData.length)];
			brewingStand.setInventorySlotContents(slot,new ItemStack(Items.potionitem,1,data > 16 && rand.nextInt(5) == 0 ? data|16384 : data));
		}
	}
	
	private void spawnChest(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, boolean isTrapped, int minItems, int maxItems, Facing facing){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		placeBlockAtCurrentPosition(world,isTrapped?Blocks.trapped_chest:Blocks.chest,getMetadataWithOffset(Blocks.chest,facing.get6Directional()),x,y,z,bb);
		
		TileEntityChest chest = (TileEntityChest)world.getTileEntity(offsets.x,offsets.y,offsets.z);
		if (chest == null)return;
		
		for(int a = 0; a < rand.nextInt(maxItems-minItems+1)+minItems; a++)chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),lootTower.generateIS(rand));
	}
	
	private void spawnDispenser(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, int minItems, int maxItems, Facing facing){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		int meta = facing.get6Directional();
		if (facing != Facing.DOWN && facing != Facing.UP)meta = getMetadataWithOffset(Blocks.dispenser,meta);
		
		placeBlockAtCurrentPosition(world,Blocks.dispenser,meta,x,y,z,bb);
		world.setBlockMetadataWithNotify(offsets.x,offsets.y,offsets.z,meta,2);
		
		TileEntityDispenser dispenser = (TileEntityDispenser)world.getTileEntity(offsets.x,offsets.y,offsets.z);
		if (dispenser == null)return;
		
		dispenser.blockMetadata = meta;
		for(int a = 0; a < rand.nextInt(maxItems-minItems+1)+minItems; a++)dispenser.setInventorySlotContents(rand.nextInt(dispenser.getSizeInventory()),lootTower.generateIS(rand));
	}
	
	private void spawnFurnace(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, float fuelChance, Facing facing){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		placeBlockAtCurrentPosition(world,Blocks.furnace,getMetadataWithOffset(Blocks.pumpkin,facing.get6Directional()),x,y,z,bb);
		
		if (rand.nextFloat() < fuelChance){
			TileEntityFurnace furnace = (TileEntityFurnace)world.getTileEntity(offsets.x,offsets.y,offsets.z);
			if (furnace != null)furnace.setInventorySlotContents(1,lootFuel.generateIS(rand));
		}
	}
	
	private void spawnEndermanHead(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, Facing facing){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		placeBlockAtCurrentPosition(world,BlockList.enderman_head,getMetadataWithOffset(Blocks.skull,facing.getSkull()),x,y,z,bb); // may be broken
		
		TileEntityEndermanHead head = (TileEntityEndermanHead)world.getTileEntity(offsets.x,offsets.y,offsets.z);
		if (head != null)head.setMeta(facing.getSkull());
	}
	
	private void spawnEndermanHead(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, int pointingAtX, int pointingAtZ){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		placeBlockAtCurrentPosition(world,BlockList.enderman_head,1,x,y,z,bb);
		Offsets pointOff = getOffsets(pointingAtX,y,pointingAtZ,bb);

		TileEntityEndermanHead head = (TileEntityEndermanHead)world.getTileEntity(offsets.x,offsets.y,offsets.z);
		if (head != null)head.setRotation((byte)Math.floor(90D+MathUtil.toDeg(Math.atan2(pointOff.z-offsets.z,pointOff.x-offsets.x))/22.5D));
	}
	
	private void spawnEndermanSpawner(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, int difficulty, int minY){
		Offsets offsets = getOffsets(x,y,z,bb);
		if (!offsets.isInsideBB)return;
		
		placeBlockAtCurrentPosition(world,BlockList.custom_spawner,0,x,y,z,bb);
		if (difficulty == 0)return;

		TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)world.getTileEntity(offsets.x,offsets.y,offsets.z);
		if (spawner != null){
			List<Potion> availablePotions = new ArrayList<>(Arrays.asList(new Potion[]{
				Potion.damageBoost, /*Potion.invisibility, */Potion.moveSpeed, Potion.regeneration, Potion.resistance, Potion.fireResistance
			}));
			
			spawnerRand.setSeed(minY*256L+x*341873128712L+z*132897987541L+world.getWorldInfo().getSeed()+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount());
			
			List<PotionEffect> effects = new ArrayList<>();
			int amount = (int)Math.round(difficulty/2.7D)+spawnerRand.nextInt(1+(int)Math.ceil(difficulty/3D));
			
			for(int a = 0; a < amount; a++){
				Potion potion = availablePotions.get(spawnerRand.nextInt(availablePotions.size()));
				effects.add(new PotionEffect(potion.id,12000,Math.min(3,(int)Math.floor(difficulty/4.4D+spawnerRand.nextDouble()*(difficulty/3.8D)))));
				
				availablePotions.remove(potion);
				if (availablePotions.isEmpty())break;
			}
			
			MobSpawnerBaseLogic logic = spawner.getSpawnerLogic();
			if (logic instanceof TowerEndermanSpawnerLogic)((TowerEndermanSpawnerLogic)logic).setSpawnEffects(effects).setTestingY(minY,minY+roomHeight-2);
		}
	}
	
	private void spawnAnvil(World world, Random rand, StructureBoundingBox bb, int x, int y, int z, Facing facing){
		int meta = facing.getAnvil();
		if (coordBaseMode == 1 || coordBaseMode == 3)meta = 1-meta;
		
		float broken = rand.nextFloat();
		if (broken < 0.25F)meta |= 0x8;
		else if (broken < 0.7F)meta |= 0x4;
		
		placeBlockAtCurrentPosition(world,Blocks.anvil,meta,x,y,z,bb);
	}

	@Override
	protected void func_143012_a(NBTTagCompound nbt){ // OBFUSCATED writeToNBT
		nbt.setInteger("startX",startX);
		nbt.setInteger("startZ",startZ);
		nbt.setByte("lastRoomUsed",lastRoomUsed);
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbt){ // OBFUSCATED readFromNBT
		startX = nbt.getInteger("startX");
		startZ = nbt.getInteger("startZ");
		lastRoomUsed = nbt.getByte("lastRoomUsed");
	}
}