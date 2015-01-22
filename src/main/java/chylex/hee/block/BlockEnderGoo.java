package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.block.material.MaterialEnderGoo;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.BlockPosM;

public class BlockEnderGoo extends BlockFluidClassic{
	public static boolean shouldBattleWater = true;
	
	public static final Material enderGoo = new MaterialEnderGoo();
	public static final Fluid fluid = new Fluid("enderGoo").setDensity(1500).setTemperature(220).setViscosity(1500);
	
	private final byte[] xOff = new byte[]{ -1, 1, 0, 0, 0, 0 },
						 yOff = new byte[]{ 0, 0, -1, 1, 0, 0 },
						 zOff = new byte[]{ 0, 0, 0, 0, -1, 1 };
	
	public BlockEnderGoo(){
		super(fluid,enderGoo);
		disableStats();
		
		setQuantaPerBlock(5);
		setTickRate(18);
		setTickRandomly(true);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		if (!world.isBlockLoaded(pos.add(-1,0,-1)) || !world.isBlockLoaded(pos.add(1,0,1))){
			world.scheduleUpdate(pos,this,tickRate(world));
			return;
		}
		
		super.updateTick(world,pos,state,rand);
		
		if (shouldBattleWater){
			BlockPosM tmpPos = new BlockPosM(pos);
			int level = ((Integer)tmpPos.getBlockState(world).getValue(LEVEL)).intValue();
			
			for(int a = 0; a < 6; a++){
				if (tmpPos.moveTo(pos).moveBy(xOff[a],yOff[a],zOff[a]).getBlockMaterial(world) != Material.water)continue;
				
				if ((rand.nextInt(Math.max(1,10-level-(world.provider.getDimensionId() == 1 ? 7 : 0)+(a == 2 || a == 3 ? 2 : 0))) == 0)){
					tmpPos.setBlock(world,state.withProperty(LEVEL,Math.max(2,((Integer)tmpPos.getBlockState(world).getValue(LEVEL)).intValue())));
					if (rand.nextInt(6-level) == 0)tmpPos.moveTo(pos).setToAir(world);
				}
				else if (world.provider.getDimensionId() != 1 && rand.nextInt(4) != 0){
					tmpPos.moveTo(pos).setBlock(world,Blocks.flowing_water.getDefaultState().withProperty(LEVEL,2));
					
					for(int b = 0, index; b < 2+rand.nextInt(5); b++){
						index = rand.nextInt(6);
						if (tmpPos.moveTo(pos).moveBy(xOff[index],yOff[index],zOff[index]).getBlock(world) == this)tmpPos.setBlock(world,Blocks.flowing_water.getDefaultState().withProperty(LEVEL,2));
					}
					
					return;
				}
			}
		}
	}
	
	@Override
	public Vec3 modifyAcceleration(World world, BlockPos pos, Entity entity, Vec3 vec){
		return vec;
	}
	
	private static final PotionEffect weakness = new PotionEffect(Potion.weakness.id,5,1,false,true),
									  miningFatigue = new PotionEffect(Potion.digSlowdown.id,5,1,false,true),
									  poison = new PotionEffect(Potion.poison.id,100,2,false,true);
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
		if (entity instanceof EntityLivingBase && !(entity instanceof IIgnoreEnderGoo) && entity.getClass() != EntitySilverfish.class){
			EntityLivingBase e = (EntityLivingBase)entity;
			e.addPotionEffect(weakness);
			e.addPotionEffect(miningFatigue);
			
			PotionEffect eff = e.getActivePotionEffect(Potion.poison);
			if (eff == null){
				e.addPotionEffect(poison);
				if ((eff = e.getActivePotionEffect(Potion.poison)) == null)return;
			}
			
			if (eff.getDuration() < 102)eff.combine(new PotionEffect(Potion.poison.id,eff.getDuration()+17,eff.getAmplifier(),eff.getIsAmbient(),eff.getIsShowParticles()));
			
			Vec3 vec = new Vec3(0D,0D,0D);
			vec = super.modifyAcceleration(world,pos,entity,vec).normalize();
			
			entity.addVelocity(vec.xCoord*0.0075D,vec.yCoord*0.005D,vec.zCoord*0.0075D);
			entity.motionX *= 0.25D;
			entity.motionY *= 0.45D;
			entity.motionZ *= 0.25D;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		HardcoreEnderExpansion.fx.enderGoo(world,pos.getX(),pos.getY(),pos.getZ());
	}
	
	@SubscribeEvent
	public void onBucketFill(FillBucketEvent e){
		BlockPosM pos = new BlockPosM(e.target.getBlockPos());
		
		if (pos.getBlock(e.world) == this){
			pos.setToAir(e.world);
			e.result = new ItemStack(ItemList.bucket_ender_goo);
			e.setResult(Result.ALLOW);
		}
	}
}
