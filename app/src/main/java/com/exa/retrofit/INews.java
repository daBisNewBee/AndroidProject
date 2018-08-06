package com.exa.retrofit;

/**
 * Created by user on 2018/7/22.
 */

public interface INews {

    @Get("http://www.qq.com")
    String getQQ();

    @Get("http://www.sina.com.cn")
    String getSina();
}
