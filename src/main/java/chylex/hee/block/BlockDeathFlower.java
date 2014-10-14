package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.item.ItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDeathFlower extends BlockFlower{
	private static int[] yOffsets = new int[]{
		0,1,2,3,-2,-1
	};
	
	@SideOnly(Side.CLIENT)
	private IIcon iconDeadFlower;
	
	public BlockDeathFlower(){
		super(0);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		super.updateTick(world,x,y,z,rand);
		updateFlowerLogic(world,x,y,z,rand);
	}
	
	public void updateFlowerLogic(World world, int x, int y, int z, Random rand){
		if (world.provider.dimensionId != 1 && rand.nextInt(5) <= 1){
			int meta = world.getBlockMetadata(x,y,z);
			
			if (meta > 3 && meta < 15){
				List nearbyEndermen = world.getEntitiesWithinAABB(EntityMobAngryEnderman.class,AxisAlignedBB.getBoundingBox(x-8D,y-2D,z-8D,x+8D,y+2D,z+8D));
				if (nearbyEndermen != null && nearbyEndermen.size() > meta)return;
				
				for(int attempt = 0, spawned = 0; attempt < 30 && spawned<(meta/3)+rand.nextInt(meta/2); attempt++){
					int px = x+rand.nextInt(8)-4,pz = z+rand.nextInt(8)-4,py;
					for(int a = 0; a < yOffsets.length; a++){
						py = y+yOffsets[a];
						if (!world.getBlock(px,py,pz).isOpaqueCube()){
							EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
							enderman.setCanDespawn(false);
							enderman.setPosition(px+rand.nextFloat(),py+0.01F,pz+rand.nextFloat());
							world.spawnEntityInWorld(enderman);							
							break;
						}
					}
				}
			}
			
			if (rand.nextInt(6) == 0){
				if (++meta == 15){
					for(int xx = x-8; xx <= x+8; xx++){
						for(int yy = y-8; yy <= y+8; yy++){
							for(int zz = z-8; zz <= z+8; zz++){
								if (xx == x && yy == y && zz == z || rand.nextFloat() < 0.2F)continue;
								if (Math.sqrt(Math.pow(xx-x,2)+Math.pow(yy-y,2)+Math.pow(zz-z,2)) > 4D+rand.nextFloat()*3.8D)continue;
								
								Block block = world.getBlock(xx,yy,zz);
								if (block == Blocks.air){
									if (rand.nextInt(7) == 0)setBlock(world,xx,yy,zz,Blocks.web);
									continue;
								}
								
								if (block == Blocks.grass && world.getBlock(xx,yy+1,zz) == Blocks.air)setBlock(world,xx,yy,zz,Blocks.mycelium);
								else if (block == Blocks.stone)setBlock(world,xx,yy,zz,Blocks.cobblestone);
								else if (block == Blocks.stonebrick)setMeta(world,xx,yy,zz,1);
								else if (block == Blocks.sandstone)setBlock(world,xx,yy,zz,Blocks.sand);
								else if (block == Blocks.cobblestone)setBlock(world,xx,yy,zz,Blocks.gravel);
								else if (block == Blocks.sand)setBlock(world,xx,yy,zz,Blocks.soul_sand);
								else if (block == Blocks.brick_block)setBlock(world,xx,yy,zz,Blocks.nether_brick);
								else if (block == Blocks.fence)setBlock(world,xx,yy,zz,Blocks.nether_brick_fence);
								else if (block == Blocks.torch)setBlock(world,xx,yy,zz,Blocks.redstone_torch);
								else if (block == Blocks.wool || block == Blocks.carpet || block == Blocks.stained_hardened_clay)setMeta(world,xx,yy,zz,15);
								else if (block == Blocks.quartz_block)setMeta(world,xx,yy,zz,1);
								else if (block == Blocks.glass)setBlock(world,xx,yy,zz,Blocks.glass_pane);
								else if (block instanceof BlockFlower)setBlock(world,xx,yy,zz,Blocks.deadbush);
								else if (block instanceof BlockCrops)world.setBlockToAir(xx,yy,zz);
							}
						}
					}
					
					for(int attempt = 0, xx, yy, zz; attempt < 400; attempt++){
						xx = x+rand.nextInt(16)-8;
						yy = y+rand.nextInt(16)-8;
						zz = z+rand.nextInt(16)-8;
						
						if (world.isAirBlock(xx,yy,zz)){
							world.setBlock(xx,yy,zz,BlockList.energy_cluster);
							break;
						}
					}
					
					for(int a = 0; a < 6; a++){
						int xx = x+rand.nextInt(8)-4,zz = z+rand.nextInt(8)-4,yy;
						for(int b = 0; b < yOffsets.length; b++){
							yy = y+yOffsets[b];
							if (!world.getBlock(xx,yy,zz).isOpaqueCube()){
								EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
								enderman.setPosition(xx+rand.nextFloat(),yy+0.01F,zz+rand.nextFloat());
								enderman.setCanDespawn(false);
								world.spawnEntityInWorld(enderman);								
								break;
							}
						}
					}
				}
				
				world.setBlockMetadataWithNotify(x,y,z,Math.min(meta,15),3);
			}
		}
	}
	
	private void setBlock(World world, int x, int y, int z, Block newBlock){
		world.setBlock(x,y,z,newBlock);
	}
	
	private void setMeta(World world, int x, int y, int z, int newMeta){
		world.setBlockMetadataWithNotify(x,y,z,newMeta,3);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		ItemStack is = player.inventory.getCurrentItem();
		if (is == null || is.getItem() != ItemList.end_powder)return false;
		
		int meta = world.getBlockMetadata(x,y,z);
		if (meta > 0 && meta < 15){
			if (!world.isRemote){
				world.setBlockMetadataWithNotify(x,y,z,meta-1,2);
				if (!player.capabilities.isCreativeMode)--is.stackSize;
				world.playAuxSFX(2005,x,y,z,0);
			}
			
			for(int a = 0; a < 3; a++)world.spawnParticle("portal",x+world.rand.nextFloat(),y+world.rand.nextFloat(),z+world.rand.nextFloat(),0D,0D,0D);
			return true;
		}
		return false;
	}
	
	@Override
	public int damageDropped(int meta){
		return meta;
	}
	
	@Override
	protected boolean canPlaceBlockOn(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || block == Blocks.grass || block == Blocks.dirt;
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z){
		return canPlaceBlockOn(world.getBlock(x,y-1,z));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		int meta = world.getBlockMetadata(x,y,z);
		
		if (meta > 0 && meta < 15 && (rand.nextInt(50) < meta*Math.sqrt(meta) || rand.nextInt(18-meta) == 0)){
			double speedMp = 0.003D*meta;
			float colMp = 1.1F-rand.nextFloat()*0.2F-meta*meta*0.003F;
			HardcoreEnderExpansion.fx.portalBig(world,x+0.2D+rand.nextDouble()*0.6D,y+0.2D+rand.nextDouble()*0.6D,z+0.2D+rand.nextDouble()*0.6D,(rand.nextDouble()-0.5D)*speedMp,(rand.nextDouble()-0.5D)*speedMp,(rand.nextDouble()-0.5D)*speedMp,0.2F+rand.nextFloat()*0.1F,0.72F*colMp,0.24F*colMp,0.8F*colMp);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta == 15 ? iconDeadFlower : blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,15));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon(getTextureName());
		iconDeadFlower = iconRegister.registerIcon(getTextureName()+"_dead");
	}
}
