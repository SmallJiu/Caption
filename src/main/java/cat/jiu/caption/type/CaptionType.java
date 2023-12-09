package cat.jiu.caption.type;

public enum CaptionType {
	Main, Secondary;
	public static final CaptionType[] VALUES = {Main, Secondary};
	public boolean isMainCaption() {
		return this == Main;
	}
}
