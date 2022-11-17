package cat.jiu.caption.type;

import java.util.Random;

public enum DisplaySideType {
	DOWN(0), LEFT(1), RIGHT(2), RAND_SIDE(3), RAND(4);
	
	public static final DisplaySideType[] TYPES = {LEFT, RIGHT, DOWN};
	public static final DisplaySideType[] SIDE = {LEFT, RIGHT};
	public static final DisplaySideType[] VALUES;
	
	private final int id;
	public int getID() {return id;}
	private DisplaySideType(int id) {
		this.id = id;
	}
	
	static Random rand = new Random();
	
	public static DisplaySideType rand(DisplaySideType type) {
		if(type == DisplaySideType.RAND_SIDE) {
			return SIDE[rand.nextInt(SIDE.length-1)];
		}else if(type == DisplaySideType.RAND) {
			return rand(VALUES[rand.nextInt(VALUES.length-1)]);
		}else {
			return type;
		}
	}
	
	public static DisplaySideType getType(int id) {
		for(DisplaySideType type : VALUES) {
			if(type.getID()==id) {
				return type;
			}
		}
		return DOWN;
	}
	static {
		DisplaySideType[] values = DisplaySideType.values();
		VALUES = new DisplaySideType[values.length];
		for(int i = 0; i < values.length; i++) {
			VALUES[i] = values[i];
		}
	}
}
