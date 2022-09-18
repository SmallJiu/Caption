package cat.jiu.caption.jiucore.time;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cat.jiu.caption.jiucore.CoreUtils;

public class CaptionTime implements ICaptionTime {
	protected long day;
	protected long hour;
	protected long minute;
	protected long second;
	protected long tick;
	protected long ticks;
	protected long allTicks;
	
	public CaptionTime() {
		this(0);
	}
	public CaptionTime(long sec, long tick) {
		this(0, sec, tick);
	}
	public CaptionTime(long min, long sec, long tick) {
		this(0, 0, min, sec, tick);
	}
	public CaptionTime(long hour, long min, long sec, long tick) {
		this(0, hour, min, sec, tick);
	}
	public CaptionTime(long day, long hour, long min, long sec, long tick) {
		this(parseTick(day, hour, min, sec, tick));
	}
	public CaptionTime(long ticks) {
		this.setTicks(ticks);
		this.setAllTicks(ticks);
	}
	
	public CaptionTime subtractDay(long day) {
		this.day -= day;
		this.replace();
		return this;
	}
	public CaptionTime subtractHour(long hour) {
		this.hour -= hour;
		this.replace();
		return this;
	}
	public CaptionTime subtractMinute(long minute) {
		this.minute -= minute;
		this.replace();
		return this;
	}
	public CaptionTime subtractSecond(long second) {
		this.second -= second;
		this.replace();
		return this;
	}
	public CaptionTime subtractTick(long tick) {
		this.tick -= tick;
		this.replace();
		return this;
	}
	
	public CaptionTime addDay(long day) {
		this.day += day;
		this.replace();
		return this;
	}
	public CaptionTime addHour(long hour) {
		this.hour += hour;
		this.replace();
		return this;
	}
	public CaptionTime addMinute(long minute) {
		this.minute += minute;
		this.replace();
		return this;
	}
	public CaptionTime addSecond(long second) {
		this.second += second;
		this.replace();
		return this;
	}
	public CaptionTime addTick(long tick) {
		this.tick += tick;
		this.replace();
		return this;
	}
	
	public CaptionTime setDay(long day) {
		this.day = day;
		this.replace();
		return this;
	}
	public CaptionTime setHour(long hour) {
		this.hour = hour;
		this.replace();
		return this;
	}
	public CaptionTime setMinute(long minute) {
		this.minute = minute;
		this.replace();
		return this;
	}
	public CaptionTime setSecond(long second) {
		this.second = second;
		this.replace();
		return this;
	}
	public CaptionTime setTick(long tick) {
		this.tick = tick;
		this.replace();
		return this;
	}
	public CaptionTime setAllTicks(long allTicks) {
		this.allTicks = allTicks;
		return this;
	}
	
	@Override
	public CaptionTime add(ICaptionTime time) {
		long ticks = 0;
		if(time instanceof CaptionBigTime) {
			ticks = this.ticks + ((CaptionBigTime)time).ticks.longValue();
		}else {
			ticks = this.ticks + time.getTicks();
		}
		this.format(ticks);
		return this;
	}
	
	@Override
	public CaptionTime subtract(ICaptionTime time) {
		long ticks = 0;
		if(time instanceof CaptionBigTime) {
			ticks = this.ticks - ((CaptionBigTime)time).ticks.longValue();
		}else {
			ticks = this.ticks - time.getTicks();
		}
		this.format(ticks);
		return this;
	}
	
	@Override
	public CaptionTime reset() {
		this.format(this.allTicks);
		return this;
	}
	
	public void format(long ticks) {
		if(ticks <= 0) {
			this.day = 0;
			this.hour = 0;
			this.minute = 0;
			this.second = 0;
			this.tick = 0;
			this.ticks = 0;
			return;
		}
		
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long tick = 0;
		
		if(ticks >= 20) {
			tick = ticks % 20;
			sec += ticks / 20;
		}else {
			tick = ticks;
		}
		if(sec >= 60) {
			min = sec / 60;
			sec %= 60;
		}
		if(min >= 60) {
			hour = min / 60;
			min %= 60;
		}
		if(hour >= 24) {
			day = hour / 24;
			hour %= 24;
		}
		this.day = day;
		this.hour = hour;
		this.minute = min;
		this.second = sec;
		this.tick = tick;
		this.ticks = ticks;
	}
	
	public long getDay() {return day;}
	public long getHour() {return hour;}
	public long getMinute() {return minute;}
	public long getSecond() {return second;}
	public long getTick() {return tick;}
	public long getTicks() {return ticks;}
	public long getAllTicks() {return allTicks;}
	
	public int hashCode() {return (int) this.hash();}
	public boolean equals(Object obj) {return equalsTime(obj);}
	public String toString() {return toStringTime(false);}
	public CaptionTime clone() {return this.copy();}

	public CaptionTime copy() {return new CaptionTime(ticks);}
	
// static
	
	public static CaptionTime getTime(JsonElement e) {
		if(e.isJsonObject()) {
			return getTime(e.getAsJsonObject());
		}else if(e.isJsonPrimitive()) {
			return getTime(e.getAsJsonPrimitive());
		}
		return new CaptionTime();
	}
	
	public static CaptionTime getTime(JsonPrimitive json) {
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long tick = 0;
		if(json.isString()) {
			return getTime(json.getAsString());
		}else {
			tick = json.getAsLong();
		}
		
		return new CaptionTime(day, hour, min, sec, tick);
	}
	
	public static CaptionTime getTime(String time) {
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long tick = 0;
		if(time.contains(":")) {
			String[] times = time.split(":");
			switch(times.length) {
				case 5: day = Long.parseLong(times[4]);
				case 4: hour = Long.parseLong(times[3]);
				case 3: min = Long.parseLong(times[2]);
				case 2: sec = Long.parseLong(times[1]);
				default:
					tick = Long.parseLong(times[0]);
					break;
			}
		}else {
			tick = Long.parseLong(time);
		}
		return new CaptionTime(day, hour, min, sec, tick);
	}
	
	public static CaptionTime getTime(JsonObject obj) {
		return new CaptionTime(
						time(obj, "d", "ds", "day",   "days"),
						time(obj, "h", "hs", "hour",   "hours"),
						time(obj, "m", "ms", "minute", "minutes"),
						time(obj, "s", "ss", "sec",    "secs", "second", "seconds"),
						time(obj, "t", "ts", "tick")
					);
	}
	
	private static long time(JsonObject obj, String... keys) {
		JsonPrimitive pri = CoreUtils.getElement(JsonPrimitive.class, obj, keys);
		if(pri != null && pri.isNumber()) {
			return pri.getAsLong();
		}
		return 0;
	}
	
	public static long parseTick(long s, long tick) {
		return parseTick(0, s, tick);
	}
	
	public static long parseTick(long m, long s, long tick) {
		return parseTick(0, m, s, tick);
	}
	
	public static long parseTick(long h, long m, long s, long tick) {
		return parseTick(0, h, m, s, tick);
	}
	
	public static long parseTick(long day, long h, long m, long s, long tick) {
		return (((((((day*24)+h)*60)+m)*60)+s)*20)+tick;
	}
}
