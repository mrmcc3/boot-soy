(ns mrmcc3.boot-soy
  {:boot/export-tasks true}
  (:require [boot.core :as core :refer [deftask]]
            [boot.pod :as pod]
            [boot.util :as util]))

(def ^:private deps
  '[[com.google.template/soy "2016-08-25"
     :exclusions [args4j
                  com.google.gwt/gwt-user
                  com.google.guava/guava-testlib
                  org.json/json
                  com.google.code.gson/gson]]])

(deftask soy
  "compile all soy templates and provide a render function
  in the fileset metadata (key ::render) for use in tasks that follow.
  The render function takes the qualified template name as a string,
  a map of data, optionally a map of injected data and
  returns the rendered template as a string."
  []
  (let [p (-> (core/get-env)
              (update-in [:dependencies] into deps)
              pod/make-pod
              future)
        render (fn r
                 ([tpl data] (r tpl data nil))
                 ([tpl data inj]
                  (pod/with-call-in @p
                    (mrmcc3.boot-soy.impl/render ~tpl ~data ~inj))))
        prev-fileset (atom nil)]
    (core/with-pre-wrap fileset
      (let [prev    @prev-fileset
            changed (->> (core/fileset-changed prev fileset)
                         core/input-files (core/by-ext [".soy"]))
            added   (->> (core/fileset-added prev fileset)
                         core/input-files (core/by-ext [".soy"]))
            removed (->> (core/fileset-removed prev fileset)
                         core/input-files (core/by-ext [".soy"]))
            paths   (->> fileset core/input-files (core/by-ext [".soy"])
                         (map #(-> % core/tmp-file .getPath)))]
        (reset! prev-fileset fileset)
        (util/info "found %s soy files...\n" (count paths))
        (cond
          (or (seq added) (seq removed))
          (pod/with-call-in @p (mrmcc3.boot-soy.impl/set-files! ~paths))
          (seq changed)
          (pod/with-call-in @p (mrmcc3.boot-soy.impl/recompile-tofu!)))
        (with-meta fileset {::render render})))))
