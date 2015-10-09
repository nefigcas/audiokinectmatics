(ns audiokinectmatics.core
  (:require [quil.core :as q])
  (:require [clojure.math.numeric-tower :as math])
  (:gen-class) )
(use 'overtone.live) ; load overtone

; global variables for RGB-values
(def red-v 150) (def green-v 150) (def blue-v 150)

; quil draw function
(defn draw [] ;(q/background (q/random 255) (q/random 255) (q/random 255))
  (q/background red-v green-v blue-v))
; quil setup
(defn setup []
  (q/smooth)                          ;; Turn on anti-aliasing
  (q/frame-rate 10)                    ;; Set framerate to 10 FPS
  (q/background 200))                 ;; Set the background colour to
                                      ;; a nice shade of grey

(def NUMERO_BOTONES (* 11 11))

(definst sonido [freq 440]
    (sin-osc freq) ; rango audible 20Hz a 20,000Hz
)
; Tabla de frecuencias
(def LIMITE_FREQ_BAJA 400.0)
(def LIMITE_FREQ_ALTA 1000.0)
(def frecuencias
  (range LIMITE_FREQ_BAJA LIMITE_FREQ_ALTA
    (/ (- LIMITE_FREQ_ALTA LIMITE_FREQ_BAJA) NUMERO_BOTONES)))
(defn toca-microtono [numero]
  (sonido (nth frecuencias numero))) ; usa la "tabla de frecuencias" para producir el sonido deseado

(defn -main
  "OSC Server receives data from android phone and synthetizes audio"
  [& args]

  (def PORT 3333) ; 12345 is the port Synapse.app uses
  ; start a server and create a client to talk with it
  (def server (osc-server PORT))

  ; creates handlers for microtone synth
  (let  [ numbers (range NUMERO_BOTONES)
          strings (map #(str %1 %2) (repeat NUMERO_BOTONES "/life/") numbers) ]
    (dorun (map   #(osc-handle server %1 (fn [msg]
      (stop)
      (toca-microtono %2)
      ;(println "MSG: " msg)
    )) strings numbers)))

  ;remove all handlers
  ;(osc-rm-all-listeners server)

  ; stop listening and deallocate resources
  ;(osc-close server)

  (q/defsketch colorin-colorado       ;; Define a new sketch named colorin-colorado
  :title "Rainbow shit"               ;; Set the title of the sketch
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [800 600])                    ;; Window size

  )
