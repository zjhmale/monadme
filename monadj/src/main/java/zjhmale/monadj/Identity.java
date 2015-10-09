package zjhmale.monadj;

import zjhmale.monadj.function.Function;

/**
 * Created by zjh on 15/10/9.
 */

//id :: a -> a, f . id = id . f = f
public class Identity<A> implements Function<A, A> {
    public static final Identity identity = new Identity();

    public static <A> Identity<A> identity() {
        return (Identity<A>) identity;
    }

    public A apply(A a) {
        return a;
    }
}
