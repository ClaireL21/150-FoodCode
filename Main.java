import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // don't need to code anything here

        /**
         * Testing sorting
         */
        Map<String, String[]> testSort = new HashMap<>();
        String[] rec1Data = new String[]{"3", "5.6", "11"};
        String[] rec2Data = new String[]{"4", "7", "10"};
        String[] rec3Data = new String[]{"5", "3", "9"};
        String[] rec4Data = new String[]{"6", "4", "1"};
        String[] rec5Data = new String[]{"8", "2", "3"};
        String[] rec6Data = new String[]{"7", "1", "8"};
        testSort.put("rec1", rec1Data);
        testSort.put("rec2", rec2Data);
        testSort.put("rec3", rec3Data);
        testSort.put("rec4", rec4Data);
        testSort.put("rec5", rec5Data);
        testSort.put("rec6", rec6Data);
        //System.out.println(testSort.size());

        Map<String, Double> ratings = sortRatings(testSort, 0, false);
        //System.out.println(ratings.size());

        for (String s: ratings.keySet()) {
            //System.out.println("result: ");
            System.out.println(s + " " + ratings.get(s));
        }
        /*
        end sort testing
         */
    }

    /**
     * If you want to test code, run the main function at the bottom
     * of RunFoodFinder and press the Search button at the bottom of the left
     * panel. This will call the doFiltering function, where your method calls
     * will go.
     */

    /** write functions below **/



    /** end **/

    public static void doFiltering(FunFoodFinder f) {
        final String BFAST = "Breakfast and Brunch Recipes";
        final String MEAL_TYPE = chooseMealType(f);
        System.out.println(MEAL_TYPE);
        Map<String, String[]> recipeData = new HashMap<>();

        String baseUrl = "https://www.allrecipes.com/";

        Map<String, String> mealTypes = makeMealTypesMap(baseUrl);      // Breakfast --> break url, Lunch --> lunch url, ...

        String bfastUrl = mealTypes.get(BFAST);
        Map<String, String> bfastFoods = makeFoodTypesMap(bfastUrl);

        String pancakesUrl = bfastFoods.get("Pancakes");
        Map<String, String> panRecipes = makeRecipeArtMap(pancakesUrl);
        //boolean yes = true;

        int numRecipes = 0;
        //System.out.println("buttermilk".contains("milk"));
        Map<String, String> filtered = checkIngredients(panRecipes, f);
        System.out.println(filtered.size());

        /**
         * Sets recipe list and urls used in GUI
         * Call setRecipes & setRecipeUrls after we have updated stuff
         * should use filtered.keySet() ?
         */
        f.setRecipes(filtered.keySet());
        f.setRecipeUrls(filtered.values());



        for (String rec: panRecipes.keySet()) {
            /**
             * uncomment below to print each recipe title
             */
//            if (yes) {
//                System.out.println(rec);
//                AllRecipesParser recPage = new AllRecipesParser(panRecipes.get(rec));
//                ArrayList<String> ingredients = recPage.getIngreds();
            //System.out.println("Title: " + rec + " URL: " + panRecipes.get(rec));
            //numRecipes++;
//            }
//            yes = false;

        }
        for (String rec: filtered.keySet()) {
            System.out.println("Title: " + rec);

        }

       // System.out.println(numRecipes);

        /**
         * Method calls - will call functions here
         */
        System.out.println("done");     // sout to check when function is finished running

    }

    /**
     * Gets Meal type and returns String
     */
    private static String chooseMealType(FunFoodFinder f) {
        String mealType = "";
        String userMType = f.getMtype();
        switch (userMType) {
            case "Breakfast": mealType = "Breakfast And Brunch Recipes";
                break;
            case "Lunch": mealType = "Lunch Recipes";
                break;
            case "Dinner": mealType = "Dinner Recipes";
                break;
            case "Snacks": mealType = "Appetizer & Snack Recipes";
                break;
            case "Desserts": mealType = "Dessert Recipes";
                break;
            default: mealType = "Breakfast And Brunch Recipes";
        }
        return mealType;
    }

    /**
     * Takes in a map of recipes to their URLs and returns a filtered
     * map that only contains recipes that contain
     * the ingredients inputted by the user and does not include the
     * allergy ingredients of the user
     */
    private static Map<String, String> checkIngredients (Map<String, String> recipeMap, FunFoodFinder f) {
        AllRecipesParser p;
        String[] include = f.getIngred().split(",");
        String[] exclude = f.getExclude().split(",");
        Map<String, String> filtered = new TreeMap<String, String>();
        List<String> foods = new ArrayList<String>(recipeMap.keySet());
        List<String> urls = new ArrayList<String>(recipeMap.values());
        f.setFoodsInDietR();
        String[] dietR = f.getFoodsInDietR().split(",");
        for (int i = 0; i < 10; i++) {
            p = new AllRecipesParser(urls.get(i));
            p.resetCurrentDoc();
            String ingredients = p.getIngreds();
            for(int j = 0; j < include.length; j++) {
                if(!include[j].equals("")) {
                    if(!ingredients.contains(include[j])) {
                        recipeMap.put(foods.get(i), null);
                        System.out.println(foods.get(i) + " doesn't contain milk");
                    }
                }
                //System.out.println(foods.get(i));
            }
            for(int j = 0; j < exclude.length; j++) {
                if(!exclude[j].equals("")) {
                    if (ingredients.contains(exclude[j])) {
                        recipeMap.put(foods.get(i), null);
                        System.out.println(foods.get(i) + "contains milk");
                    }
                }
            }
            for(int j = 0; j < dietR.length; j++) {
                if(!dietR[j].equals("")) {
                    if (ingredients.contains(dietR[j])) {
                        recipeMap.put(foods.get(i), null);
                    }
                }
            }
        }
        List<String> urlsFiltered = new ArrayList<String>(recipeMap.values());
        for (int i = 0; i < recipeMap.size(); i++) {
            if(urlsFiltered.get(i) != null) {
                filtered.put(foods.get(i), urls.get(i));
            }
        }
        return filtered;
    }

    /**
     * passed a string and converts it to a number
     */
    private static double convertToNum(String s, int def) {
        double num = def;
        try {
            num = Double.parseDouble(s);
            return num;
        } catch (NumberFormatException e) {
            return num;
        }
        //return 1;
        // if we can't find a number - do the default
    }

    /**
     * Sorting
     * bool for sorting in order of increasing (true), or
     * order of decreasing (high to low) false
     * looked it up cuz wasn't sure, might change later *sigh* :(
     * https://www.javatpoint.com/how-to-sort-hashmap-by-value
     */
    public static Map<String, Double> sortRatings(Map<String, String[]> recipeData, int index, boolean isIncr) {
        int def = 0;

        if (!isIncr) {
            def = Integer.MAX_VALUE;
        }
        Map<String, Double> data = new HashMap<>();
        Map<String, Double> sorted = new LinkedHashMap<>();
        ArrayList<Double> numbers = new ArrayList<>();

        if (index > 2 || index < 0) {
            //System.out.println("here");
            return sorted;
        }
        //Map<Integer, Integer> indexData = new HashMap<>();
        //int ind = 0;
        for (String rec: recipeData.keySet()) {
            data.put(rec, convertToNum(recipeData.get(rec)[index], def));
            //System.out.println(rec + " " + convertToNum(recipeData.get(rec)[index], def));
            numbers.add(convertToNum(recipeData.get(rec)[index], def));
            //indexData.put(ind, convertToNum(recipeData.get(rec)[index], def));
        }
        //System.out.println(numbers.size());

        Collections.sort(numbers, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                if (isIncr) {
                    if (o2 < o1) {
                        //System.out.println(o2 + " " + o1);
                        return 1;
                    } else if (o2 > o1) {
                        return -1;
                    }
                } else {
                    if (o2 < o1) {
                        //System.out.println(o2 + " " + o1);
                        return -1;
                    } else if (o2 > o1) {
                        return 1;
                    }
                }

                return 0;
            }
        });

        //if (isIncr) {
            for (int i = 0; i < numbers.size(); i++) {
                //System.out.println(" num " + i + " " + numbers.get(i));
                for (String recName: data.keySet()) {
                    if (data.get(recName).equals(numbers.get(i)) && !sorted.containsKey(recName)) {
                        sorted.put(recName, numbers.get(i));
                        break;
                    }
                }
            }
            //System.out.println(sorted.size());
//        } else {
//            for (int i = numbers.size() - 1; i >= 0; i--) {
//                for (String recName: data.keySet()) {
//                    if (data.get(recName) == numbers.get(i) && !sorted.containsKey(recName)) {
//                        sorted.put(recName, numbers.get(i));
//                        break;
//                    }
//                }
//            }
//        }

//        // sort data
//        //Collections.sort(data);
//        List<Entry<String, Double>> dataList = new LinkedList<> (data.entrySet());
//
//        Collections.sort(dataList, new Comparator<Entry<String, Double>>() {
//            public int compare(Entry<String, Double> data1, Entry<String, Double> data2) {
//                if (isIncr) {
//                    return data1.getValue().compareTo(data2.getValue());
//                } else {
//                    return data2.getValue().compareTo(data1.getValue());
//                }
//            }
//        });
//
//        for (Entry<String, Double> e: dataList) {
//            sorted.put(e.getKey(), e.getValue());
//        }
        return sorted;
    }


    /**
     * Makes a mapping from meal types to their url.
     * Ex: Breakfast --> break url, Lunch --> lunch url, ...
     * @param url
     * @return
     */
    public static Map<String, String> makeMealTypesMap(String url) {
        AllRecipesParser mTyp = new AllRecipesParser(url);
        mTyp.makeArtMapMealType();
        Map<String, String> mealTypes = mTyp.getArticleMap();
        return mealTypes;
    }

    /**
     * for a given meal type, find subcategories associated with it
     * Pass in the url associated w/ web page for a given meal type (aka Breakfast url)
     * We loop through breakfast, lunch, etc.
     * Breakfast has subcategories "Pancakes", "Breakfast Casseroles," etc.
     * "Pancakes" is the artTitle in mealFoods Map, link to "Pancakes is artUrl
     */
    public static Map<String, String> makeFoodTypesMap(String url) {
        AllRecipesParser mFoods = new AllRecipesParser(url);
        mFoods.makeArtMapMealFoods();
        Map<String, String> mealFoods = mFoods.getArticleMap();
        return mealFoods;
    }

    /**
     * For a given subcategory of food, look at all recipe articles
     * Ex: given "Pancakes" subcategory, find all recipes under "Pancakes"
     * We map the recipe article titles under "Pancakes" to the url
     * (like, "Chocolate Pancakes" to "https:chocpanckaesurl")
     * (last page reference:
     * https://www.allrecipes.com/recipes/151/breakfast-and-brunch/pancakes/?page=19)
     */
    public static Map<String, String> makeRecipeArtMap(String url) {
        AllRecipesParser foods = new AllRecipesParser(url);
        foods.makeArtMapRecipes();
        Map<String, String> recipes = foods.getArticleMap();
        return recipes;
    }

    /**
     * for a given Map of recipe titles to articles,
     * make a parser object to parse recipe page
     * Check for ingreds, etc.
     */
}
