package cat.jiu.caption.type;

import java.util.Random;

public enum DisplaySideType {
	DOWN(0), LEFT(1), RIGHT(2), RAND_SIDE(3), RAND(4);
	
	static final DisplaySideType[] SIDE = {LEFT, RIGHT};
	
	private final int id;
	private DisplaySideType(int id) {
		this.id = id;
	}
	public int getID() {return id;}
	
	static Random rand = new Random();
	
	public static DisplaySideType rand(DisplaySideType type) {
		if(type == DisplaySideType.RAND_SIDE) {
			return rand_side();
		}else if(type == DisplaySideType.RAND) {
			return rand();
		}else {
			return type;
		}
	}
	
	public static DisplaySideType rand() {
		DisplaySideType t = DisplaySideType.values()[rand.nextInt(DisplaySideType.values().length-1)];
		if(t == RAND_SIDE) {
			return rand_side();
		}else if(t == RAND) {
			return rand();
		}
		return t;
	}
	public static DisplaySideType rand_side() {
		DisplaySideType t = DisplaySideType.values()[rand.nextInt(SIDE.length-1)];
		return t == DisplaySideType.RAND_SIDE ? rand_side() : t;
	}
	public static DisplaySideType getType(int id) {
		for(DisplaySideType type : DisplaySideType.values()) {
			if(type.getID()==id) {
				return type;
			}
		}
		return DOWN;
	}
}
