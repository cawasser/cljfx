(ns e101-multiple-windows
  (:require [cljfx.api :as fx]
            [e101-support :as s]
            [e101-window :as w])
  (:import [javafx.stage Screen]))


(def app-db (atom {:description "some new text"
                   :color :yellow}))


(def width 300)
(def height 100)

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


(defn root [{:keys [description color]}]
  {:fx/type fx/ext-many
   :desc [{:fx/type     w/window
           :x           0
           :y           0
           :width       width
           :height      height
           :description description
           :content     s/text-content}
          {:fx/type w/window
           :x 400
           :y 0
           :width width
           :height height
           :color color
           :content s/color-content}]})

(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root)))


(fx/mount-renderer app-db renderer)




(comment
  (def content s/text-content)
  (def params {:fx/type     w/window
               :x           0
               :y           0
               :width       width
               :height      height
               :description "Top Left"
               :content     s/text-content})
  (merge {:fx/type content} (dissoc params :fx/type))


  (swap! app-db assoc :color :blue)
  (swap! app-db assoc :color :cyan)
  (swap! app-db assoc :color :yellow)


  (swap! app-db assoc :description "YELLOW")


  ())