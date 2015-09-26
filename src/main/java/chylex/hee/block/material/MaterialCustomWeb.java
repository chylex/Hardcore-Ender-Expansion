package chylex.hee.block.material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialCustomWeb extends Material{
	public MaterialCustomWeb(){
		super(MapColor.clothColor);
		setNoPushMobility();
	}
	
	@Override
	public boolean blocksMovement(){
		return false;
	}
}
