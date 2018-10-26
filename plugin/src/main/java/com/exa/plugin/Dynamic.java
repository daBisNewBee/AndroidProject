package com.exa.plugin;

import com.exa.plugin.lib.Callback;
import com.exa.plugin.lib.IDynamic;

/**
 * Created by user on 2018/8/18.
 */

public class Dynamic implements IDynamic {
    @Override
    public void methodWithCallback(Callback callback) {
        System.out.println("Dynamic.methodWithCallback --------> ");
        Bean bean = new Bean();
        bean.setName("liu da shuai");
        //回调宿主APP的方法
        callback.update(bean);
    }
}
