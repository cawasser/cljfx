(ns e101-multiple-windows
  (:require [cljfx.api :as fx]
            [e101-support :as s])
  (:import [javafx.stage Screen]))


(def app-db (atom {:description "some new text"
                   :color :yellow}))



(defn window [{:keys [x y width height content] :as params}]
  (println "window" content)
  {:fx/type       :stage
   :always-on-top true
   :showing       true
   :x             x
   :y             y
   :width         width
   :height        height
   :scene         {:fx/type :scene
                   :root    (merge {:fx/type content}
                              (dissoc params :fx/type))}})


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
   :desc [{:fx/type     window
           :x           0
           :y           0
           :width       width
           :height      height
           :description description
           :content     s/text-content}
          {:fx/type window
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
  (def params {:fx/type     window
               :x           0
               :y           0
               :width       width
               :height      height
               :description "Top Left"
               :content     s/text-content})
  (merge {:fx/type content} (dissoc params :fx/type))




  ())