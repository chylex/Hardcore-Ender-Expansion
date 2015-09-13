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
}
