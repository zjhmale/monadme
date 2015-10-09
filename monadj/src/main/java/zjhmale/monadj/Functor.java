package zjhmale.monadj;

import zjhmale.monadj.function.Function;

/**
 * Created by zjh on 15/10/8.
 */
public interface Functor<A> {
    //:t fmap => Functor f => (a -> b) -> f a -> f b
    <B> Functor<B> fmap(final Function<A, B> f);
}
