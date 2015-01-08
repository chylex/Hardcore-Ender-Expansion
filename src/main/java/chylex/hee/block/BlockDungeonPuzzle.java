package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.technical.EntityTechnicalPuzzleChain;
import chylex.hee.entity.technical.EntityTechnicalPuzzleSolved;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDungeonPuzzle extends Block implements IBlockSubtypes{
	private static final Material dungeonPuzzle = new MaterialDungeonPuzzle();
	
	public static final byte minDungeonSize = 7, maxDungeonSize = 13;
	
	public static final byte metaTriggerUnlit = 0, metaTriggerLit = 1, metaChainedUnlit = 2, metaChainedLit = 3,
							 metaDistributorSpreadUnlit = 4, metaDistributorSpreadLit = 5,
							 metaDistributorSquareUnlit = 6, metaDistributorSquareLit = 7,
							 metaPortal = 11, metaDisabled = 12, metaWall = 13, metaRock = 14, metaCeiling = 15;
	
	public static final byte[] icons = new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 3, 3, 3, 10, 10, 0, 1, 3 };
	
	public static final String[] names = new String[]{
		"trigger.unlit", "trigger.lit", "chained.unlit", "chained.lit", "distr.spread.unlit", "distr.spread.lit", "distr.square.unlit", "distr.square.lit",
		null, null, null, null, "disabled", "wall", "rock", "ceiling"
	};
	
	public static final boolean canTrigger(int meta){
		return meta == metaTriggerUnlit || meta == metaTriggerLit;
	}
	
	public static final int toggleState(int meta){
		if (meta == metaWall || meta == metaRock || meta == metaCeiling || meta == metaDisabled || meta == metaPortal)return meta;
		else return (meta&1) == 0 ? meta+1 : meta-1;
	}
	
	public static final boolean isLit(int meta){
		return (meta&1) != 0;
	}
	
	public static final int getUnlit(int meta){
		return (meta&1) != 0 ? meta-1 : meta;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockDungeonPuzzle(){
		super(dungeonPuzzle);
	}
	
	/**
	 * Update chain from the entity, return false to stop the chain.
	 */
	public boolean updateChain(World world, int x, int y, int z, byte chainDir){
		int meta = world.getBlockMetadata(x,y,z), toggled = toggleState(meta);
		
		if (meta != toggled){
			world.setBlockMetadataWithNotify(x,y,z,toggled,3);
			
			int unlit = getUnlit(meta);
			
			if (unlit == metaDistributorSpreadUnlit){
				for(int dir = 0, distrMeta, distrToggled, tx, tz; dir < 4; dir++){
					if (dir == Direction.rotateOpposite[chainDir])continue;
					
					if ((distrToggled = toggleState(distrMeta = world.getBlockMetadata(x+(tx = Direction.offsetX[dir]),y,z+(tz = Direction.offsetZ[dir])))) != distrMeta && world.getBlock(x+tx,y,z+tz) == BlockList.dungeon_puzzle){
						world.spawnEntityInWorld(new EntityTechnicalPuzzleChain(world,x+tx,y,z+tz,dir));
					}
				}
			}
			else if (unlit == metaDistributorSquareUnlit){
				for(int xx = -1, zz, distrMeta, distrToggled; xx <= 1; xx++){
					for(zz = -1; zz <= 1; zz++){
						if ((distrToggled = toggleState(distrMeta = world.getBlockMetadata(x+xx,y,z+zz))) != distrMeta && !(xx == 0 && zz == 0) && world.getBlock(x+xx,y,z+zz) == BlockList.dungeon_puzzle){
							PacketPipeline.sendToAllAround(world.provider.dimensionId,x+xx+0.5D,y+0.5D,z+zz+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,x+xx+0.5D,y+0.5D,z+zz+0.5D));
							world.setBlockMetadataWithNotify(x+xx,y,z+zz,distrToggled,3);
						}
					}
				}
			}
			else return true;
			
			PacketPipeline.sendToAllAround(world.provider.dimensionId,x+0.5D,y+0.5D,z+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,x+0.5D,y+0.5D,z+0.5D));
		}
		
		checkWinConditions(world,x,y,z);
		return false;
	}
	
	public void checkWinConditions(World world, int x, int y, int z){
		if (world.getEntitiesWithinAABB(EntityTechnicalPuzzleChain.class,AxisAlignedBB.getBoundingBox(x+0.5D-maxDungeonSize,y,z+0.5D-maxDungeonSize,x+0.5D+maxDungeonSize,y+1D,z+0.5D+maxDungeonSize)).size() == 1){
			int minX = x, minZ = z, maxX = x, maxZ = z, cnt = 0;
			boolean isFinished = true;
			
			Stopwatch.time("BlockDungeonPuzzle - win detection - coords");
			
			while(world.getBlock(--minX,y,z) == this);
			while(world.getBlock(x,y,--minZ) == this);
			while(world.getBlock(++maxX,y,z) == this);
			while(world.getBlock(x,y,++maxZ) == this);
			
			++minX;
			++minZ;
			--maxX;
			--maxZ;
			
			for(int px = minX, pz = z; px <= maxX; px++){
				while(world.getBlock(px,y,--pz) == this);
				if (pz+1 < minZ)minZ = pz+1;
				
				pz = z;
				
				while(world.getBlock(px,y,++pz) == this);
				if (pz-1 > maxZ)maxZ = pz-1;
			}
			
			for(int pz = minZ, px = x; pz <= maxZ; pz++){
				while(world.getBlock(--px,y,pz) == this);
				if (px+1 < minX)minX = px+1;
				
				px = x;
				
				while(world.getBlock(++px,y,pz) == this);
				if (px-1 > maxX)maxX = px-1;
			}

			++minX;
			++minZ;
			--maxX;
			--maxZ;
			
			Stopwatch.finish("BlockDungeonPuzzle - win detection - coords");
			Stopwatch.time("BlockDungeonPuzzle - win detection - conditions");
			
			for(int xx = minX; xx <= maxX; xx++){
				for(int zz = minZ; zz <= maxZ; zz++){
					if (world.getBlock(xx,y,zz) != this)continue;
					
					++cnt;
					
					if (isLit(toggleState(world.getBlockMetadata(xx,y,zz)))){
						isFinished = false;
						xx = maxX+1;
						break;
					}
				}
			}
			
			Stopwatch.finish("BlockDungeonPuzzle - win detection - conditions");
			
			if (isFinished && cnt > (maxX-minX+1)*(maxZ-minZ+1)*0.9D)world.spawnEntityInWorld(new EntityTechnicalPuzzleSolved(world,minX+((maxX-minX+1)>>1),y,minZ+((maxZ-minZ+1)>>1),minX,minZ,maxX,maxZ));
		}
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z){
		return world.getBlockMetadata(x,y,z) == metaPortal ? 15 : super.getLightValue(world,x,y,z);
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventData){
		if (eventID == 69){
			for(int a = 0; a < (eventData == 0 ? 3 : 25); a++)world.spawnParticle("flame",x+world.rand.nextDouble(),y+(eventData == 0 ? 1.15D : 1D+world.rand.nextDouble()*2D),z+world.rand.nextDouble(),(world.rand.nextDouble()-0.5D)*0.1D,(world.rand.nextDouble()-0.5D)*0.1D,(world.rand.nextDouble()-0.5D)*0.1D);
			world.playSoundEffect(x+0.5D,y+0.5D,z+0.5D,"random.fizz",0.5F,2.6F+(world.rand.nextFloat()-world.rand.nextFloat())*0.8F);
			return true;
		}
		else return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		if (world.getBlockMetadata(x,y,z) == metaPortal){
			for(int a = 0; a < 18; a++)HardcoreEnderExpansion.fx.portalColor(world,x+0.5D+(rand.nextDouble()-0.5D)*0.3D,y+1D+rand.nextDouble()*2D,z+0.5D+(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.8D,(rand.nextDouble()-0.5D)*0.2D,(rand.nextDouble()-0.5D)*0.8D,0.6289F,0.3359F,0.0391F);
			HardcoreEnderExpansion.fx.portalColor(world,x+0.5D+(rand.nextDouble()-0.5D)*0.3D,y+1D+rand.nextDouble()*2D,z+0.5D+(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.8D,(rand.nextDouble()-0.5D)*0.2D,(rand.nextDouble()-0.5D)*0.8D,1F,1F,1F);
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
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		int meta = world.getBlockMetadata(x,y,z);
		if (meta == metaPortal)meta = metaDisabled;
		return new ItemStack(this,1,meta);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		String name = names[MathUtil.clamp(is.getItemDamage(),0,names.length-1)];
		return name == null ? "" : "tile.dungeonPuzzle."+name;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta >= 0 && meta < icons.length ? iconArray[icons[meta]] : iconArray[3];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(byte meta:new byte[]{
			metaWall, metaRock, metaCeiling, metaDisabled, metaTriggerUnlit, metaTriggerLit, metaChainedUnlit, metaChainedLit,
			metaDistributorSpreadUnlit, metaDistributorSpreadLit, metaDistributorSquareUnlit, metaDistributorSquareLit,
		})list.add(new ItemStack(item,1,meta));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[11];
		iconArray[0] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_wall");
		iconArray[1] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_wall_rock");
		iconArray[2] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_trigger_unlit");
		iconArray[3] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_trigger_lit");
		iconArray[4] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_chained_unlit");
		iconArray[5] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_chained_lit");
		iconArray[6] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_distributor_spread_unlit");
		iconArray[7] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_distributor_spread_lit");
		iconArray[8] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_distributor_square_unlit");
		iconArray[9] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_distributor_square_lit");
		iconArray[10] = iconRegister.registerIcon("hardcoreenderexpansion:dungeon_puzzle_disabled");
	}
}
