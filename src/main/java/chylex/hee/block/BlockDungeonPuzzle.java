package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import chylex.hee.system.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.material.MaterialDungeonPuzzle;
import chylex.hee.block.state.BlockAbstractStateEnum;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.technical.EntityTechnicalPuzzleChain;
import chylex.hee.entity.technical.EntityTechnicalPuzzleSolved;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.BlockPosM;

public class BlockDungeonPuzzle extends BlockAbstractStateEnum implements IBlockSubtypes{
	private static final Material dungeonPuzzle = new MaterialDungeonPuzzle();
	
	public static final byte minDungeonSize = 7, maxDungeonSize = 13;
	
	public enum Variant{
		TRIGGER_UNLIT(false,"trigger.unlit"), TRIGGER_LIT(true,"trigger.lit"),
		CHAINED_UNLIT(false,"chained.unlit"), CHAINED_LIT(true,"chained.lit"),
		DISTRIBUTOR_SPREAD_UNLIT(false,"distr.spread.unlit"), DISTRIBUTOR_SPREAD_LIT(true,"distr.spread.lit"),
		DISTRIBUTOR_SQUARE_UNLIT(false,"distr.square.unlit"), DISTRIBUTOR_SQUARE_LIT(true,"distr.square.lit"),
		UNUSED_1(false,""), UNUSED_2(false,""), UNUSED_3(false,""),
		PORTAL(false,""), DISABLED(false,"disabled"), WALL(false,"wall"), ROCK(false,"rock"), CEILING(false,"ceiling");
		
		public static final Variant[] values = values();
		
		public final boolean isLit;
		public final String unlocalizedName;
		
		Variant(boolean isLit, String unlocalizedName){
			this.isLit = isLit;
			this.unlocalizedName = unlocalizedName;
		}
		
		public boolean canTrigger(){
			return this == TRIGGER_UNLIT || this == TRIGGER_LIT;
		}
		
		public Variant getUnlit(){
			return isLit ? (values[ordinal()-1].isLit ? this : values[ordinal()-1]) : this;
		}
		
		public Variant toggleLit(){
			return (ordinal()&1) == 0 ? values[ordinal()+1] : values[ordinal()-1];
		}
	}
	
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	public BlockDungeonPuzzle(){
		super(dungeonPuzzle);
	}
	
	@Override
	protected IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}
	
	/**
	 * Update chain from the entity, return false to stop the chain.
	 */
	public boolean updateChain(World world, BlockPosM pos, byte chainDir){
		Variant variant = (Variant)pos.getBlockState(world).getValue(VARIANT), toggled = variant.toggleLit();
		
		if (variant != toggled){
			pos.changeProperty(world,VARIANT,toggled);
			
			Variant unlit = variant.getUnlit();
			BlockPosM testPos = pos.copy();
			
			if (unlit == Variant.DISTRIBUTOR_SPREAD_UNLIT){
				Variant offVariant;
				
				for(int dir = 0, tx, tz; dir < 4; dir++){
					if (dir == Direction.rotateOpposite[chainDir])continue;
					
					IBlockState off = testPos.moveTo(pos).moveBy(Direction.offsetX[dir],0,Direction.offsetZ[dir]).getBlockState(world);
					if (off.getBlock() != BlockList.dungeon_puzzle)continue;
					
					if ((offVariant = (Variant)off.getValue(VARIANT)) != offVariant.toggleLit()){
						world.spawnEntityInWorld(new EntityTechnicalPuzzleChain(world,testPos,dir));
					}
				}
			}
			else if (unlit == Variant.DISTRIBUTOR_SQUARE_UNLIT){
				Variant offVariant;
				
				for(int xx = -1, zz, distrMeta, distrToggled; xx <= 1; xx++){
					for(zz = -1; zz <= 1; zz++){
						IBlockState off = testPos.moveTo(pos).moveBy(xx,0,zz).getBlockState(world);
						if (!(xx == 0 && zz == 0) && off.getBlock() != BlockList.dungeon_puzzle)continue;
						
						if ((offVariant = (Variant)off.getValue(VARIANT)) != offVariant.toggleLit()){
							PacketPipeline.sendToAllAround(world,testPos.x+0.5D,testPos.y+0.5D,testPos.z+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,testPos.x+0.5D,testPos.y+0.5D,testPos.z+0.5D));
							testPos.changeProperty(world,VARIANT,offVariant.toggleLit());
						}
					}
				}
			}
			else return true;
			
			PacketPipeline.sendToAllAround(world.provider.getDimensionId(),pos.x+0.5D,pos.y+0.5D,pos.z+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,pos.x+0.5D,pos.y+0.5D,pos.z+0.5D));
		}
		
		checkWinConditions(world,pos);
		return false;
	}
	
	public void checkWinConditions(World world, BlockPosM pos){
		if (world.getEntitiesWithinAABB(EntityTechnicalPuzzleChain.class,AxisAlignedBB.fromBounds(pos.x+0.5D-maxDungeonSize,pos.y,pos.z+0.5D-maxDungeonSize,pos.x+0.5D+maxDungeonSize,pos.y+1D,pos.z+0.5D+maxDungeonSize)).size() == 1){
			int minX = pos.x, minZ = pos.z, maxX = pos.x, maxZ = pos.z, cnt = 0;
			BlockPosM tmpPos = new BlockPosM();
			boolean isFinished = true;
			
			Stopwatch.time("BlockDungeonPuzzle - win detection - coords");
			
			while(tmpPos.moveTo(pos).moveBy(--minX,0,0).getBlock(world) == this);
			while(tmpPos.moveTo(pos).moveBy(0,0,--minZ).getBlock(world) == this);
			while(tmpPos.moveTo(pos).moveBy(++minX,0,0).getBlock(world) == this);
			while(tmpPos.moveTo(pos).moveBy(0,0,++maxZ).getBlock(world) == this);
			
			++minX;
			++minZ;
			--maxX;
			--maxZ;
			
			for(int px = minX, pz = pos.z; px <= maxX; px++){
				while(tmpPos.moveTo(px,pos.y,--pz).getBlock(world) == this);
				if (pz+1 < minZ)minZ = pz+1;
				
				pz = pos.z;
				
				while(tmpPos.moveTo(px,pos.y,++pz).getBlock(world) == this);
				if (pz-1 > maxZ)maxZ = pz-1;
			}
			
			for(int pz = minZ, px = pos.x; pz <= maxZ; pz++){
				while(tmpPos.moveTo(--px,pos.y,pz).getBlock(world) == this);
				if (px+1 < minX)minX = px+1;
				
				px = pos.x;
				
				while(tmpPos.moveTo(++px,pos.y,pz).getBlock(world) == this);
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
					if (tmpPos.moveTo(xx,pos.y,zz).getBlock(world) != this)continue;
					
					++cnt;
					
					if (((Variant)tmpPos.getBlockState(world).getValue(VARIANT)).toggleLit().isLit){
						isFinished = false;
						xx = maxX+1;
						break;
					}
				}
			}
			
			Stopwatch.finish("BlockDungeonPuzzle - win detection - conditions");
			
			if (isFinished && cnt > (maxX-minX+1)*(maxZ-minZ+1)*0.9D)world.spawnEntityInWorld(new EntityTechnicalPuzzleSolved(world,minX+((maxX-minX+1)>>1),pos.y,minZ+((maxZ-minZ+1)>>1),minX,minZ,maxX,maxZ));
		}
	}
	
	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos){
		return (Variant)world.getBlockState(pos).getValue(VARIANT) == Variant.PORTAL ? 15 : super.getLightValue(world,pos);
	}
	
	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventData){
		if (eventID == 69){
			for(int a = 0; a < (eventData == 0 ? 3 : 25); a++)world.spawnParticle(EnumParticleTypes.FLAME,pos.getX()+world.rand.nextDouble(),pos.getY()+(eventData == 0 ? 1.15D : 1D+world.rand.nextDouble()*2D),pos.getZ()+world.rand.nextDouble(),(world.rand.nextDouble()-0.5D)*0.1D,(world.rand.nextDouble()-0.5D)*0.1D,(world.rand.nextDouble()-0.5D)*0.1D);
			world.playSoundEffect(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,"random.fizz",0.5F,2.6F+(world.rand.nextFloat()-world.rand.nextFloat())*0.8F);
			return true;
		}
		else return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		if ((Variant)world.getBlockState(pos).getValue(VARIANT) == Variant.PORTAL){
			for(int a = 0; a < 18; a++)HardcoreEnderExpansion.fx.portalColor(world,pos.getX()+0.5D+(rand.nextDouble()-0.5D)*0.3D,pos.getY()+1D+rand.nextDouble()*2D,pos.getZ()+0.5D+(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.8D,(rand.nextDouble()-0.5D)*0.2D,(rand.nextDouble()-0.5D)*0.8D,0.6289F,0.3359F,0.0391F);
			HardcoreEnderExpansion.fx.portalColor(world,pos.getX()+0.5D+(rand.nextDouble()-0.5D)*0.3D,pos.getY()+1D+rand.nextDouble()*2D,pos.getZ()+0.5D+(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.8D,(rand.nextDouble()-0.5D)*0.2D,(rand.nextDouble()-0.5D)*0.8D,1F,1F,1F);
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
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos){
		if ((Variant)world.getBlockState(pos).getValue(VARIANT) == Variant.PORTAL)return new ItemStack(this,1,Variant.DISABLED.ordinal());
		else return super.getPickBlock(target,world,pos);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return "tile.dungeonPuzzle."+((Variant)getEnumFromDamage(is.getItemDamage())).unlocalizedName;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(Variant variant:new Variant[]{
			Variant.WALL, Variant.ROCK, Variant.CEILING, Variant.DISABLED, Variant.TRIGGER_UNLIT, Variant.TRIGGER_LIT,
			Variant.CHAINED_UNLIT, Variant.CHAINED_LIT, Variant.DISTRIBUTOR_SPREAD_UNLIT, Variant.DISTRIBUTOR_SPREAD_LIT,
			Variant.DISTRIBUTOR_SQUARE_UNLIT, Variant.DISTRIBUTOR_SQUARE_LIT
		})list.add(new ItemStack(item,1,variant.ordinal()));
	}
}
