import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AllRecipesParser {
    private String baseUrl;
    private Document currentDoc;

    // mapping of all the articles on a website
    // key value - article title
    // value - article url
    private Map<String, String> articleMap;

    public AllRecipesParser(String baseUrl) {
        this.baseUrl = baseUrl;
        try {
            this.currentDoc = Jsoup.connect(this.baseUrl).get();
        } catch (IOException e) {
            this.currentDoc = null;
            System.out.println("baseUrl can't connect");
        }
        this.articleMap = new HashMap<>();
    }

    public Map<String, String> getArticleMap() {
        return this.articleMap;
    }

    /**
     * update currentDoc
     */
    public void resetCurrentDoc() {
        try {
            this.currentDoc = Jsoup.connect(this.baseUrl).get();
        } catch (IOException e) {
            this.currentDoc = null;
            System.out.println("set url can't connect");
        }
    }

    /**
     * maps Meal Types Article Titles to the link for them
     *
     * "Breakfast And Brunch Recipes" (artTitle) to
     * the url for this link (artUrl)
     * https://www.allrecipes.com/recipes/78/breakfast-and-brunch/
     *
     * Breakfast And Brunch Recipes
     * Lunch Recipes
     * Dinner Recipes
     * Appetizer & Snack Recipes
     * Bread Recipes
     * Dessert Recipes
     * Drink Recipes
     * Main Dishes
     * Salad Recipes
     * Side Dish Recipes
     * Soup, Stew & Chili Recipes
     */
    public void makeArtMapMealType() {
        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return;
        }
        Elements artElts = this.currentDoc.select("span");
        for (Element art: artElts) {
            String idTag = art.attr("id");
            boolean isRecipe = art.text().contains("Recipes") || art.text().contains("Dishes");

            if (idTag.contains("submenu-label") && isRecipe) {
                String artTitle = art.text();
                Element parent = art.parent().parent();
                Elements elts = parent.select("a");
                for (int i = 0; i < elts.size(); i++) {
                    Element a = elts.get(i);
                    if (a.text().equals("See All " + artTitle)) {
                        String artUrl = a.attr("href");
                        this.articleMap.put(artTitle, artUrl);
                    }
                }
            }
        }
    }

    /**
     * assumes some ARParser object w/ baseUrl for a meal type,
     * like "Breakfast and Brunch Recipes"'s url
     * and gets all the subcategories on the carousel
     * gets titles and urls associated with that
     */
    public void makeArtMapMealFoods() {
        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return;
        }
        Elements artElts = this.currentDoc.select("div");
        for (Element art: artElts) {
            String classTag = art.attr("class");
            if(classTag.equals("carouselNav__linkText elementFont__fine")) {
                String artTitle = art.text();
                Element parent = art.parent();
                String artUrl = parent.attr("href");
                this.articleMap.put(artTitle, artUrl);
            }
        }
    }

    /**
     * assumes some ARParser object w/ baseUrl for a food category,
     * like "Pancakes"'s url
     * and gets all the Recipe title / urls under this food category
     * gets titles and urls associated with that
     */
    public void makeArtMapRecipes() {
        // limit for num recipes so parsing doesn't take too long
        int limit = 10;

        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return;
        }
        Elements artElts = this.currentDoc.select("h3");
        for (Element art: artElts) {
            //System.out.println(art.text());
            String classTag = art.attr("class");
            if(classTag.equals("card__title elementFont__resetHeading") && this.articleMap.size() < limit) {
                String artTitle = art.text();
                Element parent = art.parent();
                String artUrl = parent.attr("href");
                //System.out.println(artTitle + " " + artUrl);

                this.articleMap.put(artTitle, artUrl);
            }
        }
        Elements nextElts = this.currentDoc.select("div");
        for (Element art: nextElts) {
            //System.out.println(art.text());
            String idTag = art.attr("id");
            if(idTag.equals("category-page-list-related-load-more-container")) {
                //String artTitle = art.text();
                String artUrl = art.select("a").get(0).attr("href");
                //System.out.println(artUrl);
                this.baseUrl = artUrl;
                resetCurrentDoc();      // update current doc
                visitNextPages(0);
                //System.out.println(artTitle + " " + artUrl);
                //this.articleMap.put(artTitle, artUrl);
            }
        }
    }
    /**
     * Assumes we are at a page reached after clicking "Load More"
     * and goes to the pages you would
     * get after clicking "Next"
     *
     * Updates articleMap when visiting next pages
     */
    public void visitNextPages(int count) {
        int limit = 10;
        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return;
        }
        String nextUrl = "";
        Elements artElts = this.currentDoc.select("a");
        //System.out.println(artElts.size());
        for (Element art: artElts) {
            //System.out.println(art.text());
            String classTag = art.attr("class");
            //System.out.println(classTag);
            if(classTag.equals("tout__titleLink elementFont__toutLink") && this.articleMap.size() < limit) {
                //System.out.println("yea");
                String artTitle = art.text();
                String artUrl = art.attr("href");
                if (!artUrl.contains("https://")) {
                    artUrl = "https://www.allrecipes.com" + artUrl;
                }
                //System.out.println(artTitle + " " + artUrl);
                this.articleMap.put(artTitle, artUrl);
            }
            // check if there's a "Next" Button
            // if so, store the url to use later
            if (art.text().equals("Next")) {
                nextUrl = art.attr("href");
                //System.out.println(nextUrl);
            }
        }
        // check if need to visit next pages
       if (count < 1) {
           if (nextUrl.length() > 0) {
               //System.out.println("here");
               this.baseUrl = nextUrl;
               resetCurrentDoc();
               visitNextPages(count + 1);
           }
       }
    }

    /**
     * assumes currentDoc is at a recipe page
     * @return an arraylist of String ingredients (empty if current
     * doc is null)
     *
     */
    public String getIngreds() {
        String ingreds = "";
        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return ingreds;
        }
        Elements artElts = this.currentDoc.select("span");

        for (Element art: artElts) {
            String classTag = art.attr("class");
            if(classTag.equals("ingredients-item-name elementFont__body")) {
                String artTitle = art.text();
                ingreds += artTitle + " ; ";    // Added semi colon here! For gui to look right - claire
            }
        }
        return ingreds;
    }

    /**
     * assumes currentDoc is at a recipe page
     * @return an arraylist of String directions (empty if current
     * doc is null)
     *
     */
    public ArrayList<String> getDirections() {
        ArrayList<String> steps = new ArrayList<>();
        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return steps;
        }
        Elements artElts = this.currentDoc.select("div");

        for (Element art: artElts) {
            String classTag = art.attr("class");
            if(classTag.equals("section-body elementFont__body--paragraphWithin elementFont__body--linkWithin")) {
                String artTitle = art.text();
                steps.add(artTitle);
            }
        }
        return steps;
    }

    /**
     * assumes currentDoc is at a recipe page
     * @return an arraylist of String directions (empty if current
     * doc is null)
     *
     */
    public String getImageUrl(String recTitle) {
        String imgUrl = "";
        if (this.currentDoc == null) {
            System.out.println("Current doc is null");
            return imgUrl;
        }
        Elements artElts = this.currentDoc.select("div");


        for (Element art: artElts) {
            String classTag = art.attr("class");
            //System.out.println(classTag);
            Elements children = art.children();


            if(classTag.equals("image-container")) {
                //System.out.println("art text: " + art.text());
                for (Element child: children) {
                    String dataSrcTag = child.attr("data-src");
                    //System.out.println("data-alt: " + dataSrcTag);
                    imgUrl = dataSrcTag;
                    if (imgUrl.length() > 0) {
                        return imgUrl;
                    }
                }
            }
            if (classTag.contains("component lazy-image lazy-image-udf")) {
                if (art.text().contains(recTitle)) {
                    imgUrl = art.attr("data-src");
                    if (imgUrl.length() > 0) {
                        return imgUrl;
                    }
                }
            }
        }
        return imgUrl;
    }

    /**
     * star rating
     * Assumes connection with a recipe Document
     * Modifies recipeData
     */

    public void populateStarRating(Map<String, String[]> recipeData, String recipeName, int index, int length) {
        double sum = 0;
        int starCount = 5;
        int count = 0;
        Elements artElts = this.currentDoc.select("li");
        for (Element art: artElts) {
            String classTag = art.attr("class");
            //System.out.println(classTag); recipeNutritionSectionBlock
            Elements children = art.children();
            if (classTag.equals("rating") && starCount > 0) {
                if (art.text().contains("star values: ")) {
                    int startIndex = art.text().indexOf("star values: ") + "star values: ".length();
                    String value = art.text().substring(startIndex);
                    try {
                        int numValue = Integer.parseInt(value);
                        count += numValue;
                        sum += numValue * starCount;
                        //System.out.println("num " + numValue + " count " + count + " sum " + sum);
                        starCount--;
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                }
            }
        }
        double avg = 0;
        if (count > 0) {
            avg = sum / count;
            avg = Math.round(avg * 100.0) / 100.0;
        }
        if (!recipeData.containsKey(recipeName)) {
            recipeData.put(recipeName, new String[length]);
        }
        recipeData.get(recipeName)[index] = "" + avg;
    }

    /**
     * cooking time
     * Assumes connection with a recipe Document
     * Modifies recipeData
     */
    public void populateCookingTime(Map<String, String[]> recipeData, String recipeName, int index, int length) {
        Elements artElts = this.currentDoc.select("div");
        for (Element art: artElts) {
            String classTag = art.attr("class");
            //System.out.println(classTag); recipeNutritionSectionBlock
            Elements children = art.children();
            if (classTag.equals("recipe-meta-item")) {
                String timeInfo = art.text();
                String totalTime = "";
                if (timeInfo.contains("total: ")) {
                    int startIndex = timeInfo.indexOf("total: ") + "total: ".length();
                    totalTime = timeInfo.substring(startIndex);
                    //System.out.println(totalTime);
                    if (!recipeData.containsKey(recipeName)) {
                        recipeData.put(recipeName, new String[length]);
                    }
                    recipeData.get(recipeName)[index] = "" + timeHelper(totalTime);
                    return;
                }
            }
        }
        if (recipeData.get(recipeName)[index] == null) {
            recipeData.get(recipeName)[index] = "60";   // default value of 60 mins
        }
    }

    /**
     * takes in a String of words representing time amount
     * and returns the number version
     * (example: 1 hr 15 mins --> 75)
     * @param timeWords
     * @return
     */
    public int timeHelper(String timeWords) {
        int time = 0;
        String[] timeArray = timeWords.split(" ");
        for (int i = 0; i < timeArray.length - 1; i++) {
            try {
                int duration = Integer.parseInt(timeArray[i]);
                if (timeArray[i + 1].equals("hr")) {
                    time += duration * 60;
                } else if (timeArray[i + 1].equals("mins")) {
                    time += duration;
                }
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return time;
    }

    /**
     * calorie info
     * Assumes connection with a recipe Document
     * Modifies recipeData
     */

    public void populateCalorieInfo(Map<String, String[]> recipeData, String recipeName, int index, int length) {
        Elements artElts = this.currentDoc.select("div");
        for (Element art: artElts) {
            String classTag = art.attr("class");
            //System.out.println(classTag); recipeNutritionSectionBlock
            Elements children = art.children();
            if(classTag.equals("recipeNutritionSectionBlock")) {
                //System.out.println("art text: " + art.text());
                for (Element child: children) {
                    String nutrInfo = child.text();
                    String calCount = "";
                    if (nutrInfo.contains("calories")) {
                        int endIndex = nutrInfo.indexOf("calories");
                        calCount = nutrInfo.substring(0, endIndex);
                        if (!recipeData.containsKey(recipeName)) {
                            recipeData.put(recipeName, new String[length]);
                        }
                        recipeData.get(recipeName)[index] = calCount;
                        return;
                    }
                }
            }
        }
        if (recipeData.get(recipeName)[index] == null) {
            recipeData.get(recipeName)[index] = "500 ";   // default value of 500 calories
        }
    }


}
