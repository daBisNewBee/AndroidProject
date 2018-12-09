package koal.glide_demo.dagger;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class Son {

    @Inject
    public Son() {
    }

    public void loud(){
        System.out.println("Son.loud ------------->");
    }
}
