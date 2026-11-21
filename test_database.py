import sqlite3

connectDB = sqlite3.connect("forecast_database.db")
connectDB.execute("PRAGMA foreign_keys = ON")




connectDB.close()