package com.exa.plugin;

import android.app.Activity;

/**
 * Hook技术之Activity的启动过程拦截基本思路：
 *
 *  DroidPlugin采用的是预先注册占坑，预先注册权限的方式。
    利用假的Activity来做“运行”真的PluginActivity。

 * 1. 找到两个Hook点
 * 2. 在第一个hook点，使用假的Activity来替换真实的Activity
 * 3. 在第二个hook点，恢复真实的Activity
 * 4. hook点1：ActivityManagerNative 中的 "IActivityManagerSingleton"
 * 5. hook点2：ActivityThread 中的 "mH"
 *
 * Created by user on 2018/8/18.
 */

public class ProxyActivity extends Activity {
}
