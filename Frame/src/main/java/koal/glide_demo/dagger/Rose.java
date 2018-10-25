package koal.glide_demo.dagger;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class Rose {

    Son son;

    @Inject
    public Rose(Son son) {
        this.son = son;
    }

    public String whisper(){
        son.loud();
        return "I love you";
    }
}
