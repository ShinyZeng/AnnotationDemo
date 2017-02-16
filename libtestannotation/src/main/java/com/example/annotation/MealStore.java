/**
 * Copyright (C) © 2014 深圳市掌玩网络技术有限公司
 * TestDemo
 * MealStore.java
 */
package com.example.annotation;


import java.util.Scanner;

/**
 * @author Administrator
 * @since 2017/2/15 20:27
 * @version 1.0
 * <p><strong>Features draft description.主要功能介绍</strong></p>
 */
public class MealStore {
    // ===========================================================
    // Constants
    // ===========================================================
//
    private MealFactory factory = new MealFactory();

    public Meal order(String mealName) {
        return factory.create(mealName);
//        return new Beef();
    }

    private static String readConsole() {
        Scanner scanner = new Scanner(System.in);
        String meal = scanner.nextLine();
        scanner.close();
        return meal;
    }
    // ===========================================================
    // Fields
    // ===========================================================
    public static void main(String[] args){
        System.out.println("welcome to meal store");
        MealStore pizzaStore = new MealStore();
        Meal meal = pizzaStore.order(readConsole());
        System.out.println("Bill:$" + meal.getPrice());
    }

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
