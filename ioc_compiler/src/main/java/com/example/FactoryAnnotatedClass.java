/**
 * Copyright (C) © 2014 深圳市掌玩网络技术有限公司
 * TestDemo
 * FactoryAnnotatedClass.java
 */
package com.example;


import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * @author Administrator
 * @since 2017/2/15 15:56
 * @version 1.0
 * <p><strong>Features draft description.主要功能介绍
 *  主要是做@Factory 注解 类的数据模型，存放Element和一些类的信息
 * </strong></p>
 */
public class FactoryAnnotatedClass {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    private TypeElement mAnnotatedClassElement;
    private String mQualifiedSuperClassName;//全路径
    private String mSimpleTypeName;
    private String mId;

    // ===========================================================
    // Constructors
    // ===========================================================
    public FactoryAnnotatedClass(TypeElement pClassElement) throws IllegalArgumentException{
        this.mAnnotatedClassElement = pClassElement;
        Factory annotation = pClassElement.getAnnotation(Factory.class);
        mId = annotation.id();
        if(StringUtils.isEmpty(mId)){
            throw new IllegalArgumentException(String.format("id() in @%s for class %s is null or empty!",
                    Factory.class.getSimpleName(),pClassElement.getQualifiedName().toString()));
        }

        try {
            // 注意这个是java工程，运行时机是在processor中process方法，对应的扫描的是带有注解@Factory的类
            // 当这个类是已经被编译过的称谓class格式时，这个时候带有@Factory注解时，将在try块中的代码。
            Class<?> type = annotation.type();
            mQualifiedSuperClassName = type.getCanonicalName();
            mSimpleTypeName = type.getSimpleName();
        }catch (MirroredTypeException mte){
            // 另一种情况，如果这个类还没有被编译成class格式的文件，是java源码的话，则会走到catch块中代码
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement)classTypeMirror.asElement();
            mQualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            mSimpleTypeName = classTypeElement.getSimpleName().toString();
        }
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    /**
     * {@link Factory#id()}
     * @return the id
     */
    public String getId(){
        return mId;
    }

    public String getQualifiedSuperClassName(){
        return mQualifiedSuperClassName;
    }

    public String getSimpleTypeName(){
        return mSimpleTypeName;
    }

    public TypeElement getAnnotatedClassElement(){
        return mAnnotatedClassElement;
    }

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
