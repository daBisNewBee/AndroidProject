package koal.glide_demo.dagger.module;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

/**
 * Created by liuwb on 2018/10/25.
 */

// @Module是告诉Component，可以从这里获取依赖对象。Component就会去找被@Provide标注的方法，相当于构造器的@Inject，可以提供依赖。
@Module
public class AnimalModule {

    /*
    *
    * Error:(12, 10) 错误: koal.glide_demo.dagger.module.Animal is bound multiple times:
@Provides koal.glide_demo.dagger.module.Animal koal.glide_demo.dagger.module.AnimalModule.provideDog()
@Provides koal.glide_demo.dagger.module.Animal koal.glide_demo.dagger.module.AnimalModule.provideCat()
    * */
    @Provides
    @DogAnimal
//    @Named("Dog")
    Animal provideDog(){
        return new Dog();
    }

    @Provides
    @CatAnimal
//    @Named("Cat")
    Animal provideCat(){
        return new Cat();
    }
}
