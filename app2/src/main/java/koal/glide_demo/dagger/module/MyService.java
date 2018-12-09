package koal.glide_demo.dagger.module;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class MyService {

    @Inject
    Zoom zoom;

    public static void speak(){
        MyService myService = new MyService();
        DaggerMyServiceComponent.create().inject(myService);
        System.out.println(myService.zoom.show());
    }
}
