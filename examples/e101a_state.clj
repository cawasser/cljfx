(ns e101a-state
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]))


(def app-db (atom
              (fx/create-context
                {:description     "This is the orginal text"
                 :color           :yellow
                 :provider-catalog-view {"alpha-googoos" {:resource/catalog [{:resource/type 0, :resource/time-frames [0 1 2 3 4 5], :resource/cost 10}
                                                                             {:resource/type 1, :resource/time-frames [0 1 2 3 4 5], :resource/cost 10}
                                                                             {:resource/type 2, :resource/time-frames [0 1 2 3 4 5], :resource/cost 10}
                                                                             {:resource/type 3, :resource/time-frames [0 1 2 3 4 5], :resource/cost 10}
                                                                             {:resource/type 4, :resource/time-frames [0 1 2 3 4 5], :resource/cost 10}]},
                                         "bravo-googoos" {:resource/catalog [{:resource/type 0, :resource/time-frames [1 3 5], :resource/cost 5}
                                                                             {:resource/type 1, :resource/time-frames [1 3 5], :resource/cost 5}
                                                                             {:resource/type 2, :resource/time-frames [1 3 5], :resource/cost 5}
                                                                             {:resource/type 3, :resource/time-frames [1 3 5], :resource/cost 5}
                                                                             {:resource/type 4, :resource/time-frames [1 3 5], :resource/cost 5}]},
                                         "charlie-googoos" {:resource/catalog [{:resource/type 0, :resource/time-frames [2 4 5], :resource/cost 5}
                                                                               {:resource/type 1, :resource/time-frames [2 4 5], :resource/cost 5}
                                                                               {:resource/type 2, :resource/time-frames [2 4 5], :resource/cost 5}
                                                                               {:resource/type 3, :resource/time-frames [2 4 5], :resource/cost 5}
                                                                               {:resource/type 4, :resource/time-frames [2 4 5], :resource/cost 5}]},
                                         "delta-googoos" {:resource/catalog [{:resource/type 0, :resource/time-frames [0 1 2], :resource/cost 5}
                                                                             {:resource/type 1, :resource/time-frames [0 1 2], :resource/cost 5}
                                                                             {:resource/type 2, :resource/time-frames [0 1 2], :resource/cost 5}
                                                                             {:resource/type 3, :resource/time-frames [0 1 2], :resource/cost 5}
                                                                             {:resource/type 4, :resource/time-frames [0 1 2], :resource/cost 5}]},
                                         "echo-googoos" {:resource/catalog [{:resource/type 0, :resource/time-frames [3 4], :resource/cost 2}
                                                                            {:resource/type 1, :resource/time-frames [3 4], :resource/cost 2}
                                                                            {:resource/type 2, :resource/time-frames [3 4], :resource/cost 2}
                                                                            {:resource/type 3, :resource/time-frames [3 4], :resource/cost 2}
                                                                            {:resource/type 4, :resource/time-frames [3 4], :resource/cost 2}]}}

                 :customer/orders [{:order/id "alice-order-1" :customer/id "alice"}
                                   {:order/id "alice-order-2" :customer/id "alice"}
                                   {:order/id "bob-order-1" :customer/id "bob"}
                                   {:order/id "carol-order-1" :customer/id "carol"}
                                   {:order/id "carol-order-2" :customer/id "carol"}
                                   {:order/id "carol-order-3" :customer/id "carol"}
                                   {:order/id "dave-order-1" :customer/id "dave"}]}
                cache/lru-cache-factory)))


(defmulti event-handler :event/type)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; basic app-db stuff

(defmethod event-handler :change-description
  [{:keys [description] :as params}]

  (println "event-handler :change-description" params)

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

; endregion

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; :provider-catalog

(defn- update-catalog [event current new-value]
  ; planning for the future...
  ;       more flexible changes to an existing catalog
  (condp = event
    :catalog/time-revision (vec current)
    :catalog/cost-revision (vec current)
    (vec new-value)))


(defmethod event-handler :provider-catalog
  [{event-key                                            :event/key
    {:keys [catalog/event resource/id resource/catalog]} :event/content :as params}]
  (println ":provider-catalog" params)
  (swap! app-db
    fx/swap-context
    update-in
    [:provider-catalog-view (:provider/id event-key) :resource/catalog]
    #(update-catalog event % catalog)))


(defn provider-catalogs [context]
  (fx/sub-val context :provider-catalog-view))


(defn provider-catalog-view [[event-key event-content :as event]]
  (println "provider-catalog-view" event)
  (event-handler {:event/type    :provider-catalog
                  :event/key     event-key
                  :event/content event-content}))


(defn reset-provider-catalog-view []
  (swap! app-db
    fx/swap-context
    assoc :provider-catalog-view {}))


; endregion





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

