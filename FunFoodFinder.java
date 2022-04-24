import java.util.*;

/**
 * This class has instance fields that record the specifications
 * of the user. It creates an AllRecipesParser object and
 * uses these specifications to output the list of websites that
 * fit the user's requirements.
 */
public class FunFoodFinder {

    // Meal Type (Breakfast, Lunch, Dinner, Snacks, etc.)
    private String mtype;

    // Ingredients to include
    private String ingred;

    // Ingredients to exclude
    private String exclude;

    // Sort by
    private String sortBy;

    // Dietary Restrictions
    private TreeSet<String> dietR;

    // Foods that must be excluded due to dietary Restrictions
    private String foodsInDietR;

    // recipe titles and urls to return
    //private Map<String, String> recipes;
    private String[] recipeTitles;
    private String[] recipeUrls;

    public FunFoodFinder() {
        this.mtype = "";
        this.ingred = "";
        this.exclude = "";
        this.sortBy = "";
        this.foodsInDietR = "";
        this.dietR = new TreeSet<>();
        //this.recipes = new HashMap<>();
        this.recipeTitles = new String[1];
        recipeTitles[0] = "recipe1";

    }

    // some function that creates a parser website,
    // calls methods, and then returns all the websites
    // that fit the specifications of the user

    public String getMtype() {
        return this.mtype;
    }
    public String getIngred() {
        return this.ingred;
    }

    public String getExclude() {
        return this.exclude;
    }

    public String getSortBy() {
        return this.sortBy;
    }
    public TreeSet<String> getDietR () {
        TreeSet<String> dietRCopy = new TreeSet<>();
        for (String s: this.dietR) {
            dietRCopy.add(s);
        }
        return dietRCopy;
    }
    public void setDietR(String food) {
        if (dietR.size() == 0) {
            dietR = new TreeSet<>();
        }
        dietR.add(food);
    }
    public void removeDietR(String food) {
        if (dietR.size() > 0 && dietR.contains(food)) {
            dietR.remove(food);
        }
    }

    // semma's
    public void setFoodsInDietR() {
        if (dietR.contains("Vegan") || dietR.contains("Vegetarian")) {
            foodsInDietR += "beef, pork, chicken, turkey, duck, shrimp, fish, " +
                    "gelatin, tilapia, cod, lamb, crab, clam, lobster";
        }
        if (dietR.contains("Vegan") || dietR.contains("Lactose")) {
            foodsInDietR += "milk, cheese, yogurt, butter, cream, mayonnaise, chocolate";
        }
        if (dietR.contains("Vegan")) {
            foodsInDietR += "eggs, honey";
        }
        if (dietR.contains("Gluten")) {
            foodsInDietR += "bread, pasta, biscuits, crackers, flour, couscous, wheat, oats, chips";
        }
        if (dietR.contains("Nuts")) {
            foodsInDietR += "nuts, peanuts, cashews, tree nuts, almonds, walnuts," +
                    " hazelnuts, pecans, macadamias, pistachios, chestnuts";
        }
    }

    public String getFoodsInDietR() {
        return foodsInDietR;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
       // System.out.println(mtype);
    }

    public void setIngred(String ingred){
        this.ingred = ingred;
        //System.out.println(ingred);
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
        //System.out.println(exclude);
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setRecipes(Set<String> titles) {
//        if (recipes.size() == 0) {
//            this.recipes = new HashMap<>();
//        }
        //System.out.println("setting recipes" + titles.size());
        // set recipes here
        String[] recipes =  new String[titles.size()];
        int index = 0;
        for (String rec: titles) {
            //System.out.println("in setRecipes " + rec);
            recipes[index] = rec;
            index++;
        }
        recipeTitles = recipes;
    }
    public String[] getRecipes() {
        String[] recTitlesCopy = new String[recipeTitles.length];
        System.arraycopy(recipeTitles, 0, recTitlesCopy, 0, recipeTitles.length);
//        for (int i = 0; i < recTitlesCopy.length; i++) {
//            System.out.println("rec in getR " + recTitlesCopy[i]);
//        }
        return recTitlesCopy;
    }

    public void setRecipeUrls(Collection<String> urls) {
//        if (recipes.size() == 0) {
//            this.recipes = new HashMap<>();
//        }
        //System.out.println("setting recipes" + titles.size());
        // set recipes here
        String[] recUrls =  new String[urls.size()];
        int index = 0;
        for (String rec: urls) {
            //System.out.println("in setRecipes " + rec);
            recUrls[index] = rec;
            index++;
        }
        recipeUrls = recUrls;
    }
    public String[] getRecipeUrls() {
        String[] recUrlsCopy = new String[recipeUrls.length];
        System.arraycopy(recipeUrls, 0, recUrlsCopy, 0, recipeUrls.length);
//        for (int i = 0; i < recTitlesCopy.length; i++) {
//            System.out.println("rec in getR " + recTitlesCopy[i]);
//        }
        return recUrlsCopy;
    }
}
