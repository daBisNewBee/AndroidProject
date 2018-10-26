package koal.glide_demo.dagger;

import dagger.Component;

/**
 *
 * 注入器
 *
 * 命名方式推荐为：目标类名+Component，在编译后Dagger2就会为我们生成DaggerXXXComponent这个类，
 * 它是我们定义的xxxComponent的实现，在目标类中使用它就可以实现依赖注入了。
 *
 * Created by liuwb on 2018/10/25.
 */

@Component
public interface MyActivityComponent {

    // 必须是子类，不可以是接口或者基类
    void inject(MyActivity myActivity);
}
