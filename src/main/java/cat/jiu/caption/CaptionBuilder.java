package cat.jiu.caption;

import javax.annotation.Nullable;

import cat.jiu.caption.element.CaptionImage;
import cat.jiu.caption.element.CaptionSound;
import cat.jiu.caption.element.CaptionText;
import cat.jiu.caption.element.style.StyleUpToDown;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;

public class CaptionBuilder {
	protected static final ICaptionTime empty_time = new CaptionTime(0);
	
	protected CaptionType type = CaptionType.Main;
	protected CaptionText displayName = CaptionText.empty;
	protected CaptionText displayText = CaptionText.empty;
	protected ICaptionTime displayTime = empty_time;
	protected DisplaySideType displaySide = DisplaySideType.DOWN;
	protected ICaptionTime displayDelay = empty_time;
	protected boolean needBG = false;
	@Nullable
	protected CaptionImage img;
	@Nullable
	protected CaptionSound sound;
	protected DisplayStyle style = StyleUpToDown.instance;
	
	public CaptionBuilder setType(CaptionType type) {
		this.type = type;
		return this;
	}
	public CaptionBuilder setDisplayName(CaptionText displayName) {
		this.displayName = displayName;
		return this;
	}
	public CaptionBuilder setDisplayText(CaptionText displayText) {
		this.displayText = displayText;
		return this;
	}
	public CaptionBuilder setDisplayTime(ICaptionTime displayTime) {
		this.displayTime = displayTime;
		return this;
	}
	public CaptionBuilder setDisplaySide(DisplaySideType displaySide) {
		this.displaySide = displaySide;
		return this;
	}
	public CaptionBuilder setDisplayDelay(ICaptionTime displayDelay) {
		this.displayDelay = displayDelay;
		return this;
	}
	public CaptionBuilder setStyle(DisplayStyle style) {
		this.style = style;
		return this;
	}
	public CaptionBuilder setNeedBG(boolean needBG) {
		this.needBG = needBG;
		return this;
	}
	public CaptionBuilder setImg(CaptionImage img) {
		this.img = img;
		return this;
	}
	public CaptionBuilder setSound(CaptionSound sound) {
		this.sound = sound;
		return this;
	}
	
	public Caption.Element build(){
		return new Caption.Element(type, displayName, displayText, style, displayTime, displaySide, displayDelay, needBG, img, sound);
	}
}
