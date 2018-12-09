package koal.glide_demo.dagger.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created by liuwb on 2018/10/25.
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface CatAnimal {

}
