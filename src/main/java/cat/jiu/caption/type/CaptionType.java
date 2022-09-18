package cat.jiu.caption.type;

public enum CaptionType {
	Main, Secondary;
	public boolean isMainCaption() {
		return this == Main;
	}
}
