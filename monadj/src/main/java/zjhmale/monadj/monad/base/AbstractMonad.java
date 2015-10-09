package zjhmale.monadj.monad.base;

import zjhmale.monadj.function.Function;

/**
 * Created by zjh on 15/10/8.
 */
public abstract class AbstractMonad<A> implements Monad<A> {
    //这个抽象类里的bind和liftM函数会调用到的bind和ret函数是分别调用继承了这个抽象类的类中实现的bind和ret
    //比如maybemonad继承了这个abstractmonad那么在调用liftM的时候就会调用maybemonad自己的bind函数和ret函数
    public <B> Monad<B> bind(final Monad<B> b) {
        return bind(new Function<A, Monad<B>>() {
            public Monad<B> apply(final A a) {
                return b;
            }
        });
    }

    //:t liftM => Monad m => (a -> b) -> m a -> m b
    //liftM (\a -> a + 1) (Just 1) => Just 2
    public <B> Monad<B> liftM(final Function<A, B> f) {
        return bind(new Function<A, Monad<B>>() {
            public Monad<B> apply(final A a) {
                return ret(f.apply(a));
            }
        });
    }
}
