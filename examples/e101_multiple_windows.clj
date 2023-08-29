(ns e101-multiple-windows
  (:require [cljfx.api :as fx])
  (:import [javafx.stage Screen]))


(defn text-description [{:keys [x y width height description]}]
  {:fx/type :v-box
   :alignment :center
   :children [{:fx/type :label
               :text (str "Window at [" x ", " y "] "
                       "with size " width "x" height)}
              {:fx/type :label
               :text (or description "DEFAULT TEXT")}]})


(defn color-content [{:keys [color]}]
  {:fx/type :pane
   :style {:-fx-background-color color}})


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

(fx/on-fx-thread
  (fx/create-component
    {:fx/type fx/ext-many
     :desc [{:fx/type window
             :x 0
             :y 0
             :width width
             :height height
             :description "Top Left"
             :content text-description}
            {:fx/type window
             :x 400
             :y 0
             :width width
             :height height
             :color :green
             :content color-content}]}))



(comment
  (def content text-description)
  (def params {:fx/type window
               :x 0
               :y 0
               :width width
               :height height
               :description "Top Left"
               :content text-description})
  (merge {:fx/type content} (dissoc params :fx/type))




  ())