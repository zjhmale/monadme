## Monad

```rust
interface Monad<A>
  fn bind(A,A -> Monad<B>) -> Monad<B>
  fn return(A) -> Monad<A>
end

## Maybe Monad

type Maybe<A> = Some A | Nothing

fn divide(Int top, Int bottom)
  if(bottom == 0)
    return Nothing
  else
    return Some (top/bottom)
  end
end
```

```shell
> divide(3,2)
 Some 1.5
 > divide(0,2)
 Some 0
 > divide(2,0)
 Nothing
 > divide(0,0)
 Nothing
 > divide(divide(1,2),3)
 Error: divide expected an Int but received a Maybe<Int>.
```

```rust
implementation of Monad<A> for Maybe<A>
  fn return(a:A) -> Maybe<A> = Some a
  fn bind(ma:Maybe<A>,f:A->Maybe<B>) -> Maybe<B> =
    if ma == Nothing
      Nothing
    else
      (Some x) = ma; //x is the value inside ma's `Some`.
      f(x)
    end
  end
end
```

```shell
> bind(divide(1,2), fn x -> divide(x,3))
Some 0.167
> bind(divide(1,0), fn x -> divide(x,9))
Nothing
> bind(divide(1,2), fn x -> divide(x,0))
Nothing
```

* we can chain the operation and avoid nullpointer exception with maybe monad i.e. cps
* 其实判断空指针的操作被隐藏在了bind内部 外部任然可以很流畅的将操作chain在一起

## List Monad

```rust
implementation of Monad<A> for List<A>
  fn return(a:A) -> List<A> = [a]
  fn bind(ma:List<A>,f:A->List<B>) -> List<B> =
    result = []
    for elem in ma
      result = result ++ f(ma) //append the result of each call to our output
    end
    result
  end
end
```

## Async Monad

```rust
//read in a file
Future<String> foo = read_file("./hello_world.txt");
//when we've read in the file, read in the file it names
Future<String> bar = bind(foo,fn(String s){read_file(s)});
//just wait for the contents of that second file
String output = wait(bar);
//print out the contents of the second file.
println(output);
```

* whenever you call bind or wait in the async monad, you yield, allowing the scheduler to switch tasks. This means that other work is being done while you wait for data
* 因为在网络IO 磁盘IO操作时 需要等待套接字等组建的初始化 建立连接 这些时候如果不使用非阻塞架构会需要白白浪费CPU时间去等待初始化完成 再继续后续工作 如果使用非阻塞架构 这些时间完全可以让CPU去调度其他任务
