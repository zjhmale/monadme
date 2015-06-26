--带上下文的计算过程 具体例子可以看clojure文件中的例子 将上下文处理的细节封装在内部 让外部的代码保证一致性与简洁性

class Monad m where
    return :: a -> m a
    (>>=) :: m a -> (a -> m b) -> m b
    (>>) :: m a -> m b -> m b
    x >> y = x >>= \_ -> y

--return类似applicative functor中的pure函数 将一个值包裹在最小上下文中
--(>>=)也就是大名鼎鼎的bind函数 用来进行带上下文的计算 bind runs the monad m a, feeding the yielded a value into the function it received as an argument - any context carried by that monad will be taken into account.
--(>>)是bind函数中的回调忽略之前参数值的版本 成为then

--maybe monad 带有一个可能计算失败的上下文 加入一串计算中 某一处出现了失败 直接返回失败结果 而不继续进行下去了 防止系统崩溃

instance Monad Maybe where
    return x = Just x
    Nothing >>= f = Nothing
    Just x >>= f  = f x
    fail _ = Nothing

a = Just 9 >>= \x -> return (x*10) --Just 90
b = Nothing >>= \x -> return (x*10) --Nothing

--list monad 带有一个不确定结果的上下文

instance Monad [] where
    return x = [x]
    xs >>= f = concat (map f xs)
    fail _ = []

c = [3,4,5] >>= \x -> [x,-x] --[3,-3,4,-4,5,-5]
--just list comprehension
d = [1,2] >>= \n -> ['a','b'] >>= \ch -> return (n,ch) --[(1,'a'),(1,'b'),(2,'a'),(2,'b')]

--为了防止出现bind的嵌套影响可读性 haskell中有do notation语法糖来简化代码

routine :: Maybe Pole
routine = do
    start <- return (0,0)
    first <- landLeft 2 start
    Nothing
    second <- landRight 2 first
    landLeft 1 second

--上面的代码展开为bind嵌套为

routine = return (0, 0) >>=
            (\start -> landLeft 2 start >>=
                \first -> Nothing >>=
                    (\_ -> landRight 2 first >>=
                        (\_ second -> landLeft 1 second)))

--上面 do notation中的Nothing其实写得verbose一点就是_ <- Nothing

routine = return (0, 0) >>=
            (\start -> landLeft 2 start >>=
                \first -> Nothing >> landRight 2 first >>=
                    (\_ second -> landLeft 1 second))

--monad law

--Right unit

m >>= return = m --m is a monad value

--Left unit

return x >>= f = f x

--Associativity(结合律)

(m >>= f) >>= g = m >>= (\x -> f x >>= g)