package koal.glide_demo.dagger.module;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class Cat extends Animal {

    private static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN_FAKE = 0x00000100;

    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 0x00000400;

    public static final int SYSTEM_UI_FLAG_IMMERSIVE = 0x00000800;

    @Override
    public String speak() {
        return "Dog speak miao miao!";
    }
}
