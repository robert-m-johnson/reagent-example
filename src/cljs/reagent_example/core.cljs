(ns reagent-example.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :refer [GET POST]]
              [ajax.edn :refer [edn-response-format]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views

;;(defn home-page []
;;  [:div [:h2 "Welcome to the about page"]
;;   [:div [:a {:href "/about"} "go to about page"]]])

;;(defn about-page []
;;  [:div [:h2 "About reagent-example"]
;;   [:div [:a {:href "/"} "go to the home page"]]])

;;(defn current-page []
;;  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

;;(secretary/defroute "/" []
;;  (session/put! :current-page #'home-page))

;;(secretary/defroute "/about" []
;;  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(def nam (atom "Brian"))

(defn rendered-timestamp []
  "foo")

(defn greeter-component []
  [:div "Hello " @nam])

(defn timer-component []
  (let [seconds-elapsed (reagent/atom 0)]     ;; setup, and local state
    (fn []        ;; inner, render function is returned
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div "Seconds Elapsed: " @seconds-elapsed])))

(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])

(def frameworks (atom [{:name "Regeant"}]))

(defn framework-table-row [framework]
  [:tr
   [:td (:name framework)]])

(defn frameworks-table []
  [:table
   [:tbody
    (map framework-table-row @frameworks)]])

(defn layout []
  [:div
   [:div "Reagent example"]
   [frameworks-table]])

(defn mount-root []
  (reagent/render [layout] (.getElementById js/document "app")))

;; Ajax
(defn handler [response]
  (.log js/console (str response)))

(defn fetch-data []
  (GET "/frameworks"
      {:response-format :transit
       :handler (fn [response]
                  (.log js/console (str response))
                  (reset! frameworks response)
                  )
       }))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root)
  (fetch-data)
  )
