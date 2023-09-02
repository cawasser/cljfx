(ns e101a-color
  (:require [cljfx.api :as fx]
            [e101a-support :as s]
            [e101a-window :as w]))



(defn window [{:keys [title x y width height fx/context]}]
  (let [color (fx/sub-val context :color)]
    {:fx/type w/window
     :title   title
     :x       x
     :y       y
     :width   width
     :height  height
     :content {:fx/type s/color-content
               :color   color
               :width   width
               :height  height}}))




(comment
  (def context @e101a-state/app-db)



  ())