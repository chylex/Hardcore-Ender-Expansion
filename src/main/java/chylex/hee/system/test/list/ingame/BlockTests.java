package chylex.hee.system.test.list.ingame;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.block.BlockSacredStone;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockTests{
	private static final String testTrigger = "ingame/blocks/";
	
	private World world;
	private EntityPlayer player;
	private BlockPosM pos;
	
	public void setup(){
		if (world != null)return;
		world = DimensionManager.getWorld(0);
		player = Minecraft.getMinecraft().thePlayer;
		pos = new BlockPosM();
	}
	
	@UnitTest(type = MethodType.PREPARATION, runTime = RunTime.INGAME, trigger = testTrigger+"prep")
	public void prepBlockChunk(){
		setup();
		player.setPositionAndUpdate(-1D,world.getTopSolidOrLiquidBlock(-1,-1),-1D);
		
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
		setFloor(0,0,9,Blocks.stonebrick);
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
		
		// third floor - enhanced brewing
		
		// fourth floor - energy
	}
	
	private void setFloor(int floor, int startRow, int endRow, Block block){
		pos.set(0,7+6*floor,startRow);
		for(int a = 0; a < (1+endRow-startRow)*16; a++)setMove(block);
	}
	
	private void setPos(int floor, int z){
		pos.set(0,8+6*floor,z);
	}
	
	private void setMove(Block block){
		setMove(block,0);
	}
	
	private void setMove(Block block, int meta){
		pos.setBlock(world,block,meta);
		pos.move(1,0,0);
		if (pos.x >= 16)pos.setX(0).move(0,0,1);
	}
}
