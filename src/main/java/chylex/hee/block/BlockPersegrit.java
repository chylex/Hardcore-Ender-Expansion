package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import chylex.hee.block.state.BlockAbstractStateEnum;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class BlockPersegrit extends BlockAbstractStateEnum implements IBlockSubtypes{
	public static enum Variant{ PLAIN, LR, TB, END_1, END_2, END_3, END_4, TL, TR, BL, BR, TRB, TRL, TBL, RBL, LRTB }
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	public BlockPersegrit(){
		super(Material.cloth);
		createSimpleMeta(VARIANT,Variant.class);
	}
	
	@Override
	protected IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}
	
	@Override
	public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, SpawnPlacementType type){
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName();
	}
	
	public static int getConnectionMeta(LargeStructureWorld world, Random rand, int x, int y, int z){
		boolean l = false, r = false, t = false, b = false;
		
		if (world.getBlock(x,y-1,z) != BlockList.persegrit || world.getBlock(x,y+1,z) != BlockList.persegrit){ // xz plane
			l = isConnectable(world,x-1,y,z);
			r = isConnectable(world,x+1,y,z);
			t = isConnectable(world,x,y,z-1);
			b = isConnectable(world,x,y,z+1);
		}
		else if (world.getBlock(x-1,y,z) != BlockList.persegrit || world.getBlock(x+1,y,z) != BlockList.persegrit){ // yz plane
			l = isConnectable(world,x,y,z-1);
			r = isConnectable(world,x,y,z+1);
			t = isConnectable(world,x,y+1,z);
			b = isConnectable(world,x,y-1,z);
		}
		else if (world.getBlock(x,y,z-1) != BlockList.persegrit || world.getBlock(x,y,z+1) != BlockList.persegrit){ // xy plane
			l = isConnectable(world,x+1,y,z);
			r = isConnectable(world,x-1,y,z);
			t = isConnectable(world,x,y+1,z);
			b = isConnectable(world,x,y-1,z);
		}
		
		if (rand.nextBoolean()){
			boolean oldL = l;
			l = r;
			r = oldL;
		}
		
		if (rand.nextBoolean()){
			boolean oldT = t;
			t = b;
			b = oldT;
		}
		
		if (l && r && t && b)return 15;
		else if (r && b && l)return 14;
		else if (t && b && l)return 13;
		else if (t && r && l)return 12;
		else if (t && r && b)return 11;
		else if (b && r)return 10;
		else if (b && l)return 9;
		else if (t && r)return 8;
		else if (t && l)return 7;
		else if (t && b)return 2;
		else if (l && r)return 1;
		else return 0;
	}
	
	public static int getEndMeta(LargeStructureWorld world, int addX, int addY, int addZ, boolean wall){
		if (wall){
			if (addY == 0)return addX == 1 || addZ == 1 ? 3 : 4;
			else return addY == -1 ? 5 : 6;
			
		}
		else{
			if (addX != 0)return addX == -1 ? 3 : 4;
			else if (addZ != 0)return addZ == 1 ? 5 : 6;
		}
		
		return 0;
	}
	
	public static boolean isConnectable(LargeStructureWorld world, int x, int y, int z){
		if (world.getBlock(x,y,z) == BlockList.persegrit){
			int meta = world.getMetadata(x,y,z);
			
			if (meta == 0)return false;
			else if (meta >= 3 && meta <= 6){
				return false;
			}
			else return true;
		}
		else return false;
	}
}
