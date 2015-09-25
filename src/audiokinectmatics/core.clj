(ns audiokinectmatics.core
  (:gen-class))
(use 'overtone.live)


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
  (osc-handle server "/accelerometer" (fn [msg]
    ; difference between x - y via accelerometer
    (ctl id :adj (int (- (first (:args msg)) (second (:args msg)))))
    ;(println "MSG: " msg)
  ))


  ;remove handler
  ;(osc-rm-handler server "/accelerometer")

  ; stop listening and deallocate resources
  ;(osc-close server)

  )
