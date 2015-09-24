package chylex.hee.entity.projectile;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.WorldGenStronghold;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileEyeOfEnder extends Entity{
	public int timer;
	private double moveX, moveZ, targetY;
	private float speed;
	private boolean foundStronghold;
	private Pos prevBlockPos;
	
	public EntityProjectileEyeOfEnder(World world){
		super(world);
		setSize(0.5F,1F);
	}
	
	public EntityProjectileEyeOfEnder(World world, Entity thrower){
		this(world);
		setPosition(thrower.posX+MathUtil.lendirx(1D,-thrower.rotationYaw),thrower.posY+1.1D+MathUtil.lendirx(1D,-thrower.rotationPitch),thrower.posZ+MathUtil.lendiry(1D,-thrower.rotationYaw));
	}

	@Override
	protected void entityInit(){
		dataWatcher.addObject(16,0);
		dataWatcher.addObject(17,0);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		++timer;
		
		if (timer == 40){
			double[] vec = DragonUtil.getNormalizedVector(dataWatcher.getWatchableObjectInt(16)+0.5D-posX,dataWatcher.getWatchableObjectInt(17)+0.5D-posZ);
			moveX = vec[0]*0.27D;
			moveZ = vec[1]*0.27D;
			targetY = posY;
		}
		else if (timer > 40){
			Pos center = Pos.at(this);
			
			if (!center.equals(prevBlockPos)){
				Set<Pos> checkedBlocks = new HashSet<>(); // y = 0
				Vec3 perpendicular = Vec3.createVectorHelper(-moveZ*3D,0D,moveX*3D);
				
				for(int line = -1; line <= 1; line++){
					Vec3 vec = Vec3.createVectorHelper(posX+line*perpendicular.xCoord,0D,posZ+line*perpendicular.zCoord);
					
					for(int distance = 0; distance < 12; distance++){
						vec.xCoord += moveX*4D;
						vec.zCoord += moveZ*4D;
						checkedBlocks.add(Pos.at(vec.xCoord,0,vec.zCoord));
					}
				}
				
				targetY = 2.5D+checkedBlocks.stream()
				.map(pos -> 1+Pos.getTopBlock(worldObj,pos.getX(),pos.getZ(),info -> isConsideredSolid(info)).getY())
				.sorted((i1, i2) -> Integer.compare(i2,i1))
				.limit(1+(checkedBlocks.size()/4))
				.mapToInt(height -> height).average().orElse(posY);
				
				prevBlockPos = center;
			}
			
			if (timer <= 399 && speed < 1F)speed += 0.02F;
			else if (timer >= 400 && speed > 0.25F)speed -= 0.01F;
			
			if (speed > 0.7F && targetY-posY > 4D)speed -= 0.05F;
			
			setPosition(posX+moveX*speed,posY+(targetY-posY)*(timer < 100 ? speed : 1F)*0.03D,posZ+moveZ*speed);
		}
		
		if (!worldObj.isRemote){
			if (timer == 1){
				Optional<ChunkCoordIntPair> stronghold = WorldGenStronghold.findNearestStronghold(MathUtil.floor(posX)>>4,MathUtil.floor(posZ)>>4,worldObj);
				
				if (stronghold.isPresent()){
					dataWatcher.updateObject(16,stronghold.get().chunkXPos+8);
					dataWatcher.updateObject(17,stronghold.get().chunkZPos+8);
					foundStronghold = true;
				}
			}
			else if (timer == 35 && !foundStronghold)setDead();
			else if (timer > 440 && timer > 440+rand.nextInt(100))setDead();
		}
		else{
			if (timer == 1)FXHelper.create("smoke").pos(posX,posY+getRenderOffset()+0.2F,posZ).fluctuatePos(0.15D).fluctuateMotion(0.1D).spawn(rand,8);
			else if (timer > 30){
				FXHelper.create("glitter")
				.pos(posX,posY+height*0.5F+(float)Math.sin(timer*0.15D)*0.25F,posZ)
				.fluctuatePos(0.2D)
				.motion(0D,-0.025D,0)
				.fluctuateMotion(0.02D)
				.paramColor(0.15F+rand.nextFloat()*0.3F,0.25F+rand.nextFloat()*0.4F,0.3F+rand.nextFloat()*0.05F)
				.spawn(rand,3);
			}
		}
	}
	
	private final float getRenderOffset(){
		return 0.35F+(float)Math.sin(timer*0.15D)*0.25F; // RenderProjectileEyeOfEnder
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float rotationYaw, float rotationPitch, int three){}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setShort("eoetim",(short)timer);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		timer = nbt.getShort("eoetim");
	}
	
	private static final boolean isConsideredSolid(BlockInfo info){
		return (info.block.getMaterial().blocksMovement() && info.block.renderAsNormalBlock()) || info.block instanceof BlockFluidBase || info.block instanceof BlockLiquid;
	}
}
