package koal.glide_demo.dagger;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class Pot {

    private Rose rose;

    /**
     * Inject 两个作用：
     * 1. 指出构造器
     * 2. 指出构造器需要的参数
     *
     * @param rose
     */
    @Inject
    public Pot(Rose rose) {
        this.rose = rose;
    }

    public String show(){
        return rose.whisper();
    }
}
