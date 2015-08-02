(ns leiningen.uberjarbeforefrege
  (:require [leiningen.compile :as lc]
            [leiningen.uberjar :as uj]
            [leiningen.fregec :as lf]))

;; inspired from http://stackoverflow.com/questions/10644087/how-to-add-a-hook-into-the-uberjar-process-building-with-lein

(defn uberjarbeforefrege [project]
  (lc/compile project)
  (lf/fregec project)
  (uj/uberjar project))