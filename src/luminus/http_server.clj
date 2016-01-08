(ns luminus.http-server
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]))

(defonce http-server (atom nil))

(defn start [{:keys [handler init port] :as opts}]
  (if @http-server
    (log/error "HTTP server is already running!")
    (try
      (init)
      (reset! http-server
              (http-kit/run-server
               handler
               (dissoc opts :handler :init)))
      (log/info "server started on port" port)
      (catch Throwable t
        (log/error t (str "server failed to start on port " port))))))

(defn stop [destroy]
  (when @http-server
    (destroy)
    (@http-server :timeout 100)
    (reset! http-server nil)
    (log/info "HTTP server stopped")))
