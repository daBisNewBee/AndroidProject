package koal.glide_demo.dagger.module;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liuwb on 2018/10/25.
 */

@Module
public class ZoomModule {

    @Provides
    Zoom provideZoom(@DogAnimal Animal animal){
        return new Zoom(animal);
    }

}
