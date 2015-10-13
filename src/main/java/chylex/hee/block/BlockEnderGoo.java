package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.material.MaterialEnderGoo;
import chylex.hee.entity.GlobalMobData;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing6;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderGoo extends BlockFluidClassic{
	public static boolean shouldBattleWater = true;
	
	public static final Material enderGoo = new MaterialEnderGoo();
	public static final Fluid fluid = new Fluid("enderGoo").setDensity(1500).setTemperature(220).setViscosity(1500);
	
	public BlockEnderGoo(){
		super(fluid,enderGoo);
		disableStats();
		
		setQuantaPerBlock(5);
		setTickRate(18);
		setTickRandomly(true);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		if (!world.blockExists(x-1,y,z-1) || !world.blockExists(x+1,y,z+1)){
			world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
			return;
		}
		
		super.updateTick(world,x,y,z,rand);
		
		if (shouldBattleWater){
			PosMutable mpos = new PosMutable();
			Pos currentPos = Pos.at(x,y,z);
			int meta = currentPos.getMetadata(world);
			
			for(Facing6 facing:Facing6.list){
				mpos.set(currentPos).move(facing);
				
				if (mpos.getMaterial(world) != Material.water)continue;
				
				if ((rand.nextInt(Math.max(1,10-meta-(world.provider.dimensionId == 1 ? 7 : 0)+(facing.getY() != 0 ? 2 : 0))) == 0)){
					mpos.setBlock(world,this,Math.max(2,mpos.getMetadata(world)));
					if (rand.nextInt(6-meta) == 0)mpos.setAir(world);
				}
				else if (world.provider.dimensionId != 1 && rand.nextInt(4) != 0){
					currentPos.setBlock(world,Blocks.flowing_water,2);
					
					for(int b = 0; b < 2+rand.nextInt(5); b++){
						mpos.set(currentPos).move(Facing6.list[rand.nextInt(Facing6.list.length)]);
						if (mpos.getBlock(world) == this)mpos.setBlock(world,Blocks.flowing_water,2);
					}
					
					return;
				}
			}
		}
	}
	
	@Override
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 vec){}
	
	private static final PotionEffect weakness = new PotionEffect(Potion.weakness.id,5,1,false),
									  miningFatigue = new PotionEffect(Potion.digSlowdown.id,5,1,false),
									  poison = new PotionEffect(Potion.poison.id,100,2,false);
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (!world.isRemote && entity instanceof EntityLivingBase && !GlobalMobData.isEnderGooTolerant((EntityLivingBase)entity)){
			EntityLivingBase e = (EntityLivingBase)entity;
			e.addPotionEffect(weakness);
			e.addPotionEffect(miningFatigue);
			
			PotionEffect eff = e.getActivePotionEffect(Potion.poison);
			if (eff == null){
				e.addPotionEffect(poison);
				if ((eff = e.getActivePotionEffect(Potion.poison)) == null)return;
			}
			
			if (eff.getDuration() < 102)eff.combine(new PotionEffect(Potion.poison.id,eff.getDuration()+17,eff.getAmplifier(),eff.getIsAmbient()));
			
			Vec3 vec = Vec3.createVectorHelper(0D,0D,0D);
			super.velocityToAddToEntity(world,x,y,z,entity,vec); // UPD breaks with removed vec mutability in 1.8
			vec.normalize();
			
			entity.addVelocity(vec.xCoord*0.0075D,vec.yCoord*0.005D,vec.zCoord*0.0075D);
			entity.motionX *= 0.25D;
			entity.motionY *= 0.45D;
			entity.motionZ *= 0.25D;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		HardcoreEnderExpansion.fx.enderGoo(x,y,z);
	}
	
	@SubscribeEvent
	public void onBucketFill(FillBucketEvent e){
		Pos pos = Pos.at(e.target);
		
		if (pos.getBlock(e.world) == this){
			pos.setAir(e.world);
			e.result = new ItemStack(ItemList.bucket_ender_goo);
			e.setResult(Result.ALLOW);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		super.registerBlockIcons(iconRegister);
		fluid.setIcons(blockIcon);
	}
}
