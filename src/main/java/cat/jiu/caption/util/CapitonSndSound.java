package cat.jiu.caption.util;

import cat.jiu.caption.element.CaptionSound;
import cat.jiu.caption.jiucore.time.ICaptionTime;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CapitonSndSound extends MovingSound {
	protected final EntityPlayer player;
	protected final ICaptionTime playTime;
	public CapitonSndSound(EntityPlayer player, CaptionSound sound, ICaptionTime playTime) {
		super(sound.getSound(), sound.getSoundCategory());
		this.player = player;
		this.playTime = playTime;
		this.pitch = sound.getSoundPitch();
		this.volume = sound.getSoundVolume();
	}

	@Override
	public void update() {
		this.xPosF = (float)this.player.posX;
        this.yPosF = (float)this.player.posY;
        this.zPosF = (float)this.player.posZ;
	}
	@Override
	public boolean isDonePlaying() {
		return this.playTime.isDone();
	}
}
