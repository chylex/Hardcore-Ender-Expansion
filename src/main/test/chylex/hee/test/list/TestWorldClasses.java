package chylex.hee.test.list;
import gnu.trove.set.hash.TIntHashSet;
import java.lang.reflect.Method;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.test.Assert;
import chylex.hee.test.UnitTest;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.Range;

public class TestWorldClasses{
	@UnitTest
	public void testRange(){
		Range range;
		int rand;
		
		range = new Range(1, 5);
		Assert.isTrue(range.in(1));
		Assert.isTrue(range.in(2));
		Assert.isTrue(range.in(5));
		Assert.isFalse(range.in(0));
		Assert.isFalse(range.in(6));
		
		rand = range.random(new Random());
		Assert.isTrue(rand >= 1 && rand <= 5);
		
		range = new Range(-4, -4);
		Assert.isTrue(range.in(-4));
		Assert.equal(range.random(new Random()), -4);
	}
	
	@UnitTest
	public void testBoundingBox(){
		BoundingBox reference;
		
		reference = new BoundingBox(Pos.at(4, 0, 7), Pos.at(12, 8, 30));
		
		BoundingBox inside = new BoundingBox(Pos.at(5, 1, 9), Pos.at(10, 5, 22));
		BoundingBox intersecting = new BoundingBox(Pos.at(1, -3, 10), Pos.at(6, 7, 15));
		BoundingBox outside = new BoundingBox(Pos.at(-7, -10, 0), Pos.at(0, -4, 5));
		
		Assert.isTrue(inside.isInside(reference));
		Assert.isFalse(intersecting.isInside(reference));
		Assert.isFalse(outside.isInside(reference));
		
		Assert.isTrue(inside.intersects(reference));
		Assert.isTrue(intersecting.intersects(reference));
		Assert.isFalse(outside.intersects(reference));
		
		reference = new BoundingBox(Pos.at(0, 0, 0), Pos.at(1, 1, 1));
		
		BoundingBox[] touching = new BoundingBox[]{
			new BoundingBox(Pos.at(-2, -2, -2), Pos.at(-1, -1, -1)),
			new BoundingBox(Pos.at(2, 2, 2), Pos.at(3, 3, 3)),
			new BoundingBox(Pos.at(0, 2, 0), Pos.at(1, 3, 1)),
			new BoundingBox(Pos.at(0, -2, 0), Pos.at(1, -1, 1))
		};
		
		for(BoundingBox box:touching){
			Assert.isFalse(box.isInside(reference));
			Assert.isFalse(box.intersects(reference));
		}
	}
	
	@UnitTest
	public void testStructureWorld(){
		StructureWorld world = new StructureWorld(2, 3, 2);
		
		Assert.isTrue(world.isInside(0, 0, 0));
		Assert.isTrue(world.isInside(-2, 0, -2));
		Assert.isTrue(world.isInside(2, 0, 2));
		Assert.isTrue(world.isInside(0, 2, 0));
		Assert.isFalse(world.isInside(0, -1, 0));
		Assert.isFalse(world.isInside(0, 3, 0));
		Assert.isFalse(world.isInside(3, 0, 0));
		
		int x = 0, y = 1, z = -2;
		Assert.isTrue(world.isAir(x, y, z));
		world.setBlock(x, y, z, Blocks.bedrock, 1);
		Assert.equal(world.getBlock(x, y, z), Blocks.bedrock);
		Assert.equal(world.getMetadata(x, y, z), 1);
		
		try{
			Method toIndex = StructureWorld.class.getDeclaredMethod("toIndex", int.class, int.class, int.class);
			toIndex.setAccessible(true);
			
			Method toPos = StructureWorld.class.getDeclaredMethod("toPos", int.class, PosMutable.class);
			toPos.setAccessible(true);
			
			TIntHashSet usedIndexes = new TIntHashSet(76, 1F);
			
			Pos.forEachBlock(Pos.at(-2, 0, -2), Pos.at(2, 2, 2), pos -> {
				try{
					int index = (int)toIndex.invoke(world, pos.getX(), pos.getY(), pos.getZ());
					usedIndexes.add(index);
					
					PosMutable rev = new PosMutable();
					toPos.invoke(world, index, rev);
					
					Assert.equal(rev, pos);
				}catch(Throwable t){
					t.printStackTrace();
				}
			});
			
			Assert.equal(usedIndexes.size(), 75);
		}catch(Throwable t){
			throw new RuntimeException(t);
		}
		
		world.clearArea(Blocks.dirt, 2);
		
		Pos.forEachBlock(Pos.at(-2, 0, -2), Pos.at(2, 2, 2), pos -> {
			Assert.equal(world.getBlock(pos), Blocks.dirt);
			Assert.equal(world.getMetadata(pos), 2);
		});
	}
}
