package cat.jiu.caption;

import java.util.List;

import javax.annotation.Nullable;

import cat.jiu.caption.Caption.Element;
import cat.jiu.caption.Caption.Sound;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Optional;

/**
 * @author small_jiu
 */
public final class Utils {
	public final CoreTimeUtils core_time = new CoreTimeUtils();
	/**
	 * @see #add(EntityPlayer, String, Object[], String, Object[], ICaptionTime, DisplaySideType, ICaptionTime, boolean, List, long, Sound)
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
	public void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable Sound sound) {
		Caption.add(player, type, displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound);
	}
	
	public void addSecondary(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable Sound sound) {
		Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, sound);
	}
	public void addSecondaryOnlySound(EntityPlayer player, Sound sound, ICaptionTime playTime, ICaptionTime playDelay) {
		Caption.add(player, CaptionType.Secondary, "", null, "", null, playTime, DisplaySideType.DOWN, playDelay, false, null, 0, sound);
	}
	public void addSecondaryNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, null);
	}
	
	public void addDown(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable Sound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, sound);
	}
	public void addDownNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, null);
	}
	
	public void addLeft(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable Sound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound);
	}
	public void addLeftNoImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable Sound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, 0, sound);
	}
	public void addLeftNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, 0, null);
	}
	public void addLeftNoSoundButHasImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, displayImgs, displayImgDelayTicks, null);
	}
	
	public void addRight(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable Sound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound);
	}
	public void addRightNoImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable Sound sound) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, 0, sound);
	}
	public void addRightNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, 0, null);
	}
	public void addRightNoSoundButHasImage(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks) {
		Caption.add(player, CaptionType.Main,displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, displayImgs, displayImgDelayTicks, null);
	}
	
//  for JiuCore times
	public static final class CoreTimeUtils {

		/**
		 * this is time of JiuCore method
		 * @see #add(EntityPlayer, String, Object[], String, Object[], ICaptionTime, DisplaySideType, ICaptionTime, boolean, List, long, Sound)
		 */
		@Optional.Method(modid = "jiucore")
		public static void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable Sound sound) {
			Caption.add(player, new Element(type, displayName, nameArg, displayText, textArg, ICaptionTime.fromCoreTime(displayTime), displaySide, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound));
		}
		
		@Optional.Method(modid = "jiucore")
		public void addDown(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable Sound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addDownNoSound(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, null);
		}
		
		public void addSecondary(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable Sound sound) {
			Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, sound);
		}
		public void addSecondaryOnlySound(EntityPlayer player, Sound sound, ICaptionTime playTime, ICaptionTime playDelay) {
			Caption.add(player, CaptionType.Secondary, "", null, "", null, playTime, DisplaySideType.DOWN, playDelay, false, null, 0, sound);
		}
		public void addSecondaryNoSound(EntityPlayer player, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Secondary, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.DOWN, displayDelay, needBg, null, 0, null);
		}

		@Optional.Method(modid = "jiucore")
		public void addLeft(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable Sound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addLeftNoImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable Sound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, 0, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addLeftNoSound(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, null, 0, null);
		}
		@Optional.Method(modid = "jiucore")
		public void addLeftNoSoundButHasImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.LEFT, displayDelay, needBg, displayImgs, displayImgDelayTicks, null);
		}

		@Optional.Method(modid = "jiucore")
		public void addRight(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable Sound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addRightNoImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable Sound sound) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, 0, sound);
		}
		@Optional.Method(modid = "jiucore")
		public void addRightNoSound(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, null, 0, null);
		}
		@Optional.Method(modid = "jiucore")
		public void addRightNoSoundButHasImage(EntityPlayer player,String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks) {
			Caption.add(player, CaptionType.Main, displayName, nameArg, displayText, textArg, displayTime, DisplaySideType.RIGHT, displayDelay, needBg, displayImgs, displayImgDelayTicks, null);
		}
	}
}
