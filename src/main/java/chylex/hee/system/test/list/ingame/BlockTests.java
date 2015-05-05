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
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.block.BlockSacredStone;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockTests{
	private static final String testTrigger = "ingame/blocks/";
	
	private World world;
	private EntityPlayer player;
	private BlockPosM pos;
	
	private Multimap<String,BlockPosM> storedLocs = HashMultimap.create();
	
	public void setup(){
		if (world != null)return;
		world = DimensionManager.getWorld(0);
		player = Minecraft.getMinecraft().thePlayer;
		pos = new BlockPosM();
		storedLocs.clear();
	}
	
	@UnitTest(type = MethodType.PREPARATION, runTime = RunTime.INGAME, trigger = testTrigger+"prep")
	public void prepBlockChunk(){
		setup();
		
		if (MathUtil.distance(player.posX,player.posZ) > 64D)player.setPositionAndUpdate(-1D,world.getTopSolidOrLiquidBlock(-1,-1),-1D);
		
		// clear area
		for(pos.x = 0; pos.x < 16; pos.x++){
			for(pos.z = 0; pos.z < 16; pos.z++){
				for(pos.y = 4; pos.y < 128; pos.y++){
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
		setFloor(0,0,7,Blocks.stonebrick);
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
		setFloor(0,9,9,Blocks.end_stone);
		setPos(0,9);
		
		for(int a = 0; a < 8; a++){
			setMove(Blocks.torch);
			setMove(Blocks.stone_slab);
		}
		
		setPos(0,9);
		pos.moveUp().moveUp();
		
		for(int a = 0; a < 2; a++){
			storedLocs.put("FallingBlock",pos.copy());
			setMove(BlockList.obsidian_falling);
		}
		
		for(int a = 0; a < 2; a++){
			storedLocs.put("FallingBlock",pos.copy());
			setMove(Blocks.dragon_egg);
		}
		
		// first floor - special condition decorative blocks
		setFloor(0,11,15,Blocks.end_stone);
		setPos(0,11);
		
		for(int a = 2; a < 14; a++)setMove(BlockList.crossed_decoration,a);
		setMove(BlockList.enderman_head,1);
		setMove(BlockList.death_flower_pot,0);
		setMove(BlockList.death_flower,0);
		
		// second floor - ores
		setFloor(1,0,12,Blocks.stonebrick);
		setPos(1,0);
		
		for(int a = 0; a < 16; a++)setMove(BlockList.end_powder_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.endium_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.stardust_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.igneous_rock_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.instability_orb_ore);
		for(int a = 0; a < 16; a++)setMove(BlockList.sphalerite,1);
		
		// second floor - static functional blocks
		setFloor(1,14,15,Blocks.stonebrick);
		setPos(1,14);
		
		setMove(BlockList.transport_beacon);
		setMove(BlockList.void_chest);
		
		// third floor - essence altars
		setFloor(2,0,6,Blocks.end_stone);
		setPos(2,0);
		
		pos.setX(3).setZ(3).setBlock(world,BlockList.essence_altar,EssenceType.DRAGON.id);
		storedLocs.put("DragonEssenceAltar",pos.copy());
		getTile(TileEntityEssenceAltar.class).loadFromDamage(EssenceType.DRAGON.id);
		
		try{
			Field essenceLevel = TileEntityEssenceAltar.class.getDeclaredField("essenceLevel");
			essenceLevel.setAccessible(true);
			essenceLevel.set(pos.getTileEntity(world),512);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		pos.setX(0).setZ(3).setBlock(world,Blocks.stonebrick);
		spawnItem(0.5D,pos.y+1.5D,3.5D,new ItemStack(Items.brewing_stand));
		pos.setX(3).setZ(0).setBlock(world,Blocks.stonebrick);
		spawnItem(3.5D,pos.y+1.5D,0.5D,new ItemStack(Items.ender_eye));
		pos.setX(6).setZ(3).setBlock(world,Blocks.stonebrick);
		spawnItem(6.5D,pos.y+1.5D,3.5D,new ItemStack(ItemList.ghost_amulet,1,0));
		pos.setX(3).setZ(6).setBlock(world,Blocks.stonebrick);
		spawnItem(3.5D,pos.y+1.5D,6.5D,new ItemStack(Items.diamond_sword,1,50));
		pos.setX(5).setZ(5).setBlock(world,Blocks.stonebrick);
		pos.setX(1).setZ(5).setBlock(world,Blocks.stonebrick);
		pos.setX(5).setZ(1).setBlock(world,Blocks.stonebrick);
		pos.setX(1).setZ(1).setBlock(world,Blocks.stonebrick);
		
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
		// TODO
		
		for(Object[] t:potions){
			setMove(BlockList.enhanced_brewing_stand);
			getTile(TileEntityEnhancedBrewingStand.class).setInventorySlotContents(1,new ItemStack(Items.potionitem,1,(Integer)t[0]));
			getTile(TileEntityEnhancedBrewingStand.class).setInventorySlotContents(3,t[1] instanceof Item ? new ItemStack((Item)t[1]) : (ItemStack)t[1]);
			if ((Integer)t[2] != 0)getTile(TileEntityEnhancedBrewingStand.class).setInventorySlotContents(4,new ItemStack(ItemList.end_powder,1,(Integer)t[2]));
		}
		
		// fourth floor - energy
		setFloor(3,5,15,Blocks.end_stone);
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.INGAME, trigger = testTrigger+"test")
	public void testBlockChunk(){
		
	}
	
	private void setFloor(int floor, int startRow, int endRow, Block block){
		pos.set(0,9+6*floor,startRow);
		for(int a = 0; a < (1+endRow-startRow)*16; a++)setMove(block);
	}
	
	private void setPos(int floor, int z){
		pos.set(0,10+6*floor,z);
	}
	
	private void setMove(Block block){
		setMove(block,0);
	}
	
	private void setMove(Block block, int meta){
		pos.setBlock(world,block,meta);
		pos.move(1,0,0);
		if (pos.x >= 16)pos.setX(0).move(0,0,1);
	}
	
	private <T> T getTile(Class<T> cls){
		return (T)pos.getTileEntity(world);
	}
	
	private void spawnItem(double x, double y, double z, ItemStack is){
		EntityItem item = new EntityItem(world,x,y,z,is);
		item.motionX = item.motionY = item.motionZ = 0D;
		item.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(item);
	}
}
