package cat.jiu.dialog.element;

import java.lang.reflect.InvocationTargetException;

import cat.jiu.caption.DialogAPI;
import cat.jiu.caption.element.CaptionText;

import net.minecraft.nbt.NBTTagCompound;

public interface IDialogOption {
	void onOptionSelected(Object... args);
	CaptionText getOptionString();
	NBTTagCompound writeToNBT();
	void readFromNBT(NBTTagCompound nbt);
	
	static IDialogOption get(NBTTagCompound nbt) {
		IDialogOption option = null;
		if(DialogAPI.hasOption(nbt.getInteger("type"))) {
			try {
				option = DialogAPI.getOptionType(nbt.getInteger("type")).getConstructor(NBTTagCompound.class).newInstance(nbt);
			}catch(InstantiationException e) {
				e.printStackTrace();
			}catch(IllegalAccessException e) {
				e.printStackTrace();
			}catch(IllegalArgumentException e) {
				e.printStackTrace();
			}catch(InvocationTargetException e) {
				e.printStackTrace();
			}catch(NoSuchMethodException e) {
				e.printStackTrace();
			}catch(SecurityException e) {
				e.printStackTrace();
			}
		}
		return option;
	}
}
