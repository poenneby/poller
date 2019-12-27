(ns leboncoin-poller.core-test
  (:require [leboncoin-poller.core :refer :all]
            [midje.sweet :refer :all]
            [clojure.string :as str]))

(facts "about `split`"
  (str/split "a/b/c" #"/") => ["a" "b" "c"])
