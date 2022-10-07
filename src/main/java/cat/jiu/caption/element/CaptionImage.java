package cat.jiu.caption.element;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.jiucore.time.ICaptionTime;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import java.util.Arrays;

public class CaptionImage {
	public static final CaptionImage DEFAULT_IMAGE = new CaptionImage(new ResourceLocation("caption:textures/gui/default_img.png"));
	private static final Map<Integer, int[]> orderCache = Maps.newHashMap();
	
	protected final List<ResourceLocation> imgs;
	/** image display order, like mcmeta {@code frames} */
	protected final int[] order;
	/** image delayTick, like mcmeta {@code frametime} */
	protected ICaptionTime delayTick;
	protected long delayMillis;
	protected final boolean useMillisToTiming;
	protected boolean isDonePlaying = false;
	protected long currentDelayMillis = 0;
	
	protected final ResourceLocation img;
	
	public CaptionImage(ResourceLocation img) {
		this(null, null, false, 0, img);
	}
	protected static int[] genDefaultOrder(int size) {
		if(!orderCache.containsKey(size) || orderCache.get(size) == null || orderCache.get(size).length != size) {
			int[] order = new int[size];
			for(int i = 0; i < order.length; i++) {
				order[i] = i;
			}
			orderCache.put(size, order);
			return order;
		}
		return orderCache.get(size);
	}
	
	public CaptionImage(long delayTick, ResourceLocation... imgs) {
		this(Lists.newArrayList(imgs), genDefaultOrder(imgs.length), false, delayTick, null);
	}
	
	public CaptionImage(int[] order, long delayTick, ResourceLocation... imgs) {
		this(Lists.newArrayList(imgs), order, false, delayTick, null);
	}
	
	public CaptionImage(List<ResourceLocation> imgs, long delayTick) {
		this(imgs, genDefaultOrder(imgs.size()), false, delayTick, null);
	}
	
	public CaptionImage(List<ResourceLocation> imgs, int[] order, long delayTick) {
		this(imgs, order, false, delayTick, null);
	}
	
	// useMsToTiming
	public CaptionImage(boolean useMsToTiming, long delay, ResourceLocation... imgs) {
		this(Lists.newArrayList(imgs), genDefaultOrder(imgs.length), useMsToTiming, delay, null);
	}
	
	public CaptionImage(int[] order, boolean useMsToTiming, long delay, ResourceLocation... imgs) {
		this(Lists.newArrayList(imgs), order, useMsToTiming, delay, null);
	}
	
	public CaptionImage(List<ResourceLocation> imgs, boolean useMsToTiming, long delay) {
		this(imgs, genDefaultOrder(imgs.size()), useMsToTiming, delay, null);
	}
	
	public CaptionImage(List<ResourceLocation> imgs, int[] order, boolean useMsToTiming, long delay) {
		this(imgs, order, useMsToTiming, delay, null);
	}

	protected CaptionImage(List<ResourceLocation> imgs, int[] order, boolean useMsToTiming, long delay, ResourceLocation img) {
		this.imgs = imgs;
		this.order = order;
		this.img = img;
		this.useMillisToTiming = useMsToTiming;
		if(this.useMillisToTiming) {
			this.delayMillis = delay;
		}else {
			this.delayTick = new CaptionTime(delay);
		}
		if(order!=null) {
			for(int i = 0; i < this.order.length; i++) {
				if(this.order[i] >= imgs.size()) {
					this.order[i] = imgs.size()-1;
				}
			}
		}
	}
	
	protected boolean isStartTiming = false;
	public void startTiming() {
		if(!isStartTiming) {
			new Thread(()->{
				while(!this.isDonePlaying) {
					try {
						Thread.sleep(this.delayMillis);
						if(!this.isDoneDelay()) {
							this.update();
						}
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			this.isStartTiming=true;
		}
	}
	
	protected int displayIndex = 0;
	public int[] getOrder() {return Arrays.copyOf(order, order.length);}
	public ICaptionTime getDelay() {return delayTick;}
	public boolean isUseMillisToTiming() {return this.useMillisToTiming;} 
	public ResourceLocation getImage() {
		if(this.imgs != null && !this.imgs.isEmpty()) {
			return this.imgs.get(this.order[this.displayIndex]);
		}
		return this.img;
	}
	
	public void setDonePlaying(boolean isDonePlaying) {this.isDonePlaying = isDonePlaying;}
	public boolean hasMoreFrame() {return this.imgs != null && this.imgs.size() > 1;}
	public void update() {
		if(this.isUseMillisToTiming()) {
			this.currentDelayMillis++;
		}else {
			this.delayTick.update();
		}
	}
	public boolean isDoneDelay() {
		if(this.isUseMillisToTiming()) {
			return this.currentDelayMillis >= this.delayMillis;
		}
		return this.delayTick.isDone();
	}
	public void resetDelay() {
		this.displayIndex++;
		if(this.isUseMillisToTiming()) {
			this.currentDelayMillis = 0;
		}else {
			this.delayTick.reset();
		}
		if(this.displayIndex >= this.order.length) {
			this.displayIndex = 0;
		}
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		if(this.hasMoreFrame()) {
			NBTTagList imgsTag = new NBTTagList();
			for(int i = 0; i < this.imgs.size(); i++) {
				imgsTag.appendTag(new NBTTagString(this.imgs.get(i).toString()));
			}
			nbt.setTag("imgs", imgsTag);
			nbt.setTag("order", new NBTTagIntArray(this.order));
			nbt.setBoolean("useMillis", this.useMillisToTiming);
			nbt.setLong("delay", this.isUseMillisToTiming() ? this.delayMillis : this.delayTick.getAllTicks());
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
			
			return new CaptionImage(imgs, order, nbt.getBoolean("useMillis"), delay);
		}else {
			if(!nbt.hasKey("img") || nbt.getString("img").equalsIgnoreCase(DEFAULT_IMAGE.img.toString())) {
				return DEFAULT_IMAGE;
			}
			return new CaptionImage(new ResourceLocation(nbt.getString("img")));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		CaptionImage other = (CaptionImage) obj;
		if(img == null) {
			if(other.img != null)
				return false;
		}else if(!img.equals(other.img))
			return false;
		if(imgs == null) {
			if(other.imgs != null)
				return false;
		}else if(!imgs.equals(other.imgs))
			return false;
		if(!Arrays.equals(order, other.order))
			return false;
		return true;
	}
}
