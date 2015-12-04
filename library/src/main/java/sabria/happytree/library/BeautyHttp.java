package sabria.happytree.library;

import android.app.Application;
import android.os.AsyncTask;

import java.util.HashMap;

import sabria.happytree.library.core.Request;
import sabria.happytree.library.core.async.BeautyAsyncTask;
import sabria.happytree.library.inter.RequestCallBack;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-02  09:55
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class BeautyHttp {

    private static Application application;

    public static Application getApplication() {
        return application;
    }

    /**
     * should use in Application
     * @param application
     */
    public  void init(Application application){
        this.application = application;
    }


    public BeautyHttp() {

    }


    public void get(String requestUrl,RequestCallBack callback) {
        if(requestUrl==null){
            return;
        }
        Request vo = new Request.Builder().url(requestUrl).get(null).build();
        send(vo, callback);
    }

    public void get(String requestUrl,HashMap<String, String> body,RequestCallBack callback) {
        if(requestUrl==null){
            return;
        }
        Request vo = new Request.Builder().url(requestUrl).get(body).build();
        send(vo, callback);
    }

    public void post(String requestUrl,RequestCallBack callback) {
        if(requestUrl==null){
            return;
        }
        Request vo = new Request.Builder().url(requestUrl).post(null).build();
        send(vo, callback);
    }

    public void post(String requestUrl,HashMap<String, String> body,RequestCallBack callback) {
        if(requestUrl==null){
            return;
        }
        Request vo = new Request.Builder().url(requestUrl).post(body).build();
        send(vo, callback);
    }

    public void send(Request requestVo,RequestCallBack callback) {
        if(requestVo==null){
            return;
        }
        sendRequest(requestVo, callback);
    }


    //**************************整个流程最终会调用的方法************************************
    private void sendRequest(Request vo,RequestCallBack callback){
        BeautyAsyncTask task=new BeautyAsyncTask(vo, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
