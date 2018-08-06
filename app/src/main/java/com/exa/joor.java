package com.exa;

import static org.joor.Reflect.*;

/**
 * Created by user on 2018/8/4.
 */


public class joor {

    public interface StringProxy{
        String substring(int beginIndex);
    }

    public static void main(String[] args) {

        final String CONST = "hello world";

        String world = on("java.lang.String")
                .create(CONST)
                .call("substring",6)
                .call("toString")
                .get();
        String old = "hello world".substring(6);
        System.out.println("old = " + old);
        System.out.println("world = " + world);

        String substring = on("java.lang.String")
                .create(CONST)
                .as(StringProxy.class)
                .substring(6);

        System.out.println("substring = " + substring);

    }
}
