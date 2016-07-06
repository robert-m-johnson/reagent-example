(ns reagent-example.components)

(defn- item-row-fn [index-key keys item-sym]
  (into [:tr] (map #(vector :td (list % item-sym)) keys)))

(defmacro table [source index-key config]
  (let [headers (map first config)
        keys (map second config)
        header-row (into [:tr] (map #(vector :th %) headers))
        item-sym (gensym)
        item-row (item-row-fn index-key keys item-sym)]
    `[:table
      [:thead ~header-row]
      [:tbody
       (let [src# (deref ~source)]
         (for [~item-sym src#]
           (with-meta
             ~item-row
             {:key (~index-key ~item-sym)})))]]))
