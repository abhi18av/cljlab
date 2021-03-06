;; Copyright 2014 Alastair Pharo

;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at

;;     http://www.apache.org/licenses/LICENSE-2.0

;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns cljlab.util-test
  (:use midje.sweet)
  (:require [cljlab.basic :as b]
            [cljlab.expr :as expr]
            [cljlab.util :as util]))

(fact "`clear` executes `clear` commands on the lab"
      (util/clear :my-lab :a :b :c) => :ok
      (provided
       (b/eval :my-lab "clear a b c") => :ok))

(facts "about `with-placeholders`"
       (fact "it evaluates each form and returns the last one"
             (util/with-placeholders :my-lab []
               (println "test1")
               (println "test2")) => :ok
               (provided
                (println "test1") => nil
                (println "test2") => :ok))

       (fact "it calls `clear` on the list of placeholders after evaluating"
             (util/with-placeholders :my-lab [:pl1 :pl2] :ok) => :ok
               (provided
                (util/clear :my-lab :pl1 :pl2) => nil)))

(fact "`call-fn-with-basic-vals` does a set, eval, get loop"
      (util/call-fn-with-basic-vals :my-lab 0 1 :sum [1] [2] [3]) => :ok
      (provided
       (b/set :my-lab :cljlab__0__in0__ [1]) => nil
       (b/set :my-lab :cljlab__0__in1__ [2]) => nil
       (b/set :my-lab :cljlab__0__in2__ [3]) => nil
       (util/call-fn-with-vars :my-lab 0 1 :sum :cljlab__0__in0__ :cljlab__0__in1__ :cljlab__0__in2__) => :ok
       (b/eval :my-lab "clear cljlab__0__in0__ cljlab__0__in1__ cljlab__0__in2__") => nil)

      (util/call-fn-with-basic-vals :my-lab 0 1 :sum [1] [2] [3]) => '((4.0))
      (provided
       (b/set :my-lab :cljlab__0__in0__ [1]) => nil
       (b/set :my-lab :cljlab__0__in1__ [2]) => nil
       (b/set :my-lab :cljlab__0__in2__ [3]) => nil
       (b/eval :my-lab "[cljlab__0__out0__] = sum(cljlab__0__in0__,cljlab__0__in1__,cljlab__0__in2__);") => nil
       (b/get :my-lab :cljlab__0__out0__) => (list 4.0)
       (b/eval :my-lab "clear cljlab__0__in0__ cljlab__0__in1__ cljlab__0__in2__") => nil
       (b/eval :my-lab "clear cljlab__0__out0__") => nil)

      (util/call-fn-with-basic-vals :my-lab 0 0 :prod [1] [2]) => nil
      (provided
       (b/set :my-lab :cljlab__0__in0__ [1]) => nil
       (b/set :my-lab :cljlab__0__in1__ [2]) => nil
       (b/eval :my-lab "prod(cljlab__0__in0__,cljlab__0__in1__);") => nil
       (b/eval :my-lab "clear cljlab__0__in0__ cljlab__0__in1__") => nil))

(fact "`size` returns the size of a variable as a vector"
      (util/size :my-lab 0 :x) => [1 2 3]
      (provided
       (util/call-fn-with-vars :my-lab 0 1 :size :x) => '((1.0 2.0 3.0))))

(fact "`class` returns the class of a variable as a keyword"
      (util/class :my-lab 0 :z) => :double
      (provided
       (util/call-fn-with-vars :my-lab 0 1 :class :z) => '("double")))

(facts "about `get-var-part-basic-val`"
       "with a string, can return any row"
       (util/get-var-part-basic-val :my-lab 0 :x ["1" expr/all]) => "test"
       (provided
        (util/class :my-lab 0 :x) => :char
        (b/eval :my-lab "[cljlab__0__get_part0__] = x(1,:);") => nil
        (b/get :my-lab :cljlab__0__get_part0__) => "test"
        (b/eval :my-lab "clear cljlab__0__get_part0__") => nil))
