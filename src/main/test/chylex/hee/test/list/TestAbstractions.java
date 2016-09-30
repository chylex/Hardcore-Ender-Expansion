package chylex.hee.test.list;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.abstractions.nbt.NBTList;
import chylex.hee.test.Assert;
import chylex.hee.test.UnitTest;

public class TestAbstractions{
	@UnitTest
	public void testImmutablePos(){
		Pos reference = Pos.at(15, 32, -90);
		
		Assert.equal(reference, Pos.at(15.5D, 32.1D, -90D));
		Assert.equal(reference, Pos.at(new int[]{ 15, 32, -90 }));
		
		Assert.equal(reference.getX(), 15);
		Assert.equal(reference.getY(), 32);
		Assert.equal(reference.getZ(), -90);
		
		Assert.equal(reference, Pos.at(reference.toLong()));
		Assert.isTrue(reference == reference.immutable());
		
		Assert.equal(reference.offset(8, -2, 3), Pos.at(23, 30, -87));
		Assert.equal(reference.offset(0), Pos.at(15, 31, -90));
		Assert.equal(reference.offset(1, 8), Pos.at(15, 40, -90));
		Assert.equal(reference.offset(EnumFacing.NORTH), Pos.at(15, 32, -91));
		Assert.equal(reference.offset(EnumFacing.SOUTH, 3), Pos.at(15, 32, -87));
		Assert.equal(reference.offset(Facing4.EAST_POSX), Pos.at(16, 32, -90));
		Assert.equal(reference.offset(Facing6.WEST_NEGX, 5), Pos.at(10, 32, -90));
		
		for(Facing4 facing:Facing4.list)Assert.isFalse(reference.equals(reference.offset(facing)));
		for(Facing6 facing:Facing6.list)Assert.isFalse(reference.equals(reference.offset(facing)));
		
		Assert.equal(reference.getDown(), Pos.at(15, 31, -90));
		Assert.equal(reference.getUp(), Pos.at(15, 33, -90));
		Assert.equal(reference.getNorth(), Pos.at(15, 32, -91));
		Assert.equal(reference.getSouth(), Pos.at(15, 32, -89));
		Assert.equal(reference.getEast(), Pos.at(16, 32, -90));
		Assert.equal(reference.getWest(), Pos.at(14, 32, -90));
		
		Assert.equal(reference.distance(20, 32, -90), 5);
		Assert.equal(reference.distance(Pos.at(20, 32, -90)), 5);
		Assert.equal(reference.distance(reference), 0);
		Assert.equal(reference.distance(16, 38, -95), 7.874007874D);
		
		Assert.isFalse(reference.equals((Pos)null)); // NPE check
		Assert.equal(reference, Pos.at(15, 32, -90)); // immutability check
	}
	
	@UnitTest
	public void testMutablePos(){
		Pos reference = Pos.at(15, 32, -90);
		PosMutable mutable = new PosMutable(reference);
		
		Assert.equal(reference.getX(), mutable.x);
		Assert.equal(mutable.getX(), mutable.x);
		Assert.equal(reference.getY(), mutable.y);
		Assert.equal(mutable.getY(), mutable.y);
		Assert.equal(reference.getZ(), mutable.z);
		Assert.equal(mutable.getZ(), mutable.z);
		
		Assert.equal(mutable.toLong(), reference.toLong());
		Assert.isFalse(mutable == mutable.immutable());
		Assert.isFalse(mutable == mutable.copy());
		Assert.equal(mutable, mutable.immutable());
		Assert.equal(mutable, mutable.copy());
		
		Assert.equal(mutable.set(10, 25, -15), new PosMutable(10, 25, -15));
		Assert.equal(mutable.set(10.4D, 25.8D, -15), new PosMutable(10, 25, -15));
		Assert.equal(mutable.setX(10).setY(25).setZ(-15), new PosMutable(10, 25, -15));
		
		Assert.equal(mutable.set(11, 27, -20).move(-1, -2, 5), new PosMutable(10, 25, -15));
		Assert.equal(mutable.set(10, 26, -15).move(EnumFacing.DOWN), new PosMutable(10, 25, -15));
		Assert.equal(mutable.set(10, 21, -15).move(Facing6.UP_POSY, 4), new PosMutable(10, 25, -15));
		Assert.equal(mutable.set(10, 24, -15).moveUp(), new PosMutable(10, 25, -15));
		
		for(Facing4 facing:Facing4.list)Assert.isFalse(reference.equals(new PosMutable(reference).move(facing)));
		for(Facing6 facing:Facing6.list)Assert.isFalse(reference.equals(new PosMutable(reference).move(facing)));
	}
	
	@UnitTest
	public void testImmutableVec(){
		Assert.equal(Vec.xyzRandom(new Random()).normalized().length(), 1D);
		Assert.equal(Vec.xzRandom(new Random()).normalized().length(), 1D);
		
		Vec refXYZ = Vec.xyz(10D, 20.1D, -5D);
		Vec refXZ = Vec.xz(10D, -5D);
		Vec refZero = Vec.zero();
		
		Assert.equal(refXYZ.x, 10D);
		Assert.equal(refXYZ.y, 20.1D);
		Assert.equal(refXYZ.z, -5D);
		
		Assert.equal(refXZ.x, 10D);
		Assert.equal(refXZ.y, 0D);
		Assert.equal(refXZ.z, -5D);
		
		Assert.equal(refZero.x, 0D);
		Assert.equal(refZero.y, 0D);
		Assert.equal(refZero.z, 0D);
		
		Assert.equal(refXYZ, refXYZ.copy());
		Assert.isFalse(refXYZ == refXYZ.copy());
		
		Assert.equal(refXYZ.length(), 23.00021739D);
		Assert.equal(refXZ.length(), 11.18033989D);
		Assert.equal(refZero.length(), 0D);
		
		Assert.equal(refXYZ.normalized(), Vec.xyz(0.4347784992774615D, 0.8739047835476976D, -0.21738924963873074D));
		Assert.equal(refXZ.normalized(), Vec.xz(0.8944271909999159D, -0.4472135954999579D));
		Assert.equal(refZero.normalized(), Vec.zero());
		
		Assert.equal(refXYZ.offset(1D, -0.1D, -8D), Vec.xyz(11D, 20D, -13D));
		Assert.equal(refXYZ.offset(refXZ), Vec.xyz(20D, 20.1D, -10D));
		Assert.equal(refXYZ.offset(refXZ, 2D), Vec.xyz(30D, 20.1D, -15D));
		Assert.equal(refXYZ.offset(refZero), refXYZ);
		
		Assert.equal(refXYZ.multiplied(10D), Vec.xyz(100D, 201D, -50D));
		Assert.equal(refXZ.multiplied(-1D), Vec.xz(-10D, 5D));
		Assert.equal(refXYZ.multiplied(0D, -1D, -10D), Vec.xyz(0D, -20.1D, 50D));
		
		Assert.equal(refXYZ.interpolated(refXZ, 0D), refXYZ);
		Assert.equal(refXYZ.interpolated(refXZ, 1D), refXZ);
		Assert.equal(refXYZ.interpolated(refXZ, 0.5D), Vec.xyz(10D, 10.05D, -5D));
		
		Assert.equal(refXYZ.distance(refZero), 23.00021739D);
		Assert.equal(refXYZ.distance(refXZ), 20.1D);
		
		Assert.equal(refXYZ.toPos(), Pos.at(10, 20, -5));
		Assert.equal(Vec.from(refXYZ.toVec3()), refXYZ);
		Assert.isTrue(refXYZ.toAABB().expand(0.00001D, 0.00001D, 0.00001D).isVecInside(refXYZ.toVec3()));
		
		Assert.equal(refXYZ, Vec.xyz(10D, 20.1D, -5D));
		Assert.equal(refXZ, Vec.xz(10D, -5D));
		Assert.equal(refZero, Vec.zero());
	}
	
	@UnitTest
	public void testMutableVec(){
		Vec refXYZ = Vec.xyz(0D, 0D, 0D);
		refXYZ.moveBy(Vec.xyz(1D, 0.5D, -12D));
		
		Assert.equal(refXYZ, Vec.xyz(1D, 0.5D, -12D));
	}
	
	@UnitTest
	public void testNBT(){
		ItemStack is = new ItemStack(Blocks.bedrock);
		
		Assert.isTrue(NBT.item(is, false).isEmpty());
		Assert.isNull(is.getTagCompound());
		
		Assert.isTrue(NBT.item(is, true).isEmpty());
		Assert.notNull(is.getTagCompound());
		
		NBT.item(is, true).setBool("test", true);
		Assert.isTrue(NBT.item(is, true).getBool("test"));
	}
	
	@UnitTest
	public void testNBTCompound(){
		NBTCompound compound = new NBTCompound();
		
		Assert.isTrue(compound.isEmpty());
		Assert.equal(compound.size(), 0);
		Assert.equal(compound.keySet().size(), 0);
		
		compound.setBool("a", true);
		compound.setByte("b", Byte.MIN_VALUE);
		compound.setByteArray("c", new byte[]{ Byte.MAX_VALUE });
		compound.setCompound("d", new NBTCompound());
		compound.setDouble("e", 0D);
		compound.setFloat("f", 10.5F);
		compound.setInt("g", -30);
		compound.setIntArray("h", new int[]{ 1, 2 });
		compound.setList("i", new NBTList());
		compound.setLong("j", Long.MAX_VALUE);
		compound.setShort("k", Short.MIN_VALUE);
		compound.setString("l", "test");
		
		Assert.isFalse(compound.isEmpty());
		Assert.equal(compound.size(), 12);
		
		Assert.contains(compound.keySet(), "a");
		Assert.contains(compound.keySet(), "l");
		Assert.notContains(compound.keySet(), "z");
		
		Assert.equal(compound.getBool("a"), true);
		Assert.equal(compound.getByte("b"), Byte.MIN_VALUE);
		Assert.equal(compound.getByteArray("c")[0], Byte.MAX_VALUE);
		Assert.isTrue(compound.getCompound("d").isEmpty());
		Assert.equal(compound.getDouble("e"), 0D);
		Assert.equal(compound.getFloat("f"), 10.5F);
		Assert.equal(compound.getInt("g"), -30);
		Assert.equal(compound.getIntArray("h")[0], 1);
		Assert.equal(compound.getIntArray("h")[1], 2);
		Assert.isTrue(compound.getList("i").isEmpty());
		Assert.equal(compound.getLong("j"), Long.MAX_VALUE);
		Assert.equal(compound.getShort("k"), Short.MIN_VALUE);
		Assert.equal(compound.getString("l"), "test");
		
		compound.removeTag("a");
		Assert.equal(compound.size(), 11);
		Assert.notContains(compound.keySet(), "a");
		
		NBTTagCompound vanillaCompound = new NBTTagCompound();
		Assert.isTrue(vanillaCompound == NBT.wrap(vanillaCompound).getUnderlyingTag());
		
		vanillaCompound.setBoolean("a", true);
		vanillaCompound.setInteger("b", 10);
		vanillaCompound.setString("c", "test");
		
		Assert.equal(vanillaCompound.func_150296_c(), NBT.wrap(vanillaCompound).keySet());
		Assert.equal(vanillaCompound.toString(), NBT.wrap(vanillaCompound).toString());
	}
	
	@UnitTest
	public void testNBTList(){
		NBTList list = new NBTList();
		
		Assert.isTrue(list.isEmpty());
		Assert.equal(list.size(), 0);
		
		NBTList stringList = new NBTList();
		stringList.appendString("Test1");
		stringList.appendString("Test2");
		stringList.appendString("Test3");
		
		Assert.isFalse(stringList.isEmpty());
		Assert.equal(stringList.size(), 3);
		Assert.equal(stringList.getString(0), "Test1");
		Assert.equal(stringList.getString(1), "Test2");
		Assert.equal(stringList.getString(2), "Test3");
		
		NBTTagList vanillaList = new NBTTagList();
		Assert.isTrue(vanillaList == new NBTList(vanillaList).getUnderlyingTag());
	}
}
