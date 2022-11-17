package cat.jiu.dialog.element;

import java.util.List;

import com.google.common.collect.Lists;

import cat.jiu.caption.DialogAPI;
import cat.jiu.caption.element.CaptionText;
import cat.jiu.core.Logger;
import cat.jiu.core.util.mc.SimpleNBTTagList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class Dialog {
	private static final Logger log = new Logger();
	
	protected final CaptionText title;
	protected final List<? extends IDialogOption> options;
	public Dialog(CaptionText title, List<? extends IDialogOption> options) {
		this.title = title;
		this.options = options;
	}
	public List<? extends IDialogOption> getOptions() {
		return options;
	}
	public CaptionText getTitle() {
		return title;
	}
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if(nbt==null) nbt = new NBTTagCompound();
		
		nbt.setTag("title", this.title.writeToNBT());
		SimpleNBTTagList options = new SimpleNBTTagList();
		for(int i = 0; i < this.options.size(); i++) {
			options.append(this.options.get(i).writeToNBT());
		}
		nbt.setTag("options", options);
		
		return nbt;
	}
	
	public static Dialog get(NBTTagCompound nbt) {
		CaptionText title = CaptionText.get(nbt.getCompoundTag("title"));
		List<IDialogOption> options = Lists.newArrayList();
		NBTTagList optionList = nbt.getTagList("options", SimpleNBTTagList.getType(NBTTagCompound.class));
		for(int i = 0; i < optionList.tagCount(); i++) {
			IDialogOption option = IDialogOption.get(optionList.getCompoundTagAt(i));
			if(option!=null) {
				options.add(option);
			}else {
				log.warning("Option is not registry");
			}
		}
		return new Dialog(title, options);
	}
	
	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent event) {
		boolean s = false;
		if(s) {
			List<IDialogOption> options = Lists.newArrayList();
			options.add(new DialogButton(new CaptionText("666"), Lists.newArrayList(new CaptionText("这是一个对话框"), new CaptionText("这是一个对话框的按钮显示"))) {
				public void onOptionSelected(Object... args) {
					event.getPlayer().sendMessage(new TextComponentString("欢迎使用对话框"));
				}
			});
			DialogAPI.displayDialog(event.getPlayer(), new Dialog(new CaptionText("999"), options));
		}
	}
}
