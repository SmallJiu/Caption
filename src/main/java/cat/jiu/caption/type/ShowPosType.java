package cat.jiu.caption.type;

public enum ShowPosType {
	UP, DOWN, LEFT, RIGHT, RAND_SIDE;
	
	public int getID() {return this.ordinal();}
	
	public static ShowPosType getType(int id) {
		for(ShowPosType type : ShowPosType.values()) {
			if(type.getID()==id) {
				return type;
			}
		}
		return DOWN;
	}
}
