package cat.jiu.caption.util;

import cat.jiu.caption.element.Caption;
import cat.jiu.caption.element.style.UpToDownStyle;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.IImage;
import cat.jiu.core.api.element.ISound;
import cat.jiu.core.api.element.IText;
import cat.jiu.core.util.element.Text;

public class CaptionBuilder {
	protected CaptionType type = CaptionType.Main;
	protected IText displayName = Text.empty;
	protected IText displayText = Text.empty;
	protected ITimer displayTime = CaptionImp.noDelay();
	protected DisplaySideType displaySide = DisplaySideType.DOWN;
	protected ITimer displayDelay = CaptionImp.noDelay();
	protected boolean needBG = true;
	protected DisplayStyle style = UpToDownStyle.instance;

	protected IImage img;
	protected ISound sound;

	protected ITimer show_pre_delay;
	protected ITimer show_post_delay;

	public CaptionBuilder setType(CaptionType type) {
		this.type = type;
		return this;
	}

	public CaptionBuilder setDisplayName(IText displayName) {
		this.displayName = displayName;
		return this;
	}

	public CaptionBuilder setDisplayText(IText displayText) {
		this.displayText = displayText;
		return this;
	}

	public CaptionBuilder setDisplayTime(ITimer displayTime) {
		this.displayTime = displayTime;
		return this;
	}

	public CaptionBuilder setDisplaySide(DisplaySideType displaySide) {
		this.displaySide = displaySide;
		return this;
	}

	public CaptionBuilder setDisplayDelay(ITimer displayDelay) {
		this.displayDelay = displayDelay;
		return this;
	}

	public CaptionBuilder setStyle(DisplayStyle style) {
		this.style = style;
		return this;
	}

	public CaptionBuilder setNeedBackground(boolean needBG) {
		this.needBG = needBG;
		return this;
	}

	public CaptionBuilder setImg(IImage img) {
		this.img = img;
		return this;
	}

	public CaptionBuilder setSound(ISound sound) {
		this.sound = sound;
		return this;
	}

	public CaptionBuilder setShowPreDelay(ITimer delay) {
		this.show_pre_delay = delay;
		return this;
	}

	public CaptionBuilder setShowPostDelay(ITimer delay) {
		this.show_post_delay = delay;
		return this;
	}

	public Caption build() {
		Caption c = new Caption(type, displayName, displayText, style, displayTime, displaySide, displayDelay, needBG, img, sound);
		if(this.show_pre_delay != null) {
			c.setShowPreDelay(show_pre_delay);
		}
		if(this.show_post_delay != null) {
			c.setShowPostDelay(show_post_delay);
		}
		return c;
	}
}
