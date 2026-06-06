package com.hong.juc;
/**
运行结果：
线程1 --  waiting data...
---  set flag...
--- set flag 结束
并且，程序并不停止

说明：按照理解，应该是修改了flag之后，会执行输出 “线程1结束” ，
 实际并没有，这是因为，两个线程操作的是<b>工作内存中的共享变量副本</b> ，
 线程2的修改并不影响线程1的内存副本中的flag值

 <b>但如果给flag加上volatile关键字，则线程1会输出“线程1结束”</b>
 */
public class JmmDemo {
    private static boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println("线程1 --  waiting data...");
            while (!flag) {
                // do nothing
            }
            // 实际上这行输出代码不会执行
            System.out.println("线程1结束");
        }).start();
        Thread.sleep(1000);
        // 以下两种写法均可
//        new Thread(JmmDemo::setData).start();
        new Thread(() -> setData()).start();
    }
    public static void setData() {
        System.out.println("---  set flag...");
        flag = true;
        System.out.println("--- set flag 结束");
    }
}
