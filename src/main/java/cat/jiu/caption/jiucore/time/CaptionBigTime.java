package cat.jiu.caption.jiucore.time;

import java.math.BigInteger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cat.jiu.caption.jiucore.CoreUtils;
import cat.jiu.core.util.JiuUtils;
import net.minecraft.nbt.NBTTagCompound;

public class CaptionBigTime implements ICaptionTime {
	protected BigInteger day;
	protected BigInteger hour;
	protected BigInteger minute;
	protected BigInteger second;
	protected BigInteger tick;
	protected BigInteger ticks;
	protected BigInteger allTicks;
	
	public CaptionBigTime() {
		this(0);
	}
	public CaptionBigTime(long ticks) {
		this(BigInteger.valueOf(ticks));
	}
	public CaptionBigTime(long sec, long tick) {
		this(0, sec, tick);
	}
	public CaptionBigTime(long min, long sec, long tick) {
		this(0, 0, min, sec, tick);
	}
	public CaptionBigTime(long hour, long min, long sec, long tick) {
		this(0, hour, min, sec, tick);
	}
	public CaptionBigTime(long day, long hour, long min, long sec, long tick) {
		this(parseTick(day, hour, min, sec, tick));
	}
	
	public CaptionBigTime(BigInteger ticks) {
		this.format(ticks);
		this.setAllTicks(ticks);
	}
	public CaptionBigTime(BigInteger s, BigInteger tick) {
		this(BigInteger.ZERO, s, tick);
	}
	
	public CaptionBigTime(BigInteger m, BigInteger s, BigInteger tick) {
		this(BigInteger.ZERO, m, s, tick);
	}
	
	public CaptionBigTime(BigInteger h, BigInteger m, BigInteger s, BigInteger tick) {
		this(BigInteger.ZERO, h, m, s, tick);
	}
	
	public CaptionBigTime(BigInteger day, BigInteger h, BigInteger m, BigInteger s, BigInteger tick) {
		this(parseTick(day, h, m, s, tick));
	}
	
	protected static final BigInteger twenty = BigInteger.valueOf(20);
	protected static final BigInteger sixty = BigInteger.valueOf(60);
	protected static final BigInteger twenty_four = BigInteger.valueOf(24);
	
	public void format(BigInteger ticks) {
		if(CoreUtils.lessOrEqual(ticks, BigInteger.ZERO)) {
			this.day = BigInteger.ZERO;
			this.hour = BigInteger.ZERO;
			this.minute = BigInteger.ZERO;
			this.second = BigInteger.ZERO;
			this.tick = BigInteger.ZERO;
			this.ticks = BigInteger.ZERO;
			return;
		}
		BigInteger day = BigInteger.ZERO;
		BigInteger hour = BigInteger.ZERO;
		BigInteger min = BigInteger.ZERO;
		BigInteger sec = BigInteger.ZERO;
		BigInteger tick = BigInteger.ZERO;

		if(CoreUtils.greaterOrEqual(ticks, twenty)) {
			tick = ticks.remainder(twenty);
			sec = ticks.divide(twenty);
		}else {
			tick = ticks;
		}
		if(CoreUtils.greaterOrEqual(sec, sixty)) {
			min = sec.divide(sixty);
			sec = sec.remainder(sixty);
		}
		if(CoreUtils.greaterOrEqual(min, sixty)) {
			hour = min.divide(sixty);
			min = min.remainder(sixty);
		}
		if(CoreUtils.greaterOrEqual(hour, twenty_four)) {
			day = hour.divide(twenty_four);
			hour = hour.remainder(twenty_four);
		}
		
		this.day = day;
		this.hour = hour;
		this.minute = min;
		this.second = sec;
		this.tick = tick;
		this.ticks = ticks;
	}
	
	public CaptionBigTime setDay(BigInteger day) {
		this.day = day;
		this.replace();
		return this;
	}
	public CaptionBigTime setHour(BigInteger hour) {
		this.hour = hour;
		this.replace();
		return this;
	}
	public CaptionBigTime setMinute(BigInteger minute) {
		this.minute = minute;
		this.replace();
		return this;
	}
	public CaptionBigTime setSecond(BigInteger second) {
		this.second = second;
		this.replace();
		return this;
	}
	public CaptionBigTime setTick(BigInteger tick) {
		this.tick = tick;
		this.replace();
		return this;
	}
	public CaptionBigTime setAllTicks(BigInteger allTicks) {
		this.allTicks = allTicks;
		return this;
	}
	
	@Override
	public void replace() {
		if(CoreUtils.lessOrEqual(this.tick, BigInteger.ZERO)) this.tick = BigInteger.ZERO;
		if(CoreUtils.lessOrEqual(this.second, BigInteger.ZERO)) this.second = BigInteger.ZERO;
		if(CoreUtils.lessOrEqual(this.minute, BigInteger.ZERO)) this.minute = BigInteger.ZERO;
		if(CoreUtils.lessOrEqual(this.hour, BigInteger.ZERO)) this.hour = BigInteger.ZERO;
		if(CoreUtils.lessOrEqual(this.day, BigInteger.ZERO)) this.day = BigInteger.ZERO;
		this.format(parseTick(day, hour, minute, second, tick));
	}

// implement
	public void update(int subtractTick) {this.format(this.ticks.subtract(BigInteger.valueOf(subtractTick)));}
	public void format(long ticks) {this.format(BigInteger.valueOf(ticks));}
	public boolean isDone() {return CoreUtils.lessOrEqual(this.ticks, BigInteger.ZERO);}
	
	public long getDay() {return this.day.longValue();}
	public long getHour() {return this.hour.longValue();}
	public long getMinute() {return this.minute.longValue();}
	public long getSecond() {return this.second.longValue();}
	public long getTick() {return this.tick.longValue();}
	public long getTicks() {return this.ticks.longValue();}
	public long getAllTicks() {return this.allTicks.longValue();}
	
	public BigInteger getBigDay() {return this.day;}
	public BigInteger getBigHour() {return this.hour;}
	public BigInteger getBigMinute() {return this.minute;}
	public BigInteger getBigSecond() {return this.second;}
	public BigInteger getBigTick() {return this.tick;}
	public BigInteger getBigTicks() {return this.ticks;}
	public BigInteger getBigAllTicks() {return this.allTicks;}
	
	public CaptionBigTime setDay(long day) {return this.setDay(BigInteger.valueOf(day));}
	public CaptionBigTime setHour(long hour) {return this.setHour(BigInteger.valueOf(hour));}
	public CaptionBigTime setMinute(long minute) {return this.setMinute(BigInteger.valueOf(minute));}
	public CaptionBigTime setSecond(long second) {return this.setSecond(BigInteger.valueOf(second));}
	public CaptionBigTime setTick(long tick) {return this.setTick(BigInteger.valueOf(tick));}
	public CaptionBigTime setAllTicks(long allTicks) {return this.setAllTicks(BigInteger.valueOf(allTicks));}
	public CaptionBigTime copy() {return new CaptionBigTime(ticks);}
	public long hash() {return this.allTicks.hashCode();}
	
	public boolean equals(Object obj) {return this.equalsTime(obj);}
	public String toString() {return this.toStringTime(false);}
	public CaptionBigTime clone() {return this.copy();}
	public int hashCode() {return this.allTicks.hashCode();}
	
	public boolean isPart(int numerator, int denominator) {
		BigInteger parts = this.allTicks.divide(BigInteger.valueOf(denominator)).multiply(BigInteger.valueOf(numerator));
		return this.ticks.equals(parts);
	}
	public float getSurplusPart() {
		/* unsafe
		BigDecimal ticks = new BigDecimal(this.ticks);
		BigDecimal allTicks = new BigDecimal(this.allTicks);
		
		BigDecimal b3 = ticks.subtract(allTicks).multiply(BigDecimal.valueOf(1)).divide(allTicks).add(BigDecimal.valueOf(1));
		return b3.floatValue();
		*/
		return ICaptionTime.super.getSurplusPart();
	}
	
	public int getPart(int denominator) {
		BigInteger part = this.allTicks.divide(BigInteger.valueOf(denominator));
		for(int i = 1; i < denominator+1; i++) {
			if(this.ticks.equals(part.multiply(BigInteger.valueOf(i)))) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public String toStringTime(boolean reverse) {
		if(reverse) {
			return CoreUtils.addJoins(10, ":", this.tick, this.second, this.minute, this.hour, this.day);
		}
		return CoreUtils.addJoins(10, ":", this.day, this.hour, this.minute, this.second,this.tick);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		this.format(JiuUtils.big_integer.create(nbt.getString("ticks")));
		this.setAllTicks(JiuUtils.big_integer.create(nbt.getString("allTicks")));
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt, boolean writeAll) {
		if(nbt == null) nbt = new NBTTagCompound();
		if(writeAll) {
			nbt.setString("day", this.getBigDay().toString());
			nbt.setString("hour", this.getBigHour().toString());
			nbt.setString("minute", this.getBigMinute().toString());
			nbt.setString("second", this.getBigSecond().toString());
			nbt.setString("tick", this.getBigTick().toString());
		}
		nbt.setString("ticks", this.getBigTicks().toString());
		nbt.setString("allTicks", this.getBigAllTicks().toString());
		nbt.setBoolean("isBig", true);
		return nbt;
	}
	
	public void toTime(JsonObject obj) {
		this.format(obj.get("ticks").getAsBigInteger());
		this.setAllTicks(JiuUtils.big_integer.create(obj.get("allTicks").getAsString()));
	}
	
	public JsonObject toJson(boolean writeAll) {
		JsonObject obj = new JsonObject();
		if(writeAll) {
			obj.addProperty("day", this.day);
			obj.addProperty("hour", this.hour);
			obj.addProperty("minute", this.minute);
			obj.addProperty("second", this.second);
			obj.addProperty("tick", this.tick);
		}
		obj.addProperty("ticks", this.ticks);
		obj.addProperty("allTicks", this.getBigAllTicks().toString());
		obj.addProperty("isBig", true);
		
		return obj;
	}
	
// static
	
	public static CaptionBigTime getTime(JsonElement e) {
		if(e.isJsonObject()) {
			return getTime(e.getAsJsonObject());
		}else if(e.isJsonPrimitive()) {
			return getTime(e.getAsJsonPrimitive());
		}
		return new CaptionBigTime();
	}
	
	public static CaptionBigTime getTime(JsonPrimitive json) {
		BigInteger day = BigInteger.ZERO;
		BigInteger hour = BigInteger.ZERO;
		BigInteger min = BigInteger.ZERO;
		BigInteger sec = BigInteger.ZERO;
		BigInteger tick = BigInteger.ZERO;
		if(json.isString()) {
			return getTime(json.getAsString());
		}else {
			tick = new BigInteger(json.getAsString());
		}
		
		return new CaptionBigTime(day, hour, min, sec, tick);
	}
	
	public static CaptionBigTime getTime(String time) {
		BigInteger day = BigInteger.ZERO;
		BigInteger hour = BigInteger.ZERO;
		BigInteger min = BigInteger.ZERO;
		BigInteger sec = BigInteger.ZERO;
		BigInteger tick = BigInteger.ZERO;
		if(time.contains(":")) {
			String[] times = time.split(":");
			switch(times.length) {
				case 5: day = new BigInteger(times[4]);
				case 4: hour = new BigInteger(times[3]);
				case 3: min = new BigInteger(times[2]);
				case 2: sec = new BigInteger(times[1]);
				default:
					tick = new BigInteger(times[0]);
					break;
			}
		}else {
			tick = new BigInteger(time);
		}
		return new CaptionBigTime(day, hour, min, sec, tick);
	}
	
	public static CaptionBigTime getTime(JsonObject obj) {
		return new CaptionBigTime(
						time(obj, "d", "ds", "day",   "days"),
						time(obj, "h", "hs", "hour",   "hours"),
						time(obj, "m", "ms", "minute", "minutes"),
						time(obj, "s", "ss", "sec",    "secs", "second", "seconds"),
						time(obj, "t", "ts", "tick")
					);
	}
	
	private static BigInteger time(JsonObject obj, String... keys) {
		JsonPrimitive pri = CoreUtils.getElement(JsonPrimitive.class, obj, keys);
		if(pri != null && pri.isString()) {
			return new BigInteger(pri.getAsString());
		}
		return BigInteger.ZERO;
	}
	
	public static BigInteger parseTick(long s, long tick) {
		return parseTick(0, s, tick);
	}
	
	public static BigInteger parseTick(long m, long s, long tick) {
		return parseTick(0, m, s, tick);
	}
	
	public static BigInteger parseTick(long h, long m, long s, long tick) {
		return parseTick(0, h, m, s, tick);
	}
	
	public static BigInteger parseTick(long day, long h, long m, long s, long tick) {
		return parseTick(BigInteger.valueOf(day), BigInteger.valueOf(h), BigInteger.valueOf(m), BigInteger.valueOf(s), BigInteger.valueOf(tick));
	}
	
	public static BigInteger parseTick(BigInteger s, BigInteger tick) {
		return parseTick(BigInteger.ZERO, s, tick);
	}
	
	public static BigInteger parseTick(BigInteger m, BigInteger s, BigInteger tick) {
		return parseTick(BigInteger.ZERO, m, s, tick);
	}
	
	public static BigInteger parseTick(BigInteger h, BigInteger m, BigInteger s, BigInteger tick) {
		return parseTick(BigInteger.ZERO, h, m, s, tick);
	}
	
	public static BigInteger parseTick(BigInteger day, BigInteger h, BigInteger m, BigInteger s, BigInteger tick) {
		return tick.add(
				s.add(
					m.add(
						h.add(day.multiply(twenty_four))
						.multiply(sixty))
					.multiply(sixty))
				.multiply(twenty));
	}
}
