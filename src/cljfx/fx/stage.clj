(ns cljfx.fx.stage
  (:require [cljfx.lifecycle.composite :as lifecycle.composite]
            [cljfx.coerce :as coerce]
            [cljfx.lifecycle :as lifecycle]
            [cljfx.mutator :as mutator]
            [cljfx.fx.window :as fx.window])
  (:import [javafx.stage Stage StageStyle Modality]))

(set! *warn-on-reflection* true)

(def lifecycle
  (-> Stage
      (lifecycle.composite/describe
        :ctor []
        :extends [fx.window/lifecycle]
        :prop-order {:showing 1}
        :props {:always-on-top [:setter lifecycle/scalar :default false]
                :full-screen [:setter lifecycle/scalar :default false]
                :full-screen-exit-hint [:setter lifecycle/scalar]
                :full-screen-exit-key-combination [:setter lifecycle/scalar
                                                   :coerce coerce/key-combination]
                :iconified [:setter lifecycle/scalar :default false]
                :icons [:list lifecycle/scalar :coerce #(map coerce/image %)]
                :max-height [:setter lifecycle/scalar :coerce double
                             :default Double/MAX_VALUE]
                :max-width [:setter lifecycle/scalar :coerce double
                            :default Double/MAX_VALUE]
                :maximized [:setter lifecycle/scalar :default false]
                :min-height [:setter lifecycle/scalar :coerce double :default 0.0]
                :min-width [:setter lifecycle/scalar :coerce double :default 0.0]
                :modality [(mutator/setter #(.initModality ^Stage %1 %2))
                           lifecycle/scalar
                           :coerce (coerce/enum Modality)
                           :default :none]
                :resizable [:setter lifecycle/scalar :default true]
                :scene [:setter lifecycle/dynamic]
                :title [:setter lifecycle/scalar]
                :style [(mutator/setter #(.initStyle ^Stage %1 %2))
                        lifecycle/scalar
                        :coerce (coerce/enum StageStyle)
                        :default :decorated]
                :showing [(mutator/setter #(if %2 (.show ^Stage %1) (.hide ^Stage %1)))
                          lifecycle/scalar
                          :default false]})
      (lifecycle/wrap-on-delete #(.hide ^Stage %))))
