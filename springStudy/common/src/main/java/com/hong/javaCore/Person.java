package com.hong.javaCore;

public class Person {
    {
        System.out.println("父类的代码块执行");
    }
    static {
        System.out.println("父类的静态代码块执行");
    }
    public Person() {
        System.out.println("父类的无参构造方法执行");
    }
    private int age;
    private String name;

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
        System.out.println("父类的完整构造方法执行");
    }
}
