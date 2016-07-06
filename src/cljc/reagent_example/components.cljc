(ns reagent-example.components)

(defmacro table [source index-key config]
  (let [headers# (map first config)
        keys# (map second config)
        header-row# (into [:tr] (map #(vector :th %) headers#))
        item-row# (into [:tr] (map #(vector :td (list % 'item)) keys#))]
    `[:table
      [:thead ~header-row#]
      [:tbody
       (let [~'src (deref ~source)]
         (for [~'item ~'src]
           (with-meta
             ~item-row#
             {:key (~index-key ~'item)})))]]))
