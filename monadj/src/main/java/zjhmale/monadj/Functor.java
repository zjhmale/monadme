package zjhmale.monadj;

import zjhmale.monadj.function.Function;

/**
 * Created by zjh on 15/10/8.
 */
public interface Functor<A> {
    <B> Functor<B> fmap(final Function<A, B> f);
}
