package com.lue.laoyoutiao.network;

/**
 * Created by Lue on 2015/12/24.
 */
public class NetworkException extends Exception
{
    public static final String NETWORK_EXCEPTION = "网络异常";

    public NetworkException()
    {
        super();
    }

    public NetworkException(String message)
    {
        super(message);
    }
}
