package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import chylex.hee.block.BlockGloomrock;
import chylex.hee.entity.technical.EntityTechnicalTrigger;
import chylex.hee.entity.technical.EntityTechnicalTrigger.TriggerBase;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.world.structure.StructurePiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.IBlockPicker;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenEnergyShrine implements IWorldGenerator{
	private static final int height = 20;
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
		int blockX = chunkX*16, blockZ = chunkZ*16;
		createStructure(world,rand); // TODO
	}
	
	private StructureWorld createStructure(World world, Random rand){
		StructureWorld structureWorld = new StructureWorld(world,24,height,24);
		new ShrinePiece().generate(structureWorld,rand);
		return structureWorld;
	}
	
	private static final class ShrinePiece extends StructurePiece{
		private void generate(StructureWorld world, Random rand){
			final Facing4 roomDir = Facing4.random(rand), left = roomDir.rotateLeft(), right = roomDir.rotateRight();
			final int top = height-1;
			
			final int fallHeight = 5+rand.nextInt(height-6);
			final int hallwayLength = 6+rand.nextInt(5);
			
			PosMutable mpos = new PosMutable(0,top,0);
			
			// color
			final float hue = rand.nextFloat();
			final int hueFull = MathUtil.floor(hue*360F);
			
			final float[] rgb = ColorUtil.hsvToRgb(hue,0.5F,0.65F);
			BlockGloomrock.State color;
			
			if (hueFull > 340 || hueFull < 10)color = BlockGloomrock.State.COL_RED;
			else if (hueFull < 40)color = BlockGloomrock.State.COL_ORANGE;
			else if (hueFull < 80)color = BlockGloomrock.State.COL_YELLOW;
			else if (hueFull < 130)color = BlockGloomrock.State.COL_GREEN;
			else if (hueFull < 165)color = BlockGloomrock.State.COL_CYAN;
			else if (hueFull < 210)color = BlockGloomrock.State.COL_LIGHT_BLUE;
			else if (hueFull < 240)color = BlockGloomrock.State.COL_BLUE;
			else if (hueFull < 270)color = BlockGloomrock.State.COL_MAGENTA;
			else if (hueFull < 310)color = BlockGloomrock.State.COL_PURPLE;
			else color = BlockGloomrock.State.COL_PINK;
			
			IBlockPicker placeColor = IBlockPicker.basic(BlockList.gloomrock,color.value);
			
			// entrance
			mpos.set(0,top,0);
			placeStairOutline(world,rand,BlockList.gloomrock_smooth_stairs,0,mpos.y,0,1,false,false);
			placeBlock(world,rand,placeColor,0,mpos.y,0);
			
			// the fall
			--mpos.y;
			placeCube(world,rand,IBlockPicker.basic(BlockList.gloomrock,BlockGloomrock.State.PLAIN.value),-2,mpos.y,-2,2,mpos.y-fallHeight,2);
			placeCube(world,rand,placeAir,-1,mpos.y,-1,1,mpos.y-fallHeight+1,1);
			placeOutline(world,rand,IBlockPicker.basic(BlockList.gloomrock_brick_slab,Meta.slabTop),-1,mpos.y,-1,1,mpos.y,1,1);
			
			// the hallway
			mpos.y -= fallHeight;
			mpos.move(roomDir,2);
			
			Pos point1 = mpos.offset(left,2);
			Pos point2 = mpos.offset(right,2).offset(roomDir,hallwayLength);
			placeCube(world,rand,IBlockPicker.basic(BlockList.gloomrock,BlockGloomrock.State.PLAIN.value),point1.getX(),mpos.y,point1.getZ(),point2.getX(),mpos.y+4,point2.getZ());
			
			point1 = mpos.offset(left);
			point2 = mpos.offset(right).offset(roomDir,hallwayLength);
			placeCube(world,rand,placeAir,point1.getX(),mpos.y+1,point1.getZ(),point2.getX(),mpos.y+3,point2.getZ());
			
			// the room (layout)
			mpos.move(roomDir,hallwayLength);
			
			point1 = mpos.offset(left,5);
			point2 = mpos.offset(right,5).offset(roomDir,10);
			placeCube(world,rand,IBlockPicker.basic(BlockList.gloomrock,BlockGloomrock.State.SMOOTH.value),point1.getX(),mpos.y,point1.getZ(),point2.getX(),mpos.y,point2.getZ());
			placeWalls(world,rand,IBlockPicker.basic(BlockList.gloomrock,BlockGloomrock.State.BRICK.value),point1.getX(),mpos.y+1,point1.getZ(),point2.getX(),mpos.y+5,point2.getZ());
			placeCube(world,rand,IBlockPicker.basic(BlockList.gloomrock,BlockGloomrock.State.PLAIN.value),point1.getX(),mpos.y+6,point1.getZ(),point2.getX(),mpos.y+6,point2.getZ());

			point1 = mpos.offset(left,4).offset(roomDir,1);
			point2 = mpos.offset(right,4).offset(roomDir,8);
			placeCube(world,rand,placeAir,point1.getX(),mpos.y+1,point1.getZ(),point2.getX(),mpos.y+5,point2.getZ());
			
			// entrance to the room
			point1 = mpos.offset(left);
			point2 = mpos.offset(right);
			placeCube(world,rand,placeAir,point1.getX(),mpos.y+1,point1.getZ(),point2.getX(),mpos.y+3,point2.getZ());
			
			// trigger
			mpos.move(roomDir,4);
			world.addEntity(new EntityTechnicalTrigger(null,mpos.x,mpos.y,mpos.z,new TriggerShrine()));
			
			// cluster
			mpos.move(roomDir);
			
			placeBlock(world,rand,IBlockPicker.basic(BlockList.energy_cluster),mpos.x,mpos.y+2,mpos.z);
			
			world.setTileEntity(mpos.x,mpos.y+2,mpos.z,(tile, random) -> {
				((TileEntityEnergyCluster)tile).generate(EnergyClusterGenerator.energyShrine,random);
				((TileEntityEnergyCluster)tile).setColor(rgb);
			});
			
			// side colors
			Pos sidePos;
			
			for(Facing4 facing:Facing4.list){
				if (facing == roomDir.opposite())continue;
				
				sidePos = mpos.offset(facing,4);
				placeLine(world,rand,IBlockPicker.basic(BlockList.gloomrock_brick_stairs,Meta.getStairs(facing,true)),sidePos.getX()+2*facing.rotateLeft().getX(),mpos.y+1,sidePos.getZ()+2*facing.rotateLeft().getZ(),sidePos.getX()+2*facing.rotateRight().getX(),mpos.y+1,sidePos.getZ()+2*facing.rotateRight().getZ());
				
				sidePos = mpos.offset(facing,5);
				placeBlock(world,rand,placeColor,sidePos.getX(),mpos.y+2,sidePos.getZ());
			}
		}
	}
	
	public static final class TriggerShrine extends TriggerBase{
		@Override
		protected void update(EntityTechnicalTrigger entity, World world, Random rand){
			
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			new WorldGenEnergyShrine().createStructure(world,world.rand).generateInWorld(world,world.rand,MathUtil.floor(player.posX),MathUtil.floor(player.posY)-height,MathUtil.floor(player.posZ));
		}
	};
}
