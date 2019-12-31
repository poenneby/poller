(ns leboncoin-poller.core
  (:gen-class)
  (:require [leboncoin-poller.mailer :as m]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]))

(defonce my-cs (clj-http.cookies/cookie-store))

(def search-keywords (env :search-keywords))

(defn search-body [search-keywords]
  (str "{\"limit\":35,\"limit_alu\":3,\"filters\":{\"category\":{},\"enums\":{\"ad_type\":[\"offer\"]},\"location\":{\"locations\":[]},\"keywords\":{\"text\":\"" search-keywords "\"},\"ranges\":{}},\"user_id\":\"83373d7d-06a5-4505-bf0f-fec08f928efc\",\"store_id\":\"6348695\"}"
  ))

(def h {
        "Accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"
        "Accept-Encoding" "gzip, deflate, br"
        "Accept-Language" "en,fr-FR;q=0.9,fr;q=0.8,en-US;q=0.7,sv-SE;q=0.6,sv;q=0.5"
        "Cache-Control" "no-cache"
        "Connection" "keep-alive"
        "Host" "api.leboncoin.fr"
        "Pragma" "no-cache"
        "Sec-Fetch-Mode" "navigate"
        "Sec-Fetch-User" "?1"
        "Upgrade-Insecure-Requests" 1
        "User-Agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36"
        })

(defn search []
  (log/info "Search for " search-keywords)
  (:body (http/post "https://api.leboncoin.fr/finder/search" {:body (search-body search-keywords)
                                                              :insecure?           true
                                                              :headers h
                                                              :content-type :json
                                                              :cookie-store        my-cs})))

(defn search-results []
  (json/parse-string (search) true))

(defn handler [request]
  (m/send-message "New results" (apply str (let [ads (:ads (search-results))]
    (for [ad ads] (str "First published: " (:first_publication_date ad) "\nLast updated: " (:index_date ad) "\nSubject: " (:subject ad) "\n\n")))))
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Checked"})

(defn -main
  [& args]
 (run-jetty handler {:port (Integer/valueOf (or (System/getenv "PORT") "3000")) :join? false}))
