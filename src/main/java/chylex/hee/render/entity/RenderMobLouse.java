package chylex.hee.render.entity;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.render.model.ModelLouse;
import chylex.hee.tileentity.spawner.LouseSpawnerLogic.LouseSpawnData;
import chylex.hee.tileentity.spawner.LouseSpawnerLogic.LouseSpawnData.EnumLouseAbility;
import chylex.hee.tileentity.spawner.LouseSpawnerLogic.LouseSpawnData.EnumLouseAttribute;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobLouse extends RenderLiving{
	private static final ResourceLocation texLouse = new ResourceLocation("hardcoreenderexpansion:textures/entity/louse.png");
	
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

	public RenderMobLouse(){
		super(new ModelLouse(),0.65F);
	}
	
	private enum RuneColor{
		RED(0.5469F,0F,0F), YELLOW(0.5469F,0.5469F,0F), GREEN(0F,0.5469F,0F), BLUE(0F,0.41F,0.5469F), PURPLE(0.3633F,0F,0.5469F), GRAY(0.5469F,0.5469F,0.5469F);
		
		public final float red, green, blue;
		
		RuneColor(float red, float green, float blue){
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	}
	
	private RuneColor getColorFromAttribute(EnumLouseAttribute attribute){
		switch(attribute){
			case ATTACK: return RuneColor.RED;
			case SPEED: return RuneColor.YELLOW;
			case HEALTH: return RuneColor.GREEN;
			case ARMOR: return RuneColor.BLUE;
			case TELEPORT: return RuneColor.PURPLE;
			default: return RuneColor.RED;
		}
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL11.glScalef(0.6F,0.6F,0.6F);
	}
	
	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime){
		if (pass == 1 || pass == 2){
			LouseSpawnData louseData = ((EntityMobLouse)entity).getSpawnData();
			
			Set<EnumLouseAttribute> attributes = louseData.getAttributeSet();
			Set<EnumLouseAbility> abilities = louseData.getAbilitySet();
			
			if (attributes.isEmpty() && abilities.isEmpty())return -1;
			
			ResourceLocation res;
			RuneColor color = RuneColor.RED;
			
			if (abilities.isEmpty()){
				if (attributes.size() == 1 || pass == 1)color = getColorFromAttribute(attributes.iterator().next());
				else if (pass == 2 && attributes.size() == 2){
					Iterator<EnumLouseAttribute> iter = attributes.iterator();
					iter.next();
					color = getColorFromAttribute(iter.next());
				}
			}
			else if (attributes.size() == 1){
				if (pass == 1)color = getColorFromAttribute(attributes.iterator().next());
				else if (pass == 2)color = RuneColor.GRAY;
			}
			
			runeTexRand.setSeed(42);
			for(EnumLouseAttribute attribute:attributes)runeTexRand.nextInt(attribute.ordinal());
			for(EnumLouseAbility ability:abilities)runeTexRand.nextInt(ability.ordinal());
			
			if (pass == 1){
				
			}
			else{
				
			}
			
			bindTexture(res);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			setRenderPassModel(mainModel);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(color.red,color.green,color.blue,1F);
			GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
			
			return 1;
		}
		else if (pass == 3){
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glDisable(GL11.GL_BLEND);
		}

		return -1;
	}
	
	private void setupOverlayTex(){
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		setRenderPassModel(mainModel);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
	}

	@Override
	protected int inheritRenderPass(EntityLivingBase entity, int pass, float partialTickTime){
		return -1;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texLouse;
	}
}
