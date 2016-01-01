package com.lue.laoyoutiao.network;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Lue on 2015/12/24.
 */
public class HttpGetMethod
{
    private DefaultHttpClient httpClient;
    private HttpGet httpGet;

    /**
     * 构造方法
     * @param httpClient
     * @param auth
     * @param url
     */
    public HttpGetMethod(DefaultHttpClient httpClient, String auth, String url)
    {
        this.httpClient = httpClient;
        httpGet = new HttpGet(url);
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Authorization", "Basic " + auth);
    }


//    public JSONObject getJSON() throws JSONException, NetworkException, IOException
//    {
//        HttpResponse response = httpClient.execute(httpGet);
//
//        int statusCode = response.getStatusLine().getStatusCode();
//        if(statusCode != 200)
//            throw new NetworkException(NetworkException.NETWORK_EXCEPTION + ":" + statusCode);
//
//        Header header = response.getEntity().getContentEncoding();
//
//        if( header != null )
//        {
//
//            for( HeaderElement headerElement : header.getElements() )
//            {
//                if ( headerElement.getName().equalsIgnoreCase("gzip"))
//                {
//
//                    response.setEntity(new GZIPInputStream() );
//                }
//            }
//
//        }
//
//    }
}
