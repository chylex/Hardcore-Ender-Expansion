package chylex.hee.world.structure.island.biome.interaction;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;

public class BiomeInteractionEnchantedIsland{
	public static class InteractionCellarSounds extends AbstractBiomeInteraction{
		private enum Procedure{
			FOOTSTEPS, BLOCK_BREAKING, CHEST_OPENING, MOB_KILLING;
			static final Procedure[] values = values();
		}
		
		private enum BreakEffects{
			GRASS, GRAVEL, SAND, WOOD;
			static final BreakEffects[] values = values();
		}
		
		private enum MobEffects{
			PIG, COW, SHEEP, CHICKEN, ZOMBIE, SKELETON;
			static final MobEffects[] values = values();
		}
		
		private EntityPlayer target;
		private Procedure procedure;
		private byte lastSoundId, cellarCheckTimer;
		private int timer, waitTimer;
		private float soundAngle, soundDist;
		private boolean soundAngleSign, isActive = true;
		
		@Override
		public void init(){
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, getIslandBoundingBox());
			
			if (players.isEmpty()){
				entity.setDead();
				return;
			}
			
			List<EntityTechnicalBiomeInteraction> interactions = world.getEntitiesWithinAABB(EntityTechnicalBiomeInteraction.class, entity.boundingBox.expand(1D, 1D, 1D));
			
			for(int attempt = 0; attempt <= 10; attempt++){
				if (attempt == 10){
					target = null;
					entity.setDead();
					return;
				}
				
				if ((target = players.get(rand.nextInt(players.size()))).isDead)continue;
				
				for(EntityTechnicalBiomeInteraction interaction:interactions){
					if (interaction != entity && interaction.getInteractionType() == InteractionCellarSounds.class && ((InteractionCellarSounds)interaction.getInteraction()).target == target){
						target = null;
						break;
					}
				}
				
				if (target != null)break;
			}
			
			procedure = Procedure.values[rand.nextInt(Procedure.values.length)];
			
			soundDist = 21F+rand.nextFloat()*5F;
			soundAngle = (float)(rand.nextDouble()*Math.PI*2D);
			soundAngleSign = rand.nextBoolean();
		}

		@Override
		public void update(){
			if (target == null || target.isDead || !target.boundingBox.intersectsWith(getIslandBoundingBox())){
				entity.setDead();
				return;
			}
			
			if (rand.nextInt(10) == 0){
				soundAngle += (soundAngleSign ? -1 : 1)*(0.8D+rand.nextDouble()*0.5D)*MathUtil.toRad(40D);
				if (rand.nextInt(80) == 0)soundAngleSign = !soundAngleSign;
			}
			
			if (rand.nextInt(200) == 0)procedure = Procedure.values[rand.nextInt(Procedure.values.length)];
			
			if (waitTimer > 0)--waitTimer;
			if (soundDist > 40D)entity.setDead();
			
			if (++cellarCheckTimer > 10){
				boolean foundBottom = false, foundTop = false;
				int tx = (int)target.posX, tz = (int)target.posZ, minPersegritY = (int)target.posY, maxPersegritY = minPersegritY+1;
				
				for(int a = 0; a < 8; a++){
					if (!foundBottom){
						if (world.getBlock(tx, minPersegritY, tz) != BlockList.persegrit)--minPersegritY;
						else foundBottom = true;
					}
					
					if (!foundTop){
						if (world.getBlock(tx, maxPersegritY, tz) != BlockList.persegrit)++maxPersegritY;
						else foundTop = true;
					}
					
					if (foundBottom && foundTop)break;
				}
				
				if (foundBottom && foundTop){
					if (soundDist > 6D)soundDist -= 0.012D+rand.nextDouble()*0.01D;
					else if (rand.nextInt(100) == 0)soundDist += rand.nextDouble()*5D;
					isActive = true;
				}
				else{
					soundDist += 0.08D;
					isActive = false;
				}
			}
			
			if (!isActive)return;
			
			if (--timer < 0){
				switch(procedure){
					case FOOTSTEPS:
						timer = 7+rand.nextInt(3);
						play(C08PlaySound.PERSEGRIT_FOOTSTEPS, BlockList.persegrit.stepSound.getVolume()*0.15F, BlockList.persegrit.stepSound.getPitch()*1F);
						break;
						
					case BLOCK_BREAKING:
						timer = 7+rand.nextInt(8+rand.nextInt(15));
						if (lastSoundId == 0 || rand.nextInt(8) == 0)lastSoundId = (byte)(1+rand.nextInt(BreakEffects.values.length));
						
						play((byte)(C08PlaySound.GRASS_BREAK-1+lastSoundId), lastSoundId == BreakEffects.WOOD.ordinal()+1 ? 0.5F : 0.25F, BlockList.persegrit.stepSound.getPitch()*0.8F);
						break;
						
					case CHEST_OPENING:
						if (lastSoundId == 0){
							timer = 12+rand.nextInt(10+rand.nextInt(50))+rand.nextInt(15);
							lastSoundId = 1;
							play(C08PlaySound.CHEST_OPEN, 0.12F, 0.9F+rand.nextFloat()*0.1F);
						}
						else{
							timer = 4+rand.nextInt(10+rand.nextInt(70))+rand.nextInt(25);
							lastSoundId = 0;
							play(C08PlaySound.CHEST_CLOSE, 0.1F, 0.9F+rand.nextFloat()*0.1F);
						}
						
						break;
						
					case MOB_KILLING:
						if (lastSoundId == 0)lastSoundId = (byte)(1+rand.nextInt(MobEffects.values.length));
						timer = 10+rand.nextInt(4+rand.nextInt(7)*rand.nextInt(2));
						play((byte)(C08PlaySound.PIG_HURT+2*(lastSoundId-1)+(rand.nextInt(6) == 0 ? 1 : 0)), lastSoundId == MobEffects.COW.ordinal()+1 ? 0.4F : 1F, 1F+(rand.nextFloat()-rand.nextFloat())*0.2F);
						break;
				}
				
				if (--waitTimer < -8-rand.nextInt(12)){
					waitTimer = 10+MathUtil.floor(15F*soundDist*(0.3F+rand.nextFloat()*0.6F));
				}
			}
		}
		
		private void play(byte soundId, float volume, float pitch){
			PacketPipeline.sendToPlayer(target, new C08PlaySound(soundId, target.posX+MathHelper.cos(soundAngle)*soundDist, target.posY, target.posZ+MathHelper.sin(soundAngle)*soundDist, volume, pitch));
		}

		@Override
		public void saveToNBT(NBTTagCompound nbt){}

		@Override
		public void loadFromNBT(NBTTagCompound nbt){}
	}
}
