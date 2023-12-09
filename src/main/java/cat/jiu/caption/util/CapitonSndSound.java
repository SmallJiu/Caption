package cat.jiu.caption.util;

import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CapitonSndSound extends MovingSound {
	protected final EntityPlayer player;
	protected final ITimer playTime;
	protected final ISound sound;
	public CapitonSndSound(EntityPlayer player, ISound sound, ITimer playTime) {
		super(sound.getSound(), sound.getSoundCategory());
		this.player = player;
		this.playTime = playTime;
		this.sound = sound;
		this.pitch = sound.getSoundPitch();
		this.volume = sound.getSoundVolume();
		
		this.xPosF = (float)sound.getPlayPosition().getX() + 0.5F;
        this.yPosF = (float)sound.getPlayPosition().getY() + 0.5F;
        this.zPosF = (float)sound.getPlayPosition().getZ() + 0.5F;
	}

	@Override
	public void update() {
		if(this.sound.isFollowEntity()) {
			this.xPosF = (float)this.player.posX;
	        this.yPosF = (float)this.player.posY;
	        this.zPosF = (float)this.player.posZ;
		}
	}
	@Override
	public boolean isDonePlaying() {
		return this.playTime.isDone();
	}
}
