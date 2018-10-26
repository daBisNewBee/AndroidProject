package com.exa.plugin;

import com.exa.plugin.lib.IBean;

/**
 * Created by user on 2018/8/18.
 */

public class Bean implements IBean {

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String _name) {
        name = _name;
    }
}
