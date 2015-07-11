import Data.Char

type Parser a = String -> [(a, String)]

--three primitive monadic parser combinator

result :: a -> Parser a
result v = \inp -> [(v, inp)]

zero :: Parser a
zero = \inp -> []

item :: Parser Char
item = \inp -> case inp of
                []     -> []
                (x:xs) -> [(x,xs)]

--seq :: Parser a -> Parser b -> Parser (a, b)
--p `seq` q = \inp -> [((v, w) inp') | (v, inp')  <- p inp
--                                   , (w, inp'') <- q inp']

parse :: Parser a -> String -> [(a, String)]
parse p inp = p inp

parse1 = parse (result 1) "abc"
parse2 = parse zero "abc"
parse3 = parse item ""
parse4 = parse item "abc"

--just the bind function and Parser is the monad
--sequance
(>>>=) :: Parser a -> (a -> Parser b) -> Parser b
p >>>= f = \inp -> case parse p inp of
                   [] -> []
                   [(v, out)] -> parse (f v) out

p :: Parser (Char, Char)
p = item >>>= (\x ->
               (item >>>= (\_ ->
                           (item >>>= (\y ->
                                       (result (x, y)))))))

parse5 = parse p "abcdef"
parse6 = parse p "ab"

--choice
(+++) :: Parser a -> Parser a -> Parser a
p +++ q = \inp -> case parse p inp of
                   [] -> parse q inp
                   [(v, out)] -> [(v, out)]

parse7 = parse (item +++ result 'd') "abc"
parse8 = parse (zero +++ result 'd') "abc"
parse9 = parse (zero +++ zero) "abc"

--satisfied the predicate p
sat :: (Char -> Bool) -> Parser Char
sat p = item >>>= (\x -> if p x then result x else zero)

digit :: Parser Char
digit = sat isDigit

lower :: Parser Char
lower = sat isLower

upper :: Parser Char
upper = sat isUpper

letter :: Parser Char
letter = sat isAlpha

alphanum :: Parser Char
alphanum = sat isAlphaNum

char :: Char -> Parser Char
char x = sat (==x)

parse10 = parse digit "123"
parse11 = parse digit "abc"
parse12 = parse (char 'a') "abc"
parse13 = parse (char 'a') "123"

string :: String -> Parser String
string [] = result []
string (x:xs) = (char x) >>>= (\_ -> (string xs) >>>= (\_ -> result (x:xs)))

parse14 = parse (string "abc") "abcdef"
parse15 = parse (string "abc") "ab1234"

--many just like * zero or more
--many1 just like + one or more
many :: Parser a -> Parser [a]
many p = many1 p +++ result []

many1 :: Parser a -> Parser [a]
many1 p = p >>>= (\v -> (many p) >>>= (\vs -> result (v:vs)))

parse16 = parse (many digit) "123abc"
parse17 = parse (many digit) "abcdef"
parse18 = parse (many1 digit) "abcdef"

--identity
ident :: Parser String
ident = lower >>>= (\x -> (many alphanum >>>= (\xs -> result (x:xs))))

nat :: Parser Int
nat = (many1 digit) >>>= (\xs -> result (read xs))

--() is a type. Its one value, (), happens to have the same name, but that's ok because the type and expression languages are separate. It's useful to have a type representing "no information" because, in context (e.g., of a monad or a container), it tells you that only the context is interesting.reflecting the fact that the details of spacing are not usually important.
space :: Parser ()
space = many (sat isSpace) >>>= (\_ -> result ())

parse19 = parse ident "abc def"
parse20 = parse nat "123 abc"
parse21 = parse space "     abc"

token :: Parser a -> Parser a
token p = space >>>= (\_ -> p >>>= (\v -> space >>>= (\_ -> result v)))

--Using token, it is now easy to define parsers that ignore spacing around identifiers, natural numbers, and special symbols
identifier :: Parser String
identifier = token ident

natural :: Parser Int
natural = token nat

symbol :: String -> Parser String
symbol xs = token $ string xs

pp :: Parser [Int]
pp = symbol "[" >>>= (\_ ->
                       natural >>>= (\n -> (many (symbol "," >>>= (\_ -> natural))) >>>= (\ns ->
                                                                                           symbol "]" >>>= (\_ -> result (n:ns)))))

parse22 = parse pp " [1, 2, 3] "
parse23 = parse pp "[1,2,]"

--Arithmetic expressions 算数表达式
expr :: Parser Int
expr = term >>>= (\t ->
                   (symbol "+" >>>= (\_ ->
                                      expr >>>= (\e -> result (t + e)))) +++ (result t))

term :: Parser Int
term = factor >>>= (\f ->
                     (symbol "*" >>>= (\_ ->
                                        term >>>= (\t -> result (f * t)))) +++ (result f))

factor :: Parser Int
factor = symbol "(" >>>= (\_ ->
                           expr >>>= (\e -> symbol ")" >>>= (\_ -> result e))) +++ natural

eval :: String -> Int
eval xs = case parse expr xs of
           [(n, [])] -> n
           [(_, out)] -> error ("unused input " ++ out)
           [] -> error "invalid input"

parse24 = eval "2*3+4"
parse25 = eval "2*(3+4)"
parse26 = eval "2 * (3 + 4)"
parse27 = eval "2*3-4"
parse28 = eval "-1"