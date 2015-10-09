package zjhmale.monadj.monad.base;

import zjhmale.monadj.Functor;
import zjhmale.monadj.function.Function;

/**
 * Created by zjh on 15/10/8.
 */
public interface Monad<A> extends Functor<A> {
    //the monadic bind >>=
    <B> Monad<B> bind(final Function<A, Monad<B>> f);

    <B> Monad<B> ret(final B b);
}
