package chylex.hee.entity.fx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.util.MathUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityAltarOrbFX extends EntityFX{
	private static final ResourceLocation tex = new ResourceLocation("hardcoreenderexpansion:textures/particles/altar_orb.png");
	
	private final Vec3 movementVec;
	private double trueX,trueY,trueZ,targetX,targetY,targetZ;
	private float offsetDistance,offsetAngle[];
	private byte offsetAngleMode[];
	
	public EntityAltarOrbFX(World world, double x, double y, double z, double targetX, double targetY, double targetZ, EssenceType essenceType){
		super(world,x,y,z,0D,0D,0D);
		this.trueX = x;
		this.trueY = y;
		this.trueZ = z;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		
		particleScale = 0.08F;
		particleRed = essenceType.glyphColors[0];
		particleGreen = essenceType.glyphColors[1];
		particleBlue = essenceType.glyphColors[2];
		particleAlpha = 1F;
		
		movementVec = Vec3.createVectorHelper(targetX-posX,targetY-posY,targetZ-posZ).normalize();
		movementVec.xCoord *= 0.065D;
		movementVec.yCoord *= 0.065D;
		movementVec.zCoord *= 0.065D;
		
		offsetAngle = new float[3];
		offsetAngleMode = new byte[3];
		
		for(int a = 0; a < 3; a++){
			offsetAngle[a] = (float)(rand.nextDouble()*2D*Math.PI);
			offsetAngleMode[a] = (byte)rand.nextInt(2);
		}
	}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		trueX += movementVec.xCoord;
		trueY += movementVec.yCoord;
		trueZ += movementVec.zCoord;
		
		posX = trueX+Math.cos(offsetAngle[0])*offsetDistance;
		posY = trueY+Math.cos(offsetAngle[1])*offsetDistance;
		posZ = trueZ+Math.cos(offsetAngle[2])*offsetDistance;
		
		for(int a = 0; a < 3; a++){
			if (rand.nextInt(18) == 0)offsetAngleMode[a] = (byte)(offsetAngleMode[a] == 1?-1:1);
			offsetAngle[a] += offsetAngleMode[a]*0.0698131*rand.nextFloat()*0.2D;
		}
		
		double dist = Math.sqrt(MathUtil.square(trueX-targetX)+MathUtil.square(trueY-targetY)+MathUtil.square(trueZ-targetZ));

		if (++particleAge > 300 || dist < 0.06D){
			setDead();
		}
		else if (particleAge > 8 && particleAge < 35)offsetDistance += 0.015F;
		else if (dist < 1D && offsetDistance >= 0.015D)offsetDistance -= 0.015F;
	}
	
	@Override
	public void renderParticle(Tessellator tessellator, float partialTickTime, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY){
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		
		float x = (float)(prevPosX+(posX-prevPosX)*partialTickTime-interpPosX),
			  y = (float)(prevPosY+(posY-prevPosY)*partialTickTime-interpPosY),
			  z = (float)(prevPosZ+(posZ-prevPosZ)*partialTickTime-interpPosZ);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
		GL11.glDepthMask(false);
		GL11.glColor4f(1F,1F,1F,1F);
		RenderHelper.disableStandardItemLighting();
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(particleRed,particleGreen,particleBlue,particleAlpha);
		tessellator.setNormal(0F,1F,0F);
		tessellator.setBrightness(240);
		tessellator.addVertexWithUV(x-rotX*particleScale-rotYZ*particleScale,y-rotXZ*particleScale,z-rotZ*particleScale-rotXY*particleScale,1F,1F);
		tessellator.addVertexWithUV(x-rotX*particleScale+rotYZ*particleScale,y+rotXZ*particleScale,z-rotZ*particleScale+rotXY*particleScale,1F,0F);
		tessellator.addVertexWithUV(x+rotX*particleScale+rotYZ*particleScale,y+rotXZ*particleScale,z+rotZ*particleScale+rotXY*particleScale,0F,0F);
		tessellator.addVertexWithUV(x+rotX*particleScale-rotYZ*particleScale,y-rotXZ*particleScale,z+rotZ*particleScale-rotXY*particleScale,0F,1F);
		tessellator.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
	}

	@Override
	public int getFXLayer(){
		return 3;
	}
}
