package com.exa.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by user on 2018/8/19.
 */

public class PluginUtil {

    public static Resources createResources(String pluginFilePath, Context context)
            throws InvocationTargetException, NoSuchMethodException
            , InstantiationException, IllegalAccessException {

        AssetManager assetManager  = createAssetManager(pluginFilePath);

        Resources superRes = context.getResources();

        return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
    }

    public static AssetManager createAssetManager(String pluginFilePath)
            throws IllegalAccessException, InstantiationException
            , NoSuchMethodException, InvocationTargetException {

        AssetManager assetManager = AssetManager.class.newInstance();

        Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath",String.class
        );

        addAssetPathMethod.setAccessible(true);

        addAssetPathMethod.invoke(assetManager, pluginFilePath);

        return assetManager;
    }
}
