package koal.glide_demo.dagger.module;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by liuwb on 2018/10/25.
 */

public class Zoom {

    private Animal animal;

    /**
     * 依赖抽象类时，需要指定“AnimalModule”
     * 并使用@Module、@Provides等注解
     *
     * 当AnimalModule中有多个方法可以提供Animal对象实例时，
     * 两种方法区分使用哪种依赖：
     * 1. @Named
     * 2. @DogAnimal
     * 更推荐使用@Qualifier，因为@Named需要手写字符串，容易出错。
     *
     * @param animal
     */
    @Inject
//    public Zoom(@Named("Dog") Animal animal) {
    public Zoom(@DogAnimal Animal animal) {
        this.animal = animal;
    }

    public String show(){
        return animal.speak();
    }
}
