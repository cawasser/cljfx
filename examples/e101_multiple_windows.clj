(ns e101-multiple-windows
  (:require [cljfx.api :as fx]
            [e101-support :as s]
            [e101-window :as w])
  (:import [javafx.stage Screen]))


(def app-db (atom {:description            "some new text"
                   :color                  :yellow
                   :customer/orders        [{:name "red" :color :red :description "This is red"}
                                            {:name "green" :color :green :description "This is green"}
                                            {:name "blue" :color :blue :description "This is blue"}
                                            {:name "yellow" :color :yellow :description "This is yellow"}
                                            {:name "cyan" :color :cyan :description "This is cyan"}]
                   :customer/order-columns [{:column/id     :name
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
                                             :column/render     :cell/string}]}))


(def width 600)
(def height 300)

(def bottom
  (-> (Screen/getPrimary)
    .getBounds
    .getHeight
    (- height)))

(def right
  (-> (Screen/getPrimary)
    .getBounds
    .getWidth
    (- width)))



(defn root [{:keys [description color
                    customer/orders customer/order-columns]}]
  {:fx/type fx/ext-many
   :desc    [{:fx/type     w/window
              :x           0
              :y           0
              :width       width
              :height      height
              :description description
              :content     s/text-content}                  ;s/text-content}
             {:fx/type w/window
              :x       400
              :y       0
              :width   width
              :height  height
              :color   color
              :content s/color-content}
             {:fx/type     w/window
              :x           800
              :y           0
              :width       width
              :height      height
              :pref-width  width
              :pref-height height
              :data        orders
              :columns     order-columns
              :content     s/table-view}]})


(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root)))


(fx/mount-renderer app-db renderer)




(comment
  (def content s/text-content)
  (def params {:fx/type     w/window
               :x           0
               :y           0
               :width       width
               :height      height
               :description "Top Left"
               :content     s/text-content})
  (merge {:fx/type content} (dissoc params :fx/type))


  (swap! app-db assoc :color :blue)
  (swap! app-db assoc :color :cyan)
  (swap! app-db assoc :color :yellow)


  (swap! app-db assoc :description "YELLOW")


  ())