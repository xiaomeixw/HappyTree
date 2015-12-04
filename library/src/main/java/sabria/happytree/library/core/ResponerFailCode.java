package sabria.happytree.library.core;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-02  09:58
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class ResponerFailCode {

    public static int ErrorString = 400;

    public static int ErrorNull = 410;

    private int code;
    private String message;

    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public ResponerFailCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
