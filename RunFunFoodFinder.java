
// imports necessary libraries for Java swing
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import java.net.URL;

public class RunFunFoodFinder implements Runnable {
    public void randMealPlanButton(JPanel results_panel, JPanel display_panel, JPanel top_bar, FunFoodFinder f, JFrame frame) {
        JButton random = new JButton("Fun Random Meal Plan");
        random.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /**
                 * remove everything on results and display first
                 */
                Component[] results = results_panel.getComponents();
                for (int i = 0; i < results.length; i++) {
                    results_panel.remove(i);
                }
                Component[] display = display_panel.getComponents();

                for (int i = display.length-1; i >= 0; i--) {
                    display_panel.remove(i);
                }

                results_panel.revalidate();
                results_panel.repaint();
                display_panel.revalidate();
                display_panel.repaint();
                System.out.println("finished removing");

                /**
                 * make the random meal plan
                 */
                Main.makeRandomMealPlan(f);
                System.out.println("called makeRandom");
                createListScroller(results_panel, display_panel, f, frame);
                System.out.println("created list scroller");
                SwingUtilities.updateComponentTreeUI(frame);
            }
        });

        top_bar.add(random);
    }

    /**
     * https://stackoverflow.com/questions/1090098/newline-in-jlabel
     * @param results_panel
     * @param display_panel
     * @param f
     * @param frame
     */
    public void createListScroller(JPanel results_panel, JPanel display_panel, FunFoodFinder f, JFrame frame) {
        String[] recTitles = f.getRecipes();
        String[] recUrls = f.getRecipeUrls();
        DefaultListModel<String> listMod = new DefaultListModel<String>();      // added <String> ???

        for (int i = 0; i < recTitles.length; i++) {
            listMod.addElement(recTitles[i]);
            System.out.println("recipe title: " + recTitles[i] + "recipe url: " + recUrls[i]);
        }
        final JList recList = new JList(listMod);
        recList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recList.setLayoutOrientation(JList.VERTICAL);
        recList.setSelectedIndex(-1);

        // label of recipe title
        final JLabel label = new JLabel("");
        display_panel.add(label);

        // label of sorting info
        final JLabel sortLab = new JLabel("");
        display_panel.add(sortLab);

        // label of ingredients
        final JLabel ingrLab = new JLabel("");
        display_panel.add(ingrLab);

        // label for image
        final JLabel imageLab = new JLabel("");
        display_panel.add(imageLab);

        // label for steps
        final JLabel stepsLab = new JLabel("");
        JScrollPane stepsSc = new JScrollPane(stepsLab);
        stepsSc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //stepsSc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        display_panel.add(stepsSc);

        // action listener to register when value in list was changed
        recList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (recList.getSelectedIndex() != -1) {
                        System.out.println("entering the value changed");
                        int index = recList.getSelectedIndex();
                        String title = recTitles[index];
                        //System.out.println(index);

                        label.setText(title);   // label for recipe title
                        System.out.println("checkpoint 1");

                        /**
                         * Setting Sorting Label info
                         */

                        String sortingInfo = f.getRecipeDataCell(title, 0) + " stars; " +
                                f.getRecipeDataCell(title, 1) + " minutes; " +
                                f.getRecipeDataCell(title, 2) + "calories";
                        sortLab.setText(sortingInfo);

                        System.out.println("checkpoint 2");


                        // Creating a parser object to get ingreds, steps, image
                        // System.out.println(recUrls[index]);
                        AllRecipesParser recipe = new AllRecipesParser(recUrls[index]);
                        String ingr = recipe.getIngreds();
                        String[] ingrList = ingr.split(" ; ");
                        recipe.getImageUrl(title);
                        ArrayList<String> steps = recipe.getDirections();


                        /**
                         * Setting the Ingredients label
                         * https://stackoverflow.com/questions/1090098/newline-in-jlabel
                         */
                        String ingrText = "<html><br/>Ingredients<br/><br/>";
                        for (String s : ingrList) {
                            ingrText += s + "<br/>";
                            //System.out.println(ingrText);
                        }
                        ingrText += "</html>";
                        ingrLab.setText(ingrText);

                        /**
                         * Displaying the image
                         */
                        String imageUrl = recipe.getImageUrl(title);
                            //"https://imagesvc.meredithcorp.io/v3/mm/image?url=https%3A%2F%2Fimages.media-allrecipes.com%2Fuserphotos%2F5252699.jpg";

                        try {
                            BufferedImage buffImage = ImageIO.read(new URL(imageUrl));
                            Image newImg = buffImage.getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING);
                            imageLab.setIcon(new ImageIcon(newImg));
                        } catch (IOException ex) {
                                //ex.printStackTrace();
                        }


                        /**
                         * Displaying the steps
                         */
                        String stepsText = "<html><br/>Directions<br/><br/>";
                        for (String s : steps) {
                            stepsText += s + "<br/>";
                                //System.out.println(stepsText);
                        }
                        stepsText += "</html>";
                        stepsLab.setText(stepsText);

                        System.out.println("checkpoint 6");

                            //ImageIcon image = new ImageIcon(imageUrl);
                            //imageLab.setIcon(image);
                            //System.out.println(imageLab);


                            //createDisplayPane(display_panel, f, title);
                        SwingUtilities.updateComponentTreeUI(frame);
                    }
                }
            }
        });

        JScrollPane listSc = new JScrollPane(recList);
        //listSc.setPreferredSize(new Dimension(200, 300));

        results_panel.add(listSc);
        System.out.println("checkpoint 8");

    }
    /**
     *
     * https://docs.oracle.com/javase/tutorial/uiswing/components/splitpane.html
     *
     * @param display_panel
     * @param f
     */
    public void createDisplayPane(JPanel display_panel, FunFoodFinder f, String title) {

        final JLabel recLab = new JLabel(title);
        display_panel.add(recLab);

    }

    /**
     * checkboxes for dietary restrictions
     * @param panel
     * @param f
     */
    public void createDietaryRestrictions(JPanel panel, FunFoodFinder f) {
        final JLabel dietLab = new JLabel("Dietary Restrictions");
        panel.add(dietLab);

        String[] choices = new String[] {"Gluten", "Lactose", "Vegan", "Vegetarian", "Peanuts"};

        // Radio Buttons for Meal Types
        for (int i = 0; i < choices.length; i++) {
            String restr = choices[i];
            JCheckBox chBox = new JCheckBox(restr);
            chBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chBox.isSelected()) {
                        f.setDietR(restr);
                        //System.out.println(f.getDietR());
                    } else {
                        f.removeDietR(restr);
                        //System.out.println(f.getDietR());
                    }
                }
            });
            panel.add(chBox);
        }
    }
    /**
     * Meal Type Filters in the Filter Panel
     * @param panel
     * @param f
     */
    public void createSearchButton(JPanel panel, FunFoodFinder f, JPanel results_panel, JPanel display_panel, JFrame frame) {
        final JButton search = new JButton("Search");
        panel.add(search);

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.doFiltering(f);
                createListScroller(results_panel, display_panel, f, frame);
                SwingUtilities.updateComponentTreeUI(frame);

            }
        });

        panel.add(search);

    }
    /**
     * Meal Type Filters in the Filter Panel
     * @param panel
     * @param f
     */
    public void createMTypeFilter(JPanel panel, FunFoodFinder f) {
        final JLabel mLab = new JLabel("Choose Meal Type");
        panel.add(mLab);

        String[] choices = new String[] {"Breakfast", "Lunch", "Dinner", "Snacks", "Desserts"};
        ButtonGroup mtypes = new ButtonGroup();

        // Radio Buttons for Meal Types
        for (int i = 0; i < choices.length; i++) {
            String type = choices[i];
            JRadioButton mButton = new JRadioButton(type);
            mButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    f.setMtype(type);
                    //System.out.println(f.getMtype());
                }
            });
            mtypes.add(mButton);
            panel.add(mButton);
        }
    }

    /**
     * Enter Fields Text fields, and Enter button
     * @param panel
     * @param f
     * @param label
     */
    public void createEnterText(JPanel panel, FunFoodFinder f, String label, String des, boolean include) {
        final JLabel textLabel = new JLabel(label);
        panel.add(textLabel);

        final JLabel description = new JLabel(des);
        panel.add(description);

        JTextField textBox = new JTextField(1);
        JButton enterBut = new JButton("Enter");
        enterBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (include) {
                    f.setIngred(textBox.getText());
                } else {
                    f.setExclude(textBox.getText());
                }

            }
        });
        panel.add(textBox);
        panel.add(enterBut);
    }

    public void createSortSelection(JPanel panel, FunFoodFinder f) {
        final JLabel sLab = new JLabel("Sort By");
        panel.add(sLab);

        String[] choices = new String[] {"Ratings", "Time", "Calories"};
        ButtonGroup sortTypes = new ButtonGroup();

        // Radio Buttons for Meal Types
        for (int i = 0; i < choices.length; i++) {
            String type = choices[i];
            JRadioButton button = new JRadioButton(type);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    f.setSortBy(type);
                    //System.out.println(f.getSortBy());
                }
            });
            sortTypes.add(button);
            panel.add(button);
        }
    }
    public void run() {
        JFrame frame = new JFrame("Fun Food Finder");
        frame.setLocation(300, 300);
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        Border outer = BorderFactory.createCompoundBorder(margin, blackline);
        Border border = BorderFactory.createCompoundBorder(outer, margin);
       //GridLayout layout = new GridLayout(1, 3);
        //BorderLayout layout2 = new BorderLayout();
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
       // frame.setSize(800, 800);

        FunFoodFinder f = new FunFoodFinder();

        /**
         * Filters Panel
         */
        JPanel filters_panel = new JPanel();
        filters_panel.setBorder(border);
        filters_panel.setLayout(new BoxLayout(filters_panel, BoxLayout.Y_AXIS));
        filters_panel.setPreferredSize(new Dimension(400, 700));
        frame.add(filters_panel, BorderLayout.WEST);

        // Label
        final JLabel fLab = new JLabel("Filters");
        filters_panel.add(fLab);

        // Create components in Filters column
        createMTypeFilter(filters_panel, f);    // Radio Buttons for Meal Types
        String ingrDes = "<html>Use a comma to separate ingredients <br/> (ex: egg, milk, ...). </html>";
        createEnterText(filters_panel, f, "Enter Ingredients", ingrDes, true);   // Ingredients Text Field
        String excDes = "<html>Use a comma to separate ingredients to exclude <br/> (ex: nuts, fish, ...). </html>";
        createEnterText(filters_panel, f, "Enter Allergies", excDes, false);
        createDietaryRestrictions(filters_panel, f);

        // Results panel
        JPanel results_panel = new JPanel();
        results_panel.setBorder(border);
        results_panel.setLayout(new BoxLayout(results_panel, BoxLayout.Y_AXIS));
        results_panel.setPreferredSize(new Dimension(400, 700));
        frame.add(results_panel, BorderLayout.EAST);

        createSortSelection(filters_panel, f);



        // Display panel
        JPanel display_panel = new JPanel();
        display_panel.setBorder(border);
        display_panel.setLayout(new BoxLayout(display_panel, BoxLayout.Y_AXIS));
        display_panel.setPreferredSize(new Dimension(600, 700));
        frame.add(display_panel, BorderLayout.CENTER);

        //createDisplayPane(display_panel, f, "https://www.allrecipes.com/recipe/263880/bacon-okonomiyaki/");

        createSearchButton(filters_panel, f, results_panel, display_panel, frame);

        // list and panel display
        //createListScroller(results_panel, f);

        JPanel top_bar = new JPanel();
        //top_bar.setBorder(border);
        //top_bar.setLayout(new BoxLayout(top_bar, BoxLayout.Y_AXIS));
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] results = results_panel.getComponents();
                for (int i = 0; i < results.length; i++) {
                    results_panel.remove(i);
                }
                Component[] display = display_panel.getComponents();

                for (int i = display.length-1; i >= 0; i--) {
                    display_panel.remove(i);
                }
                results_panel.revalidate();
                results_panel.repaint();
                display_panel.revalidate();
                display_panel.repaint();
            }
        });
        top_bar.setPreferredSize(new Dimension(800, 50));
        top_bar.add(reset);
        frame.add(top_bar, BorderLayout.NORTH);

        randMealPlanButton(results_panel, display_panel, top_bar, f, frame);


        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
    public static void main(String[] args) {
        Runnable foodFinder = new RunFunFoodFinder();
        SwingUtilities.invokeLater(foodFinder);
    }
}
