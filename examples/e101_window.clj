(ns e101-window)


(defn window [{:keys [x y width height content] :as params}]
  (println "window" content)
  {:fx/type       :stage
   :always-on-top true
   :showing       true
   :x             x
   :y             y
   :width         width
   :height        height
   :scene         {:fx/type :scene
                   :root    (merge {:fx/type content}
                              (dissoc params :fx/type))}})