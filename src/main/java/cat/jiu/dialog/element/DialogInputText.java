package cat.jiu.dialog.element;

import cat.jiu.caption.DialogAPI;
import cat.jiu.caption.element.CaptionText;

import net.minecraft.nbt.NBTTagCompound;

public abstract class DialogInputText implements IDialogOption {
	public DialogInputText(NBTTagCompound nbt) {this.readFromNBT(nbt);}
	
	protected CaptionText info;
	public DialogInputText(CaptionText info) {
		this.info = info;
	}
	
	@Override
	public CaptionText getOptionString() {
		return this.info;
	}

	@Override
	public NBTTagCompound writeToNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("info", this.info.writeToNBT());
		nbt.setInteger("type", DialogAPI.getOptionType(this.getClass()));
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.info = new CaptionText("");
		this.info.readFromNBT(nbt.getCompoundTag("info"));
	}
}
