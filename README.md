# SmartPantryManager
A Mobile Development Assignment

The main purpose of the application is to help users to 
create, read, update, delete and manage both in ingredients and recipes.

The application assists to reduce food wastage by monitoring expiration of stock
and meal preparation and planning especially those with busy schedules.

## Login Details

The default login details are:

Username: `admin`

Password: `admin123`

## How to Run the Project

- Download or clone the GitHub repository.
- Open Android Studio. 
- Select Open. 
- Select the SmartPantryManager project folder. 
- Wait for Gradle to finish syncing. 
- Start an Android Emulator or connect an Android device. 
- Click Run. 
- Select the required device. 
- Login using the default login details.

## The Application allows the user to:
- Login to the application
- View the main home page
- Add pantry ingredients
- Edit pantry ingredients
- Delete pantry ingredients
- View all pantry ingredients
- Search for ingredients
- View recipes
- Search for recipes
- View suggested recipes
- Add new recipes
- Navigate between the different screens
- Store information using SQLite

## Each Ingredient can contain
- Ingredient Name
- Quantity 
- Unit
- Expiry date

This provides the CRUD functionality required for the project.

## CRUD design:

## Recipes

The application contains recipes that are stored in the database.

## Each recipe contains:

- Recipe name
- Required ingredients
- Required quantities
- Units
- Cooking or preparation steps

The application also allows the user to add their own recipes.
When adding a recipe the user can enter the recipe name, ingredients and 
cooking steps.

## Suggested Recipes

The Suggested Recipes section checks which recipes can currently be 
made using the ingredients available in the pantry.
The application uses a strict matching rule.
A recipe will only appear in Suggested Recipes if the user has all the ingredients required for the recipe.
The user must also have enough of each ingredient.

Example:

- 25kg Mutton, 4 potatoes , 1 onion

The application is also able to different unit measurements such as
- kg and g
- l and ml
- single and plural names of ingredients


## Database

The application uses a SQLite with SQLiteOpenHelper

I selected SQLite because the data can be stored directly on the Android device.

The application does not require an internet connection or an external database server to access the pantry or recipe information.

The main database tables used are:

- Pantry
- Recipes
- Recipe Ingredients

## Pantry Table

The Pantry table stores information such as:

- Ingredient ID
- Ingredient name
- Quantity
- Unit
- Expiry date

## Recipes Table

The Recipes table stores:

- Recipe ID
- Recipe name
- Preparation method

## Recipe Ingredients Table

The Recipe Ingredients table stores:

- Recipe ID
- Ingredient name
- Required quantity
- Unit

## Technologies Used

The project uses:

- Java
- Android Studio
- XML
- SQLite
- SQLiteOpenHelper
- ListView
- Custom Adapters
- Intents
- Git
- GitHub

## Main Java Files

Some of the main Java files used in the application are:

- `MainActivity.java` - Controls the login page
- `HomePgActivity.java` - Controls the home page and navigation
- `PantryAct.java` - Displays and manages pantry ingredients
- `IngredientsActivity.java` - Allows ingredients to be added or edited
- `RecipesAct.java` - Displays the available recipes
- `SuggestedRecipesAct.java` - Displays recipes that match the pantry
- `act_add_recipe.java` - Allows the user to add a new recipe
- `DatabaseHelper.java` - Controls the SQLite database
- `PantryApt.java` - Displays pantry information inside the ListView
- `RecipesApt.java` - Displays recipe information inside the ListView

## XML Files

The XML files are used to design the different screens of the application.

They control things such as:

- Buttons
- Text fields
- Text
- Lists
- Colours
- Layouts
- Navigation

The Java files are then connected to the XML components using IDs.

For example, a button created in XML can be connected to Java using `findViewById()`.

## HamBurger Nav

The application currently includes the following screens:

- Login
- Home
- My Pantry
- Add/Edit Ingredient
- Recipes
- Suggested Recipes
- Add Recipe

The application uses Intents to move between the different Activities.

It also uses a hamburger navigation menu on some screens.



## GitHub

GitHub is used to keep track of the development of the project.

Different commits were made while creating and improving the application.

Examples:

- Creating the application screens
- Adding the database
- Adding pantry functions
- Adding recipes
- Adding suggested recipes
- Adding navigation
- Adding search functions
- Adding the Add Recipe feature
- Fixing errors and improving the application



