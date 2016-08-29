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
  "replace all soy templates with a render function in the fileset
  metadata (key ::render) for use in tasks that follow. the render
  function takes the qualified template name as a string, a map
  of data and returns the rendered template as a string."
  []
  (let [p (-> (core/get-env)
              (update-in [:dependencies] into deps)
              pod/make-pod
              future)
        render (fn [tpl data]
                 (pod/with-call-in @p (mrmcc3.boot-soy.impl/render ~tpl ~data)))
        old-fileset (atom nil)]
    (core/with-pre-wrap fileset
      (let [old @old-fileset
            changed (->> (core/fileset-changed old fileset)
                         core/input-files (core/by-ext [".soy"]))
            added (->> (core/fileset-added old fileset)
                       core/input-files (core/by-ext [".soy"]))
            removed (->> (core/fileset-removed old fileset)
                         core/input-files (core/by-ext [".soy"]))
            soy-src (->> fileset core/input-files (core/by-ext [".soy"])) ;; haha soy-src
            paths (into #{} (map #(-> % core/tmp-file .getPath)) soy-src)]
        (reset! old-fileset fileset)
        (cond
          (or (seq added) (seq removed))
          (pod/with-call-in @p (mrmcc3.boot-soy.impl/set-files! ~paths))
          (seq changed)
          (pod/with-call-in @p (mrmcc3.boot-soy.impl/recompile-tofu!)))
        (util/info "loaded %s soy files...\n" (count soy-src))
        (-> (with-meta fileset {::render render})
            (core/rm soy-src)
            core/commit!)))))
