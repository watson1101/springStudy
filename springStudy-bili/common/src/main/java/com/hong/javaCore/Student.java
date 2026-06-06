package com.hong.javaCore;

public class Student extends Person {
    {
        System.out.println("子类的代码块执行");
    }

    static {
        System.out.println("子类的静态代码块执行");
    }

    public Student() {
        super();
        System.out.println("子类的无参构造方法执行");
    }

    public Student(int age, String name) {
        super(age, name);
        System.out.println("子类的完整构造方法执行");
    }
}

class Test {
    public static void main(String[] args) {
        new Student(15,"Lily");
    }
    /*
     输出：
     父类的静态代码块执行
子类的静态代码块执行
父类的代码块执行
父类的完整构造方法执行
子类的代码块执行
子类的完整构造方法执行


     */
}
