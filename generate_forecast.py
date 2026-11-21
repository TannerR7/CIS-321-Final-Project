import sqlite3
import joblib
import pandas as pd

saved_data = joblib.load("random_forest_model.joblib")

model = saved_data["model"]
features = saved_data["features"]
restaurant_map = saved_data["restaurant_map"]
error_threshold = saved_data["error_threshold"]

restaurant_to_numbers = {
    restaurant_id: number
    for number, restaurant_id in restaurant_map.items()
}

def generate_forecast(restaurant_id, user_id, forecast_start, daily_reservations, daily_holiday_flags):
    forecast_start = pd.Timestamp(forecast_start)

    connectDB = sqlite3.connect("forecast_database.db")
    connectDB.execute("PRAGMA foreign_keys = ON")
    query = """
    select day_date, customer_count
    from past_visitors
    where restaurant_id = ?
    order by day_date
    """

    records = pd.read_sql_query(query, connectDB, params=(restaurant_id,))
    records["day_date"] = pd.to_datetime(records["day_date"])
    records = records[records["day_date"] < forecast_start].copy()

    if records.empty:
        connectDB.close()
        raise ValueError("No historical customer data found.")
    
    records["weekday_num"] = records["day_date"].dt.dayofweek
    documented_overall_avg = records["customer_count"].mean()
    recent_records = records[records["day_date"] >= forecast_start - pd.Timedelta(days=28)]
    recent_mean_28 = recent_records["customer_count"].mean()

    if pd.isna(recent_mean_28):
        recent_mean_28 = documented_overall_avg

    weekday_means = records.groupby("weekday_num")["customer_count"].mean()

    forecast_dates = pd.date_range(start=forecast_start, periods=7,freq="D")

    rows_of_forecast = []

    for x, date in enumerate(forecast_dates):
        weekday = date.dayofweek

        rows_of_forecast.append({
            "restaurant_num": restaurant_to_numbers[restaurant_id], "weekday_num": weekday, "month": date.month, "day": date.day,
            "holiday_flg": daily_holiday_flags[x], "documented_weekday_avg": weekday_means.get(weekday, documented_overall_avg),
            "documented_overall_avg": documented_overall_avg, "recent_mean_28": recent_mean_28, "known_reservations": daily_reservations[x]})

    forecast_features = pd.DataFrame(rows_of_forecast, columns=features)

    predictions = model.predict(forecast_features)
    predictions = predictions.round().clip(min=0).astype(int)

    lower_range = (predictions - error_threshold).clip(min=0).round().astype(int)
    upper_range = (predictions + error_threshold).round().astype(int)

    with connectDB:
        executeSql = connectDB.execute(
            """
            insert into weekly_forecast
            (restaurant_id, user_id, week_start, week_end)
            values(?, ?, ?, ?)
            """,
            (restaurant_id, user_id, forecast_dates[0].strftime("%Y-%m-%d"), forecast_dates[-1].strftime("%Y-%m-%d"))
        )

        forecast_id = executeSql.lastrowid

        for x, date in enumerate(forecast_dates):
            executeSql = connectDB.execute(
                """
                insert into forecast_input
                (forecast_id, forecast_date, reserved_visitors, holiday_flag)
                values(?, ?, ?, ?)
                """,
                (forecast_id, date.strftime("%Y-%m-%d"), daily_reservations[x], daily_holiday_flags[x])
            )

            input_id = executeSql.lastrowid

            connectDB.execute(
                """
                insert into past_predictions
                (input_id, predicted_visitors, lower_range, upper_range)
                values (?, ?, ?, ?)
                """,
                (input_id, int(predictions[x]), int(lower_range[x]), int(upper_range[x]))
            )
    
    forecast_results = []
    for x, (date, prediction) in enumerate(zip(forecast_dates, predictions)):
        forecast_results.append({"date": date.strftime("%Y-%m-%d"), "predicted_visitors": int(prediction), "lower_range": int(lower_range[x]), "upper_range": int(upper_range[x])})

    
    connectDB.close()
    return {"forecast_id": forecast_id, "predictions": forecast_results}