(ns audiokinectmatics.core
  (:require [quil.core :as q])
  (:require [clojure.math.numeric-tower :as math])
  (:gen-class) )
(use 'overtone.live)

(def red-v 150)
(def green-v 150)
(def blue-v 150)

(defn draw [];[msg]
  ;(q/background (q/random 255) (q/random 255) (q/random 255))
  (q/background red-v green-v blue-v)
  ; move origin point to centre of the sketch
  ; by default origin is in the left top corner
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
   ; parameter t goes 0, 0.01, 0.02, ..., 99.99, 100
   (doseq [t (range 0 100 0.01)]
     ; draw a point with x = t * sin(t) and y = t * cos(t)
     (q/point (* t (q/sin t))
              (* t (q/cos t)))))
)

(defn setup []
  (q/smooth)                          ;; Turn on anti-aliasing
  (q/frame-rate 10)                    ;; Set framerate to 1 FPS
  (q/background 200))                 ;; Set the background colour to
                                      ;; a nice shade of grey.

(defn -main
  "OSC Server receives data from accelerometer from android phone and synthetizes audio"
  [& args]

  (def PORT 3333) ; 12345 is the port Synapse.app uses
  ; start a server and create a client to talk with it
  (def server (osc-server PORT))

  (definst theremin [adj 0 volume 1 scale 1]
  (let [sound (lpf (* (sin-osc 100000) (sin-osc (+ 100250 (* adj scale)))) 12000)]
    (* sound volume)))

  (def id (theremin))


  ; Register a handler function for the /test OSC address
  ; The handler takes a message map with the following keys:
  ;   [:src-host, :src-port, :path, :type-tag, :args]
  ;;(osc-handle server "/accelerometer" (fn [msg]
    ; difference between x - y via accelerometer
    ;;(ctl id :adj (int (- (first (:args msg)) (second (:args msg)))))
    ;(println "MSG: " msg)
  ;;))


   (osc-handle server "/accelerometer" (fn [msg]
    ; difference between x - y via accelerometer
     ; (ctl id :adj (int (- x y ))) ; deprecated
     ; distance between "origin" and 3d point
     (def x (first (:args msg)))
     (def y (second (:args msg)))
     (def z (nth (:args msg) 2))

     ; (ctl id :adj (int (math/sqrt (+  x y z ))))
     (ctl id :adj (int (- x y )) )
     (def red-v   (int (* 2 x)) )
     (def green-v (int (* 2 y)) )
     (def blue-v  (int (* 2 z))) )
    ;(println "MSG: " msg)
   )

  ;remove handler
  ;(osc-rm-handler server "/accelerometer")

  ; stop listening and deallocate resources
  ;(osc-close server)

  (q/defsketch colorin-colorado       ;; Define a new sketch named example
  :title "Rainbow shit"               ;; Set the title of the sketch
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [323 200])                    ;; You struggle to beat the golden ratio

  )
