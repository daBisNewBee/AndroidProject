package koal.glide_demo.dagger.module;

import dagger.Component;
import javax.inject.Inject;

/**
 * Created by liuwb on 2018/10/25.
 */

@Component(modules = {AnimalModule.class, ZoomModule.class})
public interface MyServiceComponent {
    void inject(MyService myService);
}
