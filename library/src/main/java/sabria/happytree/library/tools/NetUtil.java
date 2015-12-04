package sabria.happytree.library.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import sabria.happytree.library.BeautyHttp;

public class NetUtil {

	/**
	 * 获得网络连接是否可用
	 * @return
	 */
	public static boolean hasNetwork() {
		ConnectivityManager con = (ConnectivityManager) BeautyHttp.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			return false;
		}
		return true;
	}
	

}
