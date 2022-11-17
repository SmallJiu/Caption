package cat.jiu.dialog.element;

import java.util.List;

import com.google.common.collect.Lists;

import cat.jiu.caption.DialogAPI;
import cat.jiu.caption.element.CaptionText;
import cat.jiu.core.util.mc.SimpleNBTTagList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class DialogButton implements IDialogOption {
	public DialogButton(NBTTagCompound nbt) {this.readFromNBT(nbt);}
	
	protected List<CaptionText> tooltips;
	protected CaptionText text;
	public DialogButton(CaptionText text) {
		this.text = text;
	}
	public DialogButton(CaptionText text, List<CaptionText> tooltips) {
		this.text = text;
		this.tooltips = tooltips;
	}
	
	@Override
	public CaptionText getOptionString() {
		return this.text;
	}
	
	public List<CaptionText> getTooltips() {
		return tooltips;
	}
	public List<String> getTooltipsString() {
		if(!this.hasTooltips()) return null;
		List<String> tips = Lists.newArrayList();
		for(int i = 0; i < this.tooltips.size(); i++) {
			tips.add(this.tooltips.get(i).format());
		}
		return tips;
	}
	public void setTooltips(List<CaptionText> tooltips) {
		this.tooltips = tooltips;
	}
	public void addTooltips(CaptionText tooltip) {
		if(this.tooltips==null) this.tooltips = Lists.newArrayList();
		this.tooltips.add(tooltip);
	}
	public boolean hasTooltips() {return this.tooltips!=null && !this.tooltips.isEmpty();}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.text = new CaptionText("");
		this.text.readFromNBT(nbt.getCompoundTag("text"));
		
		if(nbt.hasKey("tooltips")) {
			NBTTagList tooltips = nbt.getTagList("tooltips", 10);
			this.tooltips = Lists.newArrayList();
			for(int i = 0; i < tooltips.tagCount(); i++) {
				this.tooltips.add(CaptionText.get(tooltips.getCompoundTagAt(i)));
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("text", this.text.writeToNBT());
		if(this.tooltips!=null && !this.tooltips.isEmpty()) {
			SimpleNBTTagList tooltips = new SimpleNBTTagList();
			for(int i = 0; i < this.tooltips.size(); i++) {
				tooltips.append(this.tooltips.get(i).writeToNBT());
			}
			nbt.setTag("tooltips", tooltips);
			nbt.setInteger("type", DialogAPI.getOptionType(this.getClass()));
		}
		return nbt;
	}
}
