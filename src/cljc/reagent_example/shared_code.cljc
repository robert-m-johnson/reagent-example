(ns reagent-example.shared-code)

(defn get-message []
  (let [location #?(:clj "server"
                    :cljs "client")]
    (str "This is some text defined by the " location)))
