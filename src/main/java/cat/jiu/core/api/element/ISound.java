package cat.jiu.core.api.element;

import com.google.gson.JsonObject;

import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.handler.ISerializable;
import cat.jiu.core.util.timer.Timer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public interface ISound extends ISerializable {
	SoundEvent getSound();
	ISound setSound(SoundEvent sound);
	
	float getSoundVolume();
	ISound setSoundVolume(float volume);
	
	float getSoundPitch();
	ISound setSoundPitch(float pitch);
	
	SoundCategory getSoundCategory();
	ISound setSoundCategory(SoundCategory sc);
	
	BlockPos getPlayPosition();
	ISound setPlayPosition(BlockPos pos);
	
	ITimer getTime();
	ISound setTime(ITimer time);
	
	boolean isFollowEntity();
	ISound setFollowEntity(boolean isFollow);
	
	boolean isPlayed();
	ISound setPlayed(boolean played);
	
	ISound copy();
	
	default NBTTagCompound write(NBTTagCompound nbt) {
		if(nbt==null) nbt = new NBTTagCompound();
		nbt.setInteger("sound", SoundEvent.REGISTRY.getIDForObject(this.getSound()));
		nbt.setFloat("volume", this.getSoundVolume());
		nbt.setFloat("pitch", this.getSoundPitch());
		nbt.setLong("millis", this.getTime().getAllTicks());
		nbt.setString("category", this.getSoundCategory().getName());
		nbt.setBoolean("followEntity", this.isFollowEntity());
		if(!BlockPos.ORIGIN.equals(this.getPlayPosition())) {
			nbt.setTag("playPosition", writePositionNBT(this.getPlayPosition()));
		}
		return nbt;
	}
	default void read(NBTTagCompound nbt) {
		this.setTime(new Timer(nbt.getLong("millis")));
		this.setSound(SoundEvent.REGISTRY.getObjectById(nbt.getInteger("sound")));
		this.setSoundVolume(nbt.getFloat("volume"));
		this.setSoundPitch(nbt.getFloat("pitch"));
		this.setSoundCategory(SoundCategory.getByName(nbt.getString("category")));
		this.setFollowEntity(nbt.getBoolean("followEntity"));
		if(nbt.hasKey("playPosition")) {
			this.setPlayPosition(readPosition(nbt.getCompoundTag("playPosition")));
		}
	}
	
	default JsonObject write(JsonObject json) {
		if(json==null) json = new JsonObject();
		json.addProperty("id", SoundEvent.REGISTRY.getIDForObject(this.getSound()));
		json.addProperty("pitch", this.getSoundPitch());
		json.addProperty("volume", this.getSoundVolume());
		json.addProperty("millis", this.getTime().getAllTicks());
		json.addProperty("category", this.getSoundCategory().getName());
		json.addProperty("followEntity", this.isFollowEntity());
		if(!BlockPos.ORIGIN.equals(this.getPlayPosition())) {
			json.add("playPosition", writePositionJson(this.getPlayPosition()));
		}
		return json;
	}
	default void read(JsonObject json) {
		this.setTime(new Timer(json.get("millis").getAsLong()));
		
		SoundEvent sound = null;
		if(json.getAsJsonPrimitive("id").isString()) {
			sound = SoundEvent.REGISTRY.getObject(new ResourceLocation(json.get("id").getAsString()));
			if(sound == null) {
				sound = new SoundEvent(new ResourceLocation(json.get("id").getAsString()));
			}
		}else if(json.getAsJsonPrimitive("id").isNumber()) {
			sound = SoundEvent.REGISTRY.getObjectById(json.get("id").getAsInt());
		}
		this.setSound(sound);
		
		this.setSoundVolume(json.get("volume").getAsFloat());
		this.setSoundPitch(json.get("pitch").getAsFloat());
		this.setSoundCategory(SoundCategory.getByName(json.get("category").getAsString()));
		this.setFollowEntity(json.get("followEntity").getAsBoolean());
		if(json.has("playPosition")) {
			this.setPlayPosition(readPosition(json.getAsJsonObject("playPosition")));
		}
	}
	
	public static NBTTagCompound writePositionNBT(BlockPos pos) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", pos.getX());
		nbt.setInteger("y", pos.getY());
		nbt.setInteger("z", pos.getZ());
		return nbt;
	}
	public static BlockPos readPosition(NBTTagCompound nbt) {
		return new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
	}
	
	public static JsonObject writePositionJson(BlockPos pos) {
		JsonObject nbt = new JsonObject();
		nbt.addProperty("x", pos.getX());
		nbt.addProperty("y", pos.getY());
		nbt.addProperty("z", pos.getZ());
		return nbt;
	}
	public static BlockPos readPosition(JsonObject nbt) {
		return new BlockPos(nbt.get("x").getAsInt(), nbt.get("y").getAsInt(), nbt.get("z").getAsInt());
	}
}
