package sabria.happytree.library.core;

import java.util.HashMap;

import sabria.happytree.library.inter.HTTP_METHOD;


/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-02  09:59
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class Request {

    // URL
    private final String url;
    // GET OR POST
    private final HTTP_METHOD method;
    // PARAMS
    private final HashMap<String, String> body;

    public String getUrl() {
        return url;
    }
    public HTTP_METHOD getMethod() {
        return method;
    }
    public HashMap<String, String> getBody() {
        return body;
    }


    /**
     * 私有,通过builder对外暴露
     *
     * @param builder
     */
    private Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.body = builder.body;
    }

    /**
     * Builder实体
     */
    public static class Builder {
        private String url;
        private HTTP_METHOD method;
        private HashMap<String, String> body;

        /**
         * 默认调用GET方案
         */
        public Builder() {
            this.method = HTTP_METHOD.GET;
        }

        private Builder(Request request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
        }

        public Builder url(String url) {
            if (url == null)
                throw new IllegalArgumentException("url == null");
            this.url = url;
            return this;
        }

        /**
         * 这里使用方法名来控制HTTP_METHOD
         * @param
         * @return
         */
        public Builder get(HashMap<String, String> body) {
            return method(HTTP_METHOD.GET, body);
        }

        public Builder post(HashMap<String, String> body) {
            return method(HTTP_METHOD.POST, body);
        }

        private Builder method(HTTP_METHOD method, HashMap<String, String> body) {
            this.method = method;
            this.body = body;
            return this;
        }


        public Request build() {
            if (url == null)
                throw new IllegalStateException("url == null");
            return new Request(this);
        }
    }

    public Builder newBuilder() {
        return new Builder(this);
    }
}
