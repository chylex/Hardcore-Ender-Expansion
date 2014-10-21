package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.entity.fx.EntityEnergyClusterFX;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnergyCluster extends BlockContainer{
	public static final SoundType soundTypeEnergyCluster = new SoundType("holycrapthisisloud",5F,1.6F){
		@Override
		public String getStepResourcePath(){
			return "dig.glass";
		}

		@Override
		public String getBreakSound(){
			return "dig.glass";
		}

		@Override
		public String func_150496_b(){ // OBFUSCATED placed block sound
			return "dig.glass";
		}
	};

	public BlockEnergyCluster(){
		super(Material.glass);
		setBlockBounds(0.3F,0.3F,0.3F,0.7F,0.7F,0.7F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnergyCluster(world);
	}
	
	/*@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		ItemStack is = player.inventory.getCurrentItem();
		if (is == null || is.getItem() != ItemList.end_powder)return false;
		
		TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(x,y,z);
		if (tile == null)return false;

		if (tile.data.getWeaknessLevel() > 0){
			if (!world.isRemote){
				tile.data.healWeakness(4+world.rand.nextInt(5)-world.rand.nextInt(2));
				if (tile.data.getWeaknessLevel() == 0)world.playAuxSFX(2005,x,y,z,0);
			}
		}
		else if (tile.data.getBoost() < EnergyClusterData.MAX_BOOST){
			if (tile.data.getBoost() == 0)world.playAuxSFX(2005,x,y,z,0);
			tile.data.boost();
		}
		else return false;
		
		tile.synchronize();
		if (!world.isRemote && !player.capabilities.isCreativeMode)--is.stackSize;
		
		for(int a = 0; a < 3; a++)world.spawnParticle("portal",x+world.rand.nextFloat(),y+world.rand.nextFloat(),z+world.rand.nextFloat(),0D,0D,0D);

		return true;
	}*/

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(x,y,z);
		if (tile == null || tile.shouldNotExplode)return;
		
		super.breakBlock(world,x,y,z,block,meta);
		destroyCluster(tile);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity instanceof EntityArrow || entity instanceof EntityThrowable)world.setBlockToAir(x,y,z);
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
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}

	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer){
		for(int a = 0; a < 4; a++)effectRenderer.addEffect(new EntityEnergyClusterFX(world,target.blockX+0.5D,target.blockY+0.5D,target.blockZ+0.5D,0D,0D,0D,0D,0D,0D));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer){
		for(int a = 0; a < 4; a++)effectRenderer.addEffect(new EntityEnergyClusterFX(world,x+0.5D,y+0.5D,z+0.5D,0D,0D,0D,0D,0D,0D));
		return true;
	}
	
	public static void destroyCluster(TileEntityEnergyCluster tile){
		World world = tile.getWorldObj();
		int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;
		int energyMeta = Math.min(15,3+(int)(tile.data.getEnergyLevel()*0.8F));
		
		DragonUtil.createExplosion(world,x+0.5D,y+0.5D,z+0.5D,2.8F+(energyMeta-3)*0.225F,true);
		
		// TODO return some to environment
		
		for(int xx = x-4; xx <= x+4; xx++){
			for(int zz = z-4; zz <= z+4; zz++){
				for(int yy = y-4; yy <= y+4; yy++){ // TODO larger dist for larger clusters
					if (MathUtil.distance(xx-x,yy-y,zz-z) <= 5D && world.isAirBlock(xx,yy,zz))world.setBlock(xx,yy,zz,BlockList.corrupted_energy_high,energyMeta,3);
				}
			}
		}
	}
}
