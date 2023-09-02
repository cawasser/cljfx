(ns e101a-state
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]))


(def app-db (atom
              (fx/create-context
                {:description            "some new text"
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
                                           :column/render     :cell/string}]}
                cache/lru-cache-factory)))


(defmulti event-handler :event/type)
