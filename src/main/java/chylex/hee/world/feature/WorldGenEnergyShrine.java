package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import chylex.hee.block.BlockGloomrock;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.technical.EntityTechnicalTrigger;
import chylex.hee.entity.technical.EntityTechnicalTrigger.TriggerBase;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
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
	private static final int height = 25;
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
		--chunkX;
		--chunkZ;
		if (!(chunkX%3 == 0 && chunkZ%3 == 0 && rand.nextInt(3) == 0))return;
		
		for(int type = 0; type < 2; type++){ // 0 = underground, 1 = surface
			boolean underground = type == 0;
			
			ShrineSettings settings = new ShrineSettings(rand, underground);
			int attempts = underground ? 140 : 10;
			int minY = (underground ? 15 : 50)+rand.nextInt(10);
			int maxY = (underground ? 55 : 95)+rand.nextInt(5);
			
			for(int attempt = 0; attempt < attempts; attempt++){
				int blockX = chunkX*16+8+rand.nextInt(16), blockZ = chunkZ*16+8+rand.nextInt(16);
				
				for(int blockY = minY; blockY < maxY; blockY++){
					if (canSpawnAt(world, blockX, blockY, blockZ, settings)){
						createStructure(world, settings, rand).generateInWorld(world, rand, blockX, blockY-height+1, blockZ);
						return;
					}
				}
			}
			
			if (rand.nextInt(3) != 0)return;
		}
	}
	
	/**
	 * The XZ coords are the center of the entrance, and Y is the top of the entrance.
	 */
	private boolean canSpawnAt(World world, int x, int y, int z, ShrineSettings settings){
		Pos centerTop = Pos.at(x, y, z);
		PosMutable mpos = new PosMutable();
		
		if (y-(settings.fallHeight+3) <= 5)return false;
		
		// entrance
		if (settings.underground){
			if (!Pos.allBlocksMatch(centerTop.offset(-1, 1, -1), centerTop.offset(1, 1, 1), pos -> pos.isAir(world)))return false;
		}
		else{
			if (!Pos.allBlocksMatch(centerTop.offset(-2, 1, -2), centerTop.offset(2, 1, 2), pos -> pos.isAir(world)))return false;
		}
		
		if (!Pos.allBlocksMatch(centerTop.offset(-2, 0, -2), centerTop.offset(2, 0, 2), pos -> pos.getBlock(world).isOpaqueCube()))return false;
		if (!Pos.allBlocksMatch(centerTop.offset(-2, -1, -2), centerTop.offset(2, -1, 2), pos -> pos.getBlock(world).isOpaqueCube()))return false;
		
		// fall
		for(int offY = -2; offY >= -(settings.fallHeight+3); offY--){
			for(int offX = -3; offX <= 3; offX++){
				if (mpos.set(centerTop).move(offX, offY, -3).isAir(world))return false;
				if (mpos.set(centerTop).move(offX, offY, 3).isAir(world))return false;
			}
			
			for(int offZ = -3; offZ <= 3; offZ++){
				if (mpos.set(centerTop).move(-3, offY, offZ).isAir(world))return false;
				if (mpos.set(centerTop).move(3, offY, offZ).isAir(world))return false;
			}
		}
		
		// room
		if (!settings.underground){
			mpos.set(centerTop).move(0, -settings.fallHeight, 0).move(settings.roomDir, settings.hallwayLength+7);
			
			if (!Pos.allBlocksMatch(mpos.offset(-6, -3, -6), mpos.offset(6, -3, 6), pos -> !pos.isAir(world)))return false;
			if (!Pos.allBlocksMatch(mpos.offset(-6, 6, -6), mpos.offset(6, 6, 6), pos -> !pos.isAir(world)))return false;
			
			if (!Pos.allBlocksMatch(mpos.offset(-6, -2, -6), mpos.offset(6, 5, -6), pos -> !pos.isAir(world)))return false;
			if (!Pos.allBlocksMatch(mpos.offset(-6, -2, 6), mpos.offset(6, 5, 6), pos -> !pos.isAir(world)))return false;
			
			if (!Pos.allBlocksMatch(mpos.offset(-6, -2, -6), mpos.offset(-6, 5, 6), pos -> !pos.isAir(world)))return false;
			if (!Pos.allBlocksMatch(mpos.offset(6, -2, -6), mpos.offset(6, 5, 6), pos -> !pos.isAir(world)))return false;
		}
		
		return true;
	}
	
	private StructureWorld createStructure(World world, ShrineSettings settings, Random rand){
		StructureWorld structureWorld = new StructureWorld(world, 30, height, 30);
		new ShrinePiece().generate(structureWorld, settings, rand);
		return structureWorld;
	}
	
	private static final class ShrineSettings{
		private final boolean underground;
		private final int fallHeight;
		private final int hallwayLength;
		private final Facing4 roomDir;
		
		ShrineSettings(Random rand, boolean underground){
			this.underground = underground;
			
			if (underground){
				this.fallHeight = 6+rand.nextInt(height-7)/2;
				this.hallwayLength = 9+rand.nextInt(6);
			}
			else{
				this.fallHeight = 7+rand.nextInt(height-8);
				this.hallwayLength = 6+rand.nextInt(5);
			}
			
			this.roomDir = Facing4.random(rand);
		}
	}
	
	private static final class ShrinePiece extends StructurePiece{
		private void generate(StructureWorld world, ShrineSettings settings, Random rand){
			final Facing4 roomDir = settings.roomDir, left = roomDir.rotateLeft(), right = roomDir.rotateRight();
			final int fallHeight = settings.fallHeight;
			final int hallwayLength = settings.hallwayLength;
			final int top = height-1;
			
			PosMutable mpos = new PosMutable(0, top, 0);
			
			// color
			final float hue = rand.nextFloat();
			final int hueFull = MathUtil.floor(hue*360F);
			
			final float[] rgb = ColorUtil.hsvToRgb(hue, 0.5F, 0.65F);
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
			
			IBlockPicker placeColor = IBlockPicker.basic(BlockList.gloomrock, color.value);
			
			// entrance
			mpos.set(0, top, 0);
			placeStairOutline(world, rand, BlockList.gloomrock_smooth_stairs, 0, mpos.y, 0, 1, false, false);
			placeBlock(world, rand, placeColor, 0, mpos.y, 0);
			
			// the fall
			--mpos.y;
			placeCube(world, rand, IBlockPicker.basic(BlockList.gloomrock, BlockGloomrock.State.PLAIN.value), -2, mpos.y, -2, 2, mpos.y-fallHeight, 2);
			placeCube(world, rand, placeAir, -1, mpos.y, -1, 1, mpos.y-fallHeight+1, 1);
			placeOutline(world, rand, IBlockPicker.basic(BlockList.gloomrock_brick_slab, Meta.slabTop), -1, mpos.y, -1, 1, mpos.y, 1, 1);
			
			// the hallway
			mpos.y -= fallHeight;
			
			placeBlock(world, rand, IBlockPicker.basic(BlockList.gloomtorch, Meta.getGloomtorch(roomDir.opposite().toFacing6())), mpos.x-2*roomDir.getX(), mpos.y+2, mpos.z-2*roomDir.getZ());
			placeBlock(world, rand, IBlockPicker.basic(BlockList.gloomrock, BlockGloomrock.State.PLAIN.value), mpos.x-3*roomDir.getX(), mpos.y+2, mpos.z-3*roomDir.getZ());
			
			mpos.move(roomDir, 2);
			
			Pos point1 = mpos.offset(left, 2);
			Pos point2 = mpos.offset(right, 2).offset(roomDir, hallwayLength);
			placeCube(world, rand, IBlockPicker.basic(BlockList.gloomrock, BlockGloomrock.State.PLAIN.value), point1.getX(), mpos.y, point1.getZ(), point2.getX(), mpos.y+4, point2.getZ());
			
			point1 = mpos.offset(left);
			point2 = mpos.offset(right).offset(roomDir, hallwayLength);
			placeCube(world, rand, placeAir, point1.getX(), mpos.y+1, point1.getZ(), point2.getX(), mpos.y+3, point2.getZ());
			
			// the room (layout)
			mpos.move(roomDir, hallwayLength);
			
			point1 = mpos.offset(left, 5);
			point2 = mpos.offset(right, 5).offset(roomDir, 10);
			placeCube(world, rand, IBlockPicker.basic(BlockList.gloomrock, BlockGloomrock.State.SMOOTH.value), point1.getX(), mpos.y, point1.getZ(), point2.getX(), mpos.y, point2.getZ());
			placeWalls(world, rand, IBlockPicker.basic(BlockList.gloomrock, BlockGloomrock.State.BRICK.value), point1.getX(), mpos.y+1, point1.getZ(), point2.getX(), mpos.y+6, point2.getZ());
			placeCube(world, rand, IBlockPicker.basic(BlockList.gloomrock, BlockGloomrock.State.PLAIN.value), point1.getX(), mpos.y+7, point1.getZ(), point2.getX(), mpos.y+7, point2.getZ());

			point1 = mpos.offset(left, 4).offset(roomDir, 1);
			point2 = mpos.offset(right, 4).offset(roomDir, 9);
			placeCube(world, rand, placeAir, point1.getX(), mpos.y+1, point1.getZ(), point2.getX(), mpos.y+6, point2.getZ());
			
			// entrance to the room
			point1 = mpos.offset(left);
			point2 = mpos.offset(right);
			placeCube(world, rand, placeAir, point1.getX(), mpos.y+1, point1.getZ(), point2.getX(), mpos.y+3, point2.getZ());
			
			// trigger
			mpos.move(roomDir, 5);
			world.addEntity(new EntityTechnicalTrigger(null, mpos.x, mpos.y, mpos.z, new TriggerShrine()));
			
			// cluster
			placeBlock(world, rand, IBlockPicker.basic(BlockList.energy_cluster), mpos.x, mpos.y+2, mpos.z);
			
			world.setTileEntity(mpos.x, mpos.y+2, mpos.z, (tile, random) -> {
				((TileEntityEnergyCluster)tile).generate(EnergyClusterGenerator.energyShrine, random);
				((TileEntityEnergyCluster)tile).setColor(rgb);
			});
			
			// sides
			Pos sidePos;
			
			for(Facing4 facing:Facing4.list){
				if (facing == roomDir.opposite())continue;
				
				sidePos = mpos.offset(facing, 4);
				placeLine(world, rand, IBlockPicker.basic(BlockList.gloomrock_brick_stairs, Meta.getStairs(facing, true)), sidePos.getX()+2*facing.rotateLeft().getX(), mpos.y+1, sidePos.getZ()+2*facing.rotateLeft().getZ(), sidePos.getX()+2*facing.rotateRight().getX(), mpos.y+1, sidePos.getZ()+2*facing.rotateRight().getZ());
				
				placeBlock(world, rand, IBlockPicker.basic(BlockList.gloomtorch), sidePos.getX()+2*facing.rotateLeft().getX(), mpos.y+2, sidePos.getZ()+2*facing.rotateLeft().getZ());
				placeBlock(world, rand, IBlockPicker.basic(BlockList.gloomtorch), sidePos.getX()+2*facing.rotateRight().getX(), mpos.y+2, sidePos.getZ()+2*facing.rotateRight().getZ());
				
				sidePos = mpos.offset(facing, 5);
				placeBlock(world, rand, placeColor, sidePos.getX(), mpos.y+2, sidePos.getZ());
			}
		}
	}
	
	public static final class TriggerShrine extends TriggerBase{
		private int checkTimer;
		private boolean running;
		
		@Override
		protected void update(EntityTechnicalTrigger entity, World world, Random rand){
			if (++checkTimer > 20){
				checkTimer = 0;
				running = world.getClosestPlayerToEntity(entity, 12D) != null;
			}
			
			if (running){
				if (rand.nextInt(3) != 0){
					PacketPipeline.sendToAllAround(entity, 16D, new C20Effect(FXType.Basic.SHRINE_GLITTER, entity.posX+(rand.nextDouble()-0.5D)*8D, entity.posY+1D+rand.nextDouble()*4D, entity.posZ+(rand.nextDouble()-0.5D)*8D));
				}
			}
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			new WorldGenEnergyShrine().createStructure(world, new ShrineSettings(world.rand, false), world.rand).generateInWorld(world, world.rand, MathUtil.floor(player.posX), MathUtil.floor(player.posY)-height, MathUtil.floor(player.posZ));
		}
	};
}
