(ns brew-bot.recipes.views
  (:require [antizer.reagent :as ant]
            [brew-bot.util :as util]
            [brew-bot.visual-identity :as vis]
            [cljs.pprint :as pprint]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn ingredient-bill
  [title type ingredients]
  [:div {:style {:padding-top "10px"}}
   [:h3 title]
   (into [:ul]
         (for [ingredient (vals (sort-by :weight ingredients))
               :let [display-name (:name ingredient)
                     weight       (:weight ingredient)
                     weight-unit  (util/pluralize weight (if (= type :hops) "ounce" "pound"))]]
           ^{:key ingredient} [:li (str display-name ": " weight " " weight-unit)]))])

(defn recipe-display-page
  []
  (let [generated-recipe (rf/subscribe [:generated-recipe])]
    (fn []
      (let [grains           (:grains @generated-recipe)
            extracts         (:extracts @generated-recipe)
            hops             (:hops @generated-recipe)
            yeast            (first (vals (:yeasts @generated-recipe)))
            gravity          (:gravity @generated-recipe)
            color            (:sru-color @generated-recipe)]
        [:div {:style {:padding-left "10px"}}
         [:h2 "Your Recipe"]
         [:p "Projections"]
         [:ul {:style {:max-width "200px"
                       :border (str "5px solid " (vis/srm-color-to-hex color))}}
          [:li (str "OG: "  (pprint/cl-format false  "~,3f" (:gravity @generated-recipe)))]
          [:li (str "ABV: " (pprint/cl-format false  "~,2f" (:abv @generated-recipe)) "%")]
          [:li (str "SRM Color: " (int color))]]
         [:div {:style {:padding-left "5px"}}
          [ingredient-bill "Grains" :grains grains]
          (when (seq extracts)
            [ingredient-bill "Extracts" :extracts extracts])
          [ingredient-bill "Hops" :hops hops]
          [:div {:style {:padding-top "10px"}}
           [:h3 "Yeast"]
           [:ul
            [:li (str (:manufacturer yeast) " - " (:product-number yeast) ": " (:name yeast))]]]]]))))