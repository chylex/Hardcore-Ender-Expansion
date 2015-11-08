package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.block.sound.SoundTypeSingle;
import chylex.hee.entity.fx.EntityEnergyFX;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnergyCluster extends BlockContainer{
	public static final SoundType soundTypeEnergyCluster = new SoundTypeSingle("dig.glass",5F,1.6F);

	public BlockEnergyCluster(){
		super(Material.glass);
		setBlockBounds(0.35F,0.35F,0.35F,0.65F,0.65F,0.65F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnergyCluster();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(x,y,z);
		if (tile == null || tile.shouldNotExplode)return;
		
		super.breakBlock(world,x,y,z,block,meta);
		destroyCluster(tile);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack is){
		Pos.at(x,y,z).<TileEntityEnergyCluster>getTileEntity(world).generate(EnergyClusterGenerator.creative,world.rand);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity instanceof EntityArrow || entity instanceof EntityThrowable)Pos.at(x,y,z).setAir(world);
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
		for(int a = 0; a < 4; a++)effectRenderer.addEffect(new EntityEnergyFX(world,target.blockX+0.5D,target.blockY+0.5D,target.blockZ+0.5D,0F,0F,0F,0D,0D,0D));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer){
		FXHelper.create("smoke")
		.pos(x,y,z)
		.fluctuatePos(0.1D)
		.fluctuateMotion(0.05D)
		.paramSingle(0.75F)
		.spawn(world.rand,18);
		
		return true;
	}
	
	public static void destroyCluster(final TileEntityEnergyCluster tile){
		World world = tile.getWorldObj();
		int units = MathUtil.ceil(tile.getData().map(data -> data.getEnergyLevel()).orElse(0F)/EnergyValues.unit);
		
		float explosionRad = Math.min(2.5F+(float)Math.sqrt(units)/10F,7F);
		double blockDist = 2D+Math.pow(units,0.75D)/75D;
		int energyMeta = 5+MathUtil.ceil(units/60F);
		int ethereum = MathUtil.floor((1+world.rand.nextInt(3))*Math.pow(units,0.2D));

		int iBlockDist = MathUtil.ceil(blockDist);
		Pos pos1 = Pos.at(tile).offset(-iBlockDist,-iBlockDist,-iBlockDist);
		Pos pos2 = Pos.at(tile).offset(iBlockDist,iBlockDist,iBlockDist);
		
		world.newExplosion(null,tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D,explosionRad,true,true);
		
		Pos.forEachBlock(pos1,pos2,pos -> {
			if (pos.distance(tile) <= blockDist && pos.isAir(world))pos.setBlock(world,BlockList.corrupted_energy_high,energyMeta);
		});
		
		while(ethereum-- > 0){
			EntityItem item = new EntityItem(world,tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D,new ItemStack(ItemList.ethereum));
			item.delayBeforeCanPickup = 10;
			world.spawnEntityInWorld(item);
		}
	}
}
