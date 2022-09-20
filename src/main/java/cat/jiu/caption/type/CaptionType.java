package cat.jiu.caption.type;

public enum CaptionType {
	Main, Secondary;
	public static final CaptionType[] VALUES = {CaptionType.Main, CaptionType.Secondary};
	public boolean isMainCaption() {
		return this == Main;
	}
}
