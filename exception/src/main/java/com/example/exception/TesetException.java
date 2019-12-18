package com.example.exception;

/**
 * createBy keepon
 */
public class TesetException {
    public static void main(String[] args) {
        // test1();
        // test2();
        // test3();
        // test4();
        System.out.println("TesetException main "+ getNumber());
    }

    private static int getNumber2() {
        try {
            int a = 0 / 1;
            int b = 1 / 0;
            return 1;  //抛出异常下面的代码不会执行
        } catch (Exception e) {
            return 2;
        } finally {

        }
    }

    private static int getNumber() {
        try {
            int a = 0 / 1;
            int b = 1 / 0;
            return 1;  //抛出异常下面的代码不会执行
        } catch (Exception e) {
            return 2;
        } finally {
            return 3;  //这里有的话，返回的是3
        }
    }

    private static void test4() {
        try {
            int a = 0 / 1;
            int b = 1 / 0;
        } catch (Exception e) {
            System.out.println("TesetException test4 ");  //异常被捕获了，不会抛出
        } finally {
            int c = 4 / 0;  //finally抛异常会直接抛出
            return; // 这里有return也没用
        }
    }

    private static void test3() {
        try {
            int a = 0 / 1;
            int b = 1 / 0;
        } catch (Exception e) {
            int c = 2 / 0;
        } finally {
        }
    }

    private static void test2() {
        try {
            int a = 0 / 1;
            int b = 1 / 0;
        } catch (Exception e) {
            int c = 2 / 0;
        } finally {
            return;  //有retrun语句，catch中有异常也不会抛出
        }
    }

    //try块中，发生异常
    private static void test1() {
        try {
            int a = 0 / 1;
            int b = 1 / 0;
        } catch (NullPointerException e) {

        } finally {

        }
    }
    // 在发生异常时，会首先检查异常类型，是否存在于我们的 catch 块中指定的待捕获异常。如果存在，则这个异常被捕获，对应的 catch 块代码则开始运行，finally 块代码紧随其后。
    //
    // 例如：我们只监听了空指针（NullPointerException），此时如果发生了除数为 0 的崩溃（ArithmeticException），则是不会被处理的。
    //
    // 当触发了我们未捕获的异常时，finally 代码依然会被执行，在执行完毕后，继续将异常“抛出去”


}
