(ns mrmcc3.boot-soy.impl
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.walk :refer [postwalk]])
  (:import
    [com.google.template.soy SoyFileSet]
    [com.google.template.soy.shared SoyAstCache]))

;; ------------------------------------------------------------------
;; helpers

(defn- camel-case [k]
  (str/replace (name k) #"-(\w)" (comp str/upper-case second)))

(defn- camel-keys [m]
  (let [f (fn [[k v]] (if (keyword? k) [(camel-case k) v] [k v]))]
    (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

;; ------------------------------------------------------------------
;; internals. stateful rendering suitable for use in boot pod.

(def ^:private fs (atom nil))
(def ^:private tofu (atom nil))

;; the use of SoyAstCache and .addVolatile is recommended for development
;; where templates change more frequently then they are added/removed from
;; the fileset. recompile-tofu! followed by render should be performant :)

(defn- add-files [builder paths]
  (reduce #(.addVolatile %1 (io/file %2)) builder paths))

(defn- fileset [paths]
  (-> (SoyFileSet/builder)
      (.setSoyAstCache (SoyAstCache.))
      (.setStrictAutoescapingRequired true)
      (add-files paths)
      .build))

(defn- renderer [tpl data ij-data]
  (cond-> (.newRenderer @tofu tpl)
    data (.setData data)
    ij-data (.setIjData ij-data)))

;; ------------------------------------------------------------------
;; public api

;; builds the fileset and compiles tofu
(defn set-files! [paths]
  (->> paths fileset (reset! fs) .compileToTofu (reset! tofu)))

;; only call recompile-tofu! after set-files!
(defn recompile-tofu! []
  (->> @fs .compileToTofu (reset! tofu)))

;; only call render after set-files!
(defn render [tpl data ij-data]
  (-> (renderer tpl (camel-keys data) (camel-keys ij-data))
      .renderStrict .getContent))
