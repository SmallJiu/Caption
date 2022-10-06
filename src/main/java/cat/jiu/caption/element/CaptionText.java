package cat.jiu.caption.element;

import java.util.Arrays;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaptionText {
	public static final Object[] emptyArgs = new Object[0];
	public static final CaptionText empty = new CaptionText("") {
		public void setText(String key) {}
	};
	
	protected String text = "";
	protected Object[] args = emptyArgs;
	protected boolean isCenter = false;
	
	public CaptionText(String key, Object... args) {
		this(key, false, args);
	}
	
	/**
	 * 
	 * @param text the text, can be a translate text
	 * @param isCenter Align center, only on caption side == DOWN
	 * @param parameters translateKey parameters
	 */
	public CaptionText(String key, boolean isCenter, Object... parameters) {
		this.text = key;
		this.args = parameters;
		this.isCenter = isCenter;
	}
	protected CaptionText(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Object[] getParameters() {
		return args;
	}
	public void setParameters(Object... parameters) {
		this.args = parameters;
	}
	
	public boolean isCenter() {return this.isCenter;}
	public void setCenter(boolean isCenter) {this.isCenter = isCenter;}
	
	/**
	 * use i18n to format
	 * @return formatted string
	 */
	@SideOnly(Side.CLIENT)
	public String format() {
		return I18n.format(text, args);
	}
	
	public TextComponentTranslation toTextComponent() {
		return new TextComponentTranslation(text, args);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		CaptionText other = (CaptionText) obj;
		if(!Arrays.equals(args, other.args))
			return false;
		if(text == null) {
			if(other.text != null)
				return false;
		}else if(!text.equals(other.text))
			return false;
		return true;
	}
	
	protected static final NBTTagCompound emptyTag = new NBTTagCompound();
	
	public NBTTagCompound writeToNBT() {
		if(!"".equals(this.text)) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("text", this.text);
			nbt.setBoolean("isCenter", this.isCenter);
			if(this.args!=null&&this.args.length>0) {
				NBTTagList args = new NBTTagList();
				for(int i = 0; i < this.args.length; i++) {
					args.appendTag(new NBTTagString(String.valueOf(this.args[i])));
				}
				nbt.setTag("args", args);
			}
			return nbt;
		}else {
			return emptyTag;
		}
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		this.text = nbt.getString("text");
		this.isCenter = nbt.getBoolean("isCenter");
		if(nbt.hasKey("args")) {
			NBTTagList args = nbt.getTagList("args", 8);
			this.args = new Object[args.tagCount()];
			for(int i = 0; i < this.args.length; i++) {
				this.args[i] = args.getStringTagAt(i);
			}
		}
	}
	
	public static CaptionText get(NBTTagCompound nbt) {
		if(nbt==null||nbt.getSize()<=0) return empty;
		if(!"".equals(nbt.getString("text"))) {
			return new CaptionText(nbt);
		}else {
			return empty;
		}
	}
}
