package cat.jiu.caption.util;

import cat.jiu.caption.Caption;
import cat.jiu.caption.jiucore.time.ICaptionTime;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;

public class CapitonSound extends MovingSound {
	protected final EntityPlayer player;
	protected final ICaptionTime playTime;
	public CapitonSound(EntityPlayer player, Caption.Sound sound, ICaptionTime playTime) {
		super(sound.getSound(), sound.getSoundCategory());
		this.player = player;
		this.playTime = playTime;
		this.pitch = sound.getSoundPitch();
		this.volume = sound.getSoundVolume();
	}

	@Override
	public void update() {
		if(!this.playTime.isDone()) {
			this.xPosF = (float)this.player.posX;
            this.yPosF = (float)this.player.posY;
            this.zPosF = (float)this.player.posZ;
		}else {
			this.donePlaying = true;
		}
	}
}
