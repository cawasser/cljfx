(ns e101-support)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; TEXT CONTENT
;
(defn text-content [{:keys [x y width height description] :as params}]
  (println "text-content" params)
  {:fx/type :v-box
   :alignment :center
   :children [{:fx/type :label
               :text (str "Window at [" x "px, " y "px] "
                       "with size " width "px x " height
                       "px")}
              {:fx/type :label
               :text (or description "DEFAULT TEXT")}]})

; endregion


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; COLOR CONTENT
;
(defn color-content [{:keys [color] :as params}]
  (println "color-content" params)
  {:fx/type :pane
   :style {:-fx-background-color color}})

; endregion


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; TABLE CONTENT
;
(defn- string-cell [{:keys [column/id]}]
  (fn [x]
    {:text (id x)}))

(defn- color-cell [_]
  (fn [x]
    {:style {:-fx-background-color
             (:color x)}}))


(def reg {:cell/string #'string-cell
          :cell/color  #'color-cell})


(defn- header-cell [{:keys [column/name column/render
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

; endregion
