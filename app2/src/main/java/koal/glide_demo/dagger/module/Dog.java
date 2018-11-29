package koal.glide_demo.dagger.module;

import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

public class Dog extends Animal {

    @Override
    public String speak() {
        return "Dog speak wang wang!";
    }
}
