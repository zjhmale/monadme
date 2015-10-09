package zjhmale.monadj.monad.common;

import zjhmale.monadj.Functor;
import zjhmale.monadj.Identity;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monad.base.AbstractMonad;
import zjhmale.monadj.monad.base.Monad;

/**
 * Created by zjh on 15/10/9.
 */

/**
 * newtype Reader r a = Reader {  runReader :: r -> a }
 * 前一个Reader是类型构造器 后一个Reader是值构造器 构造一个符合类型的值
 * <p/>
 * instance Monad (Reader ((->) r)) where
 * return x = Reader $ \_ -> x
 * --m is a reader monad and runReader m is just get the unwrapped function
 * m >>= k = Reader $ \r -> runReader (k (runReader m r)) r
 * <p/>
 * 所以根据这里的定义[Reader r a]类型其实应该是一个[Monad a]类型 因为类型参数只是a而已 这个和eithermonad是一样的
 * <p/>
 * Reader<E, A> E => env 也就是传入的上下文 A => computation
 */
public class Reader<E, A> extends AbstractMonad<A> {
    private Function<E, A> f;

    public static <E, A> Reader<E, A> reader(final Function<E, A> f) {
        if (f == null) {
            throw new IllegalArgumentException("argument has no value");
        } else {
            return new Reader<E, A>(f);
        }
    }

    private Reader(final Function<E, A> f) {
        this.f = f;
    }

    public Function<E, A> runReader() {
        return this.f;
    }

    public Reader<A, A> ask() {
        return new Reader<A, A>((Function<A, A>) Identity.identity());
    }

    public Reader<E, A> asks(Function<E, A> f) {
        return new Reader<E, A>(f);
    }

    public <B> Monad<B> bind(final Function<A, Monad<B>> f) {
        return new Reader<E, B>(
                new Function<E, B>() {
                    public B apply(final E e) {
                        return ((Reader<E, B>) f.apply(runReader().apply(e))).runReader().apply(e);
                    }
                });
    }

    public <B> Monad<B> ret(final B b) {
        return new Reader<E, B>(
                new Function<E, B>() {
                    public B apply(final E e) {
                        return b;
                    }
                });
    }

    public <B> Functor<B> fmap(final Function<A, B> f) {
        return new Reader<E, B>(
                new Function<E, B>() {
                    public B apply(final E e) {
                        return f.apply(runReader().apply(e));
                    }
                });
    }
}
