package chylex.hee.entity.fx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.mechanics.energy.EnergyClusterHealth;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityEnergyFX extends EntityFX{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/particles/energy.png");
	
	private byte indexX, indexY;
	private int age, maxAge;
	private boolean checkBreaking ;
	
	private EntityEnergyFX(World world, double x, double y, double z, float red, float green, float blue){
		super(world,x,y,z,0D,0D,0D);
		
		indexX = (byte)rand.nextInt(4);
		indexY = (byte)rand.nextInt(4);
		maxAge = 70+rand.nextInt(45);
		particleScale = rand.nextFloat()*0.2F+0.4F;
		
		particleRed = red;
		particleGreen = green;
		particleBlue = blue;
		
		motionX = motionY = motionZ = particleAlpha = 0;
	}
	
	public EntityEnergyFX(World world, double x, double y, double z, float red, float green, float blue, EnergyClusterData data){
		this(world,x+(world.rand.nextDouble()-0.5D)*0.005D*data.getEnergyLevel(),y+(world.rand.nextDouble()-0.5D)*0.005D*data.getEnergyLevel(),z+(world.rand.nextDouble()-0.5D)*0.005D*data.getEnergyLevel(),red,green,blue);
		
		particleScale = 0.075F+rand.nextFloat()*0.05F+0.005F*data.getEnergyLevel();
		
		if (rand.nextInt(5)+1 < data.getHealth().ordinal()){
			float mp = 1F-0.4F*((float)data.getHealth().ordinal()/EnergyClusterHealth.values.length);
			particleRed *= mp;
			particleGreen *= mp;
			particleBlue *= mp;
		}
	}
	
	public EntityEnergyFX(World world, TileEntityEnergyCluster cluster){
		this(world,cluster.xCoord+0.5D+(world.rand.nextDouble()-0.5D)*0.1D,cluster.yCoord+0.5D+(world.rand.nextDouble()-0.5D)*0.1D,cluster.zCoord+0.5D+(world.rand.nextDouble()-0.5D)*0.1D,cluster.getColor(0),cluster.getColor(1),cluster.getColor(2),cluster.getData().get());
		this.checkBreaking = true;
	}
	
	public EntityEnergyFX(World world, double x, double y, double z, float red, float green, float blue, double motionX, double motionY, double motionZ){
		this(world,x,y,z,red,green,blue);
		
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.particleScale = 0.04F+rand.nextFloat()*0.1F;
	}
	
	public EntityEnergyFX(World world, double x, double y, double z, float red, float green, float blue, double motionX, double motionY, double motionZ, float scale){
		this(world,x,y,z,red,green,blue,motionX,motionY,motionZ);
		this.particleScale = scale;
	}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (++age > maxAge)setDead();
		if (age < 20)particleAlpha = Math.min(1F,particleAlpha+rand.nextFloat()*0.2F);
		if (age > maxAge-18)particleAlpha = Math.max(0F,particleAlpha-rand.nextFloat()*0.25F);
		
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		
		if (rand.nextInt(3) == 0)posX += rand.nextDouble()*0.012D-0.006D;
		if (rand.nextInt(3) == 0)posY += rand.nextDouble()*0.012D-0.006D;
		if (rand.nextInt(3) == 0)posZ += rand.nextDouble()*0.012D-0.006D;
		
		if (checkBreaking && Pos.at(this).getBlock(worldObj) != BlockList.energy_cluster)setDead();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float partialTickTime){
		return 0;
	}

	@Override
	public float getBrightness(float partialTickTime){
		return 0F;
	}
	
	@Override
	public void renderParticle(Tessellator tessellator, float partialTickTime, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY){
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		float left = indexX*0.25F, right = left+0.25F,
			  top = indexY*0.25F, bottom = top+0.25F,
		      x = (float)(prevPosX+(posX-prevPosX)*partialTickTime-interpPosX),
		      y = (float)(prevPosY+(posY-prevPosY)*partialTickTime-interpPosY),
		      z = (float)(prevPosZ+(posZ-prevPosZ)*partialTickTime-interpPosZ);
		
		GL.pushMatrix();
		GL.disableDepthMask();
		GL.disableLighting();
		GL.disableFog();
		GL.disableAlphaTest();
		GL.enableBlend(GL.SRC_ALPHA,GL.ONE);
		GL.color(1F,1F,1F,1F);
		RenderHelper.disableStandardItemLighting();
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(particleRed,particleGreen,particleBlue,particleAlpha);
		tessellator.setNormal(0F,1F,0F);
		tessellator.setBrightness(65);
		tessellator.addVertexWithUV(x-rotX*particleScale-rotYZ*particleScale,y-rotXZ*particleScale,z-rotZ*particleScale-rotXY*particleScale,right,bottom);
		tessellator.addVertexWithUV(x-rotX*particleScale+rotYZ*particleScale,y+rotXZ*particleScale,z-rotZ*particleScale+rotXY*particleScale,right,top);
		tessellator.addVertexWithUV(x+rotX*particleScale+rotYZ*particleScale,y+rotXZ*particleScale,z+rotZ*particleScale+rotXY*particleScale,left,top);
		tessellator.addVertexWithUV(x+rotX*particleScale-rotYZ*particleScale,y-rotXZ*particleScale,z+rotZ*particleScale-rotXY*particleScale,left,bottom);
		tessellator.draw();
		
		GL.disableBlend();
		GL.enableLighting();
		GL.enableAlphaTest();
		GL.enableDepthMask();
		GL.popMatrix();
	}
	
	@Override
	public int getFXLayer(){
		return 3;
	}
}
