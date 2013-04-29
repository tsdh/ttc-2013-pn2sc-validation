(ns ttc-2013-pn2sc-validation.core
  (:require [clojure.data :as d]
            clojure.pprint)
  (:use clojure.test
        clojure.walk
        funnyqt.emf
        funnyqt.protocols
        funnyqt.query))

;;* Load the metamodel

(load-metamodel "metamodel/StateCharts.ecore")

;;* The validation specs

(def test-case-1-result-spec
  [;; Does it resolve to a Statechart with unique top-AND-State?
   true
   ;; Expected element counts
   {:HyperEdge 7, :AND 4, :OR 7, :Basic 11}
   ;; Expected containment hierarchy
   #{[:AND
      #{[:OR
         #{[:HyperEdge "E17"] [:Basic "E9"]
           [:AND
            #{[:OR
               #{[:Basic "E0"] [:Basic "E1"] [:HyperEdge "E11"]
                 [:HyperEdge "E12"]
                 [:AND
                  #{[:OR
                     #{[:AND
                        #{[:OR
                           #{[:Basic "E7"] [:HyperEdge "E16"]
                             [:Basic "E8"]}]
                          [:OR #{[:Basic "E5"]}]}]
                       [:Basic "E6"] [:HyperEdge "E15"]}]
                    [:OR
                     #{[:Basic "E2"] [:Basic "E3"] [:Basic "E4"]
                       [:HyperEdge "E13"] [:HyperEdge "E14"]}]}]}]
              [:OR #{[:Basic "E10"]}]}]}]}]}
   ;; Expected HyperEdge rnext/next linkage
   {"E11" [#{"E1"} #{"E0"}],
    "E12" [#{"E2" "E6"} #{"E1"}],
    "E13" [#{"E3"} #{"E2"}],
    "E14" [#{"E4"} #{"E3"}],
    "E15" [#{"E5" "E7"} #{"E6"}],
    "E16" [#{"E8"} #{"E7"}],
    "E17" [#{"E9"} #{"E10" "E4" "E5" "E8"}]}])

(def test-case-2-result-spec
  [true
   {:HyperEdge 10, :AND 3, :OR 5, :Basic 12}
   #{[:AND
      #{[:OR
         #{[:Basic "E1"] [:Basic "E2"] [:HyperEdge "n53"]
           [:HyperEdge "n54"] [:HyperEdge "n55"]
           [:AND
            #{[:OR
               #{[:Basic "E0"]
                 [:AND
                  #{[:OR
                     #{[:Basic "pollicy unchecked"] [:HyperEdge "n51"]
                       [:Basic "policy checked"]}]
                    [:OR
                     #{[:HyperEdge "n52"] [:Basic "damage checked"]
                       [:Basic "damage unchecked"]}]}]
                 [:HyperEdge "E3"]}]
              [:OR #{[:Basic "received"]}]}]
           [:Basic "settled"] [:Basic "rejected"] [:Basic "archived"]
           [:Basic "assessed"] [:HyperEdge "n4C"] [:HyperEdge "n4D"]
           [:HyperEdge "n4E"] [:HyperEdge "n4F"]}]}]}
   {"n4F" [#{"rejected"} #{"E1"}],
    "n51" [#{"pollicy unchecked"} #{"policy checked"}],
    "n52" [#{"damage unchecked"} #{"damage checked"}],
    "n53" [#{"received" "E0"} #{"assessed"}],
    "n54" [#{"E2"} #{"received" "pollicy unchecked" "damage unchecked"}],
    "E3"  [#{"damage checked" "policy checked"} #{"E0"}],
    "n55" [#{"received" "E0"} #{"rejected"}],
    "n4C" [#{"E1"} #{"archived"}],
    "n4D" [#{"assessed"} #{"settled"}],
    "n4E" [#{"settled"} #{"E1"}]}])

(def test-case-3-result-spec
  [false
   {:HyperEdge 10, :AND 0, :OR 6, :Basic 10}
   #{[:OR
      #{[:Basic "take pictures"]
        [:Basic "patient walking to dermatology"]
        [:Basic "waiting for photographer"]
        [:HyperEdge "end photography session"]
        [:Basic "waiting for dermatologist"]
        [:HyperEdge "begin photography session"]
        [:HyperEdge "patient is sent to photographer"]}]
     [:HyperEdge "needs pics, cannot walk"]
     [:OR #{[:Basic "patient in hall"]}]
     [:OR #{[:Basic "biopsy ordered"]}]
     [:OR #{[:Basic "dermatologist session"]}]
     [:HyperEdge "dermatologist calls in patient"]
     [:HyperEdge "nurse picks up patient"]
     [:OR #{[:Basic "photographs ordered"]}]
     [:OR
      #{[:HyperEdge "eID signin"] [:HyperEdge "secretary signin"]
        [:Basic "patient arrived"] [:Basic "patient registered in HIS"]}]
     [:HyperEdge "needs pics, can walk"]
     [:HyperEdge "patient has high quality pics already"]}
   {"patient is sent to photographer"  [#{"patient walking to dermatology"}
                                        #{"waiting for photographer"}],
    "eID signin" [#{"patient arrived"} #{"patient registered in HIS"}],
    "needs pics, cannot walk" [#{"patient registered in HIS"}
                               #{"photographs ordered" "patient in hall"
                                 "biopsy ordered"}],
    "secretary signin" [#{"patient arrived"}
                        #{"patient registered in HIS"}],
    "end photography session" [#{"take pictures"}
                               #{"waiting for dermatologist"}],
    "dermatologist calls in patient" [#{"biopsy ordered"
                                        "waiting for dermatologist"}
                                      #{"dermatologist session"}],
    "nurse picks up patient" [#{"photographs ordered" "patient in hall"}
                              #{"waiting for photographer"}],
    "needs pics, can walk" [#{"patient registered in HIS"}
                            #{"photographs ordered"
                              "patient walking to dermatology"
                              "biopsy ordered"}],
    "begin photography session" [#{"waiting for photographer"}
                                 #{"take pictures"}],
    "patient has high quality pics already" [#{"patient registered in HIS"}
                                             #{"biopsy ordered"
                                               "waiting for dermatologist"}]}])

(def test-case-4-result-spec
  [false
   {:HyperEdge 8, :AND 1, :OR 5, :Basic 10}
   #{[:OR
      #{[:AND
         #{[:OR #{[:Basic "p2"] [:Basic "p5"] [:HyperEdge "t5"]}]
           [:OR #{[:HyperEdge "t6"] [:Basic "p3"] [:Basic "p6"]}]}]}]
     [:OR #{[:Basic "p1"]}]
     [:OR
      #{[:HyperEdge "t7"] [:HyperEdge "t8"] [:Basic "p10"] [:Basic "p4"]
        [:Basic "p7"] [:HyperEdge "t3"] [:Basic "p8"] [:HyperEdge "t4"]
        [:Basic "p9"]}]
     [:HyperEdge "t1"] [:HyperEdge "t2"]}
   {"t4" [#{"p9"} #{"p10"}],
    "t5" [#{"p2"} #{"p5"}],
    "t6" [#{"p3"} #{"p6"}],
    "t7" [#{"p4"} #{"p7"}],
    "t8" [#{"p7"} #{"p9"}],
    "t1" [#{"p1"} #{"p2" "p3" "p4"}],
    "t2" [#{"p5" "p6"} #{"p8"}],
    "t3" [#{"p8"} #{"p10"}]}])

(def test-case-5-result-spec
  [false
   {:HyperEdge 3, :AND 0, :OR 2, :Basic 4}
   #{[:OR #{[:Basic "p1"] [:Basic "p3"] [:HyperEdge "t2"]}]
     [:OR #{[:Basic "p2"] [:Basic "p4"] [:HyperEdge "t3"]}]
     [:HyperEdge "t1"]}
   {"t1" [#{"p1"} #{"p2" "p3"}],
    "t2" [#{"p3"} #{"p1"}],
    "t3" [#{"p2"} #{"p4"}]}] )

(def test-case-6-result-spec
  [false
   {:HyperEdge 2, :AND 1, :OR 4, :Basic 4}
   #{[:OR #{[:Basic "p4"]}] [:HyperEdge "t1"]
     [:OR
      #{[:AND #{[:OR #{[:Basic "p2"]}] [:OR #{[:Basic "p3"]}]}]
        [:Basic "p1"] [:HyperEdge "t2"]}]}
   {"t1" [#{"p1"} #{"p2" "p3" "p4"}]
    "t2" [#{"p2" "p3"} #{"p1"}]}])

(def test-case-7-result-spec
  [true
   {:HyperEdge 7, :AND 3, :OR 5, :Basic 10}
   #{[:AND
      #{[:OR
         #{[:HyperEdge "t7"] [:Basic "p10"] [:Basic "p1"]
           [:AND
            #{[:OR
               #{[:HyperEdge "t6"] [:Basic "p4"] [:Basic "p7"]
                 [:HyperEdge "t4"] [:Basic "p9"]}]
              [:OR
               #{[:AND
                  #{[:OR
                     #{[:Basic "p2"] [:Basic "p5"] [:HyperEdge "t2"]}]
                    [:OR
                     #{[:Basic "p3"] [:Basic "p6"] [:HyperEdge "t3"]}]}]
                 [:Basic "p8"] [:HyperEdge "t5"]}]}]
           [:HyperEdge "t1"]}]}]}
   {"t4" [#{"p4"} #{"p7"}],
    "t5" [#{"p5" "p6"} #{"p8"}],
    "t6" [#{"p7"} #{"p9"}],
    "t7" [#{"p8" "p9"} #{"p10"}],
    "t1" [#{"p1"} #{"p2" "p3" "p4"}],
    "t2" [#{"p2"} #{"p5"}],
    "t3" [#{"p3"} #{"p6"}]}] )

(def test-case-8-result-spec
  [true
   {:HyperEdge 8, :AND 4, :OR 7, :Basic 12}
   #{[:AND
      #{[:OR
         #{[:Basic "p12"]
           [:AND
            #{[:OR
               #{[:AND
                  #{[:OR
                     #{[:HyperEdge "t7"] [:Basic "p10"] [:Basic "p7"]}]
                    [:OR
                     #{[:HyperEdge "t8"] [:Basic "p11"] [:Basic "p8"]}]}]
                 [:Basic "p4"] [:HyperEdge "t3"]}]
              [:OR
               #{[:AND
                  #{[:OR
                     #{[:Basic "p2"] [:Basic "p5"] [:HyperEdge "t5"]}]
                    [:OR
                     #{[:HyperEdge "t6"] [:Basic "p3"] [:Basic "p6"]}]}]
                 [:HyperEdge "t2"] [:Basic "p9"]}]}]
           [:Basic "p1"] [:HyperEdge "t1"] [:HyperEdge "t4"]}]}]}
   {"t4" [#{"p9" "p10" "p11"} #{"p12"}],
    "t5" [#{"p2"} #{"p5"}],
    "t6" [#{"p3"} #{"p6"}],
    "t7" [#{"p7"} #{"p10"}],
    "t8" [#{"p8"} #{"p11"}],
    "t1" [#{"p1"} #{"p2" "p3" "p4"}],
    "t2" [#{"p5" "p6"} #{"p9"}],
    "t3" [#{"p4"} #{"p7" "p8"}]}])

(def test-case-9-result-spec
  [false
   {:HyperEdge 6, :AND 0, :OR 10, :Basic 10}
   #{[:HyperEdge "t6"] [:OR #{[:Basic "p1"]}] [:OR #{[:Basic "p2"]}]
     [:OR #{[:Basic "p3"]}] [:OR #{[:Basic "p4"]}] [:OR #{[:Basic "p5"]}]
     [:OR #{[:Basic "p6"]}] [:OR #{[:Basic "p7"]}] [:OR #{[:Basic "p8"]}]
     [:OR #{[:Basic "p9"]}] [:OR #{[:Basic "p10"]}] [:HyperEdge "t1"]
     [:HyperEdge "t2"] [:HyperEdge "t3"] [:HyperEdge "t4"]
     [:HyperEdge "t5"]}
   {"t4" [#{"p4" "p5"} #{"p6" "p8"}],
    "t5" [#{"p5" "p7"} #{"p9"}],
    "t6" [#{"p8" "p9"} #{"p10"}],
    "t1" [#{"p1"} #{"p2" "p3"}],
    "t2" [#{"p2"} #{"p4" "p5"}],
    "t3" [#{"p3" "p6"} #{"p5" "p7"}]}])

(def test-case-10-result-spec
  [false
   {:HyperEdge 6, :AND 0, :OR 9, :Basic 9}
   #{[:HyperEdge "t6"] [:OR #{[:Basic "p1"]}] [:OR #{[:Basic "p2"]}]
     [:OR #{[:Basic "p3"]}] [:OR #{[:Basic "p4"]}] [:OR #{[:Basic "p5"]}]
     [:OR #{[:Basic "p6"]}] [:OR #{[:Basic "p8"]}] [:OR #{[:Basic "p9"]}]
     [:OR #{[:Basic "p10"]}] [:HyperEdge "t1"] [:HyperEdge "t2"]
     [:HyperEdge "t3"] [:HyperEdge "t4"] [:HyperEdge "t5"]}
   {"t4" [#{"p4" "p5"} #{"p8"}],
    "t5" [#{"p5" "p6"} #{"p9"}],
    "t6" [#{"p8" "p9"} #{"p10"}],
    "t1" [#{"p1"} #{"p2" "p3"}],
    "t2" [#{"p2"} #{"p4" "p5"}],
    "t3" [#{"p3"} #{"p5" "p6"}]}])

(def test-case-11-result-spec
  [true
   {:HyperEdge 5, :AND 2, :OR 3, :Basic 6}
   #{[:AND
      #{[:OR
         #{[:HyperEdge "t7"]
           [:AND
            #{[:OR #{[:Basic "p2"] [:Basic "p3"] [:HyperEdge "t4"]}]
              [:OR #{[:Basic "p4"] [:Basic "p6"] [:HyperEdge "t3"]}]}]
           [:Basic "p1"] [:HyperEdge "t1"] [:Basic "p7"]
           [:HyperEdge "t5"]}]}]}
   {"t4" [#{"p2"} #{"p3"}],
    "t5" [#{"p3" "p6"} #{"p7"}],
    "t7" [#{"p7"} #{"p3" "p6"}],
    "t1" [#{"p1"} #{"p2" "p4"}],
    "t3" [#{"p4"} #{"p6"}]}])

(def performance-test-cases
  {200    [true {:Basic 163,    :HyperEdge 126,    :OR 63,     :AND 9}]
   300    [true {:Basic 244,    :HyperEdge 189,    :OR 94,     :AND 13}]
   400    [true {:Basic 325,    :HyperEdge 252,    :OR 125,    :AND 17}]
   500    [true {:Basic 406,    :HyperEdge 315,    :OR 156,    :AND 21}]
   1000   [true {:Basic 811,    :HyperEdge 630,    :OR 311,    :AND 41}]
   2000   [true {:Basic 1620,   :HyperEdge 1259,   :OR 621,    :AND 81}]
   3000   [true {:Basic 2429,   :HyperEdge 1888,   :OR 931,    :AND 121}]
   4000   [true {:Basic 3238,   :HyperEdge 2517,   :OR 1241,   :AND 161}]
   5000   [true {:Basic 4047,   :HyperEdge 3146,   :OR 1551,   :AND 201}]
   10000  [true {:Basic 8092,   :HyperEdge 6291,   :OR 3101,   :AND 401}]
   20000  [true {:Basic 16183,  :HyperEdge 12582,  :OR 6201,   :AND 801}]
   40000  [false {:Basic 32365,  :HyperEdge 25164,  :OR 17392,  :AND 1200}]
   80000  [false {:Basic 64729,  :HyperEdge 50328,  :OR 44765,  :AND 1600}]
   100000 [false {:Basic 80911,  :HyperEdge 62910,  :OR 50965,  :AND 2400}]
   200000 [false {:Basic 161821, :HyperEdge 125820, :OR 121892, :AND 3199}]})

;;* The validation code

(declare make-content-spec)
(defn spec-for [o]
  (type-case o
    'Statechart (first (make-content-spec (econtents o) #{}))
    'Basic      [:Basic (eget o :name)]
    'HyperEdge  [:HyperEdge (eget o :name)]
    'OR         [:OR (make-content-spec (econtents o) #{})]
    'AND        [:AND (make-content-spec (econtents o) #{})]))

(defn make-content-spec [tops v]
  (into v (map spec-for tops)))

(defn tops [sc]
  (remove #(econtainer %) (eallobjects sc)))

(defn validate-counts [sc counts]
  (is (<= (count (eallobjects sc 'Statechart)) 1)
      (format "ERROR: There should be at most one Statechart, but there are %s."
              (count (eallobjects sc 'Statechart)) 1))
  (doseq [[tkw num] counts
          :let [t (symbol (name tkw))
                c (count (eallobjects sc t))]]
    (is (= num c)
        (format "ERROR: There should be %s %s-elements, but there are %s."
                num t c))))

(defn validate-containment [sc expected-content-spec]
  (let [actual-content-spec (make-content-spec (tops sc) #{})
        [exp act both :as diff] (d/diff expected-content-spec actual-content-spec)]
    (is (and (empty? exp) (empty? act))
        (with-out-str
          (println "Expected containment hierarchy was:")
          (clojure.pprint/pprint exp)
          (println "But actual containment hierarchy was:")
          (clojure.pprint/pprint act)))))

(defn x-names [x state]
  (set (map #(eget % :name) (eget state x))))
(def rnext-names (partial x-names :rnext))
(def next-names (partial x-names :next))

(defn hyperedge [sc n]
  (first (filter #(= n (eget % :name))
                 (eallobjects sc 'HyperEdge))))

(defn validate-hyperedges [sc he-spec]
  (doseq [[hen [rnexts nexts]] he-spec]
    (if-let [he (hyperedge sc hen)]
      (do
        (is (= rnexts (rnext-names he))
            (format "ERROR: The HyperEdge \"%s\" should have :rnext %s but has %s."
                    hen rnexts (rnext-names he)))
        (is (= nexts (next-names he))
            (format "ERROR: The HyperEdge \"%s\" should have :next %s but has %s."
                    hen nexts (next-names he))))
      (let [aHyperEdge 'aHyperEdge]
        (is (= aHyperEdge nil)
            (format "ERROR: There's no HyperEdge with name \"%s\"." hen))))))

(defn contained-hyperedges? [sc]
  (let [r (exists? #(eget % :rcontains) (eallobjects sc 'HyperEdge))]
    (when-not r
      (println "WARNING: Your HyperEdges aren't contained by some Compound state.")
      (println "As an extension, they should be contained by the nearest upper")
      (println "Compound state that contains all rnext/next States of the Hyperedge.")
      (println "=> The HyperEdges will be ignored in the containment check."))
    r))

(defn toplevel-hyperedges [form]
  (let [hes (atom #{})]
    (into (clojure.walk/postwalk
           (fn [x]
             (if (set? x) #_(and (vector? x) (vector? (first x)))
               (set (remove (fn [e]
                              (if (= :HyperEdge (first e))
                                (swap! hes conj e)
                                false))
                            x))
               x))
           form)
          @hes)))


(defn validate [sc [unique-top counts containment-spec hyperedge-spec]]
  (when unique-top
    (let [no-scs (count (eallobjects sc 'Statechart))
          no-tops (count (filter #(not (eget % :rcontains)) (eallobjects sc 'AND)))]
      (testing "Testing the uniqueness of Statechart and AND-topState"
        (is (= no-scs 1)
            (format "ERROR: There's not one unique Statechart (#Statechart = %s)." no-scs))
        (is (= no-tops 1)
            (format "ERROR: There's not one unique AND-topState (#topANDStates = %s)." no-tops)))))
  (testing "Testing the instance counts of all metaclasses"
    (validate-counts sc counts))
  (when containment-spec
    (testing "Testing the containment hierarchy"
      (let [contained-hes (contained-hyperedges? sc)]
        (validate-containment sc (if contained-hes
                                   containment-spec
                                   (toplevel-hyperedges containment-spec))))))
  (when hyperedge-spec
    (testing "Testing the rnext/next references of HyperEdges"
      (validate-hyperedges sc hyperedge-spec))))

