package cat.jiu.caption.type;

public enum DisplaySideType {
	UP, DOWN, LEFT, RIGHT, RAND_SIDE;
	
	public int getID() {return this.ordinal();}
	
	public static DisplaySideType getType(int id) {
		for(DisplaySideType type : DisplaySideType.values()) {
			if(type.getID()==id) {
				return type;
			}
		}
		return DOWN;
	}
}
