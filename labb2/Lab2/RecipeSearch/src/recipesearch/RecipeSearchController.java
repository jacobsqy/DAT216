
package recipesearch;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;


public class RecipeSearchController implements Initializable {

    RecipeDatabase db = RecipeDatabase.getSharedInstance();
    @FXML FlowPane resultFlowPane;

    @FXML ComboBox comboIngredient;
    @FXML ComboBox comboCuisine;

    @FXML RadioButton radioHard;
    @FXML RadioButton radioMedium;
    @FXML RadioButton radioEasy;
    @FXML RadioButton radioAll;

    @FXML Spinner spinnerMaxPrice;

    @FXML Slider sliderMaxTime;
    @FXML Label labelMaxTime;

    @FXML ImageView imageRecipe;
    @FXML Label labelRecipeName;

    @FXML AnchorPane recipeDetailPane;
    @FXML SplitPane searchPane;

    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();

    private RecipeBackendController recipeBackendController = new RecipeBackendController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        for (Recipe recipe : recipeBackendController.getRecipes()) {
            RecipeListItem recipeListItem = new RecipeListItem(recipe, this);
            recipeListItemMap.put(recipe.getName(), recipeListItem);
        }


        comboIngredient.getItems().addAll("Visa alla", "Kött", "Fisk", "Kyckling", "Vegetarisk");
        comboIngredient.getSelectionModel().select("Visa alla");

        comboIngredient.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                recipeBackendController.setMainIngredient(newValue);
                updateRecipeList();
            }
        });

        comboCuisine.getItems().addAll("Visa alla", "Sverige", "Grekland", "Indien", "Asien", "Afrika", "Frankrike");
        comboCuisine.getSelectionModel().select("Visa alla");

        comboCuisine.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                recipeBackendController.setCuisine(newValue);
                updateRecipeList();
            }
        });

        ToggleGroup difficultyToggleGroup = new ToggleGroup();

        radioAll.setToggleGroup(difficultyToggleGroup);
        radioEasy.setToggleGroup(difficultyToggleGroup);
        radioMedium.setToggleGroup(difficultyToggleGroup);
        radioHard.setToggleGroup(difficultyToggleGroup);
        radioAll.setSelected(true);

        difficultyToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (difficultyToggleGroup.getSelectedToggle() != null) {
                    RadioButton selected = (RadioButton) difficultyToggleGroup.getSelectedToggle();
                    recipeBackendController.setDifficulty(selected.getText());
                    updateRecipeList();
                }
            }
        });

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, 50, 5);
        spinnerMaxPrice.setValueFactory(valueFactory);
        spinnerMaxPrice.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                recipeBackendController.setMaxPrice(newValue);
                updateRecipeList();
            }
        });

        spinnerMaxPrice.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    // do nothing
                } else {
                    Integer value = Integer.valueOf(spinnerMaxPrice.getEditor().getText());
                    recipeBackendController.setMaxPrice(value);
                    updateRecipeList();
                }
            }
        });
        //TODO går inte att skriva
        sliderMaxTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !newValue.equals(oldValue) && !sliderMaxTime.isValueChanging()) {
                    recipeBackendController.setMaxTime(newValue.intValue());
                    updateRecipeList();
                }
                labelMaxTime.setText(String.valueOf((Math.round(newValue.intValue() / 10) * 10)) + " minuter");
            }
        });
        updateRecipeList();
    }

    private void populateRecipeDetailView(Recipe recipe) {
        labelRecipeName.setText(recipe.getName());
        imageRecipe.setImage(recipe.getFXImage());
    }

    @FXML
    public void closeRecipeView() {
        searchPane.toFront();
    }

    public void openRecipeView(Recipe recipe) {
        populateRecipeDetailView(recipe);
        recipeDetailPane.toFront();
    }


    private void updateRecipeList() {
        resultFlowPane.getChildren().clear();
        List<Recipe> recipes = recipeBackendController.getRecipes();
        for (Recipe recipe : recipes) {
            RecipeListItem recipeListItem = recipeListItemMap.get(recipe.getName());
            resultFlowPane.getChildren().add(recipeListItem);
        }
    }

}
