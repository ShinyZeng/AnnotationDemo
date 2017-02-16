/**
 * Copyright (C) © 2014 深圳市掌玩网络技术有限公司
 * TestDemo
 * FactoryProcessor.java
 */
package com.example;


import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Administrator
 * @since 2017/2/13 19:35
 * @version 1.0
 *          <p>
 *          <strong>Features draft description.主要功能介绍</strong>
 *          </p>
 */
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private Types    mTypeUtils;   // TypeMirror
    private Filer    mFileUtils;   // 创建文件的工具
    private Elements mElementUtils;
    private Messager mMessager;    // 用来上报信息

    private Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<>();
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mTypeUtils = processingEnv.getTypeUtils();
        this.mFileUtils = processingEnv.getFiler();
        this.mElementUtils = processingEnv.getElementUtils();
        this.mMessager = processingEnv.getMessager();

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<String>();
        annotationTypes.add(Factory.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 这个是进行扫描被Factory注解的java源码，获取Element，Element会包含类的信息；TypeMirror，通过element.asType()获取
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 遍历所有被@Factory的element，而不一定是类，因为element有可能不是类。可能是方法，成员变量等等
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Factory.class)) {
            // 判断element这个是不是类，是类才继续处理
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only the classes can be annotated with @%s", Factory.class.getSimpleName());
                //返回true是退出process过程
                return true;
            }

            TypeElement typeElement = (TypeElement) annotatedElement;
            try {
                FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass(typeElement);
                if (!isValidClass(factoryAnnotatedClass)) {
                    return true; // Error message printed, exit processing
                }

                // Everything is fine, so try to add
                FactoryGroupedClasses factoryClass =
                        factoryClasses.get(factoryAnnotatedClass.getQualifiedSuperClassName());
                if (factoryClass == null) {
                    String qualifiedGroupName = factoryAnnotatedClass.getQualifiedSuperClassName();
                    factoryClass = new FactoryGroupedClasses(qualifiedGroupName);
                    factoryClasses.put(qualifiedGroupName, factoryClass);
                }

                // Throws IdAlreadyUsedException if id is conflicting with
                // another @Factory annotated class with the same id
                factoryClass.add(factoryAnnotatedClass);
            }catch (IllegalArgumentException e){
                error(typeElement,e.getMessage());
                return true;
            }

            try {
                for (FactoryGroupedClasses factoryClass : factoryClasses.values()) {
                    factoryClass.generateCode(mElementUtils, mFileUtils);
                }

                factoryClasses.clear();
            } catch (IOException e) {
                error(null, e.getMessage());
            }

            return true;
        }



        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private void error(Element pElement, String pMsg, Object... pArgs) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(pMsg, pArgs), pElement);
    }

    private boolean isValidClass(FactoryAnnotatedClass item) {

        // Cast to TypeElement, has more type specific methods
        TypeElement classElement = item.getAnnotatedClassElement();

        //这个类必须是public
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(classElement, "The class %s is not public.",
                    classElement.getQualifiedName().toString());
            return false;
        }

        // 不是是抽象的
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
            return false;
        }

        TypeElement superClassElement =
                mElementUtils.getTypeElement(item.getQualifiedSuperClassName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // Check interface implemented
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                error(classElement, "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        item.getQualifiedSuperClassName());
                return false;
            }
        } else {
            // Check subclassing
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE) {
                    // Basis class (java.lang.Object) reached, so exit
                    error(classElement, "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                            item.getQualifiedSuperClassName());
                    return false;
                }

                if (superClassType.toString().equals(item.getQualifiedSuperClassName())) {
                    // Required super class found
                    break;
                }

                // Moving up in inheritance tree
                currentClass = (TypeElement) mTypeUtils.asElement(superClassType);
            }
        }

        // 检查这个是类是不是有个空参数public的构造方法
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers()
                        .contains(Modifier.PUBLIC)) {
                    // Found an empty constructor
                    return true;
                }
            }
        }

        // No empty constructor found
        error(classElement, "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
        return false;
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
