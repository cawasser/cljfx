(ns e101a-sales-dashboard
  (:require [cljfx.api :as fx]
            [e101a-state :as state]
            [e101a-support :as s]
            [e101a-window :as w]))


(def column-headers [{:column/id     :provider/id
                      :column/name   "Provider ID"
                      :column/render :cell/string}
                     {:column/id     :resource/type
                      :column/name   "Type"
                      :column/render :cell/string}
                     {:column/id     :resource/time-frames
                      :column/name   "Time Frames"
                      :column/render :cell/string}
                     {:column/id     :resource/cost
                      :column/name   "Cost"
                      :column/render :cell/string}])


(defn dashboard [{:keys [fx/context title x y width height]}]
  (println "SALES window" context)

  (let [catalog      (state/provider-catalogs context)
        presentation (->> catalog
                       (mapcat (fn [[id {:keys [resource/catalog]}]]
                                 (map (fn [{:keys [resource/type resource/time-frames resource/cost]}]
                                        {:provider/id          id
                                         :resource/type        type
                                         :resource/time-frames time-frames
                                         :resource/cost        cost})
                                   catalog)))
                       vec
                       doall)]
    {:fx/type w/window
     :title   title
     :x       x
     :y       y
     :width   width
     :height  height
     :content {:fx/type     s/table-view
               :data        presentation
               :columns     column-headers
               :pref-width  width
               :pref-height height}}))



(comment
  (do
    (def context @state/app-db)
    (def catalog (state/provider-catalogs context))
    (def presentation (->> catalog
                        (mapcat (fn [[id {:keys [resource/catalog]}]]
                                  (map (fn [{:keys [resource/type resource/time-frames resource/cost]}]
                                         {:provider/id          id
                                          :resource/type        type
                                          :resource/time-frames time-frames
                                          :resource/cost        cost})
                                    catalog)))
                        vec
                        doall)))


  ())
