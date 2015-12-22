-- 单纯的functor没有办法做到将一个函数也放在最小上下文中然后将包裹在上下文中的函数作用在其他functor上 所以出现了applicative functor

class (Functor f) => Applicative f where
    pure :: a -> f a
    (<*>) :: f (a -> b) -> f a -> f b
    (<$>) :: (Functor f) => (a -> b) -> f a -> f b
    f <$> x = fmap f x

-- pure用来将一个类型包在一个applicative functor的上下文中
-- <*>用来将包裹在上下文中的函数作用在其他的functor上 通常叫做apply
-- <$>就是fmap的语法糖 用来和<*>一起组成lift操作

--Maybe Applicative Functor
instance Applicative Maybe where
    pure = Just
    Nothing <*> _ = Nothing
    (Just f) <*> something = fmap f something

a = Just (+3) <*> Just 9 --Just 12
b = pure (+) <*> Just 3 <*> Just 5 --Just 8

-- <*>在List Applicative Functor中就是list comprehension
instance Applicative [] where
    pure x = [x]
    fs <*> xs = [f x | f <- fs, x <- xs]

c = (*0),(+100),(^2)] <*> [1,2,3] --[0,0,0,101,102,103,1,4,9]
d = [(+),(*)] <*> [1,2] <*> [3,4]  --[4,5,5,6,3,4,6,8]
e = (++) <$> ["ha","heh","hmm"] <*> ["?","!","."] --["ha?","ha!","ha.","heh?","heh!","heh.","hmm?","hmm!","hmm."]

-- 所以下面两个表达式的值是一样的 都是[16,20,22,40,50,55,80,100,110]
f = (*) <$> [2,5,10] <*> [8,10,11]
g = [ x*y | x <- [2,5,10], y <- [8,10,11]]

-- applicative functor中还有一个非常有用的函数

liftA2 :: (Applicative f) => (a -> b -> c) -> f a -> f b -> f c
liftA2 f a b = f <$> a <*> b

h = liftA2 (:) (Just 3) (Just [4]) --Just [3,4]
i = (:) <$> Just 3 <*> Just [4] --Just [3,4]

-- 所以 上面的list comprehension可以用lift重写

import Control.Applicative
h = liftA2 (*) [2,5,10] [8,10,11] --[16,20,22,40,50,55,80,100,110]

-- applicative functor law

-- Identity

pure f <*> v = fmap f v

-- Composition

pure (.) <*> u <*> v <*> w = u <*> (v <*> w)

-- Homomorphism

pure f <*> pure x = pure (f x)

-- Interchange

u <*> pure y = pure ($ y) <*> u

-- $ in haskell
-- 一种用来代替括号
-- 一种是用来yield住参数将yield住的参数传给传入的lambda 详细可以看clojure版的$来解释这个奇葩的概念


(x+) = \y -> x + y
(+y) = \x -> x + y
(+) = \x y -> x + y

-- $ 运算符也一样

($ x) = \y -> y x

:t ($)
($) :: (a -> b) -> a -> b

(\x -> x * 2) $ 10 --20

:t ($ 10)
($ 10) :: Num a => (a -> b) -> b

($ 10) (\x -> x * 2) --20