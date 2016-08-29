(ns mrmcc3.boot-soy.impl
  (:require [clojure.java.io :as io])
  (:import [com.google.template.soy SoyFileSet]
           [com.google.template.soy.shared SoyAstCache]))

(def fs (atom nil))
(def tofu (atom nil))

(defn add-files [builder paths]
  (reduce #(.addVolatile %1 (io/file %2)) builder paths))

(defn build-fileset [paths]
  (-> (SoyFileSet/builder)
      (.setSoyAstCache (SoyAstCache.))
      (.setStrictAutoescapingRequired true)
      (add-files paths)
      .build))

(defn set-files! [paths]
  (->> paths build-fileset (reset! fs) .compileToTofu (reset! tofu)))

(defn recompile-tofu! []
  (reset! tofu (.compileToTofu @fs)))

(defn render [tpl data]
  (-> (.newRenderer @tofu tpl) (.setData data) .renderStrict .getContent))
