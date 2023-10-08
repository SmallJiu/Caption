package cat.jiu.caption;

import cat.jiu.caption.element.Caption;
import cat.jiu.caption.element.style.UpToDownStyle;
import cat.jiu.caption.iface.ICaption;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.util.CaptionImp.MsgCaption;
import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.IImage;
import cat.jiu.core.api.element.ISound;
import cat.jiu.core.api.element.IText;
import cat.jiu.core.util.element.Text;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

/**
 * @author small_jiu
 */
@ZenRegister
@ZenClass("caption.API")
public final class CaptionAPI {
	/**
	 * @see #add(EntityPlayer, CaptionType, IText, IText, DisplayStyle, ITimer, DisplaySideType, ITimer, boolean, IImage, ISound)
	 */
	@ZenMethod
	public static void add(EntityPlayer player, ICaption e) {
		if(e.getTalkTime().isDone()) return;
//		CaptionImp.addCaption(player, e);
		if(player instanceof EntityPlayerMP) {
			ModMain.getNetworkHandler().sendMessageToPlayer(new MsgCaption(e), (EntityPlayerMP) player);
		}else {
			ModMain.getNetworkHandler().sendMessageToServer(new MsgCaption(e));
		}
	}
	/**
	 * @param player the player
	 * @param type the show pos. if is {@link CaptionType.Secondary}, the textFields if is empty, it will no show on window, but will play sound, and {@code displaySide} will always is DOWN
	 * @param displayName speaker textFields, can be translate text
	 * @param nameArg if textFields is translate text, this is the args
	 * @param displayText speaker speak text, can be translate text
	 * @param textArg if text is translate text, this is the args
	 * @param displayTime the caption time of display
	 * @param displaySide the caption display side
	 * @param displayDelay the caption display delayTick
	 * @param needBg set to true if you need black background
	 * @param displayImgs the speaker image, can be null, the size must be 100x60
	 * @param displayImgDelayTicks the image delayTick, like mcmeta {@code frametime}
	 * @param sound the speak sound, can be null
	 * @see cat.jiu.caption.Caption
	 */
	@ZenMethod
	public static void add(EntityPlayer player, CaptionType type, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, DisplaySideType displaySide, ITimer displayDelay, boolean needBg, @Nullable IImage image, @Nullable ISound sound) {
		add(player, new Caption(type, displayName, displayText, style, displayTime, displaySide, displayDelay, needBg, image, sound));
	}

	@ZenMethod
	public static void addSecondary(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable ISound sound) {
		add(player, CaptionType.Secondary, displayName, displayText, style, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, sound);
	}
	@ZenMethod
	public static void addSecondaryOnlySound(EntityPlayer player, ISound sound, ITimer playTime, ITimer playDelay) {
		add(player, CaptionType.Secondary, Text.empty, Text.empty, UpToDownStyle.instance, playTime, DisplaySideType.DOWN, playDelay, false, null, sound);
	}
	@ZenMethod
	public static void addSecondaryNoSound(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg) {
		add(player, CaptionType.Secondary, displayName, displayText, style, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, null);
	}

	@ZenMethod
	public static void addDown(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable ISound sound) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, sound);
	}
	@ZenMethod
	public static void addDownNoSound(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, null);
	}

	@ZenMethod
	public static void addLeft(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable IImage image, @Nullable ISound sound) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.LEFT, displayDelay, needBg, image, sound);
	}
	@ZenMethod
	public static void addLeftNoImage(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable ISound sound) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, sound);
	}
	@ZenMethod
	public static void addLeftNoSound(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, null);
	}
	@ZenMethod
	public static void addLeftNoSoundButHasImage(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable IImage image) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.LEFT, displayDelay, needBg, image, null);
	}

	@ZenMethod
	public static void addRight(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable IImage image, @Nullable ISound sound) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, image, sound);
	}
	@ZenMethod
	public static void addRightNoImage(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable ISound sound) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, sound);
	}
	@ZenMethod
	public static void addRightNoSound(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, null);
	}
	@ZenMethod
	public static void addRightNoSoundButHasImage(EntityPlayer player, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, ITimer displayDelay, boolean needBg, @Nullable IImage image) {
		add(player, CaptionType.Main,displayName, displayText, style, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, image, null);
	}
}
