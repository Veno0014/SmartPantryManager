# SmartPantryManager
A Mobile Development Assignment

The main purpose of the application is to help users to
create, read, update, delete and manage both ingredients and recipes.

The application assists to reduce food wastage by monitoring expiration of stock
and meal preparation and planning especially those with busy schedules.

The application stores information locally using SQLite, meaning that the pantry and recipe
information can still be accessed after the application is closed and reopened.

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
- Receive low stock notifications
- Change application settings

## Each Ingredient can contain
- Ingredient Name
- Quantity
- Unit
- Expiry date

This provides the CRUD functionality required for the project.

## CRUD design:

CRUD means Create, Read, Update and Delete.

The pantry section uses CRUD in the following way:

- Create - The user can add a new pantry ingredient.
- Read - The user can view the ingredients currently stored in the pantry.
- Update - The user can edit an existing ingredient.
- Delete - The user can delete an ingredient from the pantry.

The database information remains stored after the application is closed and reopened.

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

The recipe and its ingredients are stored inside the SQLite database.

## Suggested Recipes

The Suggested Recipes section checks which recipes can currently be
made using the ingredients available in the pantry.

The application uses a strict matching rule.

A recipe will only appear in Suggested Recipes if the user has all the ingredients required for the recipe.

The user must also have enough of each ingredient.

If even one ingredient is missing or there is not enough of that ingredient,
the recipe will not appear in the Suggested Recipes list.

Example:

- 25kg Mutton, 4 potatoes, 1 onion

The application is also able to handle different unit measurements such as:

- kg and g
- l and ml
- single and plural names of ingredients

The application converts compatible units before comparing the pantry quantity
with the quantity required by the recipe.

It also standardizes some ingredient names to help prevent simple differences
from stopping a recipe from matching.

## Database

The application uses SQLite with SQLiteOpenHelper.

I selected SQLite because the data can be stored directly on the Android device.

The application does not require an internet connection or an external database server to access the pantry or recipe information.

SQLite was also selected because it is suitable for storing local application data
and allows the information to remain available after the application is closed.

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

The Recipes table and Recipe Ingredients table work together using the Recipe ID.

This allows one recipe to contain multiple required ingredients.

The Pantry table is then compared to the Recipe Ingredients table using the
strict matching logic inside the application.

## Low Stock Notifications

The application also contains low stock notifications.

When low stock notifications are enabled in the Settings screen,
the application checks the amount of stock currently available in the pantry.

If the quantity is considered low, the application can display a notification
to inform the user that more stock may be required.

The application also stores the previous notification message to help stop
the same notification from repeatedly showing.

For newer Android versions, notification permission must also be given before
the application can display notifications.

## Settings

The application includes a Settings screen.

The Settings screen currently contains options such as:

- Dark Mode
- Low Stock Notifications

The settings allow the user to control some of the behaviour of the application.

The Settings screen is controlled using `actsettings.java` and its layout is designed using `actsettings.xml`.

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
- SharedPreferences
- Android Notifications
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
- `actsettings.java` - Controls the Settings screen including Dark Mode and Low Stock Notification settings
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

The `actsettings.xml` file is used to design the Settings screen and contains the controls used for Dark Mode and Low Stock Notifications.

## HamBurger Nav

The application currently includes the following screens:

- Login
- Home
- My Pantry
- Add/Edit Ingredient
- Recipes
- Suggested Recipes
- Add Recipe
- Settings

The application uses Intents to move between the different Activities.

It also uses a hamburger navigation menu on some screens.

The navigation allows the user to move between the main sections of the
application without needing to return to the login screen.

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
- Adding low stock notifications
- Fixing errors and improving the application

The GitHub repository shows the development of the application through
different commits made during the project.