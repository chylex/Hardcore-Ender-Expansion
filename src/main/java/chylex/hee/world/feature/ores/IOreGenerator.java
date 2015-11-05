package chylex.hee.world.feature.ores;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.RangeGenerator;

public interface IOreGenerator{
	/**
	 * Runs only if {@code canPlaceAt} returns true for the starting position.
	 */
	void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores);
	
	default boolean canPlaceAt(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z){
		return world.getBlock(x,y,z) == gen.toReplace;
	}
	
	/**
	 * Generates a single ore block in the starting position.
	 */
	public static class SinglePiece implements IOreGenerator{
		@Override
		public void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores){
			world.setBlock(x,y,z,gen.orePicker.pick(rand));
		}
	}
	
	/**
	 * Generates an ore in the starting position, and then keeps generating only in positions adjacent to previously generated ores.
	 * If {@code allowDiagonal} is enabled, it may also generate in any diagonal direction from the chosen position.
	 */
	public static class AdjacentSpread implements IOreGenerator{
		private final boolean allowDiagonal;
		
		public AdjacentSpread(boolean allowDiagonal){
			this.allowDiagonal = allowDiagonal;
		}
		
		@Override
		public void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores){
			List<Pos> generated = new ArrayList<>(ores);
			PosMutable mpos;
			
			mpos = new PosMutable(x,y,z);
			generated.add(mpos.immutable());
			world.setBlock(x,y,z,gen.orePicker.pick(rand));
			
			for(int ore = 1; ore < ores; ore++){
				for(int attempt = 0; attempt < 5; attempt++){
					mpos.set(CollectionUtil.randomOrNull(generated,rand));
					
					if (allowDiagonal)mpos.move(rand.nextInt(3)-1,rand.nextInt(3)-1,rand.nextInt(3)-1);
					else mpos.move(Facing6.random(rand));
					
					if (canPlaceAt(gen,world,rand,mpos.x,mpos.y,mpos.z)){
						world.setBlock(mpos.x,mpos.y,mpos.z,gen.orePicker.pick(rand));
						generated.add(mpos.immutable());
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Generates an ore in the starting position, and then keeps generating ores in random directions from the center.
	 */
	public static class DistanceSpread implements IOreGenerator{
		private final ToDoubleFunction<Random> calculator;
		
		public DistanceSpread(final double maxDistance){
			this.calculator = rand -> rand.nextDouble()*maxDistance;
		}
		
		public DistanceSpread(ToDoubleFunction<Random> distanceCalculator){
			this.calculator = distanceCalculator;
		}
		
		@Override
		public void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores){
			double dx = x+0.5D, dy = y+0.5D, dz = z+0.5D;
			PosMutable mpos = new PosMutable();
			
			for(int ore = 0; ore < ores; ore++){
				for(int attempt = 0; attempt < 5; attempt++){
					Vec vec = Vec.xyzRandom(rand);
					double dist = calculator.applyAsDouble(rand);
					
					mpos.set(x+vec.x*dist,y+vec.y*dist,z+vec.z*dist);
					
					if (canPlaceAt(gen,world,rand,mpos.x,mpos.y,mpos.z)){
						world.setBlock(mpos.x,mpos.y,mpos.z,gen.orePicker.pick(rand));
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Keeps generating lines in random directions, with the first one starting in the starting positions, and all other lines starting in a random generated ore.
	 */
	public static class AttachingLines implements IOreGenerator{
		private final RangeGenerator oresPerLine;
		
		public AttachingLines(RangeGenerator oresPerLine){
			this.oresPerLine = oresPerLine;
		}
		
		@Override
		public void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores){
			final List<Pos> generated = new ArrayList<>(ores);
			
			world.setBlock(x,y,z,gen.orePicker.pick(rand));
			generated.add(Pos.at(x,y,z));
			
			for(int attempt = 0; attempt < 3*ores; attempt++){
				int total = oresPerLine.next(rand);
				Pos startPos = CollectionUtil.randomOrNull(generated,rand);
				Vec dir = Vec.xyzRandom(rand);
				Vec pos = Vec.xyz(startPos.getX()+0.5D,startPos.getY()+0.5D,startPos.getZ()+0.5D);
				
				for(int cycle = 1, left = total; cycle <= total && left > 0; cycle++){
					if (generated.size() >= ores)return;
					
					Pos nextPos = Pos.at(pos.x += dir.x,pos.y += dir.y,pos.z += dir.z);
					
					if (canPlaceAt(gen,world,rand,nextPos.getX(),nextPos.getY(),nextPos.getZ())){
						world.setBlock(nextPos.getX(),nextPos.getY(),nextPos.getZ(),gen.orePicker.pick(rand));
						generated.add(nextPos);
						--left;
					}
				}
			}
		}
	}
	
	/**
	 * Allows ores to only generate if there is an air block adjacent to the target positions.
	 */
	public static class NextToAir extends OreGeneratorConditioned{
		public NextToAir(IOreGenerator wrapped){
			super(wrapped);
		}
		
		@Override
		public boolean canPlaceAt(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z){
			return super.canPlaceAt(gen,world,rand,x,y,z) && Arrays.stream(Facing6.list).anyMatch(facing -> world.isAir(x+facing.getX(),y+facing.getY(),z+facing.getZ()));
		}
	}
}