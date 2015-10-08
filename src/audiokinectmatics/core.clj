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
                                      ;; a nice shade of grey


(def PORT 3333) ; 12345 is the port Synapse.app uses
; start a server and create a client to talk with it
(def server (osc-server PORT))

(def frecuencias (range 200.0 1000.0 (/ (- 1000.0 200.0) 64)))
(defn toca-microtono[numero]
  (println numero)
  (sonido (nth frecuencias numero))
  ;(@(resolve (symbol (str "sonido-" numero))))
)

(defn osc-handlers-for-microtones[quantity]
  ;generates handler strings

(let  [ numbers (range quantity)
        strings (map #(str %1 %2) (repeat quantity "/multi/") numbers) ]
  (map #(osc-handle server %1 (fn [msg]
    (stop)
    (toca-microtono %2)
    ;(println "MSG: " msg)
  )) strings numbers)
))
(osc-handlers-for-microtones 64)

(defn -main
  "OSC Server receives data from android phone and synthetizes audio"
  [& args]

  (definst sonido [freq 440]
      (sin-osc freq) ; rango audible 20Hz a 20,000Hz
  )

  ; creates handlers for microtone synth
  ; (osc-handlers-for-microtones 64)

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
