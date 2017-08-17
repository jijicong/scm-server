package org.trc.service;

import jdk.nashorn.internal.objects.NativeUint8Array;
import org.apache.poi.ss.formula.functions.T;

import java.nio.file.OpenOption;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Created by sone21 on 2017/8/10.
 */
public class TestLamada {

    public static void main(String[] args) {
        //lamada 表达式用法之一
  //    new Thread(()-> System.out.println("iamlamada")).start();
        //单列的新用法，新视线，高效 易懂
        Foo foo = Foo.getFoo();

        foo.setMsg("looking for matcch");

        Foo foo2 = Foo.getFoo();

        //System.out.println(foo2.getMsg());
        //解决线程并发的数字处理 - 选择场合慎用- 其中的线程共享关键字(?)起着关键作用
        AtomicInteger count = new AtomicInteger();

       // System.out.println(count.addAndGet(1));
        //用好预编译
        Pattern pattern = Pattern.compile("");

        /*
        Optional,用一个类包裹着另外一个类，对其进行判空的处理（专门）
        Optional对象的实例化，以及处理的函数，糅合在了一起
         */
        Optional<String> optional = Optional.of("lang");

        //Optional.of(null); //NPE

        Optional<String> optional2 = Optional.ofNullable(null);

       // System.out.println(optional2.isPresent());

        optional2.ifPresent(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("hehe");
                System.out.println(s);
            }
        });

        optional.ifPresent((value) -> {
            System.out.println("The length of the value is: " + value.length());
        });



    }

}

class Foo {

    private String msg;

    private static class FooHolder {
        static final Foo foo = new Foo();
    }
    public static Foo getFoo() {
        return FooHolder.foo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}