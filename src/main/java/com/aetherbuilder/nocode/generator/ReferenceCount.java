package com.aetherbuilder.nocode.generator;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author : Cammy.Wu
 * Description : 
 */

public class ReferenceCount {

    private Object instance = null;

    private static final int _1MB = 1024 * 1024;
    private byte[] bigObject = new byte[2 * _1MB];

//    public static void main(String[] args) {
//        ReferenceCount objA = new ReferenceCount();
//        ReferenceCount objB = new ReferenceCount();
//        objA.instance = objB;
//        objB.instance = objA;
//
//        objA = null;
//        objB = null;
//
//        System.gc();
//    }

        public static void main(String[] args) {
            softReference();// 软引用
            weakReferenceTest();// 弱引用
            phantomReference();// 虚引用

        }

    /**
     * 软引用测试
     * 会发现gc后，软引用对象的值获仍然能够获取到
     */
    private static void softReference() {
        String str = new String("hzk 666!!!");
        SoftReference<String> stringSoftReference = new SoftReference<>(str);
        System.out.println("软引用的值" + stringSoftReference.get());//没有进行gc前软引用能得到对象
        str = null;
        System.gc();
        stringSoftReference.get();
        System.out.println("软引用对象被垃圾回收了,软引用对象的值" + stringSoftReference.get());
    }

    /**
     * 弱引用测试
     * 会发现gc后，弱引用对象的值获取不到
     */
    private static void weakReferenceTest() {
        String str = new String("hzk 666!!!");
        WeakReference<String> stringWeakReference = new WeakReference<>(str);
        str = null;
        System.out.println("弱引用的值" + stringWeakReference.get());//没有进行gc前软引用能得到对象
        System.gc();//进行垃圾回收
        stringWeakReference.get();
        System.out.println("弱引用对象被垃圾回收了,软引用对象的值" + stringWeakReference.get());
    }

    /**
     * 虚引用测试
     * 会发现gc前，弱引用对象的值都获取不到
     */
    private static void phantomReference() {
        String helloWorldString = new String("hzk 666!!!");
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference ref = new PhantomReference(helloWorldString, queue);
        System.out.println(ref.get());
        System.gc();//进行垃圾回收
        System.out.println(ref.get());

    }
}
