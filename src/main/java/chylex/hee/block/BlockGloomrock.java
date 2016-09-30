package chylex.hee.block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGloomrock extends BlockAbstractSubtypes{
	public enum State{
		PLAIN, SMOOTH, BRICK,
		COL_RED, COL_ORANGE, COL_YELLOW, COL_GREEN, COL_CYAN, COL_LIGHT_BLUE,
		COL_BLUE, COL_PURPLE, COL_MAGENTA, COL_PINK, COL_WHITE, COL_GRAY, COL_BLACK;
		public final byte value = (byte)ordinal();
		public static final byte count = (byte)values().length;
		public static final byte firstColor = COL_RED.value;
	}
	
	public static final State[] colored = new State[]{
		State.COL_RED, State.COL_ORANGE, State.COL_YELLOW, State.COL_GREEN, State.COL_CYAN, State.COL_LIGHT_BLUE,
		State.COL_BLUE, State.COL_PURPLE, State.COL_MAGENTA, State.COL_PINK, State.COL_WHITE, State.COL_GRAY, State.COL_BLACK
	};
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockGloomrock(){
		super(Material.rock);
	}
	
	@Override
	public int countSubtypes(){
		return State.count;
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z){
		return Pos.at(x, y, z).getMetadata(world) == State.PLAIN.value ? blockHardness*0.5F : blockHardness;
	}

	@Override
	public String getUnlocalizedName(int meta){
		switch(meta){
			case 0: return "tile.gloomrock.plain";
			case 1: return "tile.gloomrock.smooth";
			case 2: return "tile.gloomrock.brick";
			default: return "tile.gloomrock.color."+(meta-3);
		}
	}
	
	@Override
	protected String getTextureName(int meta){
		switch(meta){
			case 0: return "hardcoreenderexpansion:gloomrock_plain";
			case 1: return "hardcoreenderexpansion:gloomrock_smooth";
			case 2: return "hardcoreenderexpansion:gloomrock_brick";
			default: return "hardcoreenderexpansion:gloomrock_color_"+(meta-2);
		}
	}
}
