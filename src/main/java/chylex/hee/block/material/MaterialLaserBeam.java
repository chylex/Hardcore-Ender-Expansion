package chylex.hee.block.material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialLaserBeam extends Material{
	public MaterialLaserBeam(){
		super(MapColor.airColor);
		setImmovableMobility();
	}
	
	@Override
	public boolean isSolid(){
		return false;
	}

	@Override
	public boolean getCanBlockGrass(){
		return false;
	}

	@Override
	public boolean blocksMovement(){
		return true;
	}
}
