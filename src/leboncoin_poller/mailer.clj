(ns leboncoin-poller.mailer
  (:require [leboncoin-poller.config :as c]
            [postal.core :as p]))

(defn send-message [subject body] 
  (p/send-message c/smtp-config 
                {:from c/email-from 
                 :to c/email-to
                 :subject subject 
                 :body body
                 }))

