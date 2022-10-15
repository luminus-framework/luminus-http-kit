(ns luminus.http-server
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]))

;; i know nothing about http-kit, but this blog post has a conversion function
;; https://www.booleanknot.com/blog/2016/07/15/asynchronous-ring.html
;; with-channel is deprecated though, so i rewrote it to use as-channel
(defn async-ring->httpkit2 [handler]
  (fn [request]
    (http-kit/as-channel
     request
     {:on-open (fn [ch]
                 (handler request
                          #(http-kit/send! ch %)
                          (fn [_] (http-kit/close ch))))})))

(defn start [{:keys [handler host port async?] :as opts}]
  (try
    (log/info "starting HTTP server on port" port)
    (http-kit/run-server
      (if async?
        (async-ring->httpkit2 handler)
        handler)
      (-> opts
          (assoc  :legacy-return-value? false)
          (dissoc :handler :init)))
    (catch Throwable t
      (log/error t (str "server failed to start on" host "port" port))
      (throw t))))

(defn stop 
  ([http-server] (stop http-server 100))
  ([http-server timeout]
   (let [result @(future (http-kit/server-stop! http-server {:timeout timeout}))]
     (log/info "HTTP server stopped")
     result)))
