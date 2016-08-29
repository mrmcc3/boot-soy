(def project 'mrmcc3/boot-soy)
(def version "0.1.0-SNAPSHOT")

(set-env! :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "1.8.0" :scope "provided"]
                            [boot/core "2.6.0" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "Boot task that loads soy (google closure) template files for rendering in subsequent tasks."
      :url         "https://github.com/mrmcc3/boot-soy"
      :scm         {:url "https://github.com/mrmcc3/boot-soy"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(require '[mrmcc3.boot-soy :refer [soy]])

(deftask build []
  (comp (pom) (jar) (install)))
  
