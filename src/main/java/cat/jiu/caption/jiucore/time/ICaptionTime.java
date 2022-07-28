package cat.jiu.caption.jiucore.time;

import com.google.gson.JsonObject;

import cat.jiu.caption.jiucore.CoreUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

public interface ICaptionTime {
	/**
	 * the core method, 
	 * use {@code ticks} to format time
	 * 
	 * @param ticks all tick
	 * @author small_jiu
	 */
	void format(long ticks);
	
	long getDay();
	long getHour();
	long getMinute();
	long getSecond();
	long getTick();
	long getTicks();
	long getAllTicks();
	
	ICaptionTime setDay(long day);
	ICaptionTime setHour(long hour);
	ICaptionTime setMinute(long minute);
	ICaptionTime setSecond(long second);
	ICaptionTime setTick(long tick);
	ICaptionTime setAllTicks(long allTicks);

	ICaptionTime copy();
	
	default ICaptionTime setAllTicks(long s, long tick) {
		return this.setAllTicks(CaptionTime.parseTick(s, tick));
	}
	default ICaptionTime setAllTicks(long m, long s, long tick) {
		return this.setAllTicks(CaptionTime.parseTick(m, s, tick));
	}
	default ICaptionTime setAllTicks(long h, long m, long s, long tick) {
		return this.setAllTicks(CaptionTime.parseTick(h, m, s, tick));
	}
	default ICaptionTime setAllTicks(long day, long h, long m, long s, long tick) {
		return this.setAllTicks(CaptionTime.parseTick(day, h, m, s, tick));
	}
	default ICaptionTime setTicks(long ticks) {
		this.format(ticks);
		return this;
	}
	
	default boolean isDone() {
		return this.getTicks() <= 0;
	}
	
	/**
	 * @param denominator the denominator, like '1/5' of '5' 
	 * @param numerator the numerator, like '1/5' of '1'
	 * @return true if ticks == numerator/denominator
	 */
	default boolean isPart(int numerator, int denominator) {
		return this.getTicks() == (this.getAllTicks() / denominator) * numerator;
	}
	default int getPart(int denominator) {
		long part = this.getAllTicks() / denominator;
		for(int i = 1; i < denominator+1; i++) {
			if(this.getTicks() == part*i) {
				return i;
			}
		}
		return -1;
	}
	default float getSurplusPart() {
		return (float) (((this.getTicks() - this.getAllTicks()) * 1.0 / this.getAllTicks())+1);
	}
	
	default void replace() {
		if(this.getTick() <= 0) this.setTick(0);
		if(this.getSecond() <= 0) this.setSecond(0);
		if(this.getMinute() <= 0) this.setMinute(0);
		if(this.getHour() <= 0) this.setHour(0);
		if(this.getDay() <= 0) this.setDay(0);
		
		this.format(CaptionTime.parseTick(this.getDay(), this.getHour(), this.getMinute(), this.getSecond(), this.getTick()));
	}
	
	default String toStringTime(boolean reverse) {
		if(reverse) {
			return CoreUtils.addJoins(10, ":", this.getTick(), this.getSecond(), this.getMinute(), this.getHour(), this.getDay());
		}else {
			return CoreUtils.addJoins(10, ":", this.getDay(), this.getHour(), this.getMinute(), this.getSecond(), this.getTick());
		}
	}
	default long hash() {
		return this.getAllTicks() >> 9;
	}
	default boolean equalsTime(Object obj) {
		if(obj == this) return true;
		if(obj instanceof ICaptionTime) {
			ICaptionTime other = (ICaptionTime) obj;
			other.replace();
			this.replace();
			return this.hash() == other.hash() && this.getTicks() == other.getTicks();
		}
		
		return false;
	}
	
	default void update(int subtractTick) {
		this.format(this.getTicks() - subtractTick);
	}
	default void update() {this.update(1);}
	default void readFromNBT(NBTTagCompound nbt) {
		this.format(nbt.getLong("ticks"));
		this.setAllTicks(nbt.getLong("allTicks"));
	}
	
	default NBTTagCompound writeToNBT(NBTTagCompound nbt, boolean writeAll) {
		if(nbt == null) nbt = new NBTTagCompound();
		if(writeAll) {
			nbt.setLong("day", this.getDay());
			nbt.setLong("hour", this.getHour());
			nbt.setLong("minute", this.getMinute());
			nbt.setLong("second", this.getSecond());
			nbt.setLong("tick", this.getTick());
		}
		nbt.setLong("ticks", this.getTicks());
		nbt.setLong("allTicks", this.getAllTicks());
		nbt.setBoolean("isBig", this instanceof CaptionBigTime);
		return nbt;
	}
	
	default void toTime(JsonObject obj) {
		this.format(obj.get("ticks").getAsLong());
		this.setAllTicks(obj.get("allTicks").getAsLong());
	}
	default JsonObject toJson(boolean writeAll) {
		JsonObject obj = new JsonObject();
		if(writeAll) {
			obj.addProperty("day", this.getDay());
			obj.addProperty("hour", this.getHour());
			obj.addProperty("minute", this.getMinute());
			obj.addProperty("second", this.getSecond());
			obj.addProperty("tick", this.getTick());
		}
		obj.addProperty("ticks", this.getTicks());
		obj.addProperty("allTicks", this.getAllTicks());
		obj.addProperty("isBig", this instanceof CaptionBigTime);
		
		return obj;
	}
	
	@Optional.Method(modid = "jiucore")
	default cat.jiu.core.api.ITime toCoreTime() {
		cat.jiu.core.api.ITime time = null;
		if(this instanceof CaptionTime) {
			time = new cat.jiu.core.util.Time(this.getTicks());
			time.setAllTicks(this.getAllTicks());
			
		}else if(this instanceof CaptionBigTime) {
			time = new cat.jiu.core.util.BigTime(((CaptionBigTime)this).getBigTicks());
			time.setAllTicks(((CaptionBigTime)this).getAllTicks());
		}
		return time;
	}
	
	@Optional.Method(modid = "jiucore")
	static ICaptionTime fromCoreTime(cat.jiu.core.api.ITime time) {
		ICaptionTime dTime = null;
		if(time instanceof cat.jiu.core.util.Time) {
			dTime = new CaptionTime(time.getTicks());
			dTime.setAllTicks(time.getAllTicks());
			
		}else if(time instanceof cat.jiu.core.util.BigTime) {
			dTime = new CaptionBigTime(((cat.jiu.core.util.BigTime)time).getTicks());
			dTime.setAllTicks(((cat.jiu.core.util.BigTime)time).getAllTicks());
		}
		return dTime;
	}
	
	public static ICaptionTime from(NBTTagCompound nbt) {
		ICaptionTime time = nbt.getBoolean("isBig") ? new CaptionBigTime() : new CaptionTime();
		time.readFromNBT(nbt);
		return time;
	}
	public static ICaptionTime from(JsonObject obj) {
		ICaptionTime time = obj.has("isBig") ? new CaptionBigTime() : new CaptionTime();
		time.toTime(obj);
		return time;
	}
}
