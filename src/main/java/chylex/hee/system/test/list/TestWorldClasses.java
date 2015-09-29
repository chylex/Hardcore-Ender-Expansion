package chylex.hee.system.test.list;
import java.util.Random;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Range;

public class TestWorldClasses{
	@UnitTest
	public void testRange(){
		Range range;
		int rand;
		
		range = new Range(1,5);
		Assert.isTrue(range.in(1));
		Assert.isTrue(range.in(2));
		Assert.isTrue(range.in(5));
		Assert.isFalse(range.in(0));
		Assert.isFalse(range.in(6));
		
		rand = range.random(new Random());
		Assert.isTrue(rand >= 1 && rand <= 5);
		
		range = new Range(-4,-4);
		Assert.isTrue(range.in(-4));
		Assert.equal(range.random(new Random()),-4);
	}
	
	@UnitTest
	public void testBoundingBox(){
		BoundingBox reference;
		
		reference = new BoundingBox(Pos.at(4,0,7),Pos.at(12,8,30));
		
		BoundingBox inside = new BoundingBox(Pos.at(5,1,9),Pos.at(10,5,22));
		BoundingBox intersecting = new BoundingBox(Pos.at(1,-3,10),Pos.at(6,7,15));
		BoundingBox outside = new BoundingBox(Pos.at(-7,-10,0),Pos.at(0,-4,5));
		
		Assert.isTrue(inside.isInside(reference));
		Assert.isFalse(intersecting.isInside(reference));
		Assert.isFalse(outside.isInside(reference));
		
		Assert.isTrue(inside.intersects(reference));
		Assert.isTrue(intersecting.intersects(reference));
		Assert.isFalse(outside.intersects(reference));
		
		reference = new BoundingBox(Pos.at(0,0,0),Pos.at(1,1,1));
		
		BoundingBox[] touching = new BoundingBox[]{
			new BoundingBox(Pos.at(-2,-2,-2),Pos.at(-1,-1,-1)),
			new BoundingBox(Pos.at(2,2,2),Pos.at(3,3,3)),
			new BoundingBox(Pos.at(0,2,0),Pos.at(1,3,1)),
			new BoundingBox(Pos.at(0,-2,0),Pos.at(1,-1,1))
		};
		
		for(BoundingBox box:touching){
			Assert.isFalse(box.isInside(reference));
			Assert.isFalse(box.intersects(reference));
		}
	}
}
