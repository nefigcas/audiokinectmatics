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

(defn crea-instrumentos-tonales[cantidad frecuencias-en-hz]
  (range cantidad)
  (definst sonido-numero [frecuencia-en-hz] (sin-osc frecuencia-en-hz))
)

(defn toca-microtono[numero]
  (sonido-numero)
)

(defn osc-handlers-for-microtones[quantity]
  (range quantity)
  ((osc-handle server "/multi/0" (fn [msg]
    (microtono 0)
    (println "MSG: " msg)
   ))
))


(defn -main
  "OSC Server receives data from android phone and synthetizes audio"
  [& args]

  (def PORT 3333) ; 12345 is the port Synapse.app uses
  ; start a server and create a client to talk with it
  (def server (osc-server PORT))

  ; creates handlers for microtone synth
  (osc-handlers-for-microtones 64)

  ;remove handler
  ;(osc-rm-handler server "/mlr")

  ; stop listening and deallocate resources
  ;(osc-close server)

  (q/defsketch colorin-colorado       ;; Define a new sketch named colorin-colorado ;)
  :title "Rainbow shit"               ;; Set the title of the sketch
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [800 600])                    ;; Window size

  )
