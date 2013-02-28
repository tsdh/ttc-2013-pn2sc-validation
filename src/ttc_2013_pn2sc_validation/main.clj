(ns ttc-2013-pn2sc-validation.main
  (:use funnyqt.emf
        clojure.test
        [ttc-2013-pn2sc-validation.core :only [validate
                                               test-case-1-result-spec
                                               test-case-2-result-spec
                                               test-case-3-result-spec]]))

(defn print-usage [err]
  (binding [*out* *err*]
    (if err
      (.printStackTrace err)
      (println "You are using me wrongly!"))
    (println)
    (println "Usage: lein run <testcase> <statechart-xmi>")
    (println "  - <testcase> may be 1, 2, or 3")
    (println "  - <statechart-xmi> is an EMF XMI file containing the target statechart")
    (System/exit 1)))

(defn -main [& args]
  (when-not (= 2 (count args))
    (print-usage nil))
  (try
    (let [tc-no (Integer/parseInt (first args))
          model (load-model (second args))]
      (when (or (< tc-no 1) (> tc-no 3))
        (print-usage nil))
      (deftest validate-model
        (validate model
                  (case tc-no
                    1 test-case-1-result-spec
                    2 test-case-2-result-spec
                    3 test-case-3-result-spec)))
      (validate-model))
    (catch Exception err
      (print-usage err))))

