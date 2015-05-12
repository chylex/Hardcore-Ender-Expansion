package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
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
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDeathFlower extends BlockFlower{
	private static int[] yOffsets = new int[]{
		0, 1, 2, 3, -2, -1
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
			int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
			
			if (meta > 3 && meta < 15){
				List nearbyEndermen = world.getEntitiesWithinAABB(EntityMobAngryEnderman.class,AxisAlignedBB.getBoundingBox(x-8D,y-2D,z-8D,x+8D,y+2D,z+8D));
				if (nearbyEndermen != null && nearbyEndermen.size() > meta)return;
				
				BlockPosM tmpPos = BlockPosM.tmp();
				
				for(int attempt = 0, spawned = 0; attempt < 30 && spawned < (meta/3)+rand.nextInt(meta/2); attempt++){
					tmpPos.set(x+rand.nextInt(9)-4,-1,z+rand.nextInt(9)-4);
					
					for(int a = 0; a < yOffsets.length; a++){
						if (!tmpPos.setY(y+yOffsets[a]).getBlock(world).isOpaqueCube()){
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
				if (++meta == 15){
					BlockPosM tmpPos = BlockPosM.tmp();
					
					for(int xx = x-8; xx <= x+8; xx++){
						for(int yy = y-8; yy <= y+8; yy++){
							for(int zz = z-8; zz <= z+8; zz++){
								if (xx == x && yy == y && zz == z || rand.nextFloat() < 0.2F)continue;
								if (Math.sqrt(Math.pow(xx-x,2)+Math.pow(yy-y,2)+Math.pow(zz-z,2)) > 4D+rand.nextFloat()*3.8D)continue;
								
								Block block = tmpPos.set(xx,yy,zz).getBlock(world);
								
								if (block.getMaterial() == Material.air){
									if (rand.nextInt(7) == 0)tmpPos.setBlock(world,Blocks.web);
									continue;
								}
								
								if (block == Blocks.grass && tmpPos.copy().moveUp().isAir(world))tmpPos.setBlock(world,Blocks.mycelium);
								else if (block == Blocks.stone)tmpPos.setBlock(world,Blocks.cobblestone);
								else if (block == Blocks.stonebrick)tmpPos.setMetadata(world,1);
								else if (block == Blocks.sandstone)tmpPos.setBlock(world,Blocks.sand);
								else if (block == Blocks.cobblestone)tmpPos.setBlock(world,Blocks.gravel);
								else if (block == Blocks.sand)tmpPos.setBlock(world,Blocks.soul_sand);
								else if (block == Blocks.brick_block)tmpPos.setBlock(world,Blocks.nether_brick);
								else if (block == Blocks.fence)tmpPos.setBlock(world,Blocks.nether_brick_fence);
								else if (block == Blocks.torch)tmpPos.setBlock(world,Blocks.redstone_torch);
								else if (block == Blocks.wool || block == Blocks.carpet || block == Blocks.stained_hardened_clay)tmpPos.setMetadata(world,15);
								else if (block == Blocks.quartz_block)tmpPos.setMetadata(world,1);
								else if (block == Blocks.glass)tmpPos.setBlock(world,Blocks.glass_pane);
								else if (block instanceof BlockFlower)tmpPos.setBlock(world,Blocks.deadbush);
								else if (block instanceof BlockCrops)tmpPos.setAir(world);
							}
						}
					}
					
					for(int attempt = 0; attempt < 400; attempt++){
						if (tmpPos.set(x+rand.nextInt(17)-8,y+rand.nextInt(17)-8,z+rand.nextInt(17)-8).isAir(world)){
							tmpPos.setBlock(world,BlockList.energy_cluster);
							break;
						}
					}
					
					for(int a = 0; a < 6; a++){
						tmpPos.set(x+rand.nextInt(9)-4,-1,z+rand.nextInt(9)-4);
						
						for(int b = 0; b < yOffsets.length; b++){
							if (!tmpPos.setY(y+yOffsets[b]).getBlock(world).isOpaqueCube()){
								EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(world);
								enderman.setPosition(tmpPos.x+rand.nextFloat(),tmpPos.y+0.01F,tmpPos.z+rand.nextFloat());
								enderman.setCanDespawn(false);
								world.spawnEntityInWorld(enderman);								
								break;
							}
						}
					}
				}
				
				BlockPosM.tmp(x,y,z).setMetadata(world,Math.min(meta,15));
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		ItemStack is = player.inventory.getCurrentItem();
		if (is == null || is.getItem() != ItemList.end_powder)return false;
		
		int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
		
		if (meta > 0 && meta < 15){
			if (!world.isRemote){
				BlockPosM.tmp(x,y,z).setMetadata(world,meta-1,2);
				if (!player.capabilities.isCreativeMode)--is.stackSize;
				world.playAuxSFX(2005,x,y,z,0);
			}
			
			for(int a = 0; a < 3; a++)world.spawnParticle("portal",x+world.rand.nextFloat(),y+world.rand.nextFloat(),z+world.rand.nextFloat(),0D,0D,0D);
		}
		
		return true;
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
		return canPlaceBlockOn(BlockPosM.tmp(x,y-1,z).getBlock(world));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
		
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
