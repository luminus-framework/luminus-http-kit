(ns luminus.http-server
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]))

(defonce http-server (atom nil))

(defn start [handler init port]
  (if @http-server
    (log/error "HTTP server is already running!")
    (do
      (init)
      (reset! http-server
              (http-kit/run-server
               app
               {:port port}))
      (log/info "server started on port:" (:port @http-server)))))

(defn stop [destroy]
  (when @http-server
    (destroy)
    (@http-server :timeout 100)
    (reset! http-server nil)
    (log/info "HTTP server stopped")))
