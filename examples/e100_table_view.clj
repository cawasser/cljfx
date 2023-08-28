(ns e100-table-view
  (:require [cljfx.api :as fx]))


(defn- string-cell [{:keys [column/id]}]
  (fn [x]
    {:text (id x)}))

(defn- color-cell [_]
  (fn [x]
    {:style {:-fx-background-color
             (:color x)}}))


(def reg {:cell/string #'string-cell
          :cell/color #'color-cell})


(def column-defs [{:column/id     :name
                   :column/name   "Name"
                   :column/render :cell/string}
                  {:column/id     :color
                   :column/name   "Color"
                   :column/render :cell/color}
                  {:column/id     :description
                   :column/name   "Description"
                   :column/render :cell/string}])


(def data [{:name "red" :color :red :description "This is red"}
           {:name "green" :color :green :description "This is green"}
           {:name "blue" :color :blue :description "This is blue"}
           {:name "yellow" :color :yellow :description "This is yellow"}
           {:name "cyan" :color :cyan :description "This is cyan"}])


(def app-db (atom data))


(defn header-cell [{:keys [column/name column/render] :as cell-heading}]
  {:fx/type            :table-column
   :text               name
   :cell-value-factory identity
   :cell-factory       {:fx/cell-type :table-cell
                        :describe     ((render reg) cell-heading)}})


(defn table-view [{:keys [data columns pref-width pref-height]}]
  {:fx/type     :table-view
   :pref-width  pref-width
   :pref-height pref-height
   :columns     (map header-cell columns)
   :items       data})


(fx/on-fx-thread
  (fx/create-component
    {:fx/type :stage
     :showing true
     :title   "Table View example"
     :scene   {:fx/type :scene
               :root    {:fx/type     table-view
                         :pref-width  960
                         :pref-height 540
                         :columns     column-defs
                         :data        data}}}))





(comment

  ())
