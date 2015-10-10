package zjhmale.monadj;

import org.junit.Test;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monad.base.Monad;
import zjhmale.monadj.monad.common.Either;
import zjhmale.monadj.monad.common.Maybe;
import zjhmale.monadj.monad.common.Reader;

import javax.naming.Context;

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

    @Test
    public void readerMonad() {
        final Reader<String, String> mock = Reader.reader(new Function<String, String>() {
            public String apply(final String s) {
                return s;
            }
        });
        final Reader<String, Boolean> mock2 = Reader.reader(new Function<String, Boolean>() {
            public Boolean apply(final String s) {
                return true;
            }
        });

        Reader<String, String> computation = (Reader<String, String>) mock.ask().bind(new Function<String, Monad<String>>() {
            public Monad<String> apply(final String ctx) {
                return mock.ret(ctx + ", Reader Monad");
            }
        });

        Function<String, Reader<String, String>> computation1 = new Function<String, Reader<String, String>>() {
            public Reader<String, String> apply(final String s) {
                return (Reader<String, String>) mock.ask().bind(new Function<String, Monad<String>>() {
                    public Monad<String> apply(final String ctx) {
                        return mock.ret(ctx + ", " + s);
                    }
                });
            }
        };

        Function<String, Reader<String, String>> computation2 = new Function<String, Reader<String, String>>() {
            public Reader<String, String> apply(final String s) {
                return (Reader<String, String>) mock.ask().bind(new Function<String, Monad<String>>() {
                    public Monad<String> apply(final String ctx) {
                         return mock.ret(ctx + ", " + s);
                    }
                });
            }
        };

        Function<String, Monad<String>> computation3 = new Function<String, Monad<String>>() {
            public Reader<String, String> apply(final String s) {
                return (Reader<String, String>) mock2.asks(new Function<String, Boolean>() {
                    public Boolean apply(final String ctx) {
                        return ctx.equals("Hello");
                    }
                }).bind(new Function<Boolean, Monad<String>>() {
                    public Monad<String> apply(Boolean aBoolean) {
                        return mock.ret(s + (aBoolean ? "!" : "."));
                    }
                });
            }
        };

        assertTrue(computation.runReader().apply("Hello").equals("Hello, Reader Monad"));
        assertTrue(computation1.apply("Reader Monad").runReader().apply("Hello").equals("Hello, Reader Monad"));
        assertTrue(((Reader<String, String>) computation2.apply("Reader Monad").bind(computation3)).runReader().apply("Hello").equals("Hello, Reader Monad!"));
        assertTrue(((Reader<String, String>) computation2.apply("Reader Monad").bind(computation3)).runReader().apply("Bye").equals("Bye, Reader Monad."));
    }
}
