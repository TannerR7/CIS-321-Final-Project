import sqlite3
import pandas as pd

data = pd.read_csv("data/prepared_data.csv")
connectDB = sqlite3.connect("forecast_database.db")
connectDB.execute("PRAGMA foreign_keys = ON")

restaurant_ids = data["air_store_id"].unique()

for restaurant_id in restaurant_ids:
    connectDB.execute(
    """
    insert or ignore into restaurant
    (restaurant_id, restaurant_name)
    values(?, ?)
    """,
    (restaurant_id,restaurant_id))

daily_records = []

for row in data.itertuples(index=False):
    daily_records.append((row.air_store_id, row.visit_date, int(row.visitors), None, int(row.holiday_flg)))


connectDB.executemany(
    """
    insert or ignore into past_visitors
    (restaurant_id, day_date, customer_count, reserved_visitors, holiday_flag)
    values(?, ?, ?, ?, ?)
    """,
    daily_records)

connectDB.commit()

executeSql = connectDB.execute(
    "SELECT COUNT(*) FROM restaurant"
)

executeSql = connectDB.execute(
    "SELECT COUNT(*) FROM past_visitors"
)



connectDB.close()