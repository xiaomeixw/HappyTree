package sabria.happytree.library.core;

/**
 * 继承这个,就代表此时你返回的是服务端的ErrorMessage
 * 
 * @author xiongwei
 * 
 */
public class ResponerErrorBean {

	private String message;

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
