import sqlite3



connectDB = sqlite3.connect("forecast_database.db")
connectDB.execute("PRAGMA foreign_keys = ON")

with open("schema.sql", "r", encoding="utf-8") as file:
    sql_script = file.read()

connectDB.executescript(sql_script)

connectDB.commit()
connectDB.close()
