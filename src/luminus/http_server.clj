(ns luminus.http-server
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]))

(defn start [{:keys [handler host port] :as opts}]
  (try
    (log/info "starting HTTP server on port" port)
    (http-kit/run-server
      handler
      (dissoc opts :handler :init))
    (catch Throwable t
      (log/error t (str "server failed to start on" host "port" port))
      (throw t))))

(defn stop [http-server]
  (http-server :timeout 100)
  (log/info "HTTP server stopped"))
