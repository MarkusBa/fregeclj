(ns fregeclj.database
  (:require [fregeclj.config :as cf]
            [clojure.tools.logging :as log]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as cljstr :refer [blank?]]
            [yesql.core :refer [defquery]])
  ;(:import [fregeclj Fregeclj])
  (:gen-class
    :name com.fregeclj.DatabaseAccess
    :methods [#^{:static true} [existingamount [long String Object] Long]
              #^{:static true} [updateitem [long String long Object] String] ;; java.math.BigInteger actually
              #^{:static true} [insertitem [String long double long Object] String]
              #^{:static true} [deleteitem [long String Object] Integer]
              #^{:static true} [wrapintransaction [frege.runtime.Lambda] Object]
              #^{:static true} [getitems [long] clojure.lang.LazySeq]]))

;(ann db-spec Map)
(def db-spec (cf/load-config "resources/config.clj"))

;(ann ^:no-check get-items-query [Map Integer -> List])
(defquery get-items-query "sql/select.sql")

;(ann get-items [Integer -> List])
(defn get-items [idplayer]
    (get-items-query db-spec idplayer))

(defn -getitems [idplayer]
  (get-items idplayer))

;(ann ^:no-check existing-amount [Map Integer String -> List])
(defquery existing-amount "sql/existingamount.sql")

(defn -existingamount [idplayer symbol connection]
  (:amount (first (existing-amount connection idplayer symbol))))

;(ann ^:no-check update-item! [Map Double String Integer -> Integer])
(defquery update-item! "sql/updateitem.sql")

(defn -updateitem [amount symbol idplayer connection]
  (update-item! connection amount symbol idplayer)
   "success")

;(ann ^:no-check insert-item! [Map String Double Double Integer java.sql.Timestamp -> Integer])
(defquery insert-item! "sql/insertitem.sql")

(defn -insertitem [symbol amount price idplayer connection]
  (log/info (str "insertitem called with" symbol amount price idplayer))
  (insert-item! connection symbol amount price idplayer (java.sql.Timestamp. (System/currentTimeMillis)))
  "success")

;(ann ^:no-check delete-item! [Map Integer String -> Integer])
(defquery delete-item! "sql/deleteitem.sql")

(defn -deleteitem [idplayer symbol connection]
  (insert-item! connection idplayer symbol))

(defn -wrapintransaction [frege-lambda]
  (jdbc/with-db-transaction [connection db-spec]
      (.eval frege-lambda connection)))

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
  (import fregeclj.Fregeclj)
  (Fregeclj/testFrege)
  )

(comment
  (:gen-class
    :name com.fregeclj.DatabaseAccess
    :methods [#^{:static true} [getitems [int] java.util.List]
              #^{:static true} [testme [int] int]]))

