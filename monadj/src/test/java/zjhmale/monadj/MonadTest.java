package zjhmale.monadj;

import org.junit.Test;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monad.base.Monad;
import zjhmale.monadj.monad.common.Maybe;
import static org.junit.Assert.*;

/**
 * Created by zjh on 15/10/9.
 */
public class MonadTest {
    @Test
    public void maybeMonad() {
        Maybe<Integer> a = Maybe.just(1);
        Maybe<Integer> b = (Maybe<Integer>) a.bind(new Function<Integer, Monad<Integer>>() {
            public Monad<Integer> apply(Integer integer) {
                return Maybe.just(integer + 2);
            }
        });
        Maybe<Integer> c = (Maybe<Integer>) a.liftM(new Function<Integer, Integer>() {
            public Integer apply(Integer integer) {
                return integer + 2;
            }
        });
        Maybe<Integer> d = (Maybe<Integer>) a.bind(b);
        assertTrue(b.getValue() == 3);
        assertTrue(c.getValue() == 3);
        assertTrue(d.getValue() == 3);
    }
}
