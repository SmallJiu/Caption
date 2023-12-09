package cat.jiu.caption.proxy;

import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy {
	public Side getSide() {
		return this.isClient() ? Side.CLIENT : Side.SERVER;
	}

	public boolean isClient() {
		return false;
	}
}
