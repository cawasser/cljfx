(ns e101a-multiple-windows-app-db
  (:require [cljfx.api :as fx]
            [e101a-support :as s]
            [e101a-window :as w]
            [e101a-state :as state]
            [e101a-description :as d]
            [e101a-color :as c]
            [e101a-table :as t])
  (:import [javafx.stage Screen]))





(def width 600)
(def height 300)

(def bottom
  (-> (Screen/getPrimary)
    .getBounds
    .getHeight
    (- height)))

(def right
  (-> (Screen/getPrimary)
    .getBounds
    .getWidth
    (- width)))



(defn root [_]
  {:fx/type fx/ext-many
   :desc    [{:fx/type d/window
              :title "Text"
              :x 0 :y 0 :width width :height height}
             {:fx/type c/window
              :title "Color"
              :x 300 :y 300 :width width :height height}
             {:fx/type t/window
              :title "Table View"
              :x 600 :y 600 :width width :height height}]})


(def renderer
  (fx/create-renderer
    :middleware (comp
                  fx/wrap-context-desc
                  (fx/wrap-map-desc (fn [_] {:fx/type root})))
    :opts {:fx.opt/type->lifecycle   #(or (fx/keyword->lifecycle %)
                                        (fx/fn->lifecycle-with-context %))
           :fx.opt/map-event-handler state/event-handler}))


(fx/mount-renderer state/app-db renderer)




(comment
  (def content s/text-content)

  ())
