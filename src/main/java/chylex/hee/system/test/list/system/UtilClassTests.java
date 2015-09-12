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
	private final String mathError = "Unexpected value, expected $2, got $1.";
	private final String coordsError = "Unexpected coords, expected $2, got $1.";
	private final String coordError = "Unexpected coord, expected $2, got $1.";
	
	@UnitTest
	public void testColorUtilHsv(){
		float[] conv = ColorUtil.hsvToRgb(0.5F,0.55F,0.4F);
		
		Assert.equal(MathUtil.floor(conv[0]*255F),45,"Unexpected red value, expected $2, got $1.");
		Assert.equal(MathUtil.floor(conv[1]*255F),102,"Unexpected green value, expected $2, got $1.");
		Assert.equal(MathUtil.floor(conv[2]*255F),102,"Unexpected blue value, expected $2, got $1.");
	}
	
	@UnitTest
	public void testCycleProtection(){
		CycleProtection.suppressLogging = true;
		
		CycleProtection.setCounter(50);
		for(int a = 0; a < 51; a++, CycleProtection.proceed());
		Assert.state(CycleProtection.failed(),"Cycle protection did not report correct state, expected true on failed().");
		
		CycleProtection.suppressLogging = false;
	}
	
	@UnitTest
	public void testDragonUtilVec(){
		double[] vec = DragonUtil.getNormalizedVector(2D,3D);
		Vec3 vecRef = Vec3.createVectorHelper(2D,0D,3D).normalize();
		Assert.equal(vec[0],vecRef.xCoord,"Unexpected normalized vector X, expected $2, got $1.");
		Assert.equal(vec[1],vecRef.zCoord,"Unexpected normalized vector Z, expected $2, got $1.");
		Assert.equal(DragonUtil.getRandomVector(new Random()).lengthVector(),1F,"Unexpected length of randomized vector, expected $2, got $1.");
	}
	
	@UnitTest
	public void testDragonUtilArrays(){
		String[] values = new String[]{ "A", "B", null, "C", null };
		String[] nonNullValues = DragonUtil.getNonNullValues(values);
		Assert.state(!ArrayUtils.contains(nonNullValues,null),"Unexpected array elements, expected to not find any null elements. Array: "+ArrayUtils.toString(nonNullValues));
		Assert.equal(nonNullValues.length,3,"Unexpected length of array after removing null elements, expected $2, got $1. Array: "+ArrayUtils.toString(nonNullValues));
	}
	
	@UnitTest
	public void testDragonUtilChat(){
		Assert.equal(DragonUtil.stripChatFormatting(EnumChatFormatting.GOLD+"Text"),"Text","Unexpected characters after stripping chat formatting, expected '$2', got '$1'.");
	}
	
	@UnitTest
	public void testDragonUtilParsing(){
		String error = "Unexpected parsing result, expected $2, got $1.";
		Assert.equal(DragonUtil.tryParse("15",0),15,error);
		Assert.equal(DragonUtil.tryParse("+15",0),15,error);
		Assert.equal(DragonUtil.tryParse("-200",0),-200,error);
		Assert.equal(DragonUtil.tryParse("fail",5),5,error);
	}
	
	@UnitTest
	public void testMathUtilDegrees(){
		Assert.equal(MathUtil.toRad(180D),Math.PI,mathError);
		Assert.equal(MathUtil.toRad(180F),Math.PI,mathError);
		Assert.equal(MathUtil.toDeg((float)Math.PI),180F,mathError);
		Assert.equal(MathUtil.toDeg(Math.PI),180F,mathError);
	}
	
	@UnitTest
	public void testMathUtilSquare(){
		Assert.equal(MathUtil.square(5),25,mathError);
		Assert.equal(MathUtil.square(-2.5F),6.25F,mathError);
		Assert.equal(MathUtil.square(-2.5D),6.25D,mathError);
	}
	
	@UnitTest
	public void testMathUtilDistance(){
		Assert.equal(MathUtil.distance(4D,3D),5D,mathError);
		Assert.equal(MathUtil.distance(1D,2D,3D),3.7416573D,mathError);
	}
	
	@UnitTest
	public void testMathUtilRounding(){
		Assert.equal(MathUtil.floor(1.5D),1,mathError);
		Assert.equal(MathUtil.floor(1.5F),1,mathError);
		Assert.equal(MathUtil.ceil(5.1D),6,mathError);
		Assert.equal(MathUtil.ceil(5.1F),6,mathError);
	}
	
	@UnitTest
	public void testMathUtilClamping(){
		Assert.equal(MathUtil.clamp(5D,6D,8D),6D,mathError);
		Assert.equal(MathUtil.clamp(10F,6F,8F),8F,mathError);
		Assert.equal(MathUtil.clamp(7,6,8),7,mathError);
	}
	
	@UnitTest
	public void testMathUtilFloats(){
		Assert.state(MathUtil.floatEquals(0.333333333F,1F/3F),"Numbers are supposed to be equal.");
		Assert.state(!MathUtil.floatEquals(0.3333F,1F/3F),"Numbers are not supposed to be equal.");
	}
}
