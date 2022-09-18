package cat.jiu.caption.element;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class CaptionSound {
	protected boolean played = false;
	protected final SoundEvent sound;
	protected final float volume;
	protected final float pitch;
	protected final boolean isFollowPlayer;
	protected final BlockPos pos;
	protected final SoundCategory category;
	
	public CaptionSound(SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this(BlockPos.ORIGIN, true, sound, category, pitch, pitch);
	}
	public CaptionSound(BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this(pos, false, sound, category, pitch, pitch);
	}
	
	protected CaptionSound(BlockPos pos, boolean followPlayer, SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.sound = sound;
		this.category = category;
		this.volume = volume;
		this.pitch = pitch;
		this.pos = pos;
		this.isFollowPlayer = followPlayer;
	}

	public SoundEvent getSound() {return sound;}
	public BlockPos getPos() {return pos;}
	public SoundCategory getSoundCategory() {return getCategory();}
	public float getSoundVolume() {return getVolume();}
	public float getSoundPitch() {return getPitch();}
	public boolean isFollowPlayer() {return isFollowPlayer;}
	public boolean isPlayed() {return played;}
	public void setPlayed() {played = true;}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("sound", SoundEvent.REGISTRY.getIDForObject(this.sound));
		nbt.setString("category", this.getCategory().getName());
		nbt.setFloat("volume", getVolume());
		nbt.setFloat("pitch", getPitch());
		nbt.setTag("pos", toNBT(pos));
		nbt.setBoolean("followPlayer", this.isFollowPlayer);
		return nbt;
	}
	
	public static CaptionSound fromNBT(NBTTagCompound nbt) {
		if(nbt!=null) {
			return new CaptionSound(toPos(nbt.getCompoundTag("pos")), nbt.getBoolean("followPlayer"), SoundEvent.REGISTRY.getObjectById(nbt.getInteger("sound")), SoundCategory.getByName(nbt.getString("category")), nbt.getFloat("volume"), nbt.getFloat("pitch"));
		}
		return null;
	}
	private static BlockPos toPos(NBTTagCompound nbt) {
		int x = nbt.getInteger("x");
		int y = nbt.getInteger("y");
		int z = nbt.getInteger("z");
		if(x == BlockPos.ORIGIN.getX()
		&& y == BlockPos.ORIGIN.getY()
		&& z == BlockPos.ORIGIN.getZ()) {
			return BlockPos.ORIGIN;
		}
		return new BlockPos(x, y, z);
	}
	private static NBTTagCompound toNBT(BlockPos pos) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", pos.getX());
		nbt.setInteger("y", pos.getY());
		nbt.setInteger("z", pos.getZ());
		return nbt;
	}
	public SoundCategory getCategory() {
		return category;
	}
	public float getVolume() {
		return volume;
	}
	public float getPitch() {
		return pitch;
	}
}
