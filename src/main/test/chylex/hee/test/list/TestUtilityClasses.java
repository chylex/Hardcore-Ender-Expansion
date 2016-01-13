package chylex.hee.test.list;
import java.util.Arrays;
import java.util.Calendar;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.system.util.BooleanByte;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.CycleProtection;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.test.Assert;
import chylex.hee.test.UnitTest;

public class TestUtilityClasses{
	@UnitTest
	public void testBooleanByte(){
		BooleanByte bb;
		
		bb = new BooleanByte();
		bb.set(0);
		bb.set(2);
		bb.set(5);
		Assert.equal(bb.get(0),true);
		Assert.equal(bb.get(1),false);
		Assert.equal(bb.get(2),true);
		Assert.equal(bb.toByte(),0b00100101);
		Assert.isTrue(bb.equals(new BooleanByte(bb.toByte())));
		
		bb = BooleanByte.of(true,true,false,true,true,true);
		Assert.equal(bb.get(0),true);
		Assert.equal(bb.get(2),false);
		Assert.equal(bb.get(4),true);
		Assert.equal(bb.get(5),true);
		Assert.equal(bb.get(6),false);
		Assert.equal(bb.get(7),false);
	}
	
	@UnitTest
	public void testColorUtil(){
		float[] hsv;
		
		hsv = ColorUtil.hsvToRgb(0.5F,0.55F,0.4F);
		Assert.equal(MathUtil.floor(hsv[0]*255F),45);
		Assert.equal(MathUtil.floor(hsv[1]*255F),102);
		Assert.equal(MathUtil.floor(hsv[2]*255F),102);
		
		hsv = ColorUtil.hsvToRgb(0.75F,0.7F,0.7F);
		Assert.equal(MathUtil.floor(hsv[0]*255F),116);
		Assert.equal(MathUtil.floor(hsv[1]*255F),53);
		Assert.equal(MathUtil.floor(hsv[2]*255F),178);
	}
	
	@UnitTest
	public void testCycleProtection(){
		CycleProtection.suppressLogging = true;
		
		CycleProtection.setCounter(50);
		for(int a = 0; a < 49; a++, CycleProtection.proceed());
		Assert.isFalse(CycleProtection.failed());
		
		CycleProtection.setCounter(50);
		for(int a = 0; a < 50; a++, CycleProtection.proceed());
		Assert.isFalse(CycleProtection.failed());
		
		CycleProtection.setCounter(50);
		for(int a = 0; a < 51; a++, CycleProtection.proceed());
		Assert.isTrue(CycleProtection.failed());
		
		CycleProtection.suppressLogging = false;
	}
	
	@UnitTest
	public void testDragonUtil(){
		Assert.equal(DragonUtil.stripChatFormatting(EnumChatFormatting.GOLD+"Text"),"Text");
		Assert.equal(DragonUtil.stripChatFormatting(EnumChatFormatting.BOLD.toString()+EnumChatFormatting.RED.toString()+"Text"),"Text");
		
		Assert.equal(DragonUtil.tryParse("15",0),15);
		Assert.equal(DragonUtil.tryParse("+15",0),15);
		Assert.equal(DragonUtil.tryParse("-200",0),-200);
		Assert.equal(DragonUtil.tryParse("fail",5),5);
		
		String[] testValues = new String[]{ "A", "B", null, "C", null };
		String[] nonNullValues = DragonUtil.getNonNullValues(testValues);
		
		Assert.equal(nonNullValues.length,3);
		Assert.notContains(Arrays.asList(nonNullValues),null);
		
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		date1.set(2010,4,1,16,0,0);
		
		date2.set(2010,4,1,16,0,0);
		Assert.equal(DragonUtil.getDayDifference(date1,date2),0);
		date2.set(2010,4,2,15,0,0);
		Assert.equal(DragonUtil.getDayDifference(date1,date2),0);
		date2.set(2010,4,2,16,0,0);
		Assert.equal(DragonUtil.getDayDifference(date1,date2),1);
		date2.set(2010,4,2,17,0,0);
		Assert.equal(DragonUtil.getDayDifference(date1,date2),1);
		date2.set(2010,3,22,16,0,0);
		Assert.equal(DragonUtil.getDayDifference(date1,date2),9);
		date2.set(2011,4,1,16,0,0);
		Assert.equal(DragonUtil.getDayDifference(date1,date2),365);
	}
	
	@UnitTest
	public void testMathUtil(){
		Assert.equal(MathUtil.toRad(180D),Math.PI);
		Assert.equal(MathUtil.toRad(180F),Math.PI);
		Assert.equal(MathUtil.toRad(30D),Math.toRadians(30D));
		
		Assert.equal(MathUtil.toDeg((float)Math.PI),180F);
		Assert.equal(MathUtil.toDeg(Math.PI),180F);
		Assert.equal(MathUtil.toDeg(0.523333333D),Math.toDegrees(0.523333333D));
		
		Assert.equal(MathUtil.square(5),25);
		Assert.equal(MathUtil.square(-2.5F),6.25F);
		Assert.equal(MathUtil.square(-2.5D),6.25D);
		
		Assert.equal(MathUtil.distance(4D,3D),5D);
		Assert.equal(MathUtil.distance(1D,2D,3D),3.7416573D);
		Assert.equal(MathUtil.distanceSquared(1D,2D,3D),14D);
		
		Assert.isTrue(MathUtil.triangle(0,0,-1,-1,1,-1,1,1));
		Assert.isTrue(MathUtil.triangle(-1,-1,-1,-1,1,-1,1,1));
		Assert.isFalse(MathUtil.triangle(-2,-1,-1,-1,1,-1,1,1));
		
		Assert.equal(MathUtil.floor(1.5D),1);
		Assert.equal(MathUtil.floor(1.5F),1);
		Assert.equal(MathUtil.floor(-1.5F),-2);
		Assert.equal(MathUtil.ceil(1.5D),2);
		Assert.equal(MathUtil.ceil(1.5F),2);
		Assert.equal(MathUtil.ceil(-1.5F),-1);
		
		Assert.equal(MathUtil.clamp(5D,6D,8D),6D);
		Assert.equal(MathUtil.clamp(10F,6F,8F),8F);
		Assert.equal(MathUtil.clamp(7,6,8),7);
		
		Assert.isTrue(MathUtil.floatEquals(0.333333333F,1F/3F));
		Assert.isFalse(MathUtil.floatEquals(0.3333F,1F/3F));
	}
}
