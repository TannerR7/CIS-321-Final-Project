import sqlite3

def record_actual_visitors (forecast_id, actual_visitors):
    connectDB = sqlite3.connect("forecast_database.db")
    connectDB.execute("PRAGMA foreign_keys = ON")

    executeSql = connectDB.execute(
        """
        select wf.restaurant_id, fi.forecast_date, fi.reserved_visitors, fi.holiday_flag
        from weekly_forecast as wf
        join forecast_input as fi on wf.forecast_id = fi.forecast_id
        where wf.forecast_id = ? 
        order by fi.forecast_date
        """,
        (forecast_id,)
    )
    forecast_records = executeSql.fetchall()

    daily_records = []

    for index, record in enumerate(forecast_records):
        restaurant_id = record[0]
        visit_date = record[1]
        reserved_visitors = record[2]
        holiday_flag = record[3]

        daily_records.append((restaurant_id, visit_date, actual_visitors[index], reserved_visitors, holiday_flag))

    for restaurant_id, visit_date, *_ in daily_records:
        executeSql = connectDB.execute(
            """
            select visitor_id
            from past_visitors
            where restaurant_id = ? and day_date = ?
            """,
            (restaurant_id, visit_date)
        )

    with connectDB:
        connectDB.executemany(
            """
            insert into past_visitors
            (restaurant_id, day_date, customer_count, reserved_visitors, holiday_flag)
            values(?, ?, ?, ?, ?)
            """,
            daily_records
        )

    connectDB.close()
    return {"forecast_id": forecast_id, "records_saved": len(daily_records), "message": "The customer count has been saved."}       


