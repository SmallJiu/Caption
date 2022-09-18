package cat.jiu.caption;

import java.util.List;

import javax.annotation.Nullable;

import cat.jiu.caption.Caption.Element;
import cat.jiu.caption.element.CaptionImage;
import cat.jiu.caption.element.CaptionSound;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.Optional;

/**
 * @author small_jiu
 */
public final class Utils {
	public final CoreTimeUtils core_time = new CoreTimeUtils();
	/**
	 * @see #add(EntityPlayer, String, Object[], String, Object[], ICaptionTime, DisplaySideType, ICaptionTime, boolean, List, long, CaptionSound)
	 */
	public void add(EntityPlayer player, Caption.Element element) {
		Caption.add(player, element);
	}
	
	/**
	 * @param player the player
	 * @param type the show pos. if is {@link CaptionType.Secondary}, the name if is empty, it will no show on window, but will play sound, and {@code displaySide} will always is DOWN
	 * @param displayName speaker name, can be translate key
	 * @param nameArg if name is translate key, this is the args
	 * @param displayText speaker speak text, can be translate key
	 * @param textArg if text is translate key, this is the args
	 * @param displayTime the caption time of display
	 * @param displaySide the caption display side
	 * @param displayDelay the caption display delay
	 * @param needBg set to true if you need black background
	 * @param displayImgs the speaker image, can be null, the size must be 100x60
	 * @param displayImgDelayTicks the image delay, like mcmeta {@code frametime}
	 * @param sound the speak sound, can be null
	 * @see cat.jiu.caption.Caption.Element
	 */
	public void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
		Caption.add(player, type, displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, needBg, image, sound);
	}
	
	public void addSecondary(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
		Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, sound);
	}
	public void addSecondaryOnlySound(EntityPlayer player, CaptionSound sound, ICaptionTime playTime, ICaptionTime playDelay) {
		Caption.add(player, CaptionType.Secondary, "", null, "", null, playTime, DisplaySideType.DOWN, playDelay, false, null, sound);
	}
	public void addSecondaryNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, null);
	}
	
	public void addDown(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, sound);
	}
	public void addDownNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, null);
	}
	
	public void addLeft(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, image, sound);
	}
	public void addLeftNoImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, sound);
	}
	public void addLeftNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, null);
	}
	public void addLeftNoSoundButHasImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, image, null);
	}
	
	public void addRight(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, image, sound);
	}
	public void addRightNoImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, sound);
	}
	public void addRightNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, null);
	}
	public void addRightNoSoundButHasImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, image, null);
	}
	
//  for JiuCore times
	public static final class CoreTimeUtils {

		/**
		 * this is time of JiuCore method
		 * @see #add(EntityPlayer, String, Object[], String, Object[], ICaptionTime, DisplaySideType, ICaptionTime, boolean, List, long, CaptionSound)
		 */
		@Optional.Method(modid = "jiucore")
		public static void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
			Caption.add(player, new Element(type, displayName, nameArg, displayText, textArg, ICaptionTime.from(displayTime), displaySide, displayDelay, needBg, image, sound));
		}
		
		@Optional.Method(modid = "jiucore")
		public void addDown(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addDownNoSound(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, null);
		}
		
		public void addSecondary(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
			Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, sound);
		}
		public void addSecondaryOnlySound(EntityPlayer player, CaptionSound sound, ICaptionTime playTime, ICaptionTime playDelay) {
			Caption.add(player, CaptionType.Secondary, "", null, "", null, playTime, DisplaySideType.DOWN, playDelay, false, null, sound);
		}
		public void addSecondaryNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, null);
		}

		@Optional.Method(modid = "jiucore")
		public void addLeft(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, image, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addLeftNoImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addLeftNoSound(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, null);
		}
		@Optional.Method(modid = "jiucore")
		public void addLeftNoSoundButHasImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionImage image) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, image, null);
		}

		@Optional.Method(modid = "jiucore")
		public void addRight(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, image, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addRightNoImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionSound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addRightNoSound(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, null);
		}
		@Optional.Method(modid = "jiucore")
		public void addRightNoSoundButHasImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionImage image) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, image, null);
		}
	}
}
