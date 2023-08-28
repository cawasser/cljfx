(ns e100-table-view
  (:require [cljfx.api :as fx]))


(def column-defs [{:column/id :name
                   :column/name "Name"
                   :column/render (fn [x]
                                    {:text (:name x)})}
                  {:column/id :color
                   :column/name "Color"
                   :column/render (fn [x]
                                    {:style {:-fx-background-color
                                             (:color x)}})}
                  {:column/id :description
                   :column/name "Description"
                   :column/render (fn [x]
                                    {:text (:description x)})}])
(def data [{:name "red" :color :red :description "This is red"}
           {:name "green" :color :green :description "This is green"}
           {:name "blue" :color :blue :description "This is blue"}
           {:name "yellow" :color :yellow :description "This is yellow"}
           {:name "cyan" :color :cyan :description "This is cyan"}])


(defn header-cell [{:keys [column/id column/name column/render]}]
  {:fx/type            :table-column
   :text               name
   :cell-value-factory identity
   :cell-factory       {:fx/cell-type :table-cell
                        :describe     render}})


(defn table-view [{:keys [data columns pref-width pref-height]}]
  {:fx/type     :table-view
   :pref-width pref-width
   :pref-height pref-height
   :row-factory {:fx/cell-type :table-row
                 :describe     (fn [x]
                                 {:style {:-fx-border-color x}})}
   :columns     (->> columns
                  (map header-cell)
                  (into []))
   :items       data})


(fx/on-fx-thread
  (fx/create-component
    {:fx/type :stage
     :showing true
     :title   "Table View example"
     :scene   {:fx/type :scene
               :root    {:fx/type table-view
                         :pref-width 960
                         :pref-height 540
                         :columns column-defs
                         :data data}}}))





(comment
  (->> data
    first
    keys
    (map header-cell)
    (into []))



  (def name (first (->> data
                     first
                     keys)))


  {:fx/type            :table-column
   :text               (str name)
   :cell-value-factory identity
   :cell-factory       {:fx/cell-type :table-cell
                        :describe     (fn [x]
                                        {:text x})}}

  ())
