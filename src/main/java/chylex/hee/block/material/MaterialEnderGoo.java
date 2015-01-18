package chylex.hee.block.material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;

public class MaterialEnderGoo extends MaterialLiquid{
	public MaterialEnderGoo(){
		super(MapColor.waterColor);
	}
	
	@Override
	public boolean blocksMovement(){
		return true;
	}
}
