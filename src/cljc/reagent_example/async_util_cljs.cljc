(ns reagent-example.async-util-cljs)

(defmacro go-looper [body]
  `(let [stop-chan# (cljs.core.async/chan)]
     (cljs.core.async.macros/go-loop []
       (when (cljs.core.async.macros/alt!
               stop-chan# false
               :default :keep-alive)
         ~body
         (recur)))
     stop-chan#))
