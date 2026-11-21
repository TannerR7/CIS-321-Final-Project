import sqlite3

connectDB = sqlite3.connect("forecast_database.db")
connectDB.execute("PRAGMA foreign_keys = ON")

executeSql = connectDB.execute(
    """
    SELECT
        fi.forecast_date,
        pp.predicted_visitors,
        pp.lower_range,
        pp.upper_range
    FROM forecast_input AS fi
    JOIN past_predictions AS pp
        ON fi.input_id = pp.input_id
    WHERE fi.forecast_id = ?
    ORDER BY fi.forecast_date
    """,
    (4,)
)

print("\nSaved forecast with prediction ranges:")

for record in executeSql.fetchall():
    print(record)










connectDB.close()