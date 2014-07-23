package chylex.hee.block;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21ParticleIgneousRockFlame;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDungeonPuzzle extends Block implements IBlockSubtypes{
	private static final Material dungeonPuzzle = new MaterialDungeonPuzzle();
	
	public static final byte metaUnlit = 0, metaLit = 1, metaWall = 2, metaWallRock = 11, metaWallLit = 12,
							 metaSpreadingLitN = 3, metaSpreadingLitS = 4,
							 metaSpreadingLitE = 5, metaSpreadingLitW = 6,
							 metaSpreadingUnlitN = 7, metaSpreadingUnlitS = 8,
							 metaSpreadingUnlitE = 9, metaSpreadingUnlitW = 10,
							 dungeonSize = 9;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockDungeonPuzzle(){
		super(dungeonPuzzle);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		int meta = world.getBlockMetadata(x,y,z);
		byte[] offset = meta == metaSpreadingLitN || meta == metaSpreadingUnlitN ? new byte[]{ 0, -1 } :
					    meta == metaSpreadingLitS || meta == metaSpreadingUnlitS ? new byte[]{ 0, 1 } :
					    meta == metaSpreadingLitE || meta == metaSpreadingUnlitE ? new byte[]{ 1, 0 } :
					    meta == metaSpreadingLitW || meta == metaSpreadingUnlitW ? new byte[]{ -1, 0 } :
					    ArrayUtils.EMPTY_BYTE_ARRAY;
		if (offset.length == 0)return;
		
		// update nearby
		
		int tx = x+offset[0],tz = z+offset[1];
		int targMeta = world.getBlockMetadata(tx,y,tz);
		
		if (world.getBlock(tx,y,tz) == this){
			if (targMeta == metaWall){
				byte[] offx = new byte[]{ 0, 0, 1, -1 },offz = new byte[]{ -1, 1, 0, 0 };
				
				for(int a = 0,cmeta; a < 4; a++){
					if (world.getBlock(tx+offx[a],y,tz+offz[a]) != this)continue;
					if ((cmeta = world.getBlockMetadata(tx+offx[a],y,tz+offz[a])) > metaLit)continue;
					world.setBlockMetadataWithNotify(tx+offx[a],y,tz+offz[a],cmeta == metaUnlit?metaSpreadingLitN+a:metaSpreadingUnlitN+a,2);
					world.scheduleBlockUpdate(tx+offx[a],y,tz+offz[a],this,8);
					
					PacketPipeline.sendToAllAround(world.provider.dimensionId,tx+offx[a]+0.5D,y+0.5D,tz+offz[a]+0.5D,64D,new C21ParticleIgneousRockFlame(tx+offx[a]+0.5D,y+0.5D,tz+offz[a]+0.5D));
				}
			}
			else{
				int finalMeta = targMeta == metaUnlit?(meta >= metaSpreadingUnlitN?meta-4:meta):targMeta == metaLit?(meta >= metaSpreadingUnlitN?meta:meta+4):-1;
				if (finalMeta != -1){
					world.setBlockMetadataWithNotify(tx,y,tz,finalMeta,2);
					world.scheduleBlockUpdate(tx,y,tz,this,8);
				}
			}

			PacketPipeline.sendToAllAround(world.provider.dimensionId,tx+0.5D,y+0.5D,tz+0.5D,64D,new C21ParticleIgneousRockFlame(tx+0.5D,y+0.5D,tz+0.5D));
		}
		
		// update me
		
		world.setBlockMetadataWithNotify(x,y,z,meta >= metaSpreadingUnlitN ? metaUnlit : metaLit,2);
		
		// test puzzle finished
		
		int startX,startZ,cnt = 0;
		boolean isFinished = true;
		
		for(startX = x; true; --startX){
			if (world.getBlock(startX,y,z) != this)break;
		}
		for(startZ = z; true; --startZ){
			if (world.getBlock(x,y,startZ) != this)break;
		}
		++startX;
		++startZ;
		
		for(int xx = startX,testmeta; xx < startX+dungeonSize; xx++){
			for(int zz = startZ; zz < startZ+dungeonSize; zz++){
				if (world.getBlock(xx,y,zz) != this)continue;
				
				++cnt;
				testmeta = world.getBlockMetadata(xx,y,zz);
				
				if (testmeta != metaLit && testmeta != metaWall && testmeta != metaWallRock){
					isFinished = false;
					xx += dungeonSize;
					break;
				}
			}
		}
		
		int cx = startX+((dungeonSize-1)>>1),cz = startZ+((dungeonSize-1)>>1);
		
		if (isFinished && cnt > 32 && world.getEntitiesWithinAABB(EntityMiniBossFireFiend.class,AxisAlignedBB.getBoundingBox(cx-4,y-8,cz-4,cx+4,y,cz+4)).isEmpty()){
			EntityMiniBossFireFiend fireFiend = new EntityMiniBossFireFiend(world);
			fireFiend.setLocationAndAngles(cx+0.5D,y-4D,cz+0.5D,rand.nextFloat()*360F,0F);
			world.spawnEntityInWorld(fireFiend);
		}
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
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		int meta = world.getBlockMetadata(x,y,z);
		if (meta >= metaSpreadingLitN && meta <= metaSpreadingLitW)meta = metaLit;
		else if (meta >= metaSpreadingUnlitN && meta <= metaSpreadingUnlitW)meta = metaUnlit;
		
		return new ItemStack(this,1,meta);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case BlockDungeonPuzzle.metaUnlit: return "tile.dungeonPuzzle.unlit";
			case BlockDungeonPuzzle.metaLit: return "tile.dungeonPuzzle.lit";
			case BlockDungeonPuzzle.metaWall: return "tile.dungeonPuzzle.wall";
			case BlockDungeonPuzzle.metaWallRock: return "tile.dungeonPuzzle.wallRock";
			case BlockDungeonPuzzle.metaWallLit: return "tile.dungeonPuzzle.wallLit";
			default: return "";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta == metaLit || meta == metaWallLit || (meta >= metaSpreadingLitN && meta <= metaSpreadingLitW)?iconArray[1]:meta == metaWall?iconArray[2]:meta == metaWallRock?iconArray[3]:iconArray[0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,metaUnlit));
		list.add(new ItemStack(item,1,metaLit));
		list.add(new ItemStack(item,1,metaWall));
		list.add(new ItemStack(item,1,metaWallRock));
		list.add(new ItemStack(item,1,metaWallLit));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[4];
		iconArray[0] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_unlit");
		iconArray[1] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_lit");
		iconArray[2] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_wall");
		iconArray[3] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_wall_rock");
	}
}
