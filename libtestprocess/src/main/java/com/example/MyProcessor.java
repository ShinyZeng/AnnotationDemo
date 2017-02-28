/**
 * Copyright (C) © 2014 深圳市掌玩网络技术有限公司
 * TestDemo
 * MyProcessor.java
 */
package com.example;

import com.example.annotation.MyAnnotation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


/**
 * @author Administrator
 * @since 2017/2/25 16:05
 * @version 1.0
 * <p><strong>Features draft description.主要功能介绍</strong></p>
 */

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor{

    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        Set<String> supportedType = new LinkedHashSet<String>();
        supportedType.add(MyAnnotation.class.getCanonicalName());
        return supportedType;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);
        for (Element element : set){
            if(element.getKind() == ElementKind.CLASS){
                //在这个文件下写HelloWord
                TypeElement typeElement = (TypeElement) element;
                String qualifiedName = typeElement.getQualifiedName().toString();
                mMessager.printMessage(Diagnostic.Kind.NOTE,"qualifiedName:"+qualifiedName);
                brewJavaFile(typeElement);
            }
        }
        return true;
    }

    private void brewJavaFile(TypeElement pElement){
        //HelloWorld 方法
        MyAnnotation myAnnotation = pElement.getAnnotation(MyAnnotation.class);
        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(void.class)
                .addStatement("$T.out.println($S)",System.class,"Hello "+myAnnotation.value()).build();

        // class
        TypeSpec typeSpec = TypeSpec.classBuilder(pElement.getSimpleName().toString()+"$$HelloWorld")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL).addMethod(methodSpec).build();

        JavaFile javaFile = JavaFile.builder(mElementUtils.getPackageOf(pElement).getQualifiedName().toString()
                            ,typeSpec).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
