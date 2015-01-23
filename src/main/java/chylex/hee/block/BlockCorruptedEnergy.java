package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.material.MaterialCorruptedEnergy;
import chylex.hee.block.state.BlockAbstractStateInt;
import chylex.hee.system.util.BlockPosM;

public class BlockCorruptedEnergy extends BlockAbstractStateInt{
	private static final Material corruptedEnergy = new MaterialCorruptedEnergy();
	
	public static final PropertyInteger LEVEL = PropertyInteger.create("level",0,15);
	
	private static final byte[] offsetX = new byte[]{ -1, 1, 0, 0, 0, 0 },
								offsetY = new byte[]{ 0, 0, -1, 1, 0, 0 },
								offsetZ = new byte[]{ 0, 0, 0, 0, -1, 1 };
	
	public static IBlockState createState(int level){
		return level <= 15 ? BlockList.corrupted_energy_low.setProperty(level) : BlockList.corrupted_energy_high.setProperty(level-16);
	}
	
	private final boolean isHighLevel;
	
	public BlockCorruptedEnergy(boolean isHighLevel){
		super(corruptedEnergy);
		setTickRandomly(true);
		this.isHighLevel = isHighLevel;
	}
	
	@Override
	protected IProperty[] getPropertyArray(){
		return new IProperty[]{ LEVEL };
	}
	
	public int getLevel(IBlockState state){
		return ((Integer)state.getValue(LEVEL)).intValue()+(isHighLevel ? 16 : 0);
	}
	
	public int decreaseLevel(int level){
		return level > 16 ? level-17 : level-1;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		if (world.isRemote){
			int x = pos.getX(), y = pos.getY(), z = pos.getZ();
			
			for(int a = 0; a < 3; a++){
				HardcoreEnderExpansion.fx.corruptedEnergy(world,x,y,z);
				HardcoreEnderExpansion.fx.enderGoo(world,x,y,z);
			}
			
			if (world.rand.nextInt(5) == 0)world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,x+0.5D,y+0.5D,z+0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D);
			return;
		}
		
		int level = getLevel(state);
		
		if (level > 1){
			BlockPosM testPos = new BlockPosM();
			
			for(int a = 0; a < 6; a++){
				if (rand.nextInt(3) == 0){
					Block block = testPos.moveTo(pos).moveBy(offsetX[a],offsetY[a],offsetZ[a]).getBlock(world);
					if (block.getMaterial() == Material.air)testPos.setBlock(world,setProperty(decreaseLevel(level)));
					else if (!block.isOpaqueCube() && testPos.moveBy(offsetX[a],offsetY[a],offsetZ[a]).getBlockMaterial(world) == Material.air){
						testPos.setBlock(world,setProperty(decreaseLevel(level)));
					}
				}
			}
		}
		
		if (world.rand.nextInt(7) <= 3 || world.rand.nextBoolean()){
			if (level == 16){
				if (isHighLevel)world.setBlockState(pos,BlockList.corrupted_energy_low.setProperty(15));
				else world.setBlockToAir(pos);
				return;
			}
			else world.setBlockState(pos,setProperty(decreaseLevel(level)));
		}
		
		world.scheduleUpdate(pos,this,tickRate(world));
	}
	
	@Override
	public int tickRate(World world){
		return 7;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
		if (entity instanceof EntityLivingBase && !(entity instanceof IBossDisplayData)){
			EntityLivingBase living = (EntityLivingBase)entity;
			int level = getLevel(world.getBlockState(pos));
			
			if (world.rand.nextInt(6-(level>>3)) == 0 && (living.hurtTime <= 3 || world.rand.nextInt(7) == 0) && living.getHealth() > 0F){
				if (entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;
					if (player.capabilities.isCreativeMode && player.getHealth() <= 1F)return;
					player.addStat(StatList.damageTakenStat,Math.round(10F));
				}
				
				float prevHealth = living.getHealth();
				living.setHealth(living.getHealth()-1F);
				living.getCombatTracker().func_94547_a(DamageSource.magic,prevHealth,1F); // OBFUSCATED second method
				living.hurtTime = 10; // change hurt time instead of hurtResistantTime
				
				if (living.getHealth() <= 0F)living.onDeath(DamageSource.magic);
			}
			
			living.attackEntityFrom(DamageSource.generic,1.5F);
		}
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return null;
	}
	
	@Override
	protected ItemStack createStackedBlock(IBlockState state){
		return null;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state){
		return null;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		if (world.rand.nextBoolean())HardcoreEnderExpansion.fx.corruptedEnergy(world,x,y,z);
		if (world.rand.nextBoolean())HardcoreEnderExpansion.fx.enderGoo(world,x,y,z);
		if (world.rand.nextInt(30) == 0)world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,x+0.5D,y+0.5D,z+0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D);
	}
}
