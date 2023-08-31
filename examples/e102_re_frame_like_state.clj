(ns e102-re-frame-like-state
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; STATE MGMT
;

(def app-db
  (atom
    (fx/create-context
      {:next-potion-id     3
       :next-ingredient-id 5

       :id->potion         {0 {:id             0
                               :name           "Antidote"
                               :ingredient-ids [0 1]}
                            1 {:id             1
                               :name           "Explosive cocktail"
                               :ingredient-ids [2 3 4]}
                            2 {:id             2
                               :name           "Petrificoffee"
                               :ingredient-ids [1 5]}
                            3 {:id             3
                               :name           "Viscositea"
                               :ingredient-ids [0 4]}}
       :id->ingredient     {0 {:id   0
                               :name "Applebloom"}
                            1 {:id   1
                               :name "Dragonwort"}
                            2 {:id   2
                               :name "Ruby"}
                            3 {:id   3
                               :name "Fireberries"}
                            4 {:id   4
                               :name "Sulfur"}
                            5 {:id   5
                               :name "Web"}}}
      cache/lru-cache-factory)))


(defn sub-potion-ids [context]
  (sort (keys (fx/sub-val context :id->potion))))


(defn sub-ingredient-ids [context]
  (sort (keys (fx/sub-val context :id->ingredient))))


(defn- next-potion-id []
  (swap! app-db
    fx/swap-context
    update :next-potion-id inc)
  (fx/sub-ctx @app-db #(fx/sub-val % :next-potion-id)))


(defn- get-potion [potion-name]
  (let [db (fx/sub-ctx @app-db #(fx/sub-val % :id->potion))]
    (as-> db m
      (map (juxt key #(-> % val :name)) m)
      (filter (fn [[k v]] (= potion-name v)) m)
      (first m)
      (first m))))


(defn get-ingredient [ingredient-name]
  (let [db (fx/sub-ctx @app-db #(fx/sub-val % :id->ingredient))]
    (as-> db m
      (map (juxt key #(-> % val :name)) m)
      (filter (fn [[k v]] (= ingredient-name v)) m)
      (first m))))


(defn- new-ingredient [ingredient-id ingredient-name]
  (swap! app-db
    fx/swap-context
    assoc-in [:id->ingredient ingredient-id]
    {:id ingredient-id :name ingredient-name}))


(defn- ingredient-id [ingredient-name]
  (let [[current-id _] (get-ingredient ingredient-name)]
    (if current-id
      current-id
      (do
        (swap! app-db
          fx/swap-context
          update :next-ingredient-id inc)
        (let [new-id (fx/sub-ctx @app-db #(fx/sub-val % :next-ingredient-id))]
          (new-ingredient new-id ingredient-name)
          new-id)))))


(defmulti event-handler :event/type)


; ::remove-ingredient
(defmethod event-handler ::remove-ingredient
  [{:keys [potion-id ingredient-id] :as params}]
  (println "::remove-ingredient" params)
  (swap! app-db
    fx/swap-context
    update-in
    [:id->potion potion-id :ingredient-ids]
    #(vec (remove #{ingredient-id} %))))


; ::remove-ingredient-by-name
(defmethod event-handler ::remove-ingredient-by-name
  [{:keys [potion-name ingredient-name] :as params}]
  (println "::remove-ingredient-by-name" params)
  (let [potion-id (get-potion potion-name)
        ingredient-id (ingredient-id ingredient-name)]
    (swap! app-db
      fx/swap-context
      update-in
      [:id->potion potion-id :ingredient-ids]
      #(vec (remove #{ingredient-id} %)))))


; ::add-potion
(defmethod event-handler ::add-potion [{:keys [potion-name] :as params}]
  (println "::add-potion" params)
  (let [new-id     (next-potion-id)
        new-potion {:id             new-id
                    :name           potion-name
                    :ingredient-ids []}]
    (swap! app-db
      fx/swap-context
      assoc-in [:id->potion new-id] new-potion)))


; ::add-ingredient
(defmethod event-handler ::add-ingredient [{:keys [potion-name ingredient-name] :as params}]
  (println "::add-ingredient" params)
  (let [ingredient-id (ingredient-id ingredient-name)
        potion-id     (get-potion potion-name)]
    (swap! app-db
      fx/swap-context
      update-in [:id->potion potion-id :ingredient-ids]
      #(vec (conj % ingredient-id)))))


(defn sub-ingredient-id->potion-ids-map [context]
  (->> (fx/sub-val context :id->potion)
    vals
    (mapcat (fn [potion]
              (map (fn [ingredient-id]
                     [(:id potion) ingredient-id])
                (:ingredient-ids potion))))
    (group-by second)
    (map (juxt key
           #(->> % val (map first) sort)))
    (into {})))


(defn sub-ingredient-id->potion-ids [context id]
  (get (fx/sub-ctx context sub-ingredient-id->potion-ids-map) id))

; endregion


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; UI ELEMENTS
;

(defn close-icon [{:keys [on-remove]}]
  {:fx/type          :stack-pane
   :on-mouse-clicked on-remove
   :children
   [{:fx/type :svg-path
     :fill    :gray
     :scale-x 0.75
     :scale-y 0.75
     :content (str "M15.898,4.045c-0.271-0.272-0.713-0.272-0.986,0l-4.71,4.711L5.493,"
                "4.045c-0.272-0.272-0.714-0.272-0.986,0s-0.272,0.714,0,0.986l4.709,"
                "4.711l-4.71,4.711c-0.272,0.271-0.272,0.713,0,0.986c0.136,0.136,0.314,"
                "0.203,0.492,0.203c0.179,0,0.357-0.067,0.493-0.203l4.711-4.711l4.71,"
                "4.711c0.137,0.136,0.314,0.203,0.494,0.203c0.178,0,0.355-0.067,"
                "0.492-0.203c0.273-0.273,0.273-0.715,0-0.986l-4.711-4.711l4.711-4."
                "711C16.172,4.759,16.172,4.317,15.898,4.045z")}]})


(defn section-title [{:keys [text]}]
  {:fx/type :label
   :style   {:-fx-font [:bold 20 :sans-serif]}
   :text    text})


(defn item-title [{:keys [text]}]
  {:fx/type :label
   :style   {:-fx-font [15 :sans-serif]}
   :text    text})


(defn small-label [{:keys [text]}]
  {:fx/type :label
   :style   {:-fx-font      [12 :sans-serif]
             :-fx-text-fill :grey}
   :text    text})


(defn badge [{:keys [text on-remove]}]
  {:fx/type   :h-box
   :alignment :center
   :spacing   2
   :style     {:-fx-font          [12 :sans-serif]
               :-fx-text-fill     :grey
               :-fx-border-width  1
               :-fx-border-style  :solid
               :-fx-border-color  :lightgray
               :-fx-border-radius 4
               :-fx-padding       [0 2 0 2]}
   :children  [{:fx/type :label :text text}
               {:fx/type   close-icon
                :on-remove on-remove}]})


(defn ingredient-badge [{:keys [fx/context id potion-id]}]
  {:fx/type   badge
   :text      (fx/sub-val context get-in [:id->ingredient id :name])
   :on-remove {:event/type    ::remove-ingredient
               :potion-id     potion-id
               :ingredient-id id}})


(defn potion-view [{:keys [fx/context id]}]
  (let [potion (fx/sub-val context get-in [:id->potion id])]
    {:fx/type  :v-box
     :children [{:fx/type item-title
                 :text    (:name potion)}
                {:fx/type  :h-box
                 :spacing  2
                 :children (concat
                             [{:fx/type small-label
                               :text    "needs"}]
                             (for [ingredient-id (:ingredient-ids potion)]
                               {:fx/type   ingredient-badge
                                :potion-id id
                                :id        ingredient-id}))}]}))


(defn potion-list [{:keys [fx/context]}]
  {:fx/type  :v-box
   :padding  10
   :spacing  10
   :children [{:fx/type section-title
               :text    "Potions"}
              {:fx/type  :v-box
               :spacing  5
               :children (for [id (fx/sub-ctx context sub-potion-ids)]
                           {:fx/type potion-view
                            :id      id})}]})


(defn potion-badge [{:keys [fx/context id ingredient-id]}]
  {:fx/type   badge
   :text      (fx/sub-val context get-in [:id->potion id :name])
   :on-remove {:event/type    ::remove-ingredient
               :potion-id     id
               :ingredient-id ingredient-id}})


(defn ingredient-view [{:keys [fx/context id]}]
  (let [potion-ids (fx/sub-ctx context sub-ingredient-id->potion-ids id)]
    {:fx/type  :v-box
     :children [{:fx/type item-title
                 :text    (fx/sub-val context get-in [:id->ingredient id :name])}
                {:fx/type  :h-box
                 :spacing  2
                 :children (if (empty? potion-ids)
                             [{:fx/type small-label
                               :text    "unused"}]
                             (concat
                               [{:fx/type small-label
                                 :text    "used in"}]
                               (for [potion-id potion-ids]
                                 {:fx/type       potion-badge
                                  :ingredient-id id
                                  :id            potion-id})))}]}))


(defn ingredient-list [{:keys [fx/context]}]
  {:fx/type  :v-box
   :padding  10
   :spacing  10
   :children [{:fx/type section-title
               :text    "Ingredients"}
              {:fx/type  :v-box
               :spacing  5
               :children (for [id (fx/sub-ctx context sub-ingredient-ids)]
                           {:fx/type ingredient-view
                            :id      id})}]})


(defn root-view [_]
  {:fx/type :stage
   :title   "Book of Potions"
   :showing true
   :width   600
   :height  400
   :scene   {:fx/type :scene
             :root    {:fx/type           :split-pane
                       :divider-positions [0.5]
                       :items             [{:fx/type potion-list}
                                           {:fx/type ingredient-list}]}}})

; endregion


(def renderer
  (fx/create-renderer
    :middleware (comp
                  fx/wrap-context-desc
                  (fx/wrap-map-desc (fn [_] {:fx/type root-view})))
    :opts {:fx.opt/type->lifecycle   #(or (fx/keyword->lifecycle %)
                                        (fx/fn->lifecycle-with-context %))
           :fx.opt/map-event-handler event-handler}))


(fx/mount-renderer app-db renderer)




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; region ; rich comments
;

; exercise the app-db
(comment

  (event-handler {:event/type  ::add-potion
                  :potion-name "Philther"})


  (event-handler {:event/type      ::add-ingredient
                  :potion-name     "Philther"
                  :ingredient-name "Fireberries"})
  (event-handler {:event/type      ::add-ingredient
                  :potion-name     "Philther"
                  :ingredient-name "Eye of Newt"})


  (event-handler  {:event/type      ::remove-ingredient-by-name
                   :potion-name     "Philther"
                   :ingredient-name "Eye of Newt"})


  ())



; logic to find a potion by name
(comment
  (def potion-name "Antidote")

  (let [db (fx/sub-ctx @app-db #(fx/sub-val % :id->potion))]
    (as-> db m
      (map (juxt key #(-> % val :name)) m)
      (filter (fn [[k v]] (= potion-name v)) m)
      (first m)
      (first m)))


  ())


; logic to find an ingredient by name, or assign a new id if needed
(comment
  (do
    (def r (-> @app-db
             (get-in [:cljfx.context/m :id->ingredient])))
    (def ingredient-name "Fireberries")
    (def db (fx/sub-ctx @app-db #(fx/sub-val % :id->ingredient))))

  (map (juxt key #(-> % val :name)) r)

  (as-> db m
    (map (juxt key #(-> % val :name)) m)
    (filter (fn [[k v]] (= ingredient-name v)) m)
    (first m))


  (let [db (fx/sub-ctx @app-db #(fx/sub-val % :id->ingredient))]
    (as-> db m
      (map (juxt key #(-> % val :name)) m)
      (filter (fn [[k v]] (= ingredient-name v)) m)
      (first m)))


  (get-ingredient ingredient-name)

  (ingredient-id ingredient-name)
  (ingredient-id "Something New")


  ())


; logic to add a new ingredient to an existing potion
(comment
  (do
    (def potion-name "Antidote")
    (def ingredient-name "Eye of Newt"))

  (event-handler {:event/type      ::add-ingredient
                  :potion-name     potion-name
                  :ingredient-name ingredient-name})




  ())



; logic to remove an existing potion (must remove the potion from all the
; ingredients too
(comment
  (do
    (def potion-name "Philther"))



  ())

; endregion
