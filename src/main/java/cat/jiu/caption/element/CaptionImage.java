package cat.jiu.caption.element;

import java.util.List;

import com.google.common.collect.Lists;

import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class CaptionImage {
	public static final CaptionImage DEFAULT_IMAGE = new CaptionImage(new ResourceLocation("caption:textures/gui/default_img.png"));
	
	protected final List<ResourceLocation> imgs;
	/** image display order, like mcmeta {@code frames} */
	protected final int[] order;
	/** image delay, like mcmeta {@code frametime} */
	protected final ICaptionTime delay;
	protected final ResourceLocation img;
	
	public CaptionImage(ResourceLocation img) {
		this(null, null, 0, img);
	}
	protected static int[] genDefaultOrder(int size) {
		int[] order = new int[size];
		for(int i = 0; i < order.length; i++) {
			order[i] = i;
		}
		return order;
	}
	
	public CaptionImage(long delayTick, ResourceLocation... imgs) {
		this(Lists.newArrayList(imgs), genDefaultOrder(imgs.length), delayTick, null);
	}
	
	public CaptionImage(int[] order, long delayTick, ResourceLocation... imgs) {
		this(Lists.newArrayList(imgs), order, delayTick, null);
	}
	
	public CaptionImage(List<ResourceLocation> imgs, long delayTick) {
		this(imgs, genDefaultOrder(imgs.size()), delayTick, null);
	}
	
	public CaptionImage(List<ResourceLocation> imgs, int[] order, long delayTick) {
		this(imgs, order, delayTick, null);
	}

	protected CaptionImage(List<ResourceLocation> imgs, int[] order, long delayTick, ResourceLocation img) {
		this.imgs = imgs;
		this.order = order;
		this.img = img;
		this.delay = new CaptionTime(delayTick);
		if(order!=null) {
			for(int i = 0; i < this.order.length; i++) {
				if(this.order[i] >= imgs.size()) {
					this.order[i] = imgs.size()-1;
				}
			}
		}
	}
	
	protected int displayIndex = 0;
	public int[] getOrder() {return order;}
	public ICaptionTime getDelay() {return delay;}
	public ResourceLocation getImage() {
		if(this.imgs != null && !this.imgs.isEmpty()) {
			return this.imgs.get(this.order[this.displayIndex]);
		}
		return this.img;
	}
	
	public boolean hasMoreFrame() {return this.imgs != null && this.imgs.size() > 1;}
	public void update() {this.delay.update();}
	public boolean isDone() {return this.delay.isDone();}
	public void resetDelay() {
		this.displayIndex++;
		this.delay.reset();
		if(this.displayIndex >= this.order.length) {
			this.displayIndex = 0;
		}
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		if(this.imgs != null && !this.imgs.isEmpty()) {
			NBTTagList imgsTag = new NBTTagList();
			for(int i = 0; i < this.imgs.size(); i++) {
				imgsTag.appendTag(new NBTTagString(this.imgs.get(i).toString()));
			}
			nbt.setTag("imgs", imgsTag);
			nbt.setTag("order", new NBTTagIntArray(this.order));
			nbt.setLong("delay", this.delay.getAllTicks());
		}else {
			nbt.setString("img", this.img.toString());
		}
		return nbt;
	}
	public static CaptionImage fromNBT(NBTTagCompound nbt) {
		if(nbt==null) return DEFAULT_IMAGE;
		if(nbt.hasKey("imgs")) {
			List<ResourceLocation> imgs = Lists.newArrayList();
			NBTTagList imgsTag = nbt.getTagList("imgs", 8);
			for(int i = 0; i < imgsTag.tagCount(); i++) {
				imgs.add(new ResourceLocation(imgsTag.getStringTagAt(i)));
			}
			int[] order = nbt.getIntArray("order");
			long delay = nbt.getLong("delay");
			
			return new CaptionImage(imgs, order, delay);
		}else {
			if(nbt.getString("img").equalsIgnoreCase(DEFAULT_IMAGE.img.toString())) {
				return DEFAULT_IMAGE;
			}
			return new CaptionImage(new ResourceLocation(nbt.getString("img")));
		}
	}
}
