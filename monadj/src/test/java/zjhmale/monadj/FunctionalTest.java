package zjhmale.monadj;

import org.junit.Test;
import zjhmale.monadj.function.Function;

import static org.junit.Assert.assertTrue;

/**
 * Created by zjh on 15/10/9.
 */
public class FunctionalTest {
    @Test
    public void id() {
        assertTrue((Integer) Identity.identity().apply(1) == 1);
    }

    @Test
    public void compose() {
        Function<Integer, String> itos = new Function<Integer, String>() {
            public String apply(Integer integer) {
                return integer + "";
            }
        };

        Function<String, Integer> stoi = new Function<String, Integer>() {
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        };

        Function<Integer, Integer> itoi = Functional.compose(stoi, itos);
        assertTrue(itoi.apply(3) == 3);
    }
}
