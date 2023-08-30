(ns e101-support)


(defn text-content [{:keys [x y width height description]}]
  {:fx/type :v-box
   :alignment :center
   :children [{:fx/type :label
               :text (str "Window at [" x "px, " y "px] "
                       "with size " width "px x " height
                       "px")}
              {:fx/type :label
               :text (or description "DEFAULT TEXT")}]})


(defn color-content [{:keys [color]}]
  {:fx/type :pane
   :style {:-fx-background-color color}})

