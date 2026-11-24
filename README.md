# CIS 321 Final Project
Part 1: Setting up everthing
1. Download the repository
2. Make sure to have virtual environment create and active
3. Download the pandas, numpy, scikit-learn, joblib, fastapi, and uvicorn libraries
4. Run create_database.py and then run import_data.py to create the database
5. Run the create_test_user.py file to set up a temp user and password for the system for testing purposes. Username: test_owner and Password: password1234
6.	Run train_model.py

Part 2: Starting up the System
1. run the following code in the terminal: python -m uvicorn api:api --reload
2. Then run loginScreen.java(located in the interface folder) file, which will start up the javaFX interface.

Part 3: JavaFX screens
1. The first screen is the login screen: to login use the temp username and password created.
2. The second screen is the hub screen, which has fours buttons.
3. The generate forecast button will lead to a screen where you input necessary information to generate a forecast, then aftern forecast generation it will display the forecast predicitons.
4. The input button, will allow you to input the actual customer count of the previous week.
5. The view previous forecast, allows you to see your restaurants previous forecasts.
6. The view data will allow you to see the previous days customer count, holiday flag, and reservation count.
7. While view the data, if you see any errors, you can click the edit data button, which will allow you to input the correct value for the seleted field on the inputted date.
