package cat.jiu.caption.type;

import java.util.Random;

public enum DisplaySideType {
	DOWN(0), LEFT(1), RIGHT(2), RAND_SIDE(3), RAND(4);
	
	static final DisplaySideType[] SIDE = {LEFT, RIGHT};
	
	private final int id;
	public int getID() {return id;}
	private DisplaySideType(int id) {
		this.id = id;
	}
	
	static Random rand = new Random();
	
	public static DisplaySideType rand() {
		return rand(DisplaySideType.values()[rand.nextInt(DisplaySideType.values().length-1)]);
	}
	public static DisplaySideType rand_side() {
		return SIDE[rand.nextInt(SIDE.length-1)];
	}
	public static DisplaySideType rand(DisplaySideType type) {
		if(type == DisplaySideType.RAND_SIDE) {
			return rand_side();
		}else if(type == DisplaySideType.RAND) {
			return rand();
		}else {
			return type;
		}
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
