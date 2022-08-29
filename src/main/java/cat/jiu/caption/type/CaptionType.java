package cat.jiu.caption.type;

public enum CaptionType {
	Main(true), Secondary(false);
	public final boolean isMain;
	private CaptionType(boolean isMain) {
		this.isMain = isMain;
	}
}
