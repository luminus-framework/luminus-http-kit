(ns luminus.http-server
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]))

(defonce http-server (atom nil))

(defn start [{:keys [handler init host port] :as opts}]
  (try
    (init)
    (reset! http-server
            (http-kit/run-server
              handler
              (dissoc opts :handler :init)))
    (log/info "server started on" host "port" port)
    (catch Throwable t
      (log/error t (str "server failed to start on" host "port" port)))))

(defn stop [http-server destroy]
  (destroy)
  (http-server :timeout 100)
  (log/info "HTTP server stopped"))
