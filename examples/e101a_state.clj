(ns e101a-state
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]))


(def app-db (atom
              (fx/create-context
                {:description     "This is the orginal text"
                 :color           :yellow
                 :customer/orders [{:order/id "alice-order-1" :customer/id "alice"}
                                   {:order/id "alice-order-2" :customer/id "alice"}
                                   {:order/id "bob-order-1" :customer/id "bob"}
                                   {:order/id "carol-order-1" :customer/id "carol"}
                                   {:order/id "carol-order-2" :customer/id "carol"}
                                   {:order/id "carol-order-3" :customer/id "carol"}
                                   {:order/id "dave-order-1" :customer/id "dave"}]}
                cache/lru-cache-factory)))


(defmulti event-handler :event/type)


(defmethod event-handler :change-description
  [{:keys [description]}]

  (swap! app-db
    fx/swap-context
    assoc :description description))


(defmethod event-handler :change-color
  [{:keys [color]}]

  (swap! app-db
    fx/swap-context
    assoc :color color))


(defmethod event-handler :add-customer-order
  [{customer-id :customer/id
    order-id    :order/id}]

  (swap! app-db
    fx/swap-context
    update :customer/orders
    #(conj % {:order/id order-id :customer/id customer-id})))





(comment
  (event-handler {:event/type :change-description
                  :description "This is new text"})

  (event-handler {:event/type :change-color
                  :color :blue})

  (event-handler {:event/type  :add-customer-order
                  :customer/id "alice"
                  :order/id    "alice-order-3"})
  (event-handler {:event/type  :add-customer-order
                  :customer/id "dave"
                  :order/id    "dave-order-2"})



  ())

