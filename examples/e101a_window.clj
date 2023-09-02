(ns e101a-window)


(defn window [{:keys [title x y width height content] :as params}]
  (println "window" content)

  {:fx/type :stage
   :showing true
   :title   title
   :x       x
   :y       y
   :width   width
   :height  height
   :scene   {:fx/type :scene
             :root    {:fx/type   :v-box
                       :alignment :center
                       :children  [content]}}})

