package com.android.kit.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import com.android.kit.cache.imge.FlushedInputStream;
import com.android.kit.utils.KitLog;
import com.android.kit.utils.KitStreamUtils;
/**
 * 轻量级HTTPClient请求类
 * @author Danel
 *
 */
public class KitHttpClient {
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private static int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private final Map<String, String> clientHeaderMap;
    
    private static String ENCODING = "UTF-8";

    /**
     * Creates a new KitHttpClient.
     */
    public KitHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, socketTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

        HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : clientHeaderMap.keySet()) {
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        clientHeaderMap = new HashMap<String, String>();
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public void setCookieStore(CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
    }

    public void setTimeout(int timeout){
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
    }
    
    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }

    public void setBasicAuth(String user, String pass){
        AuthScope scope = AuthScope.ANY;
        setBasicAuth(user, pass, scope);
    }
    
    public void setBasicAuth( String user, String pass, AuthScope scope){
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user,pass);
        this.httpClient.getCredentialsProvider().setCredentials(scope, credentials);
    }

    /**
     * 根据URL,获取网络请求输入流
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public InputStream getInputStream(String url) throws ClientProtocolException, IOException{
        return getInputStream(url,null);
    }
    
    /**
     * 根据URL,获取网络请求输入流
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public InputStream getInputStream(String url,RequestParams params) throws ClientProtocolException, IOException{
        HttpGet httpRequest = new HttpGet(getUrlWithQueryString(url,params));
        HttpResponse response = httpClient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        return bufHttpEntity.getContent();
    }
    
    /**
     * 根据URL,获取网络请求字符串
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getString(String url) throws ClientProtocolException, IOException{
        InputStream is = getInputStream(url);
        FlushedInputStream in = new FlushedInputStream(new BufferedInputStream(is, 8*1024));
        return KitStreamUtils.readAsciiLine(in);
    }
    
    /**
     * 根据URL,获取网络请求字符串
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getString(String url,RequestParams params) throws ClientProtocolException, IOException{
        InputStream is = getInputStream(url,params);
        FlushedInputStream in = new FlushedInputStream(new BufferedInputStream(is, 8*1024));
        return KitStreamUtils.readAsciiLine(in);
    }
    
    public InputStream postInputStream(String url) throws ClientProtocolException, IOException{
        return postInputStream(url,null);
    }
    
    public InputStream postInputStream(String url,RequestParams params) throws ClientProtocolException, IOException{
        getUrlWithQueryString(url, params);
        HttpEntityEnclosingRequestBase httpRequest = new HttpPost(url);
        HttpResponse response = httpClient.execute(addEntityToRequestBase(httpRequest,paramsToEntity(params)),httpContext);
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        return bufHttpEntity.getContent();
    }
    public String postString(String url) throws ClientProtocolException, IOException{
        return postString(url,null);
    }
    
    public String postString(String url,RequestParams params) throws ClientProtocolException, IOException{
        InputStream is = postInputStream(url,params);
        FlushedInputStream in = new FlushedInputStream(new BufferedInputStream(is, 8*1024));
        return KitStreamUtils.readAsciiLine(in);
    }
    
    private HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if(params != null) {
            entity = params.getEntity();
        }

        return entity;
    }
    
    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if(entity != null){
            requestBase.setEntity(entity);
        }

        return requestBase;
    }
    
    public HttpEntity getEntity(HashMap<String, String> params) {
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(getParamsList(params), ENCODING);
        } catch (UnsupportedEncodingException e) {
            KitLog.printStackTrace(e);
        }
        return entity;
    }
    
    protected List<BasicNameValuePair> getParamsList(HashMap<String, String> params) {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
        for(ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return lparams;
    }

    public static String getUrlWithQueryString(String url, RequestParams params) {
        if(params != null) {
            String paramString = params.getParamString();
            if (url.indexOf("?") == -1) {
                url += "?" + paramString;
            } else if(url.endsWith("?")){
            	url += paramString;
            }else {
                url += "&" + paramString;
            }
        }
        KitLog.e("URL", url);
        return url;
    }
    
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
