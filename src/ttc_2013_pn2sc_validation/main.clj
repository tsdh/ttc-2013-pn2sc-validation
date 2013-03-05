(ns ttc-2013-pn2sc-validation.main
  (:use funnyqt.emf
        clojure.test
        [ttc-2013-pn2sc-validation.core :only [validate
                                               test-case-1-result-spec
                                               test-case-2-result-spec
                                               test-case-3-result-spec
                                               test-case-4-result-spec
                                               test-case-5-result-spec
                                               test-case-6-result-spec
                                               test-case-7-result-spec
                                               test-case-8-result-spec
                                               test-case-9-result-spec
                                               test-case-10-result-spec
                                               test-case-11-result-spec
                                               performance-test-cases]]))

(defn print-usage [err]
  (binding [*out* *err*]
    (if err
      (.printStackTrace err)
      (println "You are using me wrongly!"))
    (println)
    (println "Usage: lein run <testcase> <statechart-xmi>")
    (println "  - <testcase> may be 1 to 11 for one of the 11 main testcases.")
    (println "    It may also be one of 200, 300, 400, 500, 1000, 2000, 3000,")
    (println "    4000, 5000, 10000, 20000, 40000, 80000, 100000, or 200000,")
    (println "    denoting one of the performance testcases.  For the performance")
    (println "    testcases, only the number of elements are checked.")
    (println "  - <statechart-xmi> is an EMF XMI file containing the target statechart.")
    (System/exit 1)))

(defn -main [& args]
  (when-not (= 2 (count args))
    (print-usage nil))
  (try
    (let [tc-no (Integer/parseInt (first args))
          model (load-model (second args))]
      (deftest validate-model
        (validate model
                  (case tc-no
                    1  test-case-1-result-spec
                    2  test-case-2-result-spec
                    3  test-case-3-result-spec
                    4  test-case-4-result-spec
                    5  test-case-5-result-spec
                    6  test-case-6-result-spec
                    7  test-case-7-result-spec
                    8  test-case-8-result-spec
                    9  test-case-9-result-spec
                    10 test-case-10-result-spec
                    11 test-case-11-result-spec
                    (200 300 400 500 1000 2000 3000 4000 5000
                         10000 20000 40000 80000
                         100000 200000) (performance-test-cases tc-no)
                    (print-usage nil))))
      (if (successful? (run-tests 'ttc-2013-pn2sc-validation.main))
        (println "The model passes the validator. :-)")))
    (catch Exception err
      (print-usage err))))

