import sqlite3

connectDB = sqlite3.connect("forecast_database.db")
connectDB.execute("PRAGMA table_info(user)")

connectDB.execute(
    """
    insert or ignore into user
    (restaurant_id, username, passwrd, position)
    values (?, ?, ?, ?)
    """,
    ("air_ba937bf13d40fb24", "test_owner", "password1234", "Owner"))


connectDB.commit()
connectDB.close()
