package cat.jiu.caption.type;

import java.util.Map;

import com.google.common.collect.Maps;

import cat.jiu.core.api.ITimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class DisplayStyle {
	private static final Map<Integer, DisplayStyle> styles = Maps.newHashMap();
	public static DisplayStyle getStyleByID(int id) {return styles.get(id);}
	public static boolean hasStyle(int id) {return styles.containsKey(id);}
	
	public final int id;
	protected final DisplaySideType[] sides;
	protected DisplayStyle(DisplaySideType... sides) {
		this(styles.size(), sides);
	}
	protected DisplayStyle(int id, DisplaySideType... sides) {
		this.id = id;
		this.sides = sides;
		styles.put(id, this);
	}
	
	@SideOnly(Side.CLIENT)
	public abstract void draw(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY);
	
	public boolean isEffectiveSide(DisplaySideType side) {
		for(DisplaySideType type : this.sides) {
			if(side == type) return true;
		}
		return false;
	}
}
