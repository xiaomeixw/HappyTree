package sabria.happytree.library.core.async;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sabria.happytree.library.core.BaseInfoBean;
import sabria.happytree.library.core.Request;
import sabria.happytree.library.core.ResponerErrorBean;
import sabria.happytree.library.core.ResponerFailCode;
import sabria.happytree.library.http.NetConnect;
import sabria.happytree.library.inter.HTTP_METHOD;
import sabria.happytree.library.inter.RequestCallBack;
import sabria.happytree.library.tools.NetUtil;

/**
 * 执行线程具体工作
 * @author xiongwei
 * TODO 重写AsyncTask来实现自己的特殊需求
 */
public class BeautyAsyncTask extends AsyncTask<Void, Object, Object>{

	protected enum ONLISTENER_TYPE {
		TYPE_ONSUCCESS,
		TYPE_ONNONETWORK,
		TYPE_ONNULLDATA,
		TYPE_ONRESPONSEERROR,
		TYPE_ONSERVICEERROR
	}
	
	public static String MAP_FIRST_KEY = "KEY_ENUM_TYPE";
	public static String MAP_SECOND_KEY = "KEY_OBJECT";
	
	private Enum MAP_ENUM_VALUE;// 根据key得到的VALUE-ENUM
	private Object MAP_OBJECT_VALUE;// 根据key得到的VALUE-OBJ
	
	private Map<String, Object> map = new HashMap<String, Object>();
	private RequestCallBack callback;
	private Request vo;
	
	
	
	public BeautyAsyncTask(Request vo,RequestCallBack callback) {
		this.vo=vo;
		this.callback=callback;
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		callback.onStrtLoading();
	}  
	
	@Override
	protected Object doInBackground(Void... params) {
		if(NetUtil.hasNetwork()){
			//与服务器交互：由于这里本身Net方法中限定为返回JSON了,所以希冀于ResponInfo来处理区分onSucess比较困难
			//这也是即将对整体架构重新设计的重要原因
			String json = getJosn(vo.getMethod(),vo.getUrl(), vo.getBody());
			//解析JSON-->子类必须实现异常捕获
			Object obj = callback.onParserJson(json);
			//然后将object拼装成map集合
			assemblyMap(obj);
		}else{
			//没有网络的情况
			map.put(MAP_FIRST_KEY, ONLISTENER_TYPE.TYPE_ONNONETWORK);
			map.put(MAP_SECOND_KEY, "no-net-work");
		}
		return map;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		callback.onCancel();
	}


	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		callback.onProgress(values);
	}
	
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		callback.onStrtLoading();
		/**
		 * 对返回的集中结果进行逻辑分析
		 * 1.返回 Array 或者 Map 或者 ResponerBean
		 * 2.返回StringError
		 * 3.返回正常String
		 * 4.返回Null
		 * 5.返回no-net-work
		 */
		hanlerResult(result);
	}

	//*********************
	/**
	 * 获取服务端的JSON
	 * @param type
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public String getJosn(HTTP_METHOD type,String url, Map<String, String> paramsMap) {
		NetConnect connect = new NetConnect();
		if (type == HTTP_METHOD.POST) {
			return connect.post(url, paramsMap);
		} else {
			return connect.get(url, paramsMap);
		}
	}
	
	/**
	 * 拼接Map集合
	 * @param obj
	 */
	private void assemblyMap(Object obj) {
		//再次进行一次try-catch
		try {
			//TODO 正常的数据  RequestJavaBean我没使用,因为它和BaseInfoBean是一样的
			if (obj instanceof Map<?, ?>||obj instanceof ArrayList<?> || obj instanceof BaseInfoBean) {
				map.put(MAP_FIRST_KEY, ONLISTENER_TYPE.TYPE_ONSUCCESS);
				map.put(MAP_SECOND_KEY, obj);
			//服务端返回的ErrorString
			} else if(obj instanceof ResponerErrorBean){
				map.put(MAP_FIRST_KEY, ONLISTENER_TYPE.TYPE_ONRESPONSEERROR);
				ResponerErrorBean messagebean = (ResponerErrorBean) obj;
				map.put(MAP_SECOND_KEY, messagebean.getMessage());
			//正常的返回的要显示的String字符串
			} else if (obj instanceof String) {
				map.put(MAP_FIRST_KEY, ONLISTENER_TYPE.TYPE_ONSUCCESS);
				map.put(MAP_SECOND_KEY, obj);
			//exception 或者 数据为 null 或者服务器崩溃导致的返回null
			} else if (obj == null) {
				map.put(MAP_FIRST_KEY, ONLISTENER_TYPE.TYPE_ONNULLDATA);
				map.put(MAP_SECOND_KEY, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(MAP_FIRST_KEY, ONLISTENER_TYPE.TYPE_ONNULLDATA);
			map.put(MAP_SECOND_KEY, obj);
		}
	}
	
	/**
	 * 对结果进行处理,并指定相应的回调函数
	 * @param result
	 */
	private void hanlerResult(Object result) {
		// 对T做逻辑分析
		if (result instanceof Map<?, ?>) {
			ParserMap(result);
			if (MAP_ENUM_VALUE == ONLISTENER_TYPE.TYPE_ONSUCCESS) {
				callback.onSuccess(MAP_OBJECT_VALUE);
			} else if (MAP_ENUM_VALUE == ONLISTENER_TYPE.TYPE_ONNONETWORK) {
				callback.onNetFail();
			} else if (MAP_ENUM_VALUE == ONLISTENER_TYPE.TYPE_ONNULLDATA) {
				callback.onFail(new ResponerFailCode(ResponerFailCode.ErrorNull, ""));
			} else if (MAP_ENUM_VALUE == ONLISTENER_TYPE.TYPE_ONRESPONSEERROR) {
				String message = (String) MAP_OBJECT_VALUE;
				callback.onFail(new ResponerFailCode(ResponerFailCode.ErrorString, message));
			} else if (MAP_ENUM_VALUE == ONLISTENER_TYPE.TYPE_ONSERVICEERROR) {
				//这里的这个是无法会执行到的，因为Exception时我定义为TYPE_ONNULLDATA不是TYPE_ONSERVICEERROR
				callback.onServiceError("服务端错误");// 将服务器Error要尽量少的展示，这种错误，我们一般也用超时处理
			} else {
				// 其他的所有情况统一用超时或者异常来处理
				callback.onFail(new ResponerFailCode(ResponerFailCode.ErrorNull, ""));
			}
		} else {
			// 其他的所有情况统一用超时或者异常来处理
			callback.onFail(new ResponerFailCode(ResponerFailCode.ErrorNull, ""));
		}
	}
	
	/**
	 * 解析map
	 * @param result
	 */
	@SuppressWarnings("rawtypes")
	private void ParserMap(Object result) {
		Map map = (Map) result;
		Set keySet = map.keySet();
		Iterator it = keySet.iterator();
		while (it.hasNext()) {
			String MAP_KEY = (String) it.next();
			if (MAP_FIRST_KEY.equals(MAP_KEY)) {
				MAP_ENUM_VALUE = (Enum) map.get(MAP_KEY);
			} else if (MAP_SECOND_KEY.equals(MAP_KEY)) {
				MAP_OBJECT_VALUE = map.get(MAP_KEY);
			}
		}
	};
	
	/**
	 * 当message = json.getString("notice");
	 *	return HttpAsyncTask.setMessageErrorbean(message);
	 * @param message
	 * @return
	 */
	public static ResponerErrorBean setMessageErrorbean(String message){
		ResponerErrorBean messageErrorBean = new ResponerErrorBean();
		messageErrorBean.setMessage(message);
		return messageErrorBean;
	}
}
