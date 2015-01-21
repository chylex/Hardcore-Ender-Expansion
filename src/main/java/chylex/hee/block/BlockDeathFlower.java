package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.BlockPosM;

public class BlockDeathFlower extends BlockBush{
	public static final PropertyInteger DECAY = PropertyInteger.create("decay",0,15);
	
	private static int[] yOffsets = new int[]{ 0, 1, 2, 3, -2, -1 };
	
	public BlockDeathFlower(){
		setDefaultState(blockState.getBaseState().withProperty(DECAY,0));
	}
	
	@Override
	protected BlockState createBlockState(){
		return new BlockState(this,new IProperty[]{ DECAY });
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(DECAY,meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return ((Integer)state.getValue(DECAY)).intValue();
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		super.updateTick(world,pos,state,rand);
		updateFlowerLogic(world,pos,rand);
	}
	
	public void updateFlowerLogic(World world, BlockPos pos, Random rand){
		if (world.provider.getDimensionId() != 1 && rand.nextInt(5) <= 1){
			int decay = getMetaFromState(world.getBlockState(pos));
			
			if (decay > 3 && decay < 15){
				BlockPosM tmpPos = new BlockPosM();
				
				List nearbyEndermen = world.getEntitiesWithinAABB(EntityMobAngryEnderman.class,new AxisAlignedBB(pos.add(-7,-2,-7),pos.add(8,3,8)));
				if (nearbyEndermen != null && nearbyEndermen.size() > decay)return;
				
				for(int attempt = 0, spawned = 0; attempt < 30 && spawned < (decay/3)+rand.nextInt(decay/2); attempt++){
					tmpPos.moveTo(pos).moveBy(rand.nextInt(9)-4,0,rand.nextInt(9)-4);
					
					for(int a = 0; a < yOffsets.length; a++){
						tmpPos.y = pos.getY()+yOffsets[a];
						
						if (!tmpPos.getBlock(world).isOpaqueCube()){
							EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
							enderman.setCanDespawn(false);
							enderman.setPosition(tmpPos.x+rand.nextFloat(),tmpPos.y+0.01F,tmpPos.z+rand.nextFloat());
							world.spawnEntityInWorld(enderman);							
							break;
						}
					}
				}
			}
			
			if (rand.nextInt(6) == 0){
				if (++decay == 15){
					BlockPosM tmpPos = new BlockPosM();
					
					for(int xx = pos.getX()-8; xx <= pos.getX()+8; xx++){
						for(int yy = pos.getY()-8; yy <= pos.getY()+8; yy++){
							for(int zz = pos.getZ()-8; zz <= pos.getZ()+8; zz++){
								if (xx == pos.getX() && yy == pos.getY() && zz == pos.getZ() || rand.nextFloat() < 0.2F)continue;
								if (Math.sqrt(Math.pow(xx-pos.getX(),2)+Math.pow(yy-pos.getY(),2)+Math.pow(zz-pos.getZ(),2)) > 4D+rand.nextFloat()*3.8D)continue;
								
								Block block = tmpPos.moveTo(xx,yy,zz).getBlock(world);
								
								if (block == Blocks.air){
									if (rand.nextInt(7) == 0)tmpPos.setBlock(world,Blocks.web);
									continue;
								}
								
								if (block == Blocks.grass && world.isAirBlock(tmpPos.up()))tmpPos.setBlock(world,Blocks.mycelium);
								else if (block == Blocks.stone)tmpPos.setBlock(world,Blocks.cobblestone);
								else if (block == Blocks.stonebrick)tmpPos.changeProperty(world,BlockStoneBrick.VARIANT,BlockStoneBrick.EnumType.CRACKED);
								else if (block == Blocks.sandstone)tmpPos.setBlock(world,Blocks.sand);
								else if (block == Blocks.cobblestone)tmpPos.setBlock(world,Blocks.gravel);
								else if (block == Blocks.sand)tmpPos.setBlock(world,Blocks.soul_sand);
								else if (block == Blocks.brick_block)tmpPos.setBlock(world,Blocks.nether_brick);
								else if (block == Blocks.oak_fence || block == Blocks.spruce_fence || block == Blocks.jungle_fence ||
										 block == Blocks.birch_fence || block == Blocks.acacia_fence || block == Blocks.dark_oak_fence)tmpPos.setBlock(world,Blocks.nether_brick_fence);
								else if (block == Blocks.torch)tmpPos.setBlock(world,Blocks.redstone_torch);
								else if (block == Blocks.wool || block == Blocks.carpet || block == Blocks.stained_hardened_clay)tmpPos.changeProperty(world,BlockColored.COLOR,EnumDyeColor.BLACK);
								else if (block == Blocks.glass)tmpPos.setBlock(world,Blocks.glass_pane);
								else if (block instanceof BlockFlower)tmpPos.setBlock(world,Blocks.deadbush);
								else if (block instanceof BlockCrops)tmpPos.setToAir(world);
							}
						}
					}
					
					for(int attempt = 0, xx, yy, zz; attempt < 400; attempt++){
						if (tmpPos.moveTo(pos).moveBy(rand.nextInt(17)-8,rand.nextInt(17)-8,rand.nextInt(17)-8).isAir(world)){
							tmpPos.setBlock(world,BlockList.energy_cluster);
							break;
						}
					}
					
					for(int a = 0; a < 6; a++){
						tmpPos.moveTo(pos).moveBy(rand.nextInt(9)-4,0,rand.nextInt(9)-4);
						
						for(int b = 0; b < yOffsets.length; b++){
							tmpPos.y = pos.getY()+yOffsets[b];
							
							if (!tmpPos.getBlock(world).isOpaqueCube()){
								EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
								enderman.setPosition(tmpPos.x+rand.nextFloat(),tmpPos.y+0.01F,tmpPos.z+rand.nextFloat());
								enderman.setCanDespawn(false);
								world.spawnEntityInWorld(enderman);								
								break;
							}
						}
					}
				}
				
				world.setBlockState(pos,world.getBlockState(pos).withProperty(DECAY,Math.min(decay,15)));
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack is = player.inventory.getCurrentItem();
		if (is == null || is.getItem() != ItemList.end_powder)return false;
		
		int decay = getMetaFromState(state);
		
		if (decay > 0 && decay < 15){
			if (!world.isRemote){
				world.setBlockState(pos,state.withProperty(DECAY,decay-1));
				if (!player.capabilities.isCreativeMode)--is.stackSize;
				world.playAuxSFX(2005,pos,0);
			}
			
			for(int a = 0; a < 3; a++)world.spawnParticle(EnumParticleTypes.PORTAL,pos.getX()+world.rand.nextFloat(),pos.getY()+world.rand.nextFloat(),pos.getZ()+world.rand.nextFloat(),0D,0D,0D);
		}
		
		return true;
	}
	
	@Override
	protected boolean canPlaceBlockOn(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || block == Blocks.grass || block == Blocks.dirt;
	}
	
	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state){
		return canPlaceBlockOn(world.getBlockState(pos.down()).getBlock());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		int decay = getMetaFromState(state);
		
		if (decay > 0 && decay < 15 && (rand.nextInt(50) < decay*Math.sqrt(decay) || rand.nextInt(18-decay) == 0)){
			double speedMp = 0.003D*decay;
			float colMp = 1.1F-rand.nextFloat()*0.2F-decay*decay*0.003F;
			HardcoreEnderExpansion.fx.portalBig(world,pos.getX()+0.2D+rand.nextDouble()*0.6D,pos.getY()+0.2D+rand.nextDouble()*0.6D,pos.getZ()+0.2D+rand.nextDouble()*0.6D,(rand.nextDouble()-0.5D)*speedMp,(rand.nextDouble()-0.5D)*speedMp,(rand.nextDouble()-0.5D)*speedMp,0.2F+rand.nextFloat()*0.1F,0.72F*colMp,0.24F*colMp,0.8F*colMp);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,15));
	}
}
