(ns fregeclj.database
  (:require [fregeclj.config :as cf]
            [clojure.tools.logging :as log]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as cljstr :refer [blank?]]
            [yesql.core :refer [defquery]])
  (:import [fregeclj Fregeclj]))

;(ann db-spec Map)
(def db-spec (cf/load-config "resources/config.clj"))

;(ann ^:no-check get-items-query [Map Integer -> List])
(defquery get-items-query "sql/select.sql")

;(ann get-items [Integer -> List])
(def get-items
  (fn [idplayer]
    (get-items-query db-spec idplayer)))

(defn -getitems [idplayer]
  (get-items idplayer))

(defn -testme [i]
  (+ i 1))

(defn testFrege []
  (Fregeclj/testFrege (int 42)))

;(ann ^:no-check existing-amount [Map Integer String -> List])
(defquery existing-amount "sql/existingamount.sql")

;(ann ^:no-check update-item! [Map Double String Integer -> Integer])
(defquery update-item! "sql/updateitem.sql")

;(ann ^:no-check insert-item! [Map String Double Double Integer java.sql.Timestamp -> Integer])
(defquery insert-item! "sql/insertitem.sql")

;(ann ^:no-check delete-item! [Map Integer String -> Integer])
(defquery delete-item! "sql/deleteitem.sql")

;; (db/order "YHOO" 2 44.52 "1") -> 1
;(ann ^:no-check order [String Double Double String -> Integer])
(defn order [ordersymbol amount price idpl]
  (jdbc/with-db-transaction [connection db-spec]
                            (let [idplayer (Integer/parseInt idpl)
                                  money (:amount (first (existing-amount connection idplayer "CASH")))
                                  costs (* amount price)]
                              (log/info money)
                              (when (and (not (nil? money)) (>= money costs) )
                                (update-item! connection (- money costs) "CASH" idplayer)
                                (if-let [existingamount (:amount (first (existing-amount connection idplayer ordersymbol)))]
                                  (update-item! connection (+ existingamount amount) ordersymbol idplayer)
                                  (insert-item! connection ordersymbol amount price idplayer (java.sql.Timestamp. (System/currentTimeMillis))))))))

;; (db/sell "YHOO" 2 44.52 "1") -> 1
;(ann ^:no-check sell [String Double Double String -> Integer])
(defn sell [sellsymbol amount price idpl]
  (jdbc/with-db-transaction [connection db-spec]
                            (let [idplayer (Integer/parseInt idpl)
                                  existingamount (:amount (first (existing-amount connection idplayer sellsymbol)))
                                  existingcash (:amount (first (existing-amount connection idplayer "CASH")))]
                              (when (and (not (nil? existingamount)) (>= existingamount amount))
                                (update-item! connection (+ existingcash (* amount price)) "CASH" idplayer)
                                (if (> existingamount amount)
                                  (update-item! connection (- existingamount amount) sellsymbol idplayer)
                                  (delete-item! connection idplayer sellsymbol))))))

(comment 
  (require '[fregeclj.database :as db])
  (db/get-items 1)
  (import com.fregeclj.DatabaseAccess)
  (DatabaseAccess/getitems 1)
  )

(comment
  (:gen-class
    :name com.fregeclj.DatabaseAccess
    :methods [#^{:static true} [getitems [int] java.util.List]
              #^{:static true} [testme [int] int]]))

