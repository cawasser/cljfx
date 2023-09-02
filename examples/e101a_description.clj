(ns e101a-description
  (:require [cljfx.api :as fx]
            [e101a-support :as s]
            [e101a-window :as w]))


(defn window [{:keys [fx/context title x y width height]}]
  (let [description (fx/sub-val context :description)]
    {:fx/type w/window
     :title   title
     :x       x
     :y       y
     :width   width
     :height  height
     :content {:fx/type     s/text-content
               :description description}}))



(comment
  (def context @e101a-state/app-db)



  ())