package chylex.hee.system.test.list;
import net.minecraft.util.EnumFacing;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;

public class TestAbstractions{
	@UnitTest
	public void testImmutablePos(){
		Pos reference = Pos.at(15,32,-90);
		
		Assert.equal(reference,Pos.at(15.5D,32.1D,-90D));
		Assert.equal(reference,Pos.at(new int[]{ 15, 32, -90 }));
		
		Assert.equal(reference.getX(),15);
		Assert.equal(reference.getY(),32);
		Assert.equal(reference.getZ(),-90);
		
		Assert.equal(reference,Pos.at(reference.toLong()));
		Assert.isTrue(reference == reference.immutable());
		
		Assert.equal(reference.offset(8,-2,3),Pos.at(23,30,-87));
		Assert.equal(reference.offset(0),Pos.at(15,31,-90));
		Assert.equal(reference.offset(1,8),Pos.at(15,40,-90));
		Assert.equal(reference.offset(EnumFacing.NORTH),Pos.at(15,32,-91));
		Assert.equal(reference.offset(EnumFacing.SOUTH,3),Pos.at(15,32,-87));
		Assert.equal(reference.offset(Facing4.EAST_POSX),Pos.at(16,32,-90));
		Assert.equal(reference.offset(Facing6.WEST_NEGX,5),Pos.at(10,32,-90));
		
		for(Facing4 facing:Facing4.list)Assert.isFalse(reference.equals(reference.offset(facing)));
		for(Facing6 facing:Facing6.list)Assert.isFalse(reference.equals(reference.offset(facing)));
		
		Assert.equal(reference.getDown(),Pos.at(15,31,-90));
		Assert.equal(reference.getUp(),Pos.at(15,33,-90));
		Assert.equal(reference.getNorth(),Pos.at(15,32,-91));
		Assert.equal(reference.getSouth(),Pos.at(15,32,-89));
		Assert.equal(reference.getEast(),Pos.at(16,32,-90));
		Assert.equal(reference.getWest(),Pos.at(14,32,-90));
		
		Assert.equal(reference.distance(20,32,-90),5);
		Assert.equal(reference.distance(Pos.at(20,32,-90)),5);
		Assert.equal(reference.distance(reference),0);
		Assert.equal(reference.distance(16,38,-95),7.874007874D);
		
		Assert.isFalse(reference.equals((Pos)null)); // NPE check
		Assert.equal(reference,Pos.at(15,32,-90)); // immutability check
	}
	
	@UnitTest
	public void testMutablePos(){
		Pos reference = Pos.at(15,32,-90);
		PosMutable mutable = new PosMutable(reference);
		
		Assert.equal(reference.getX(),mutable.x);
		Assert.equal(mutable.getX(),mutable.x);
		Assert.equal(reference.getY(),mutable.y);
		Assert.equal(mutable.getY(),mutable.y);
		Assert.equal(reference.getZ(),mutable.z);
		Assert.equal(mutable.getZ(),mutable.z);
		
		Assert.equal(mutable.toLong(),reference.toLong());
		Assert.isFalse(mutable == mutable.immutable());
		Assert.isFalse(mutable == mutable.copy());
		Assert.equal(mutable,mutable.immutable());
		Assert.equal(mutable,mutable.copy());
		
		Assert.equal(mutable.set(10,25,-15),new PosMutable(10,25,-15));
		Assert.equal(mutable.set(10.4D,25.8D,-15),new PosMutable(10,25,-15));
		Assert.equal(mutable.setX(10).setY(25).setZ(-15),new PosMutable(10,25,-15));
		
		Assert.equal(mutable.set(11,27,-20).move(-1,-2,5),new PosMutable(10,25,-15));
		Assert.equal(mutable.set(10,26,-15).move(EnumFacing.DOWN),new PosMutable(10,25,-15));
		Assert.equal(mutable.set(10,21,-15).move(Facing6.UP_POSY,4),new PosMutable(10,25,-15));
		Assert.equal(mutable.set(10,24,-15).moveUp(),new PosMutable(10,25,-15));
		
		for(Facing4 facing:Facing4.list)Assert.isFalse(reference.equals(new PosMutable(reference).move(facing)));
		for(Facing6 facing:Facing6.list)Assert.isFalse(reference.equals(new PosMutable(reference).move(facing)));
	}
}
