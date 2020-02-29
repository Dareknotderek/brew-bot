(ns brew-bot.util
  "Common fns required across brew-bot"
  (:require [cljx-sampling.core :as rnd]
            [brew-bot.ingredients :as ingredients]))

(defn rand-key
  "Pick a random key from a map, weighted by the :probability key of the value"
  [m]
  (first (rnd/sample (keys m) :replace true :weigh #(or (:probability (get m %)) 1))))

(defn join-ingredient-maps
  "Given an ingredient map, lookup the source ingredient and combine it with the added-key"
  [ingredient-bill ingredient-source added-key]
  (reduce-kv (fn [m k v] (assoc m k (assoc (get ingredient-source k) added-key v))) {} ingredient-bill))

(defn scale-ingredients
  "Update `ingredient-map` so the combined :weights are randomly scaled up to `weight-limit`"
  [ingredient-map weight-limit]
  (loop [i-map ingredient-map
         weight (reduce + 0 (vals ingredient-map))]
    (if (< weight weight-limit)
      (let [addition (rand-nth ingredients/ingredient-amounts)
            mod-ingredient (rand-nth (keys i-map))]
        (recur (update i-map mod-ingredient + addition)
               (+ weight addition)))
      i-map)))

(defn max-n-kv
  "Given `m` with k-v pairs for which all values are numbers, return the `n` k-v pairs with the highest values"
  [m n]
  (let [ordered-tuples (take n (sort-by (comp - last) m))
        list-of-maps (map #(hash-map (first %) (second %)) ordered-tuples)]
    (apply merge list-of-maps)))

(defn min-n-kv
  "Given `m` with k-v pairs for which all values are numbers, return the `n` k-v pairs with the lowest values"
  [m n]
  (let [ordered-tuples (take n (sort-by last m))
        list-of-maps   (map #(hash-map (first %) (second %)) ordered-tuples)]
    (apply merge list-of-maps)))
