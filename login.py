import sqlite3

def login(username, password):
    connectDB = sqlite3.connect("forecast_database.db")
    connectDB.execute("PRAGMA foreign_keys = ON")

    executeSql = connectDB.execute(
        """
        select user_id, restaurant_id, username, position
        from user
        where username = ? and passwrd = ?
        """,
        (username, password)
    )

    record = executeSql.fetchone()

    connectDB.close()
    if record is None:
        raise ValueError("You have inputted an invalid username or password.")
    
    return{"user_id": record[0], "restaurant_id": record[1], "username": record[2], "position": record[3]}



if __name__ == "__main__":
    result = login("test_owner", "password124")
    print(result)