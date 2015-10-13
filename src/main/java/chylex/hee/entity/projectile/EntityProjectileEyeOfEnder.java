package chylex.hee.entity.projectile;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fluids.BlockFluidBase;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.WorldGenStronghold;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileEyeOfEnder extends Entity{
	public int timer;
	private double moveX, moveZ, targetY;
	private float speed;
	private Pos prevBlockPos;
	private int strongholdX, strongholdZ, maxTerrainY;
	
	private boolean foundStronghold;
	
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
		dataWatcher.addObject(18,(short)0);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		++timer;
		
		if (worldObj.provider.dimensionId != 0){
			if (timer == 1 && worldObj.isRemote)FXHelper.create("smoke").pos(posX,posY+getRenderOffset()+0.2F,posZ).fluctuatePos(0.15D).fluctuateMotion(0.1D).spawn(rand,8);
			else if (timer > 60 && !worldObj.isRemote){
				EntityItem item = new EntityItem(worldObj,posX,posY+getRenderOffset(),posZ,new ItemStack(Items.ender_eye));
				item.delayBeforeCanPickup = 10;
				worldObj.spawnEntityInWorld(item);
				
				PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.POP,posX,posY,posZ,1.5F,1F+rand.nextFloat()*0.25F));
				setDead();
			}
			
			return;
		}
		
		if (timer == 40){
			strongholdX = dataWatcher.getWatchableObjectInt(16);
			strongholdZ = dataWatcher.getWatchableObjectInt(17);
			maxTerrainY = dataWatcher.getWatchableObjectShort(18);
			
			double[] vec = DragonUtil.getNormalizedVector(strongholdX+0.5D-posX,strongholdZ+0.5D-posZ);
			moveX = vec[0]*0.27D;
			moveZ = vec[1]*0.27D;
		}
		else if (timer > 40){
			Pos center = Pos.at(this);
			
			if (!center.equals(prevBlockPos)){
				Set<Pos> checkedBlocks = new HashSet<>(); // y = 0
				Vec perpendicular = Vec.xz(-moveZ*3D,moveX*3D);
				
				for(int line = -1; line <= 1; line++){
					Vec vec = Vec.xz(posX+line*perpendicular.x,posZ+line*perpendicular.z);
					
					for(int distance = 0; distance < 12; distance++){
						vec.x += moveX*4D;
						vec.z += moveZ*4D;
						checkedBlocks.add(vec.toPos());
					}
				}
				
				targetY = 2.5D+checkedBlocks.stream()
				.map(pos -> 1+Pos.getTopBlock(worldObj,pos.getX(),pos.getZ(),maxTerrainY,info -> isConsideredSolid(info)).getY())
				.sorted((i1, i2) -> Integer.compare(i2,i1))
				.limit(1+(checkedBlocks.size()/4))
				.mapToInt(height -> height).average().orElse(posY);
				
				prevBlockPos = center;
			}
			
			if (MathUtil.distance(strongholdX+0.5D-posX,strongholdZ+0.5D-posZ) < 7D){
				if (speed > 0F)speed -= 0.04F;
			}
			else{
				if (timer <= 399 && speed < 1F)speed += 0.02F;
				else if (timer >= 400 && speed > 0.25F)speed -= 0.015F;
				
				if (speed > 0.7F && targetY-posY > 4D)speed -= 0.05F;
			}
			
			setPosition(posX+moveX*speed,posY+(targetY-posY)*(timer < 100 ? speed : 1F)*0.03D,posZ+moveZ*speed);
		}
		
		if (!worldObj.isRemote){
			if (timer == 1){
				Optional<ChunkCoordIntPair> stronghold = WorldGenStronghold.findNearestStronghold(MathUtil.floor(posX)>>4,MathUtil.floor(posZ)>>4,worldObj);
				
				if (stronghold.isPresent()){
					dataWatcher.updateObject(16,16*stronghold.get().chunkXPos+8);
					dataWatcher.updateObject(17,16*stronghold.get().chunkZPos+8);
					dataWatcher.updateObject(18,(short)(worldObj.getWorldInfo().getTerrainType() == WorldType.AMPLIFIED ? 256 : 128)); // ignore floating islands from other mods
					foundStronghold = true;
				}
			}
			else if (timer == 35 && !foundStronghold)setDead();
			else{
				if (MathUtil.distance(strongholdX+0.5D-posX,strongholdZ+0.5D-posZ) < 7D){
					if (timer < 500)timer = 500;
					
					if (timer > 580){
						EntityItem item = new EntityItem(worldObj,posX,posY+getRenderOffset(),posZ,new ItemStack(Items.ender_eye));
						item.delayBeforeCanPickup = 10;
						worldObj.spawnEntityInWorld(item);
						
						PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.POP,posX,posY,posZ,1.5F,1F+rand.nextFloat()*0.25F));
						setDead();
					}
				}
				else if (timer > 440 && timer > 440+rand.nextInt(100)){
					PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ENDER_EYE_BREAK,posX,posY+getRenderOffset(),posZ,0F,0F));
					setDead();
				}
			}
		}
		else{
			if (timer == 1)FXHelper.create("smoke").pos(posX,posY+getRenderOffset()+0.2F,posZ).fluctuatePos(0.15D).fluctuateMotion(0.1D).spawn(rand,8);
			else if (timer > 30){
				float r, g, b;
				
				if (rand.nextInt(3) == 0){
					r = 0.3F+rand.nextFloat()*0.2F;
					g = 0.25F+rand.nextFloat()*0.05F;
					b = 0.5F+rand.nextFloat()*0.25F;
				}
				else{
					r = 0.2F+rand.nextFloat()*0.1F;
					g = 0.25F+rand.nextFloat()*0.4F;
					b = 0.3F+rand.nextFloat()*0.1F;
				}
				
				FXHelper.create("glitter").pos(posX-moveX*1.5D*speed,posY+getRenderOffset(),posZ-moveZ*1.5D*speed).fluctuatePos(0.15D).motion(0D,-0.025D,0D).fluctuateMotion(0.02D).paramColor(r,g,b).spawn(rand,3);
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
