package chylex.hee.block.material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;

public class MaterialCorruptedEnergy extends MaterialTransparent{
	public MaterialCorruptedEnergy(){
		super(MapColor.airColor);
		setNoPushMobility();
	}
}
