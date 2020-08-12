(ns luminus.http-server
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]))

(defn start [{:keys [handler host port] :as opts}]
  (try
    (log/info "starting HTTP server on port" port)
    (http-kit/run-server
      handler
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
