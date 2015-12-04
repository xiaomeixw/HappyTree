package sabria.happytree.library.inter;

import sabria.happytree.library.core.ResponerFailCode;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-02  09:56
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public abstract class RequestCallBack {


    public abstract Object onParserJson(String json);

    public abstract void onSuccess(Object data);

    public abstract void onFail(ResponerFailCode code);

    public void onStrtLoading() {
    }

    public void onStopLoading() {
    }

    public void onNetFail() {
    }

    public void onServiceError(String exception) {

    }

    public void onProgress(Object... values) {
    }

    public void onCancel() {
    }

}
