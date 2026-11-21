import sqlite3

def edit_data(restaurant_id, visit_date, user_id, field_changed, corrected_value):
    connectDB = sqlite3.connect("forecast_database.db")
    connectDB.execute("PRAGMA foreign_keys = ON")

    executeSql = connectDB.execute(
        """
        select visitor_id, customer_count, reserved_visitors, holiday_flag
        from past_visitors
        where restaurant_id = ? and day_date = ?
        """,
        (restaurant_id, visit_date)
    )

    record = executeSql.fetchone()

    allowed_changed_fields = {"customer_count": 1, "reserved_visitors": 2, "holiday_flag": 3}
    visitor_id = record[0]
    previous_value = record[allowed_changed_fields[field_changed]]




    with connectDB:
        connectDB.execute(
            f"""
            update past_visitors
            set {field_changed} = ?
            where visitor_id = ?
            """,
            (corrected_value, visitor_id)
        )

        connectDB.execute(
            """
            insert into data_correction
            (visitor_id, user_id, field_changed, previous_value, corrected_value)
            values (?, ?, ?, ?, ?)
            """,
            (visitor_id, user_id, field_changed, previous_value, corrected_value)
        )

    connectDB.close()
    return{"restaurant_id": restaurant_id, "visit_date": visit_date, "field_changed": field_changed, "previous_value": previous_value, "corrected_value": corrected_value, "message": "data corrected"}

if __name__ == "__main__":
    result = edit_data(
        restaurant_id="air_ba937bf13d40fb24",
        visit_date="2017-04-28",
        user_id=1,
        field_changed="holiday_flag",
        corrected_value=0
    )

    print(result)