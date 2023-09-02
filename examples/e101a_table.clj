(ns e101a-table
  (:require [cljfx.api :as fx]
            [e101a-support :as s]
            [e101a-window :as w]))



(def column-headers [{:column/id     :name
                      :column/name   "Name"
                      :column/render :cell/string}
                     {:column/id     :color
                      :column/name   "Color"
                      :column/render :cell/color}
                     {:column/id         :description
                      :column/min-width  50
                      :column/pref-width 200
                      :column/max-width  300
                      :column/name       "Description"
                      :column/render     :cell/string}])


(defn window [{:keys [fx/context title x y width height]}]
  (let [data (fx/sub-val context :customer/orders)]
    {:fx/type w/window
     :title   title
     :x       x
     :y       y
     :width   width
     :height  height
     :content {:fx/type     s/table-view
               :data        data
               :columns     column-headers
               :pref-width  width
               :pref-height height}}))



(comment
  (def context @e101a-state/app-db)



  ())