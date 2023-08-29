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
          :cell/color  #'color-cell})


(def column-defs [{:column/id     :name
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


(def default-data [{:name "red" :color :red :description "This is red"}
                   {:name "green" :color :green :description "This is green"}
                   {:name "blue" :color :blue :description "This is blue"}
                   {:name "yellow" :color :yellow :description "This is yellow"}
                   {:name "cyan" :color :cyan :description "This is cyan"}])


(def app-db (atom {:data default-data}))


(defn- add-row [color-name color-label description]
  (swap! app-db assoc :data
    (conj (:data @app-db)
      {:name color-label :color color-name :description description})))


(defn header-cell [{:keys [column/name column/render
                           column/min-width column/max-width column/pref-width]
                    :as   cell-heading}]
  (merge {:fx/type            :table-column
          :text               name
          :cell-value-factory identity
          :cell-factory       {:fx/cell-type :table-cell
                               :describe     ((render reg) cell-heading)}}
    (when min-width {:min-width min-width})
    (when max-width {:max-width max-width})
    (when pref-width {:pref-width pref-width})))


(defn table-view [{:keys [data columns pref-width pref-height]}]
  {:fx/type     :table-view
   :pref-width  pref-width
   :pref-height pref-height
   :columns     (map header-cell columns)
   :items       data})



(defn root [{:keys [data]}]
  {:fx/type :stage
   :showing true
   :title   "Table View example"
   :scene   {:fx/type :scene
             :root    {:fx/type     table-view
                       :pref-width  960
                       :pref-height 540
                       :columns     column-defs
                       :data        data}}})


(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root)))


(fx/mount-renderer app-db renderer)





(comment
  (map header-cell column-defs)



  ())


(comment

  (add-row :violet "violet" "This is violet")
  (add-row :slateblue "slate blue" "This is slate blue")




  ())
