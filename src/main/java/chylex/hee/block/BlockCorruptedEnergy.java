package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.material.MaterialCorruptedEnergy;
import chylex.hee.entity.GlobalMobData;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.abstractions.damage.special.ForcedDamage;
import chylex.hee.system.abstractions.damage.special.MultiDamage;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCorruptedEnergy extends Block{
	private static final Material corruptedEnergy = new MaterialCorruptedEnergy();
	
	private static final byte[] offsetX = new byte[]{ -1, 1, 0, 0, 0, 0 },
								offsetY = new byte[]{ 0, 0, -1, 1, 0, 0 },
								offsetZ = new byte[]{ 0, 0, 0, 0, -1, 1 };
	
	public static final BlockInfo getCorruptedEnergy(int level){
		if (level >= 16)return new BlockInfo(BlockList.corrupted_energy_high,MathUtil.clamp(level-16,0,15));
		else if (level >= 0)return new BlockInfo(BlockList.corrupted_energy_low,MathUtil.clamp(level,0,15));
		else return BlockInfo.air;
	}
	
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
		Pos pos = Pos.at(x,y,z);
		int level = (isHighLevel ? 16 : 0)+pos.getMetadata(world);
		
		if (world.isRemote){
			for(int a = MathUtil.floor(rand.nextFloat()+level/8F); a > 0; a--){
				HardcoreEnderExpansion.fx.corruptedEnergy(x,y,z);
				HardcoreEnderExpansion.fx.enderGoo(x,y,z);
			}
			
			if (world.rand.nextInt(Math.max(3,7-level/5)) == 0)FXHelper.create("explosion").pos(x,y,z).motionRand(0.5D).spawn(rand,1);
			return;
		}
		
		if (level > 0){
			for(int spreadAttempt = 0; spreadAttempt < 1+rand.nextInt(3); spreadAttempt++){
				Facing6 side = Facing6.list[rand.nextInt(Facing6.list.length)];
				Pos spreadPos = pos.offset(side);
				
				if (spreadPos.isAir(world))spreadPos.setBlock(world,getCorruptedEnergy(level-1-rand.nextInt(2)));
				else{
					spreadPos = spreadPos.offset(side);
					if (spreadPos.isAir(world))spreadPos.setBlock(world,getCorruptedEnergy(level-2-rand.nextInt(2)));
				}
			}
		}
		
		if (rand.nextInt(3) != 0)pos.setBlock(world,getCorruptedEnergy(level-1));
		
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
	}
	
	@Override
	public int tickRate(World world){
		return 7;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity instanceof EntityLivingBase && entity.hurtResistantTime == 0 && !GlobalMobData.isCorruptedEnergyTolerant((EntityLivingBase)entity) && ((EntityLivingBase)entity).getHealth() > 0F){
			int level = (isHighLevel ? 16 : 0)+Pos.at(x,y,z).getMetadata(world);
			
			MultiDamage.from(
				Damage.base(0.8F).addModifiers(IDamageModifier.difficultyScaling,IDamageModifier.armorProtection,IDamageModifier.enchantmentProtection),
				ForcedDamage.from(Damage.base(level/12F).addModifiers(IDamageModifier.nudityDanger,IDamageModifier.magicDamage,IDamageModifier.rapidDamage(3)))
			).deal(entity);
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
		if (world.rand.nextBoolean())HardcoreEnderExpansion.fx.corruptedEnergy(x,y,z);
		if (world.rand.nextBoolean())HardcoreEnderExpansion.fx.enderGoo(x,y,z);
		if (world.rand.nextInt(30) == 0)FXHelper.create("explosion").pos(x,y,z).motionRand(0.5D).spawn(rand,1);
	}
}
