package koal.glide_demo;

import koal.glide_demo.dagger.MyActivity;
import koal.glide_demo.dagger.module.MyService;
import org.junit.Test;

/**
 *
 * Dagger2 最清晰的使用教程:
 *
 * https://www.jianshu.com/p/24af4c102f62
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DaggerTest {

    @Test
    public void dagger_Test() throws Exception {

        MyActivity.normal();

        MyActivity.entry();
    }

    @Test
    public void moduleTest() throws Exception {
        MyService.speak();
    }
}