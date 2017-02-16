/**
 * Copyright (C) © 2014 深圳市掌玩网络技术有限公司
 * TestDemo
 * FactoryGroupedClasses.java
 */
package com.example;



import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * @author Administrator
 * @since 2017/2/15 17:00
 * @version 1.0
 * <p><strong>Features draft description.主要功能介绍</strong></p>
 */
public class FactoryGroupedClasses {
    // ===========================================================
    // Constants
    // ===========================================================
    /**
     * Will be added to the name of the generated factory class
     */
    private static final String SUFFIX = "Factory";

    private String mQualifiedClassName;

    private Map<String,FactoryAnnotatedClass> mItemMap = new LinkedHashMap<>();

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================
    public FactoryGroupedClasses(String pQualifiedClassName){
        this.mQualifiedClassName = pQualifiedClassName;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================
    public void add(FactoryAnnotatedClass pFactoryAnnotatedClass) throws IllegalArgumentException{
        FactoryAnnotatedClass existing = mItemMap.get(pFactoryAnnotatedClass.getId());
        if(existing != null){
            throw new IllegalArgumentException(String.format("%s id %s already add!",existing.getSimpleTypeName(), existing.getId()));
        }
        mItemMap.put(pFactoryAnnotatedClass.getId(),pFactoryAnnotatedClass);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    public void generateCode(Elements pElementUtils, Filer mFileUtils) throws IOException{
        TypeElement superClassName = pElementUtils.getTypeElement(mQualifiedClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;

        JavaFileObject jfo = mFileUtils.createSourceFile(mQualifiedClassName + SUFFIX);
        Writer writer = jfo.openWriter();
        JavaWriter jw = new JavaWriter(writer);

        // Write package
        PackageElement pkg = pElementUtils.getPackageOf(superClassName);
        if (!pkg.isUnnamed()) {
            jw.emitPackage(pkg.getQualifiedName().toString());
            jw.emitEmptyLine();
        } else {
            jw.emitPackage("");
        }

        jw.beginType(factoryClassName, "class", EnumSet.of(Modifier.PUBLIC));
        jw.emitEmptyLine();
        jw.beginMethod(mQualifiedClassName, "create", EnumSet.of(Modifier.PUBLIC), "String", "id");

        jw.beginControlFlow("if (id == null)");
        jw.emitStatement("throw new IllegalArgumentException(\"id is null!\")");
        jw.endControlFlow();

        for (FactoryAnnotatedClass item : mItemMap.values()) {
            jw.beginControlFlow("if (\"%s\".equals(id))", item.getId());
            jw.emitStatement("return new %s()", item.getAnnotatedClassElement().getQualifiedName().toString());
            jw.endControlFlow();
            jw.emitEmptyLine();
        }

        jw.emitStatement("throw new IllegalArgumentException(\"Unknown id = \" + id)");
        jw.endMethod();

        jw.endType();

        jw.close();
    }

    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
