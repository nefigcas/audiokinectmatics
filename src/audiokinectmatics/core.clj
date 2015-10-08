(ns audiokinectmatics.core
  (:require [quil.core :as q])
  (:require [clojure.math.numeric-tower :as math])
  (:gen-class) )
(use 'overtone.live) ; load overtone

; global variables for RGB-values
(def red-v 150)
(def green-v 150)
(def blue-v 150)

; quil draw function
(defn draw []
  ;(q/background (q/random 255) (q/random 255) (q/random 255))
  (q/background red-v green-v blue-v)
)

(defn setup []
  (q/smooth)                          ;; Turn on anti-aliasing
  (q/frame-rate 10)                    ;; Set framerate to 10 FPS
  (q/background 200))                 ;; Set the background colour to
                                      ;; a nice shade of grey.

(defn -main
  "OSC Server receives data from android phone and synthetizes audio"
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
  (osc-handle server "/accelerometer" (fn [msg]
    ; difference between x - y via accelerometer
    ; distance between "origin" and 3d point, theremin parameters, etc needs more analisys
    (let [x (first (:args msg))
          y (second (:args msg))
          z (nth (:args msg) 2)]

     (ctl id :adj (int (math/sqrt (+  (* x x ) (* y y) (* z z)  ))))
     ; (ctl id :adj (int (- x y )) )
     (def red-v   (int (* 2 x)) )
     (def green-v (int (* 2 y)) )
     (def blue-v  (int (* 2 z))) )
    ;(println "MSG: " msg)
   ))


   ;(osc-handle server "/" (fn [msg]
  ;    (println "MSG: " msg)
   ;))

  ;remove handler
  ;(osc-rm-handler server "/accelerometer")

  ; stop listening and deallocate resources
  ;(osc-close server)

  (q/defsketch colorin-colorado       ;; Define a new sketch named colorin-colorado ;)
  :title "Rainbow shit"               ;; Set the title of the sketch
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [800 600])                    ;; Window size

  )
