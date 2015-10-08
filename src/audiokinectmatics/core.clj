(ns audiokinectmatics.core
  (:require [quil.core :as q])
  (:require [clojure.math.numeric-tower :as math])
  (:gen-class) )
(use 'overtone.live) ; load overtone


; global variables for RGB-values
(def red-v 150)
(def green-v 150)
(def blue-v 150)
; Points hands
; mano izquierda (left hand)
(def x0 0.0)
(def y0 0.0)
(def z0 0.0)
; mano derecha (right hand)
(def x1 1.0)
(def y1 1.0)
(def z1 1.0)


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

  (def PORT 12345) ; 12345 is the port Synapse.app uses
  ; start a server and create a client to talk with it
  (def server (osc-server PORT))

  (definst theremin [adj 0 volume 1 scale 1]
    (let [sound (lpf (* (sin-osc 100000) (sin-osc (+ 100250 (* adj scale)))) 12000)]
      (* sound volume)))

    (def id (theremin))

  ; creates handlers for microtone synth
  (osc-handle server "/lefthand_pos_screen"(fn [msg]
    (let [    x (first (:args msg))
              y (second (:args msg))
              z (nth (:args msg) 2)]
         (def x0 x)
         (def y0 y)
         (def z0 z)
         ))


    ;(println "MSG: " msg)
  )

  (osc-handle server "/righthand_pos_screen"(fn [msg]
    (let [    x (first (:args msg))
              y (second (:args msg))
              z (nth (:args msg) 2)
              distancia (int (math/sqrt
                (+
                  (math/expt (- x1 x0) 2)
                  (math/expt (- y1 y0) 2)
                  (math/expt (- z1 z0) 2) )
              ))]
         (def x1 x)
         (def y1 y)
         (def z1 z)
         (ctl id :adj distancia)
         ; (ctl id :adj (int (- x y )) )
         (def red-v   (mod (int x) 256) )
         (def green-v (mod (int y) 256) )
         (def blue-v  (mod (int z) 256)) )
    ;(println "MSG: " msg)
  ))
  ;remove handler
  ;(osc-rm-handler server "/")

  ; stop listening and deallocate resources
  ;(osc-close server)

  (q/defsketch colorin-colorado       ;; Define a new sketch named colorin-colorado
  :title "Rainbow shit"               ;; Set the title of the sketch
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [800 600])                    ;; Window size

  )
