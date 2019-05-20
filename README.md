# defun

[![Build Status][travis-badge]][travis]
[![Dependencies Status][deps-badge]][deps]
[![Clojars Project][clojars-badge]][clojars]
[![Tag][tag-badge]][tag]
[![Clojure version][clojure-v]](project.clj)

*A Clojure macro supporting functions with pattern matching heads a la LFE*

## About

The macros defined by this project allow one to write code a little more
closely to LFE (a Lisp that runs and inter-operates with Erlang and other BEAM
languages).

Note that this repo was originally cloned from [here](https://github.com/killme2008/defun).


## Usage

### Requiring

Require `defun.core` in clojure:

```clj
(require '[defun.core :refer [defun]])
```

Or `refer-macros` in clojurescript:

```cljs
(ns cljs-test
  (:require  [defun.core :refer-macros [defun]])
(enable-console-print!)
```

### Patterns

```clj
(defun say-hi
  ([:dennis] "Dennis, there's some lovely filth down here!")
  ([:sir-robin] "That's ... that's enough music for now lads, there's dirty work afoot.")
  ([:zoot] "No, I am Zoot's identical twin sister, Dingo.")
  ([other] (str  other ", look at the BOOOOONES!")))
```

Then calling `say-hi` with different names:

```clj
(say-hi :dennis)
;;  "Dennis, there's some lovely filth down here!"
(say-hi :sir-robin)
;;  "That's ... that's enough music for now lads, there's dirty work afoot."
(say-hi :zoot)
;;  "No, I am Zoot's identical twin sister, Dingo."
(say-hi "Tim")
;;  "Tim, look at the BOOOOONES!"
```

We can use all patterns that supported by [core.match](https://github.com/clojure/core.match/wiki/Basic-usage).

For example, matching literals:

```clj
(defun test1
    ([true false] 1)
    ([true true] 2)
    ([false true] 3)
    ([false false] 4))

(test1 true true)
;; 2
(test1 false false)
;; 4
```

Matching sequence:

```clj
(defun test2
    ([([1] :seq)] :a0)
    ([([1 2] :seq)] :a1)
    ([([1 2 nil nil nil] :seq)] :a2))

(test2 [1 2 nil nil nil])
;; a2
```

Matching vector:

```clj
(defun test3
    ([[_ _ 2]] :a0)
    ([[1 1 3]] :a1)
    ([[1 2 3]] :a2))

(test3 [1 2 3])
;; :a2
```


### Recursion

Let's move on, what about define a recursive function? That's easy too:

```clj
(defun count-down
  ([0] (println "Reach zero!"))
  ([n] (println n)
     (recur (dec n))))
```

Invoke it:

```clj
(count-down 5)
;;5
;;4
;;3
;;2
;;1
;;Reach zero!
nil
```

An accumulator from zero to number `n`:

```clj
(defun accum
  ([0 ret] ret)
  ([n ret] (recur (dec n) (+ n ret)))
  ([n] (recur n 0)))

(accum 100)
;;5050
```

A fibonacci function:

```clj
(defun fib
    ([0] 0)
    ([1] 1)
    ([n] (+ (fib (- n 1)) (fib (- n 2)))))
```

Output:

```clj
(fib 10)
;; 55
```


### Guards

Added a guard function to parameters:

```clj
(defun funny
  ([(N :guard #(= 42 %))] true)
  ([_] false))

(funny 42)
;;  true
(funny 43)
;; false
```

Another function to detect if longitude and latitude values are both valid:

```clj
(defun valid-geopoint?
    ([(_ :guard #(and (> % -180) (< % 180)))
      (_ :guard #(and (> % -90) (< % 90)))] true)
    ([_ _] false))

(valid-geopoint? 30 30)
;; true
(valid-geopoint? -181 30)
;; false
```

### Compatibility with defn

Try to define function just like `defn`:

```clj
(defun hello
   "hello world"
   [name] (str "hello," name))
(hello "defun")
;; "hello,defun"
```

`defun` also supports variadic arguments, doc, metadata etc.

Additionally, a `defun-` is provided for creating private functions a la `defn-`.


### fun and letfun

Since 0.2.0, there are two new macros: `fun` and `letfun`, just like `clojure.core/fn` and `clojure.core/letfn`

``` clojure
((fun
    ([[_ _ 2]] :a0)
    ([[1 1 3]] :a1)
    ([[1 2 3]] :a2))
  [1 2 3])
;; :a2

(letfun [(test3 ([[_ _ 2]] :a0)
                    ([[1 1 3]] :a1)
                    ([[1 2 3]] :a2))]
  (test3 [1 2 3]))
;; :a2
```


## Criterium benchmarking

Uses the above function `accum` compared with a normal clojure function:

```clj
(require '[criterium.core :refer [bench]])

(defn accum-defn
    ([n] (accum-defn 0 n))
    ([ret n] (if (= n 0) ret (recur (+ n ret) (dec n)))))

(defun accum-defun
  ([0 ret] ret)
  ([n ret] (recur (dec n) (+ n ret)))
  ([n] (recur n 0)))

(bench (accum-defn 10000))
;;Evaluation count : 210480 in 60 samples of 3508 calls.
;;             Execution time mean : 281.095682 µs
;;    Execution time std-deviation : 2.526939 µs
;;   Execution time lower quantile : 277.691624 µs ( 2.5%)
;;   Execution time upper quantile : 286.618249 µs (97.5%)
;;                   Overhead used : 1.648269 ns

(bench (accum-defun 10000))
;;Evaluation count : 26820 in 60 samples of 447 calls.
;;             Execution time mean : 2.253477 ms
;;    Execution time std-deviation : 13.082041 µs
;;   Execution time lower quantile : 2.235795 ms ( 2.5%)
;;   Execution time upper quantile : 2.281963 ms (97.5%)
;;                   Overhead used : 1.648269 ns
```

`accum-defn` is much faster than `accum-defun` ... pattern-matching in Clojure
does have a cost; trade-offs should be analyzed before using.


## License

Copyright © 2014 [Dennis Zhuang](mailto:killme2008@gmail.com)

Distributed under the Eclipse Public License either version 1.0 or (at

your option) any later version.


<!-- Named page links below: /-->

[travis]: https://travis-ci.org/clojusc/defun
[travis-badge]: https://travis-ci.org/clojusc/defun.png?branch=master
[deps]: http://jarkeeper.com/clojusc/defun
[deps-badge]: http://jarkeeper.com/clojusc/defun/status.svg
[tag-badge]: https://img.shields.io/github/tag/clojusc/defun.svg
[tag]: https://github.com/clojusc/defun/tags
[clojure-v]: https://img.shields.io/badge/clojure-1.10.0-blue.svg
[clojars]: https://clojars.org/clojusc/defun
[clojars-badge]: https://img.shields.io/clojars/v/clojusc/defun.svg
