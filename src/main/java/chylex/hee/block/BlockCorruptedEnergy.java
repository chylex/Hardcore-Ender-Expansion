package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCorruptedEnergy extends Block{
	private static final Material corruptedEnergy = new MaterialCorruptedEnergy();
	
	private static final byte[] offsetX = new byte[]{ -1, 1, 0, 0, 0, 0 },
								offsetY = new byte[]{ 0, 0, -1, 1, 0, 0 },
								offsetZ = new byte[]{ 0, 0, 0, 0, -1, 1 };
	
	private final boolean isHighLevel;
	
	public BlockCorruptedEnergy(boolean isHighLevel){
		super(corruptedEnergy);
		setTickRandomly(true);
		this.isHighLevel = isHighLevel;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		if (world.isRemote){
			for(int a = 0; a < 3; a++){
				HardcoreEnderExpansion.fx.corruptedEnergy(world,x,y,z);
				HardcoreEnderExpansion.fx.enderGoo(world,x,y,z);
			}
			
			if (world.rand.nextInt(5) == 0)world.spawnParticle("explode",x+0.5D,y+0.5D,z+0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D);
			return;
		}
		
		int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
		
		if (meta > 1){
			BlockPosM tmpPos = BlockPosM.tmp();
			
			for(int a = 0; a < 6; a++){
				if (rand.nextInt(3) == 0){
					Block block = tmpPos.set(x+offsetX[a],y+offsetY[a],z+offsetZ[a]).getBlock(world);
					
					if (block.getMaterial() == Material.air)tmpPos.setBlock(world,this,meta-1);
					else if (!block.isOpaqueCube() && tmpPos.set(x+offsetX[a]*2,y+offsetY[a]*2,z+offsetZ[a]*2).getMaterial(world) == Material.air){
						tmpPos.setBlock(world,this,meta-1,3);
					}
				}
			}
		}
		
		if (world.rand.nextInt(7) <= 3 || world.rand.nextBoolean()){
			if (meta == 1){
				if (isHighLevel)BlockPosM.tmp(x,y,z).setBlock(world,BlockList.corrupted_energy_low,15);
				else BlockPosM.tmp(x,y,z).setAir(world);
				return;
			}
			else BlockPosM.tmp(x,y,z).setMetadata(world,meta-1);
		}
		
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
	}
	
	@Override
	public int tickRate(World world){
		return 7;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity instanceof EntityLivingBase && !(entity instanceof IBossDisplayData)){
			EntityLivingBase living = (EntityLivingBase)entity;
			int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
			
			if (world.rand.nextInt(meta >= 10 ? 3 : (meta >= 5 ? 4 : 5)) == 0 && (living.hurtTime <= 3 || world.rand.nextInt(7) == 0) && living.getHealth() > 0F){
				if (entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;
					if (player.capabilities.isCreativeMode && player.getHealth() <= 1F)return;
					player.addStat(StatList.damageTakenStat,Math.round(10F));
				}
				
				living.prevHealth = living.getHealth();
				living.setHealth(living.getHealth()-1F);
				living.func_110142_aN().func_94547_a(DamageSource.magic,living.prevHealth,1F); // OBFUSCATED combat tracker, second method
				living.hurtTime = 10; // change hurt time instead of hurtResistantTime
				
				if (living.getHealth() <= 0F)living.onDeath(DamageSource.magic);
			}
			
			living.attackEntityFrom(DamageSource.generic,1.5F);
		}
	}
	
	@Override
	public boolean isAir(IBlockAccess world, int x, int y, int z){
		return true;
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	protected ItemStack createStackedBlock(int meta){
		return null;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		return null;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean canCollideCheck(int meta, boolean holdingBoat){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		if (world.rand.nextBoolean())HardcoreEnderExpansion.fx.corruptedEnergy(world,x,y,z);
		if (world.rand.nextBoolean())HardcoreEnderExpansion.fx.enderGoo(world,x,y,z);
		if (world.rand.nextInt(30) == 0)world.spawnParticle("explode",x+0.5D,y+0.5D,z+0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D);
	}
}
