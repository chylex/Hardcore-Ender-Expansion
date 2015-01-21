package chylex.hee.render.entity.layer;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.render.entity.RenderMobLouse;
import chylex.hee.render.entity.RenderMobLouse.RuneColor;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAbility;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAttribute;

@SideOnly(Side.CLIENT)
public class LayerLouseTop implements LayerRenderer{
	private static final ResourceLocation[] texLouseRuneBottom = new ResourceLocation[]{
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_b1.png"),
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_b2.png"),
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_b3.png"),
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_b4.png")
	};
	
	private static final ResourceLocation[] texLouseRuneTop = new ResourceLocation[]{
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_t1.png"),
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_t2.png"),
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_t3.png"),
		new ResourceLocation("hardcoreenderexpansion:textures/entity/louse_t4.png")
	};
	
	private static final Random runeTexRand = new Random();
	
	private final RenderMobLouse renderer;
	private final byte layerId;
	
	public LayerLouseTop(RenderMobLouse renderer, int layerId){
		this.renderer = renderer;
		this.layerId = (byte)layerId;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime, float entityTickTime, float rotationYaw, float rotationPitch, float unitSize){
		LouseSpawnData louseData = ((EntityMobLouse)entity).getSpawnData();
		if (louseData == null)return;
		
		Set<EnumLouseAttribute> attributes = louseData.getAttributeSet();
		Set<EnumLouseAbility> abilities = louseData.getAbilitySet();
		
		if (attributes.isEmpty() && abilities.isEmpty())return;
		
		ResourceLocation res;
		RuneColor color = RuneColor.RED;
		
		if (abilities.isEmpty()){
			if (attributes.size() == 1 || layerId == 0)color = renderer.getColorFromAttribute(attributes.iterator().next());
			else if (layerId == 1 && attributes.size() == 2){
				Iterator<EnumLouseAttribute> iter = attributes.iterator();
				iter.next();
				color = renderer.getColorFromAttribute(iter.next());
			}
		}
		else if (attributes.size() == 1){
			if (layerId == 0)color = renderer.getColorFromAttribute(attributes.iterator().next());
			else if (layerId == 1)color = RuneColor.GRAY;
		}
		
		runeTexRand.setSeed(42);
		for(EnumLouseAttribute attribute:attributes)runeTexRand.nextInt(1+attribute.ordinal());
		for(EnumLouseAbility ability:abilities)runeTexRand.nextInt(1+ability.ordinal());
		
		if (layerId == 0)res = texLouseRuneBottom[runeTexRand.nextInt(texLouseRuneBottom.length)];
		else res = texLouseRuneTop[runeTexRand.nextInt(texLouseRuneTop.length)];
		
		renderer.bindTexture(res);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		renderer.getMainModel().render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitSize);
		renderer.func_177105_a((EntityLiving)entity,partialTickTime);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
		
		GL11.glColor4f(color.red,color.green,color.blue,1F);
	}

	@Override
	public boolean shouldCombineTextures(){
		return false;
	}
}
