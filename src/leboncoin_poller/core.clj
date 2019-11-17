(ns leboncoin-poller.core
 (:require  [clojure.tools.logging :as log]
            [clojure.string :as str]
            [cheshire.core :as json]
            [clj-http.client :as http]) )

(defonce my-cs (clj-http.cookies/cookie-store))


(def search-body 
  "{\"limit\":35,\"limit_alu\":3,\"filters\":{\"category\":{},\"enums\":{\"ad_type\":[\"offer\"]},\"location\":{\"locations\":[]},\"keywords\":{\"text\":\"RFP1000\"},\"ranges\":{}},\"user_id\":\"83373d7d-06a5-4505-bf0f-fec08f928efc\",\"store_id\":\"6348695\"}"
  )

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

(defn complete []
  (log/info "Complete for RFPlayer")
  (:body (http/get "https://api.leboncoin.fr/api/parrot/v1/complete?q=RFP1000" {
                                                              :headers h
                                                              :cookie-store        my-cs})))
(defn location []
  (log/info "Location for RFPlayer")
  (:body (http/post "https://api.leboncoin.fr/api/parrot/v3/complete/location" {:body "{\"text\":\"\",\"context\":[]}"
                                                              :insecure?           true
                                                              :headers h
                                                              :content-type :json
                                                              :cookie-store        my-cs})))
(defn search []
  (log/info "Search for RFPlayer")
  (:body (http/post "https://api.leboncoin.fr/finder/search" {:body search-body
                                                              :insecure?           true
                                                              :headers h
                                                              :content-type :json
                                                              :cookie-store        my-cs})))

(defn search-results []
  (json/parse-string (search) true))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;;(complete)
  ;;(location)
  (println (:ads (search-results)))
  (while true
  (for [ad (:ads (search-results))]
    (println (str "Subject: " (:subject ad)))
  )
  (Thread/sleep 10000) 
  ))
