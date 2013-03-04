(ns ttc-2013-pn2sc-validation.core
  (:use clojure.test
        clojure.walk
        funnyqt.emf
        funnyqt.protocols
        funnyqt.query))

;;* Load the metamodel

;;(load-metamodel "metamodel/PetriNets.ecore")
(load-metamodel "metamodel/StateCharts.ecore")

;;* The validation specs

(def test-case-1-result-spec
  [;; there's a unique topAND with containing Statechart
   true
   ;; Element counts
   {:HyperEdge 7, :AND 4, :Statechart 1, :OR 7, :Basic 11}
   ;; The containment structure below the Statechart as a map of maps
   [[:AND
     [[:OR
       [[:AND
         [[:OR
           [[:Basic "E10"]]]
          [:OR
           [[:HyperEdge "E12"]
            [:AND
             [[:OR
               [[:Basic "E2"]
                [:HyperEdge "E13"]
                [:Basic "E3"]
                [:HyperEdge "E14"]
                [:Basic "E4"]]]
              [:OR
               [[:AND
                 [[:OR
                   [[:Basic "E5"]]]
                  [:OR
                   [[:Basic "E7"]
                    [:HyperEdge "E16"]
                    [:Basic "E8"]]]]]
                [:Basic "E6"]
                [:HyperEdge "E15"]]]]]
            [:Basic "E0"]
            [:HyperEdge "E11"]
            [:Basic "E1"]]]]]
        [:HyperEdge "E17"]
        [:Basic "E9"]]]]]]
   ;; The HyperEdge connections as a map:
   ;; HyperEdge -> [rnext-set next-set]
   {"E11" [#{"E1"}      #{"E0"}]
    "E12" [#{"E2" "E6"} #{"E1"}]
    "E13" [#{"E3"}      #{"E2"}]
    "E14" [#{"E4"}      #{"E3"}]
    "E15" [#{"E5" "E7"} #{"E6"}]
    "E16" [#{"E8"}      #{"E7"}]
    "E17" [#{"E9"}      #{"E10" "E4" "E5" "E8"}]}])

(def test-case-2-result-spec
  [;; there's a unique topAND with containing Statechart
   true
   ;; Element counts
   {:HyperEdge 10, :AND 3, :Statechart 1, :OR 5, :Basic 12}
   ;; The containment structure below the Statechart as a map of maps
   [[:AND
     [[:OR
       [[:HyperEdge "n54"]
        [:HyperEdge "n53"]
        [:HyperEdge "n55"]
        [:Basic "assessed"]
        [:HyperEdge "n4D"]
        [:Basic "settled"]
        [:HyperEdge "n4E"]
        [:Basic "rejected"]
        [:HyperEdge "n4F"]
        [:Basic "archived"]
        [:HyperEdge "n4C"]
        [:Basic "E1"]
        [:Basic "E2"]
        [:AND
         [[:OR
           [[:Basic "received"]]]
          [:OR
           [[:HyperEdge "E3"]
            [:Basic "E0"]
            [:AND
             [[:OR
               [[:Basic "policy checked"]
                [:HyperEdge "n51"]
                [:Basic "pollicy unchecked"]]]
              [:OR
               [[:Basic "damage checked"]
                [:HyperEdge "n52"]
                [:Basic "damage unchecked"]]]]]]]]]]]]]]
   ;; The HyperEdge connections as a map:
   ;; HyperEdge -> [rnext-set next-set]
   {"n54" [#{"E2"} #{"pollicy unchecked" "received" "damage unchecked"}]
    "n51" [#{"pollicy unchecked"} #{"policy checked"}]
    "n52" [#{"damage unchecked"} #{"damage checked"}]
    "E3"  [#{"policy checked" "damage checked"} #{"E0"}]
    "n55" [#{"received" "E0"} #{"rejected"}]
    "n53" [#{"received" "E0"} #{"assessed"}]
    "n4F" [#{"rejected"} #{"E1"}]
    "n4D" [#{"assessed"} #{"settled"}]
    "n4C" [#{"E1"} #{"archived"}]
    "n4E" [#{"settled"} #{"E1"}]}])

(def test-case-3-result-spec
  [;; This one has no unique top AND state
   false
   ;; Element counts
   {:HyperEdge 10, :AND 0, :Statechart 0, :OR 6, :Basic 10}
   ;; Therefore we have multiple top-level elements
   [[:OR
     [[:Basic "patient arrived"]
      [:HyperEdge "eID signin"]
      [:HyperEdge "secretary signin"]
      [:Basic "patient registered in HIS"]]]
    [:HyperEdge "needs pics, cannot walk"]
    [:HyperEdge "needs pics, can walk"]
    [:HyperEdge "patient has high quality pics already"]
    [:OR
     [[:Basic "patient walking to dermatology"]
      [:HyperEdge "patient is sent to photographer"]
      [:Basic "take pictures"]
      [:HyperEdge "begin photography session"]
      [:HyperEdge "end photography session"]
      [:Basic "waiting for photographer"]
      [:Basic "waiting for dermatologist"]]]
    [:OR
     [[:Basic "photographs ordered"]]]
    [:HyperEdge "nurse picks up patient"]
    [:OR
     [[:Basic "biopsy ordered"]]]
    [:HyperEdge "dermatologist calls in patient"]
    [:OR
     [[:Basic "patient in hall"]]]
    [:OR
     [[:Basic "dermatologist session"]]]]
   ;; The HyperEdge connections as a map:
   ;; HyperEdge -> [rnext-set next-set]
   {"eID signin" [#{"patient arrived"} #{"patient registered in HIS"}]
    "secretary signin" [#{"patient arrived"} #{"patient registered in HIS"}]
    "patient has high quality pics already" [#{"patient registered in HIS"}
                                             #{"biopsy ordered" "waiting for dermatologist"}]
    "needs pics, cannot walk" [#{"patient registered in HIS"}
                               #{"biopsy ordered" "photographs ordered" "patient in hall"}]
    "needs pics, can walk" [#{"patient registered in HIS"}
                            #{"biopsy ordered" "patient walking to dermatology"
                              "photographs ordered"}]
    "patient is sent to photographer" [#{"patient walking to dermatology"}
                                       #{"waiting for photographer"}]
    "nurse picks up patient" [#{"photographs ordered" "patient in hall"}
                              #{"waiting for photographer"}]
    "begin photography session" [#{"waiting for photographer"}
                                 #{"take pictures"}]
    "end photography session" [#{"take pictures"}
                               #{"waiting for dermatologist"}]
    "dermatologist calls in patient" [#{"biopsy ordered" "waiting for dermatologist"}
                                      #{"dermatologist session"}]}])

(def performance-test-cases
  {200    [false {:Basic 163,    :HyperEdge 126,    :OR 65,     :AND 6}]
   300    [false {:Basic 244,    :HyperEdge 189,    :OR 97,     :AND 9}]
   400    [false {:Basic 325,    :HyperEdge 252,    :OR 129,    :AND 12}]
   500    [false {:Basic 406,    :HyperEdge 315,    :OR 161,    :AND 15}]
   1000   [false {:Basic 811,    :HyperEdge 630,    :OR 321,    :AND 30}]
   2000   [false {:Basic 1620,   :HyperEdge 1259,   :OR 641,    :AND 60}]
   3000   [false {:Basic 2429,   :HyperEdge 1888,   :OR 961,    :AND 90}]
   4000   [false {:Basic 3238,   :HyperEdge 2517,   :OR 1281,   :AND 120}]
   5000   [false {:Basic 4047,   :HyperEdge 3146,   :OR 1601,   :AND 150}]
   10000  [false {:Basic 8092,   :HyperEdge 6291,   :OR 3201,   :AND 300}]
   20000  [false {:Basic 16183,  :HyperEdge 12582,  :OR 6401,   :AND 600}]
   40000  [false {:Basic 32365,  :HyperEdge 25164,  :OR 17791,  :AND 800}]
   80000  [false {:Basic 64729,  :HyperEdge 50328,  :OR 45363,  :AND 1000}]
   100000 [false {:Basic 80911,  :HyperEdge 62910,  :OR 51763,  :AND 1600}]
   200000 [false {:Basic 161821, :HyperEdge 125820, :OR 123088, :AND 2000}]})

;;* The validation code

(defn validate-counts [sc counts]
  (doseq [[tkw num] counts
          :let [t (symbol (name tkw))
                c (count (eallobjects sc t))]]
    (is (= num c)
        (format "ERROR: There should be %s %s-elements, but there are %s."
                num t c))))

(defn top-states [sc]
  (filter #(not (eget % :rcontains)) (eallobjects sc 'State)))

(declare validate-containment)  ;; Forward declaration

(defn matches [node [type x]]
  (type-cond node
    'Basic (and (= type :Basic)
                (= x (eget node :name)))
    'HyperEdge (and (= type :HyperEdge)
                    (= x (eget node :name)))
    'AND (and (= type :AND)
              (validate-containment node (eget node :contains) x))
    'OR (and (= type :OR)
             (validate-containment node (eget node :contains) x))))

(defn validate-containment [parent content-nodes content-specs]
  (forall? (fn [t]
             (exists1? #(matches t %) content-specs))
           content-nodes))

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
  (let [hes (atom [])]
    (into (clojure.walk/postwalk
           (fn [x]
             (if (and (vector? x) (vector? (first x)))
               (vec (remove (fn [e]
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
        (is (validate-containment nil (top-states sc)
                                  (if contained-hes
                                    containment-spec
                                    (toplevel-hyperedges containment-spec)))
            "ERROR: Something's wrong with the containment hierarchy!"))))
  (when hyperedge-spec
    (testing "Testing the rnext/next references of HyperEdges"
      (validate-hyperedges sc hyperedge-spec))))

