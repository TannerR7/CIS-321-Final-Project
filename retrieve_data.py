import sqlite3

def retrieve_data(restaurant_id):
    connectDB = sqlite3.connect("forecast_database.db")
    connectDB.execute("PRAGMA foreign_keys = ON")

    executeSql = connectDB.execute(
        """
        select visitor_id, day_date, customer_count, reserved_visitors, holiday_flag
        from past_visitors
        where restaurant_id = ?
        order by day_date desc
        """,
        (restaurant_id,)
    )
    records = executeSql.fetchall()

    data = []

    for record in records:
        data.append({"visitor_id": record[0], "date": record[1], "customer_count": record[2], "reserved_visitors": record[3], "holiday_flag": record[4]})


    connectDB.close()
    return data




def retrieve_forecast_predictions(restaurant_id):
    connectDB = sqlite3.connect("forecast_database.db")
    connectDB.execute("PRAGMA foreign_keys = ON")

    executeSql = connectDB.execute(
        """
        select wf.forecast_id, wf.week_start, wf.week_end, wf.when_generated, fi.forecast_date, fi.reserved_visitors, fi.holiday_flag, pp.predicted_visitors, pp.lower_range, pp.upper_range
        from weekly_forecast as wf
        join forecast_input as fi on wf.forecast_id = fi.forecast_id
        join past_predictions as pp on fi.input_id = pp.input_id
        where wf.restaurant_id = ?
        order by when_generated desc
        """,
        (restaurant_id,)
    )
    records = executeSql.fetchall()

    forecasts= []

    for record in records:
        forecasts.append({"forecast_id": record[0], "week_start": record[1], "week_end": record[2], "when_generated": record[3],
                              "date": record[4], "reserved_visitors": record[5], "holiday_flag": record[6],
                              "predicted_visitors": record[7], "lower_range": record[8], "upper_range": record[9]})


    connectDB.close()
    return forecasts




