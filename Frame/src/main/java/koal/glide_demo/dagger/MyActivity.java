package koal.glide_demo.dagger;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class MyActivity {

    /*
    * 属性注解
    * */
    @Inject
    Pot pot;

    Son son;

    public static void normal(){
        Son son = new Son();
        Rose rose = new Rose(son);
        Pot pot = new Pot(rose);
        System.out.println("pot = " + pot.show());
    }

    public static void entry(){
        MyActivity activity = new MyActivity();
        DaggerMyActivityComponent.create().inject(activity);
        System.out.println("pot.show() = " + activity.pot.show());
        activity.son.loud();
    }

    /*
    * 方法注解
    * */
    @Inject
    public void setSon(Son son) {
        this.son = son;
    }
}
