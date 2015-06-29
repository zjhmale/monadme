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

--reader monad is just wrap a binary function context
--a reader monad in haskell

newtype Reader r a = Reader {  runReader :: r -> a }

ask = Reader $ \x -> x -- or just identity function

instance Monad (Reader ((->) r)) where
    return x = Reader $ \_ -> x
    --m is a reader monad and runReader m is just get the unwrapped function
    m >>= k = Reader $ \r -> runReader (k (runReader m r)) r

greeter :: Reader String String
greeter = do
    name <- ask
    return ("hello, " ++ name ++ "!")

--expand the do notation
ask >>= (\name -> return ("hello, " ++ name ++ "!"))
--runReader ask => \x -> x
Reader $ \r -> runReader ((\name -> return ("hello, " ++ name ++ "!")) (runReader ask r)) r

runReader greeter $ "adit"
--=> "hello, adit!"

runReader (Reader $ \r -> runReader ((\name -> return ("hello, " ++ name ++ "!")) (runReader ask r)) r) "adit"
(\r -> runReader ((\name -> return ("hello, " ++ name ++ "!")) (runReader ask r)) r) "adit"
runReader ((\name -> return ("hello, " ++ name ++ "!")) (runReader ask "adit")) "adit"
runReader ((\name -> return ("hello, " ++ name ++ "!")) "adit") "adit"
runReader return ("hello, " ++ "adit" ++ "!") "adit"
(\_ -> ("hello, " ++ "adit" ++ "!")) "adit"
"hello, adit!"

--State Monad is similiar to reader monad
--The State monad is the Reader monad’s more impressionable best friend

--a是当前值的类型 s是当前状态的类型 内部包装的是一个接受一个状态返回一个值和新的状态的函数
newtype State s a = State { runState :: s -> (a, s) }

instance Monad (State s) where
    return x = State $ \s -> (x, s)
    m >>= k = State $ \s -> let (a, st) = runState m s
                             in runState (k a) st
    --yet another bind definition use pattern match not use runState function
    (State h) >>= f = State $ \s -> let (a, newState) = h s
                                        (State g) = f a
                                    in g newState

--state monad 的bind的实现乍一看有一点烧脑 其实思路很简单
--就是返回一个包在State上下文中接受一个状态值 返回的是先将传入的状态作用在旧的State monad包裹的函数上
--得到值和状态 然后将得到的值传入新的函数 最后将之前得到的状态传给新得到的状态函数 得到最终的新的值和状态

get = State $ \s -> (s, s) --获取当前的状态作为结果
put newState = State $ \s -> ((), newState) --创建一个新的带状态函数

greeter :: State String String
greeter = do
    name <- get
    put "tintin"
    return ("hello, " ++ name ++ "!")

--expand the do notation

get >>= (\name -> put "tintin" >>= (\_ -> return ("hello, " ++ name ++ "!")))
State $ \s -> let (a, st) = (\s -> (s, s)) s
                in runState ((\name -> put "tintin" >>= (\_ -> return ("hello, " ++ name ++ "!"))) a) st
State $ \s -> let (a, st) = (s, s)
                in runState ((\name -> put "tintin" >>= (\_ -> return ("hello, " ++ name ++ "!"))) a) st
State $ \s -> runState ((\name -> put "tintin" >>= (\_ -> return ("hello, " ++ name ++ "!"))) s) s
State $ \s -> runState put "tintin" >>= (\_ -> return ("hello, " ++ s ++ "!")) s
State $ \s -> runState (State $ \s1 -> let (a, st) = (\s -> ((), "tintin") s1)
                                        in runState (\_ -> return ("hello, " ++ s ++ "!")) a st) s
State $ \s -> runState (State $ \s1 -> let (a, st) = ((), "tintin")
                                        in runState (\_ -> return ("hello, " ++ s ++ "!")) a st) s
State $ \s -> runState (State $ \s1 -> runState (\_ -> return ("hello, " ++ s ++ "!")) () "tintin") s
State $ \s -> runState (State $ \s1 -> runState return ("hello, " ++ s ++ "!") "tintin") s
State $ \s -> runState (State $ \s1 -> \s2 -> ("hello, " ++ s ++ "!", s2) "tintin") s
State $ \s -> runState (State $ \s1 -> ("hello, " ++ s ++ "!", "tintin")) s
State $ \s -> \s1 -> ("hello, " ++ s ++ "!", "tintin") s
State $ \s -> ("hello, " ++ s ++ "!", "tintin")

runState greeter $ "adit" --((runState greeter) "adit") 要这样理解这个表达式 先获得等待状态传入的函数 在作用在外部的状态上
(\s -> ("hello, " ++ s ++ "!", "tintin")) "adit"
--=> ("hello, adit!", "tintin") "tintin" is just the current state

instance Monad ((->) r) where
    return x = \_ -> x
    h >>= f = \w -> f (h w) w

import Control.Monad.Instances

addStuff :: Int -> Int
addStuff = do
    a <- (*2)
    b <- (+10)
    return (a+b)

--化简展开会很复杂
(*2) >>= (\a -> (+10) >>= (\b -> (\_ -> return (a+b)))
\w -> (\a -> (+10) >>= (\b -> (\_ -> return (a+b)))) ((*2) w) w
\w -> (\a -> (\t -> (\b -> (\_ -> return (a+b))) ((+10) t) t)) ((*2) w) w

addStuff 3 -- (3*2) + (3+10) -> 19

--will expand to
((\w -> (\a -> (\t -> (\b -> (\_ -> return (a+b))) ((+10) t) t)) ((*2) w) w) 3)
\3 -> (\a -> (\t -> (\b -> (\_ -> return (a+b))) ((+10) t) t)) ((*2) 3) 3
(\a -> (\t -> (\b -> (\_ -> return (a+b))) ((+10) t) t)) 6 3
(\t -> (\b -> (\_ -> return (6+b))) ((+10) t) t) 3
\b -> (\_ -> return (6+b))) 13 3
(\_ -> return (6+13)) 3
return 19
--just get
19

--the writer monad

newtype Writer w a = Writer { runWriter :: (a, w) }

--runWriter just get the tuple from a Writer
--mempty是由w这个Monoid类型确定的 比如Maybe List

instance (Monoid w) => Monad (Writer w) where
    return x = Writer (x, mempty)
    (Writer (x, v)) >>= f = let (Writer (y, vt)) = f x in Writer (y, v `mappend` vt)

import Control.Monad.Writer

logNumber :: Int -> Writer [String] Int
logNumber x = writer (x, ["Got number: " ++ show x])

--tell implementation for my own

tell :: [String] -> Writer [String] Int
tell msg = writer (0, msg)

multWithLog :: Writer [String] Int
multWithLog = do
    a <- logNumber 3
    b <- logNumber 5
    tell ["Gonna multiply these two"]
    return (a*b)

--just expand this nested monad

logNumber 3 >>= (\a -> logNumber 5 >>= (\b -> tell ["Gonna multiply these two"] >>= (\_ -> return (a*b))))

let (Writer (y, vt)) =
    logNumber 5 >>= (\b -> tell ["Gonna multiply these two"] >>= (\_ -> return (3*b)))
    in Writer (y, "Got number: 3" `mappend` vt)

let (Writer (y, vt)) =
    (let (Writer (y1, vt1)) =
        tell ["Gonna multiply these two"] >>= (\_ -> return (3*5))
        in Writer (y1, "Got number: 5" `mappend` vt1))
    in Writer (y, "Got number: 3" `mappend` vt)

let (Writer (y, vt)) =
    (let (Writer (y1, vt1)) =
        (let (Writer (y2, vt2)) =
            return (3*5)
            in Writer (y2, ["Gonna multiply these two"] `mappend` vt2))
        in Writer (y1, ["Got number: 5"] `mappend` vt1))
    in Writer (y, ["Got number: 3"] `mappend` vt)

y2 = 15 vt2 = mempty :: [String]
y1 = 15 vt1 = ["Gonna multiply these two"]
y = 15 vt = ["Got number: 5", "Gonna multiply these two"]

-- so the final result is

Writer (15, ["Got number: 3", "Got number: 5", "Gonna multiply these two"])

--use runWriter to get the unwrapped tuple value

runWriter multWithLog --(15, ["Got number: 3", "Got number: 5", "Gonna multiply these two"])