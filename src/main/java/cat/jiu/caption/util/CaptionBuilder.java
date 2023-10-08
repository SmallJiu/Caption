package cat.jiu.caption.util;

import cat.jiu.caption.element.Caption;
import cat.jiu.caption.element.style.UpToDownStyle;
import cat.jiu.caption.iface.ICaption;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.IImage;
import cat.jiu.core.api.element.ISound;
import cat.jiu.core.api.element.IText;
import cat.jiu.core.util.element.Text;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("caption.Builder")
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
	
	@ZenMethod("type")
	public CaptionBuilder setType(CaptionType type) {
		this.type = type;
		return this;
	}
	@ZenMethod("type")
	@ZenDoc("Caption type."
			+ "0: Main"
			+ "1: Secondary")
	public CaptionBuilder setType(int type) {
		this.type = CaptionType.VALUES[type];
		return this;
	}

	@ZenMethod("name")
	@ZenDoc("Caption display name.")
	public CaptionBuilder setDisplayName(IText displayName) {
		this.displayName = displayName;
		return this;
	}

	@ZenMethod("text")
	@ZenDoc("Caption display text.")
	public CaptionBuilder setDisplayText(IText displayText) {
		this.displayText = displayText;
		return this;
	}

	@ZenMethod("time")
	@ZenDoc("Caption display time.")
	public CaptionBuilder setDisplayTime(ITimer displayTime) {
		this.displayTime = displayTime;
		return this;
	}

	@ZenMethod("side")
	public CaptionBuilder setDisplaySide(DisplaySideType displaySide) {
		this.displaySide = displaySide;
		return this;
	}
	@ZenMethod("side")
	@ZenDoc("Caption display side."
			+ "0: Down"
			+ "1: Left"
			+ "2: Right"
			+ "3: Rand right or left"
			+ "4: Rand all")
	public CaptionBuilder setDisplaySide(int displaySide) {
		this.displaySide = DisplaySideType.getType(displaySide);
		return this;
	}

	@ZenMethod("delay")
	@ZenDoc("Caption display delay.")
	public CaptionBuilder setDisplayDelay(ITimer displayDelay) {
		this.displayDelay = displayDelay;
		return this;
	}

	@ZenMethod("style")
	public CaptionBuilder setStyle(DisplayStyle style) {
		this.style = style;
		return this;
	}
	
	@ZenMethod("style")
	@ZenDoc("Caption display style."
			+ "0: Center to Side, effect: Down."
			+ "1: Up to Down, effect: All"
			+ "2: Right to Left. effect: Down, Right."
			+ "3: Left to Right, effect: Down, Left."
			+ "4: Left up corner to Right down corner. effect: Down, Left"
			+ "5: Right up corner to Left down corner. effect: Down, Left")
	public CaptionBuilder setStyle(int style) {
		this.style = DisplayStyle.getStyleByID(style);
		return this;
	}

	@ZenMethod("background")
	@ZenDoc("true if need black Background.")
	public CaptionBuilder setNeedBackground(boolean needBG) {
		this.needBG = needBG;
		return this;
	}

	@ZenMethod("image")
	@ZenDoc("Caption display image.")
	public CaptionBuilder setImg(IImage img) {
		this.img = img;
		return this;
	}

	@ZenMethod("sound")
	@ZenDoc("Caption play sound.")
	public CaptionBuilder setSound(ISound sound) {
		this.sound = sound;
		return this;
	}

	@ZenMethod("delay_pre")
	@ZenDoc("Caption display pre delay.")
	public CaptionBuilder setShowPreDelay(ITimer delay) {
		this.show_pre_delay = delay;
		return this;
	}

	@ZenMethod("delay_post")
	@ZenDoc("Caption display post delay.")
	public CaptionBuilder setShowPostDelay(ITimer delay) {
		this.show_post_delay = delay;
		return this;
	}

	@ZenMethod
	@ZenDoc("Build a Caption instance.")
	public ICaption build() {
		Caption caption = new Caption(type, displayName, displayText, style, displayTime, displaySide, displayDelay, needBG, img, sound);
		if(this.show_pre_delay != null) {
			caption.setShowPreDelay(show_pre_delay);
		}
		if(this.show_post_delay != null) {
			caption.setShowPostDelay(show_post_delay);
		}
		return caption;
	}
	
	@ZenMethod
	public static CaptionBuilder from() {
		return new CaptionBuilder();
	}
}
