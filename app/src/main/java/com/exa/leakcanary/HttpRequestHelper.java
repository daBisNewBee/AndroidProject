package com.exa.leakcanary;

import android.view.View;

/**
 *
 * Fake class for the purpose of demonstrating a leak.
 *
 * Created by user on 2018/9/23.
 */

public class HttpRequestHelper {
    private final View button;

    HttpRequestHelper(View button) {
        this.button = button;
    }
}
