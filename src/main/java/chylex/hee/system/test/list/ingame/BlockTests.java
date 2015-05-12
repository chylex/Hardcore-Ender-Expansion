package chylex.hee.system.test.list.ingame;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.block.BlockDragonEggCustom;
import chylex.hee.block.BlockObsidianEnd;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.block.BlockSacredStone;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.mechanics.enhancements.types.EnhancedBrewingStandEnhancements;
import chylex.hee.mechanics.enhancements.types.EssenceAltarEnhancements;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityAccumulationTable;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.tileentity.TileEntityExperienceTable;
import chylex.hee.tileentity.TileEntityExtractionTable;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockTests{
	private static final String testTrigger = "ingame/blocks";
	
	private World world;
	private EntityPlayer player;
	private BlockPosM pos;
	
	private ArrayListMultimap<String,BlockPosM> storedLocs = ArrayListMultimap.create();
	
	public void setup(){
		if (world != null)return;
		world = DimensionManager.getWorld(0);
		player = Minecraft.getMinecraft().thePlayer;
		pos = new BlockPosM();
		storedLocs.clear();
	}
	
	@UnitTest(type = MethodType.PREPARATION, runTime = RunTime.INGAME, trigger = testTrigger)
	public void prepBlockChunk(){
		setup();
		
		// clear area
		for(pos.x = 0; pos.x < 16; pos.x++){
			for(pos.z = 0; pos.z < 16; pos.z++){
				for(pos.y = 4; pos.y < 128; pos.y++){
					if (pos.getBlock(world) == BlockList.energy_cluster)((TileEntityEnergyCluster)pos.getTileEntity(world)).shouldNotExplode = true;
					pos.setAir(world);
				}
			}
		}
		
		for(List list:world.getChunkFromChunkCoords(0,0).entityLists){
			for(Entity e:(List<Entity>)list){
				if (!(e instanceof EntityPlayer))e.setDead();
			}
		}
		
		// first floor - building blocks
		setFloor(0,0,8,Blocks.stonebrick);
		setPos(0,0);
		
		setMove(BlockList.obsidian_falling);
		for(int a = 0; a < 4; a++)setMove(BlockList.obsidian_stairs,a);
		for(int a = 0; a < 4; a++)setMove(BlockList.obsidian_stairs,4+a);
		for(int a = 0; a < 6; a++)setMove(BlockList.obsidian_special,a);
		for(int a = 0; a < 6; a++)setMove(BlockList.obsidian_special_glow,a);
		for(int a = 0; a < 3; a++)setMove(BlockList.end_terrain,a);
		for(int a = 0; a < BlockRavagedBrick.metaAmount; a++)setMove(BlockList.ravaged_brick,a);
		setMove(BlockList.ravaged_brick_smooth);
		setMove(BlockList.ravaged_brick_glow);
		setMove(BlockList.ravaged_brick_slab);
		setMove(BlockList.ravaged_brick_slab,8);
		for(int a = 0; a < 4; a++)setMove(BlockList.ravaged_brick_stairs,a);
		for(int a = 0; a < 4; a++)setMove(BlockList.ravaged_brick_stairs,4+a);
		setMove(BlockList.ravaged_brick_fence);
		setMove(BlockList.cinder);
		for(int a = 0; a < 16; a++)setMove(BlockList.persegrit,a);
		setMove(BlockList.laboratory_obsidian);
		setMove(BlockList.laboratory_floor);
		for(int a = 0; a < 4; a++)setMove(BlockList.laboratory_stairs,a);
		for(int a = 0; a < 4; a++)setMove(BlockList.laboratory_stairs,4+a);
		for(int a = 0; a < BlockSacredStone.metaAmount; a++)setMove(BlockList.sacred_stone,a);
		setMove(BlockList.sphalerite,0);
		setMove(BlockList.endium_block);
		setMove(BlockList.laboratory_glass);
		
		// first floor - physics blocks
		setFloor(0,10,10,Blocks.end_stone);
		setPos(0,10);
		
		for(int a = 0; a < 8; a++){
			storedLocs.put("FallingBlockTarget",pos.copy());
			setMove(Blocks.torch);
			storedLocs.put("FallingBlockTarget",pos.copy());
			setMove(Blocks.stone_slab);
		}
		
		setPos(0,10);
		pos.moveUp().moveUp();
		
		for(int a = 0; a < 2; a++){
			storedLocs.put("FallingBlockObsidian",pos.copy());
			setMove(BlockList.obsidian_falling);
		}
		
		for(int a = 0; a < 2; a++){
			storedLocs.put("FallingBlockDragonEgg",pos.copy());
			setMove(Blocks.dragon_egg);
		}
		
		// first floor - special condition decorative blocks
		setFloor(0,12,15,Blocks.end_stone);
		setPos(0,12);
		
		for(int a = 2; a < 14; a++)setMove(BlockList.crossed_decoration,a);
		setMove(BlockList.enderman_head,1);
		setMove(BlockList.death_flower_pot,0);
		setMove(BlockList.death_flower,0);
		
		// second floor - ores
		setFloor(1,0,13,Blocks.stonebrick);
		setPos(1,0);
		
		for(int a = 0; a < 16; a++)setMove(BlockList.end_powder_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.endium_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.stardust_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.igneous_rock_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.instability_orb_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.sphalerite,1);
		
		// second floor - static functional blocks
		setFloor(1,15,15,Blocks.stonebrick);
		setPos(1,15);
		
		setMove(BlockList.transport_beacon);
		setMove(BlockList.void_chest);
		
		// third floor - essence altars
		setFloor(2,0,6,Blocks.end_stone);
		setPos(2,0);
		
		for(int run = 0; run < 2; run++){
			BlockPosM origin = pos.copy().setX(run == 0 ? 3 : 12).setZ(3);
			
			pos.set(origin).setBlock(world,BlockList.essence_altar,EssenceType.DRAGON.id);
			storedLocs.put("DragonEssenceAltar",pos.copy());
			getTile(TileEntityEssenceAltar.class).loadFromDamage(EssenceType.DRAGON.id);
			
			getTile(TileEntityEssenceAltar.class).getEnhancements().add(EssenceAltarEnhancements.SPEED);
			if (run == 1)getTile(TileEntityEssenceAltar.class).getEnhancements().add(EssenceAltarEnhancements.EFFICIENCY);
			
			try{
				Field essenceLevel = TileEntityEssenceAltar.class.getDeclaredField("essenceLevel");
				essenceLevel.setAccessible(true);
				essenceLevel.set(pos.getTileEntity(world),512);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
			
			pos.set(origin).move(-3,0,0).setBlock(world,Blocks.stonebrick);
			spawnItem(pos.x+0.5D,pos.y+1.25D,pos.z+0.5D,new ItemStack(Items.brewing_stand));
			pos.set(origin).move(0,0,-3).setBlock(world,Blocks.stonebrick);
			spawnItem(pos.x+0.5D,pos.y+1.25D,pos.z+0.5D,new ItemStack(Items.ender_eye));
			pos.set(origin).move(3,0,0).setBlock(world,Blocks.stonebrick);
			spawnItem(pos.x+0.5D,pos.y+1.25D,pos.z+0.5D,new ItemStack(ItemList.ghost_amulet,1,0));
			pos.set(origin).move(0,0,3).setBlock(world,Blocks.stonebrick);
			spawnItem(pos.x+0.5D,pos.y+1.25D,pos.z+0.5D,new ItemStack(Items.diamond_sword,1,50));
			pos.set(origin).move(2,0,2).setBlock(world,Blocks.stonebrick);
			pos.set(origin).move(-2,0,2).setBlock(world,Blocks.stonebrick);
			pos.set(origin).move(2,0,-2).setBlock(world,Blocks.stonebrick);
			pos.set(origin).move(-2,0,-2).setBlock(world,Blocks.stonebrick);
		}
		
		setFloor(2,9,15,Blocks.end_stone);
		setPos(2,0);
		
		pos.setX(3).setZ(12).setBlock(world,BlockList.essence_altar,EssenceType.FIERY.id);
		getTile(TileEntityEssenceAltar.class).loadFromDamage(EssenceType.FIERY.id);
		
		// fourth floor - enhanced brewing
		setFloor(3,0,3,Blocks.end_stone);
		setPos(3,0);
		
		List<Object[]> potions = new ArrayList<>();
		potions.add(new Object[]{ 0, Items.nether_wart, 0 });
		potions.add(new Object[]{ 16, Items.sugar, 0 });
		potions.add(new Object[]{ 8194, new ItemStack(Items.glowstone_dust,2), 7 }); // leave 1 powder
		potions.add(new Object[]{ 8194, new ItemStack(Items.glowstone_dust,3), 13, true }); // leave 1 powder
		potions.add(new Object[]{ 8194, new ItemStack(Items.redstone,8), 25 }); // leave 1 powder
		potions.add(new Object[]{ 8194, Items.gunpowder, 3 });
		potions.add(new Object[]{ 16, ItemList.silverfish_blood, 8 });
		potions.add(new Object[]{ 16, ItemList.instability_orb, 8 });
		potions.add(new Object[]{ 16, ItemList.ectoplasm, 8 });
		
		for(Object[] t:potions){
			storedLocs.put("EnhancedBrewingStand",pos.copy());
			set(BlockList.enhanced_brewing_stand);
			getTile(TileEntityEnhancedBrewingStand.class).setInventorySlotContents(1,new ItemStack(Items.potionitem,1,(Integer)t[0]));
			getTile(TileEntityEnhancedBrewingStand.class).setInventorySlotContents(3,t[1] instanceof Item ? new ItemStack((Item)t[1]) : (ItemStack)t[1]);
			if ((Integer)t[2] != 0)getTile(TileEntityEnhancedBrewingStand.class).setInventorySlotContents(4,new ItemStack(ItemList.end_powder,(Integer)t[2]));
			if (t.length > 3)getTile(TileEntityEnhancedBrewingStand.class).getEnhancements().add(EnhancedBrewingStandEnhancements.TIER);
			move();
		}
		
		// fourth floor - energy
		setFloor(3,5,15,Blocks.stonebrick);
		setPos(3,5);
		
		for(int a = -1; a <= 1; a++){
			for(int b = -1; b <= 1; b++){
				pos.setX(5+a).setZ(10+b).setBlock(world,BlockList.energy_cluster);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("status",(byte)0);
				tag.setFloat("lvl",10F);
				tag.setFloat("max",10F);
				getTile(TileEntityEnergyCluster.class).updateEntity();
				getTile(TileEntityEnergyCluster.class).data.readFromNBT(tag);
			}
		}
		
		int y = pos.y;
		
		// fourth floor - energy - decomposition table
		
		ItemStack[] decompositionTable = new ItemStack[]{
			new ItemStack(Items.diamond_chestplate),
			new ItemStack(Items.diamond_chestplate,1,528),
			new ItemStack(Blocks.planks,4),
			new ItemStack(ItemList.energy_wand),
			new ItemStack(BlockList.void_chest),
			new ItemStack(Blocks.beacon),
			new ItemStack(Blocks.bedrock)
		};
		
		for(int a = -3; a <= 3; a++){
			pos.setX(0).setY(y).setZ(10+a).setBlock(world,a%2 == 0 ? Blocks.chest : Blocks.trapped_chest);
			pos.moveUp().setBlock(world,Blocks.hopper);
			pos.moveUp().setBlock(world,BlockList.decomposition_table);
			getTile(TileEntityDecompositionTable.class).setInventorySlotContents(0,decompositionTable[a+3]);
			getTile(TileEntityDecompositionTable.class).setInventorySlotContents(1,new ItemStack(ItemList.stardust,64));
			storedLocs.put("DecompositionTable",pos.copy());
		}
		
		// fourth floor - energy - experience table
		
		ItemStack[] experienceTable = new ItemStack[]{
			new ItemStack(Blocks.diamond_ore),
			new ItemStack(Blocks.gold_block,3),
			new ItemStack(Items.redstone,64),
			new ItemStack(Items.dye,1,4),
			new ItemStack(ItemList.endium_ingot,17)
		};
		
		for(int a = -2; a <= 2; a++){
			pos.setX(10).setY(y).setZ(10+a).setBlock(world,a%2 == 0 ? Blocks.chest : Blocks.trapped_chest);
			pos.moveUp().setBlock(world,Blocks.hopper);
			pos.moveUp().setBlock(world,BlockList.experience_table);
			getTile(TileEntityExperienceTable.class).setInventorySlotContents(0,experienceTable[a+2]);
			getTile(TileEntityExperienceTable.class).setInventorySlotContents(1,new ItemStack(ItemList.stardust,64));
			storedLocs.put("ExperienceTable",pos.copy());
		}
		
		// fourth floor - energy - accumulation table
		
		ItemStack[] accumulationTable = new ItemStack[]{
			new ItemStack(ItemList.temple_caller),
			new ItemStack(ItemList.spatial_dash_gem,1,ItemList.spatial_dash_gem.getMaxDamage()-1),
			new ItemStack(ItemList.transference_gem,1,ItemList.transference_gem.getMaxDamage()-1)
		};
		
		for(int a = -1; a <= 1; a++){
			pos.setX(5+a).setY(y).setZ(5).setBlock(world,BlockList.accumulation_table);
			getTile(TileEntityAccumulationTable.class).setInventorySlotContents(0,accumulationTable[a+1]);
			storedLocs.put("AccumulationTable",pos.copy());
		}
		
		// fourth floor - energy - extraction table
		
		ItemStack[][] extractionTable = new ItemStack[][]{
			new ItemStack[]{
				new ItemStack(Blocks.end_stone,4),
				new ItemStack(BlockList.end_terrain,2,0),
				new ItemStack(BlockList.end_terrain,2,1),
				new ItemStack(BlockList.end_terrain,2,2)
			},
			new ItemStack[]{
				new ItemStack(ItemList.energy_wand),
				new ItemStack(ItemList.energy_wand),
				new ItemStack(ItemList.energy_wand)
			},
			new ItemStack[]{
				new ItemStack(BlockList.endium_block,64)
			}
		};
		
		for(int a = -1; a <= 1; a++){
			pos.setX(5+a).setY(y).setZ(15).setBlock(world,BlockList.extraction_table);
			getTile(TileEntityExtractionTable.class).setInventorySlotContents(1,new ItemStack(ItemList.stardust,64));
			getTile(TileEntityExtractionTable.class).setInventorySlotContents(2,new ItemStack(ItemList.instability_orb,16));
			storedLocs.put("ExtractionTable",pos.copy());
			pos.moveUp().setBlock(world,Blocks.hopper);
			
			for(int item = 0; item < extractionTable[a+1].length; item++){
				getTile(TileEntityHopper.class).setInventorySlotContents(item,extractionTable[a+1][item]);
			}
		}
		
		// simulate world for 1 minute
		
		for(int a = 0; a < 2400; a++){
			world.getWorldInfo().incrementTotalWorldTime(world.getWorldInfo().getWorldTotalTime()+1L);
			world.tickUpdates(false);
			world.updateEntities();
		}
		
		if (MathUtil.distance(player.posX,player.posZ) > 64D)player.setPositionAndUpdate(-1D,world.getTopSolidOrLiquidBlock(-1,-1),-1D);
	}
	
	/* TESTS */
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testFallingBlocks(){
		Assert.equal(storedLocs.get("FallingBlockObsidian").size(),2,"Unexpected amount of stored locs, expected $2, got $1.");
		
		for(BlockPosM testPos:storedLocs.get("FallingBlockObsidian")){
			Assert.instanceOf(testPos.move(0,-2,0).getBlock(world),BlockObsidianEnd.class,"Unexpected block class, expected $2, got $1.");
		}
		
		Assert.equal(storedLocs.get("FallingBlockDragonEgg").size(),2,"Unexpected amount of stored locs, expected $2, got $1.");
		
		for(BlockPosM testPos:storedLocs.get("FallingBlockDragonEgg")){
			Assert.instanceOf(testPos.move(0,-2,0).getBlock(world),BlockDragonEggCustom.class,"Unexpected block class, expected $2, got $1.");
		}
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testDragonEssence(){
		int essence1 = -1, essence2 = -1;
		
		Assert.equal(storedLocs.get("DragonEssenceAltar").size(),2,"Unexpected amount of stored locs, expected $2, got $1.");
		
		for(BlockPosM testPos:storedLocs.get("DragonEssenceAltar")){
			pos.set(testPos);
			
			if (essence1 == -1)essence1 = getTile(TileEntityEssenceAltar.class).getEssenceLevel();
			else essence2 = getTile(TileEntityEssenceAltar.class).getEssenceLevel();
			
			pos.set(testPos).move(-3,1,0);
			Assert.equal(getEntities(EntityItemAltar.class).get(0).getEntityItem().getItem(),ItemList.enhanced_brewing_stand,"Unexpected altar item, expected $2, got $1.");
			
			pos.set(testPos).move(0,1,-3);
			Assert.equal(getEntities(EntityItemAltar.class).get(0).getEntityItem().getItem(),ItemList.temple_caller,"Unexpected altar item, expected $2, got $1.");
			
			pos.set(testPos).move(3,1,0);
			Assert.equal(getEntities(EntityItemAltar.class).get(0).getEntityItem().getItemDamage(),1,"Unexpected altar item damage, expected $2, got $1.");
			
			pos.set(testPos).move(0,1,3);
			Assert.equal(getEntities(EntityItemAltar.class).get(0).getEntityItem().getItemDamage(),0,"Unexpected altar item damage, expected $2, got $1.");
		}
		
		Assert.state(essence1 < essence2,"Unexpected Essence levels, expected "+essence1+" to be lower than "+essence2+".");
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testBrewing(){		
		Object[][] data = new Object[][]{
			new Object[]{ 0, new ItemStack(Items.potionitem,1,16) },
			new Object[]{ 0, new ItemStack(Items.potionitem,1,8194) },
			new Object[]{ 1, PotionTypes.setCustomPotionEffect(new ItemStack(Items.potionitem,1,8194),new PotionEffect(Potion.moveSpeed.id,1200,2)) },
			new Object[]{ 1, PotionTypes.setCustomPotionEffect(new ItemStack(Items.potionitem,1,8194),new PotionEffect(Potion.moveSpeed.id,1200,3)) },
			new Object[]{ 1, PotionTypes.setCustomPotionEffect(new ItemStack(Items.potionitem,1,8194),new PotionEffect(Potion.moveSpeed.id,14400,0)) },
			new Object[]{ 0, PotionTypes.setCustomPotionEffect(new ItemStack(Items.potionitem,1,8194|16384),new PotionEffect(Potion.moveSpeed.id,1200,0)) },
			new Object[]{ 0, new ItemStack(ItemList.infestation_remedy) },
			new Object[]{ 0, new ItemStack(ItemList.potion_of_instability) },
			new Object[]{ 0, new ItemStack(ItemList.potion_of_purity) }
		};
		
		List<BlockPosM> stands = storedLocs.get("EnhancedBrewingStand");
		
		for(int a = 0; a < data.length; a++){
			pos.set(stands.get(a));
			
			if ((int)data[a][0] == 0)Assert.isNull(getTile(TileEntityEnhancedBrewingStand.class).getStackInSlot(4),"Unexpected item, expected powder slot to be empty.");
			else Assert.equal(getTile(TileEntityEnhancedBrewingStand.class).getStackInSlot(4).stackSize,data[a][0],"Unexpected powder amount, expected $2, got $1.");
			
			Assert.equal(getTile(TileEntityEnhancedBrewingStand.class).getStackInSlot(1),data[a][1],"Unexpected potion item, expected $2, got $1.");
		}
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testDecompositionTable(){
		int[] stardust = new int[]{
			59, 59, 63, 61, 57, 57, 64
		};
		
		final List<BlockPosM> decomposition = storedLocs.get("DecompositionTable");
		
		for(int a = 0; a < stardust.length; a++){
			pos.set(decomposition.get(a));
			Assert.equal(getTile(TileEntityDecompositionTable.class).getStackInSlot(1).stackSize,stardust[a],"Unexpected stardust amount, expected $2, got $1.");
		}
		
		Function<Integer,TileEntityChest> get = new Function<Integer,TileEntityChest>(){
			@Override public TileEntityChest apply(Integer input){
				return ((TileEntityChest)pos.set(decomposition.get(input)).move(0,-2,0).getTileEntity(world));
			}
		};
		
		Assert.equal(get.apply(0).getStackInSlot(0).getItem(),Items.diamond,"Unexpected item, expected $2, got $1.");
		Assert.state(get.apply(0).getStackInSlot(0).stackSize > 3,"Unexpected state, expected stack size to be greater than 4.");
		
		Assert.equal(get.apply(1).getStackInSlot(0).getItem(),Items.diamond,"Unexpected item, expected $2, got $1.");
		Assert.equal(get.apply(1).getStackInSlot(0).stackSize,1,"Unexpected stack size, expected $2, got $1.");
		
		Assert.equal(get.apply(2).getStackInSlot(0).getItem(),Item.getItemFromBlock(Blocks.log),"Unexpected item, expected $2, got $1.");
		
		Assert.notNull(get.apply(3).getStackInSlot(0),"Unexpected item, expected it not to be null.");
		Assert.notNull(get.apply(4).getStackInSlot(0),"Unexpected item, expected it not to be null.");
		Assert.notNull(get.apply(5).getStackInSlot(0),"Unexpected item, expected it not to be null.");
		
		Assert.isNull(get.apply(6).getStackInSlot(0),"Unexpected item, expected it to be null.");
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testExperienceTable(){
		
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testAccumulationTable(){
		
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger)
	public void testExtractionTable(){
		
	}
	
	/* WORLD MANIPULATION */
	
	private void setFloor(int floor, int startRow, int endRow, Block block){
		pos.set(0,9+6*floor,startRow);
		for(int a = 0; a < (1+endRow-startRow)*16; a++)setMove(block);
	}
	
	private void setPos(int floor, int z){
		pos.set(0,10+6*floor,z);
	}
	
	private void setMove(Block block){
		pos.setBlock(world,block);
		move();
	}
	
	private void setMove(Block block, int meta){
		pos.setBlock(world,block,meta);
		move();
	}
	
	private void set(Block block){
		pos.setBlock(world,block);
	}
	
	private void set(Block block, int meta){
		pos.setBlock(world,block,meta);
	}
	
	private void move(){
		pos.move(1,0,0);
		if (pos.x >= 16)pos.setX(0).move(0,0,1);
	}
	
	private <T> T getTile(Class<T> cls){
		return (T)pos.getTileEntity(world);
	}
	
	private <T extends Entity> List<T> getEntities(Class<T> cls){
		return world.getEntitiesWithinAABB(cls,AxisAlignedBB.getBoundingBox(pos.x,pos.y,pos.z,pos.x+1,pos.y+1,pos.z+1));
	}
	
	private void spawnItem(double x, double y, double z, ItemStack is){
		EntityItem item = new EntityItem(world,x,y,z,is);
		item.motionX = item.motionY = item.motionZ = 0D;
		item.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(item);
	}
	
	/* TEST METHODS */
	
	private void runEntity(Entity entity, int ticks){
		Assert.notNull(entity,"Unexpected argument, entity is null.");
		
		for(int a = 0; a < ticks; a++){
			entity.lastTickPosX = entity.posX;
			entity.lastTickPosY = entity.posY;
			entity.lastTickPosZ = entity.posZ;
			entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
			++entity.ticksExisted;
			entity.onUpdate();
			
			if (entity.isDead)return;
		}
	}
	
	private void runTile(TileEntity tile, int ticks){
		Assert.notNull(tile,"Unexpected argument, tile entity is null.");
		
		for(int a = 0; a < ticks; a++)tile.updateEntity();
	}
}
