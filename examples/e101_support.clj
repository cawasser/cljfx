(ns e101-support)


(defn text-content [{:keys [x y width height description]}]
  {:fx/type :v-box
   :alignment :center
   :children [{:fx/type :label
               :text (str "Window at [" x ", " y "] "
                       "with size " width "x" height)}
              {:fx/type :label
               :text (or description "DEFAULT TEXT")}]})


(defn color-content [{:keys [color]}]
  {:fx/type :pane
   :style {:-fx-background-color color}})

