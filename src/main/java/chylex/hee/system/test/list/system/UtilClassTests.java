package chylex.hee.system.test.list.system;
import java.util.Random;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.CycleProtection;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class UtilClassTests{
	@UnitTest
	public void testColorUtilHsv(){
		float[] conv = ColorUtil.hsvToRgb(0.5F,0.55F,0.4F);
		
		Assert.equal(MathUtil.floor(conv[0]*255F),45);
		Assert.equal(MathUtil.floor(conv[1]*255F),102);
		Assert.equal(MathUtil.floor(conv[2]*255F),102);
	}
	
	@UnitTest
	public void testCycleProtection(){
		CycleProtection.suppressLogging = true;
		
		CycleProtection.setCounter(50);
		for(int a = 0; a < 51; a++, CycleProtection.proceed());
		Assert.isTrue(CycleProtection.failed());
		
		CycleProtection.suppressLogging = false;
	}
	
	@UnitTest
	public void testDragonUtilVec(){
		double[] vec = DragonUtil.getNormalizedVector(2D,3D);
		Vec3 vecRef = Vec3.createVectorHelper(2D,0D,3D).normalize();
		Assert.equal(vec[0],vecRef.xCoord);
		Assert.equal(vec[1],vecRef.zCoord);
		Assert.equal(DragonUtil.getRandomVector(new Random()).lengthVector(),1F);
	}
	
	@UnitTest
	public void testDragonUtilArrays(){
		String[] values = new String[]{ "A", "B", null, "C", null };
		String[] nonNullValues = DragonUtil.getNonNullValues(values);
		Assert.isFalse(ArrayUtils.contains(nonNullValues,null),"Unexpected array elements, expected to not find any null elements. Array: "+ArrayUtils.toString(nonNullValues));
		Assert.equal(nonNullValues.length,3,"Unexpected length of array after removing null elements, expected $2, got $1. Array: "+ArrayUtils.toString(nonNullValues));
	}
	
	@UnitTest
	public void testDragonUtilChat(){
		Assert.equal(DragonUtil.stripChatFormatting(EnumChatFormatting.GOLD+"Text"),"Text");
	}
	
	@UnitTest
	public void testDragonUtilParsing(){
		Assert.equal(DragonUtil.tryParse("15",0),15);
		Assert.equal(DragonUtil.tryParse("+15",0),15);
		Assert.equal(DragonUtil.tryParse("-200",0),-200);
		Assert.equal(DragonUtil.tryParse("fail",5),5);
	}
	
	@UnitTest
	public void testMathUtilDegrees(){
		Assert.equal(MathUtil.toRad(180D),Math.PI);
		Assert.equal(MathUtil.toRad(180F),Math.PI);
		Assert.equal(MathUtil.toDeg((float)Math.PI),180F);
		Assert.equal(MathUtil.toDeg(Math.PI),180F);
	}
	
	@UnitTest
	public void testMathUtilSquare(){
		Assert.equal(MathUtil.square(5),25);
		Assert.equal(MathUtil.square(-2.5F),6.25F);
		Assert.equal(MathUtil.square(-2.5D),6.25D);
	}
	
	@UnitTest
	public void testMathUtilDistance(){
		Assert.equal(MathUtil.distance(4D,3D),5D);
		Assert.equal(MathUtil.distance(1D,2D,3D),3.7416573D);
	}
	
	@UnitTest
	public void testMathUtilRounding(){
		Assert.equal(MathUtil.floor(1.5D),1);
		Assert.equal(MathUtil.floor(1.5F),1);
		Assert.equal(MathUtil.ceil(5.1D),6);
		Assert.equal(MathUtil.ceil(5.1F),6);
	}
	
	@UnitTest
	public void testMathUtilClamping(){
		Assert.equal(MathUtil.clamp(5D,6D,8D),6D);
		Assert.equal(MathUtil.clamp(10F,6F,8F),8F);
		Assert.equal(MathUtil.clamp(7,6,8),7);
	}
	
	@UnitTest
	public void testMathUtilFloats(){
		Assert.isTrue(MathUtil.floatEquals(0.333333333F,1F/3F));
		Assert.isFalse(MathUtil.floatEquals(0.3333F,1F/3F));
	}
}
