package chylex.hee.world.feature.ores;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.world.structure.StructureWorld;

public interface IOreGenerator{
	void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores);
	
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
					else mpos.move(Facing6.list[rand.nextInt(Facing6.list.length)]);
					
					if (world.getBlock(mpos.x,mpos.y,mpos.z) == gen.toReplace){
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
					
					if (world.getBlock(mpos.x,mpos.y,mpos.z) == gen.toReplace){
						world.setBlock(mpos.x,mpos.y,mpos.z,gen.orePicker.pick(rand));
						break;
					}
				}
			}
		}
	}
}