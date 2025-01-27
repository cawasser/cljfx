(ns e101a-support
  (:require [e101a-table-cells :as cells]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; TEXT CONTENT
;
(defn text-content [{:keys [x y width height description] :as params}]
  (println "text-content" params)
  {:fx/type :v-box
   :alignment :center
   :children [{:fx/type :label
               :text (or description "DEFAULT TEXT")}]})

; endregion


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; COLOR CONTENT
;
(defn color-content [{:keys [width height color]}]
  (println "color-content" color)
  {:fx/type :pane
   :pref-width width
   :pref-height height
   :style {:-fx-background-color color}})

; endregion


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; TABLE CONTENT
;

(defn- header-cell [{:keys [column/name column/render
                            column/min-width column/max-width column/pref-width]
                     :as   cell-heading}]
  (merge {:fx/type            :table-column
          :text               name
          :cell-value-factory identity
          :cell-factory       {:fx/cell-type :table-cell
                               :describe     ((render cells/reg) cell-heading)}}
    (when min-width {:min-width min-width})
    (when max-width {:max-width max-width})
    (when pref-width {:pref-width pref-width})))


(defn table-view [{:keys [data columns pref-width pref-height]}]
  (println "table-view" data)
  {:fx/type     :table-view
   :pref-width  pref-width
   :pref-height pref-height
   :columns     (map header-cell columns)
   :items       data})


; endregion
