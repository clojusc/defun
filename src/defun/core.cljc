(ns ^{:author "dennis <killme2008@gmail.com>"
      :doc "A macro to define Clojure functions with parameter pattern matching
            just like LFE based on `core.match`. Please see
            https://github.com/clojusc/defun"}
  defun.core
  (:require
    #?(:clj [clojure.core.match]
       :cljs [cljs.core.match :include-macros true])
    #?@(:clj [[clojure.tools.macro :refer [name-with-attributes]]
             [clojure.walk :refer [postwalk]]]))
  #?(:cljs (:require-macros [defun.core :refer [lambda flet defun defun-]])))

#?(:clj
   (defmacro if-cljs
     "Return then if we are generating cljs code and else for clj code.
     Source:
     http://blog.nberger.com.ar/blog/2015/09/18/more-portable-complex-macro-musing/"
     [then else]
     (if (boolean (:ns &env)) then else)))

#?(:clj
   (defmacro match
     [& args]
     `(if-cljs (cljs.core.match/match ~@args)
               (clojure.core.match/match ~@args))))

#?(:clj
   (defmacro lambda
     "Defines a function just like clojure.core/fn with parameter pattern matching."
     [& sigs]
     {:forms '[(lambda name? [params* ] exprs*) (lambda name? ([params* ] exprs*)+)]}
     (let [name (when(symbol? (first sigs)) (first sigs))
           sigs (if name (next sigs) sigs)
           sigs (if (vector? (first sigs))
                  (list sigs)
                  (if (seq? (first sigs))
                    sigs
                    ;; Assume single arity syntax
                    (throw (IllegalArgumentException.
                            (if (seq sigs)
                              (str "Parameter declaration "
                                   (first sigs)
                                   " should be a vector")
                              (str "Parameter declaration missing"))))))
           sigs (postwalk
                 (fn [form]
                   (if (and (list? form) (= 'recur (first form)))
                     (list 'recur (cons 'vector (next form)))
                     form))
                 sigs)
           sigs `([& args#]
                  (match (vec args#)
                         ~@(mapcat
                            (fn [[m & more]]
                              [m (cons 'do more)])
                            sigs)))]
       (list* 'fn (if name
                    (cons name sigs)
                    sigs)))))

#?(:clj
   (defmacro flet
     "letfn with parameter pattern matching."
     {:forms '[(flet [fnspecs*] exprs*)]}
     [fnspecs & body]
     `(letfn* ~(vec (interleave (map first fnspecs)
                                (map #(cons `lambda %) fnspecs)))
              ~@body)))

#?(:clj
   (defmacro defun
     "Define a function just like clojure.core/defn, but using core.match to
     match parameters."
     [name & fdecl]
     (let [[name body] (name-with-attributes name fdecl)
           body (if (vector? (first body))
                  (list body)
                  body)
           name (vary-meta name assoc :argslist (list 'quote (@#'clojure.core/sigs body)))]
       `(def ~name (lambda ~@body)))))

#?(:clj
   (defmacro defun-
     "same as defun, yielding non-public def"
     [name & decls]
     (list* `defun (vary-meta name assoc :private true) decls)))
