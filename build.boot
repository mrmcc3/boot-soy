(def +project+ 'mrmcc3/boot-soy)
(def +version+ "0.1.2-SNAPSHOT")

(set-env! :resource-paths #{"src"})

(def snapshot? (.endsWith +version+ "-SNAPSHOT"))

(task-options!
  pom {:project     +project+
       :version     +version+
       :description "Boot task that loads soy (google closure) template files for rendering in subsequent tasks."
       :url         "https://github.com/mrmcc3/boot-soy"
       :scm         {:url "https://github.com/mrmcc3/boot-soy"}
       :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
 push {:repo "clojars"
       :ensure-clean true
       :tag (not snapshot?)
       :gpg-sign (not snapshot?)})

(require '[mrmcc3.boot-soy :refer [soy]])

(deftask build []
  (comp (pom) (jar) (install)))

(deftask deploy []
  (comp (build) (push)))
