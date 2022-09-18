package cat.jiu.caption.jiucore;

import java.math.BigInteger;
import java.util.StringJoiner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * see mod JiuCore
 * @author small_jiu
 */
public final class CoreUtils {
	@SuppressWarnings({"unchecked"})
	public static <T extends JsonElement> T getElement(Class<T> type, JsonObject obj, String... keys) {
		JsonType jsonType = JsonType.getType(type);
		T result = null;
		
		lable: for(String key : keys) {
			if(obj.has(key)) {
				JsonElement e = obj.get(key);
				if(e != null) {
					switch(jsonType) {
						case Object:
							if(e.isJsonObject()) {
								result = (T) e.getAsJsonObject();
								break lable;
							}
						case Array:
							if(e.isJsonArray()) {
								result = (T) e.getAsJsonArray();
								break lable;
							}
						case Primitive:
							if(e.isJsonPrimitive()) {
								result = (T) e.getAsJsonPrimitive();
								break lable;
							}
						case Element:
								result = (T) e;
								break lable;
					}
				}
			}
		}
		return result;
	}
	
	static enum JsonType {
		Object, Array, Primitive, Element;
		static <T extends JsonElement> JsonType getType(Class<T> type) {
			if(type == JsonObject.class) {
				return JsonType.Object;
			}else if(type == JsonArray.class) {
				return JsonType.Array;
			}else if(type == JsonPrimitive.class) {
				return JsonType.Primitive;
			}
			return JsonType.Element;
		}
	}
	
	public static String addJoins(long format, CharSequence delimiter, Object... args) {
		return addJoins(format, delimiter, "", "", args);
	}
	
	public static String addJoins(long format, CharSequence delimiter, CharSequence prefix, CharSequence suffix, Object... args) {
		StringJoiner j = new StringJoiner(delimiter);
		for(int i = 0; i < args.length; i++) {
			if(args[i] == null) {
				j.add("null");
				continue;
			}
			StringBuilder str = new StringBuilder(args[i].toString());
			if(format > 9)
				if(args[i] instanceof Number) {
					if(args[i] instanceof Long && (long)args[i] < format) str.insert(0, format((long)args[i], format));
					else if(args[i] instanceof Integer && (int)args[i] < format) str.insert(0, format((int)args[i], format));
					else if(args[i] instanceof Short && (short)args[i] < format) str.insert(0, format((short)args[i],format));
					else if(args[i] instanceof Byte && (byte)args[i] < format) str.insert(0, format((byte)args[i], format));
					else if(args[i] instanceof BigInteger && less((BigInteger) args[i], BigInteger.valueOf(format))) str.insert(0, format((BigInteger)args[i], format));
				}
			j.add(str);
		}
		return j.toString();
	}
	
	private static StringBuilder format(BigInteger num, long f) {
		StringBuilder s = new StringBuilder();
		if(less(num, BigInteger.TEN)) s.append("0");
		for(int i = 0; i < Long.toString(f).length()-2; i++) {
			s.append(0);
		}
		return s;
	}
	
	private static StringBuilder format(long num, long f) {
		StringBuilder s = new StringBuilder();
		if(num < 10) s.append("0");
		for(int i = 0; i < Long.toString(f).length()-2; i++) {
			s.append(0);
		}
		return s;
	}
	
	/**
	 * @return true if '{@code less}' < '{@code to}'
	 * @author small_jiu
	 */
	public static boolean less(BigInteger less, BigInteger to) {
		if(less == null && to == null) return true;
		if(less == null || to == null) return false;
		return to.compareTo(less) == -1;
	}
	
	/**
	 * @return true if '{@code less}' <= '{@code equ}'
	 * @author small_jiu
	 */
	public static boolean lessOrEqual(BigInteger less, BigInteger equ) {
		if(less == null && equ == null) return true;
		if(less == null || equ == null) return false;
		return equ.compareTo(less) <= 0;
	}
	
	/**
	 * @return true if '{@code greater}' > '{@code to}'
	 * @author small_jiu
	 */
	public static boolean greater(BigInteger greater, BigInteger to) {
		if(greater == null && to == null) return true;
		if(greater == null || to == null) return false;
		return greater.compareTo(to) == 1;
	}
	
	/**
	 * @return true if '{@code greater}' >= '{@code equ}'
	 * @author small_jiu
	 */
	public static boolean greaterOrEqual(BigInteger greater, BigInteger equ) {
		if(greater == null && equ == null) return true;
		if(greater == null || equ == null) return false;
		return equ.compareTo(greater) >= 0;
	}
}
