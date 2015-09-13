package chylex.hee.system.test.list.system;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;
import chylex.hee.system.util.MathUtil;

public class MathTests{
	@UnitTest
	public void testMathUtilDegrees(){
		Assert.equal(MathUtil.toRad(180D),Math.PI);
		Assert.equal(MathUtil.toRad(180F),Math.PI);
		Assert.equal(MathUtil.toRad(30D),Math.toRadians(30D));
		Assert.equal(MathUtil.toDeg((float)Math.PI),180F);
		Assert.equal(MathUtil.toDeg(Math.PI),180F);
		Assert.equal(MathUtil.toDeg(0.523333333D),Math.toDegrees(0.523333333D));
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
