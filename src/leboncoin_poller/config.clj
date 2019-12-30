(ns leboncoin-poller.config
  (:require [environ.core :refer [env]]))

(def smtp-config {:user (env :smtp-user)
                  :host (env :smtp-host)
                  :pass (env :smtp-pass)
                  :port 587
                  :tls true})
(def email-from (env :email-from))
(def email-to (env :email-to))
