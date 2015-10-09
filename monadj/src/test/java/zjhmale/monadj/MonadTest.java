package zjhmale.monadj;

import org.junit.Test;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monad.base.Monad;
import zjhmale.monadj.monad.common.Either;
import zjhmale.monadj.monad.common.Maybe;

import static org.junit.Assert.assertTrue;

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
        Maybe<Integer> e = (Maybe<Integer>) a.fmap(new Function<Integer, Integer>() {
            public Integer apply(Integer integer) {
                return integer + 2;
            }
        });

        assertTrue(b.getValue() == 3);
        assertTrue(c.getValue() == 3);
        assertTrue(d.getValue() == 3);
        assertTrue(e.getValue() == 3);
    }

    @Test
    public void eitherMonad() {
        Either<String, Integer> l = Either.left("computation failed");
        Either<String, Integer> r = Either.right(3);

        Function<Integer, Monad<Integer>> f = new Function<Integer, Monad<Integer>>() {
            public Monad<Integer> apply(Integer integer) {
                return Either.right(integer + 2);
            }
        };

        Function<Integer, Integer> ff = new Function<Integer, Integer>() {
            public Integer apply(Integer integer) {
                return integer + 2;
            }
        };

        Either<String, Integer> ll = (Either<String, Integer>) l.bind(f);
        Either<String, Integer> lll = (Either<String, Integer>) l.fmap(ff);

        Either<String, Integer> rr = (Either<String, Integer>) r.bind(f);

        Either<String, Integer> rrr = (Either<String, Integer>) r.fmap(ff);

        //eithermonad和maybemonad一样 在计算失败之后就会直接把失败的信息或者None返回 不再进行后续的计算了
        assertTrue(ll.getLeft().equals(l.getLeft()));
        assertTrue(lll.getLeft().equals(l.getLeft()));
        assertTrue(rr.getRight() == 5);
        assertTrue(rrr.getRight() == 5);
    }
}
