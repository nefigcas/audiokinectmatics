(defproject audiokinectmatics "0.1.0-SNAPSHOT"
  :description "Kinect based sound generation"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [org.clojure/clojure "1.6.0"]
                  [overtone "0.9.1"]  ; here is included also the OSC "library" :)
                ]
  :main ^:skip-aot audiokinectmatics.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
