package pers.liuhaoan.jvtcGroupOfRobot.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class HttpUtil {
    private String CHARSET = "utf-8";
    private String method;
    private String url;
    private String data;
    private String cookies;
    private Map<String, String> headerProperty;
    private Boolean isReturn;

    private String htmlText;

    public static class Builder {
        private String method;
        private String url;
        private String data;
        private String cookies;
        private Map<String, String> headerProperty;
        private Boolean isReturn;

        public Builder(String url) {
            this.url = url;
        }

        public Builder setIsReturn(Boolean isReturn) {
            this.isReturn = isReturn;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setCookies(String cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder setHeaderProperty(Map<String, String> headerProperty) {
            this.headerProperty = headerProperty;
            return this;
        }

        public HttpUtil build(){
            return new HttpUtil(method, url, data, cookies, headerProperty, isReturn);
        }
    }

    HttpUtil(String method, String url, String data, String cookies, Map<String, String> headerProperty, Boolean isReturn) {
        this.method = Optional.ofNullable(method).orElse("GET");
        this.url = url;
        this.data = data;
        this.cookies = cookies;
        this.headerProperty = headerProperty;
        this.isReturn = Optional.ofNullable(isReturn).orElse(true);

        init();
    }

    private void init(){
        HttpURLConnection conn = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;

        try {
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8080));
//            conn = (HttpURLConnection) new URL(url).openConnection(proxy);
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Cookie", cookies);
            conn.setUseCaches(false);       //设置不缓存
            conn.setDoOutput(true);         //需要输出
            conn.setDoInput(true);          //需要输入
            method = method.toUpperCase();  //转为大写
            conn.setRequestMethod(method);  //设置POST 或 GET

            //迭代设置的header头并批量配置
            if(headerProperty != null) headerProperty.forEach(conn::addRequestProperty);

            //POST请求的话则把data参数传过去
            if("POST".equalsIgnoreCase(method)) {
                writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), CHARSET));
                writer.write(data);
                writer.flush();
            }

            //读取响应内容
            if(isReturn) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                htmlText = sb.toString();
            }

            //保存返回的cookides
            cookies = conn.getHeaderField("Set-Cookie");

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public String getCookies() {
        return cookies;
    }

    public Map<String, String> getHeaderProperty() {
        return headerProperty;
    }

    public String getHtmlText() {
        return htmlText;
    }
}
