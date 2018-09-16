package com.example.processor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 *
 * 如何自定义注解处理器？
 *
 * 1. 实现：Processor接口处理注解
 *
 * 2. 注册：注解处理器。两个方法：
 *      1. 在META-INF中手写配置
 *      2. 优雅的方式：框架：@AutoService(Processor.class)
 *      参考：https://github.com/google/auto/tree/master/service
 *
 *
 * 通过定义一个annotation，在编译代码的时候，凡是用该annotation
 * 声明过的类，方法，我们都要在控制台输出他们的信息
 *
 * Annotation实战【自定义AbstractProcessor】:
 * https://www.cnblogs.com/avenwu/p/4173899.html
 *
 * 如何调试 AbstractProcessor？
 * 1. 在 processor中设置断点
 * 2. ./gradlew --daemon
 * 3. 配置 "gradle.properties"
 * 4. 新建一个"Remote"类型的Debuger
 * 5. debug 这个 "Remote_debuger"
 * 6. build宿主app，触发断点
 * 7. 需要更新"MyProcessor"的代码，才会在第二次build app时，触发断点。
 *
 * 参考：
 * https://blog.csdn.net/worst_hacker/article/details/71158250
 *
 *
 * Created by user on 2018/9/16.
 */

// 指定支持的注解类型
@SupportedAnnotationTypes({"com.exa.eventbus.PrintMe"})
@SupportedOptions({"mykey","mykey2"})// "getSupportedOptions"引用
public class MyProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 1. 打印message
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "--------> into process(MyProcessor)");
        messager.printMessage(Diagnostic.Kind.NOTE, "this is Diagnostic.Kind.NOTE");
        messager.printMessage(Diagnostic.Kind.WARNING, "this is Diagnostic.Kind.WARNING");
        messager.printMessage(Diagnostic.Kind.OTHER, "this is Diagnostic.Kind.OTHER");
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "this is Diagnostic.Kind.MANDATORY_WARNING");
//        messager.printMessage(Diagnostic.Kind.ERROR, "this is Diagnostic.Kind.ERROR");

        /*
        * 2. 获取options:
        *
         javaCompileOptions {
                annotationProcessorOptions {
                arguments = ["room.schemaLocation": "$projectDir/schemas".toString(),
                        eventBusIndex : 'com.exa.eventbus.MyEventBusIndex',
                        mykey:'myvalue']
                //生成索引的名称
            }
        }
        *
        * */
        Map<String,String> optionsMap = processingEnv.getOptions();
        Set<Map.Entry<String, String>> entry  = optionsMap.entrySet();
        for (Map.Entry<String, String> one : entry) {
            messager.printMessage(Diagnostic.Kind.NOTE,
                    String.format("key:%s value:%s", one.getKey(), one.getValue()));
        }

        // 3. 获取支持的opt
        Set<String> supportedOptSet = getSupportedOptions();
        for (String s : supportedOptSet) {
            messager.printMessage(Diagnostic.Kind.NOTE, "getSupportedOptions:"+s);
        }

        // 4. 获取被注解的方法
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)){
                messager.printMessage(Diagnostic.Kind.NOTE, "printing:"+e.toString());
            }
        }

        /*
        * 5. 生成到java源文件
        * 到：build->generated->apt->debug
        *
        * 使用：JavaFileObject sourceFile = filer.createSourceFile(path);
        *
        * 具体可以参考：
        * 自定义Java注解处理器：https://www.jianshu.com/p/50d95fbf635c
        *
        * 或者，EventBus中的注解处理器：
        * https://github.com/greenrobot/EventBus/blob/master/EventBusAnnotationProcessor/src/org/greenrobot/eventbus/annotationprocessor/EventBusAnnotationProcessor.java
        *
        * */

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
