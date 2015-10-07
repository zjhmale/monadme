package zjhmale.monadj.function;

/**
 * Created by zjh on 15/10/7.
 * A function is something that takes A and produces B by a process called application of function.
 * Basically any object that implements that interface can be considered a valid function.
 */
public interface Function<A, B> {
    B apply(final A a);
}
