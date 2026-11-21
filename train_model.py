import pandas as pd
import numpy as np
import joblib

from pathlib import Path
from sklearn.metrics import mean_absolute_error
from sklearn.ensemble import RandomForestRegressor

data_folder = Path(__file__).resolve().parent / "data"
data = pd.read_csv(data_folder / "prepared_data.csv")
data["visit_date"] = pd.to_datetime(data["visit_date"])
data["weekday_num"] = data["visit_date"].dt.day_of_week
data["month"] = data["visit_date"].dt.month
data["day"] = data["visit_date"].dt.day
data["restaurant_num"] = data["air_store_id"].astype("category").cat.codes
data = data.sort_values(["air_store_id", "visit_date"]).copy()

forecast_start = pd.Timestamp("2017-04-16")
forecast_end = pd.Timestamp("2017-04-22")

train_data = data[data["visit_date"] < forecast_start].copy()
test_data = data[(data["visit_date"] >= forecast_start) & (data["visit_date"]<= forecast_end)].copy()
train_data = train_data.sort_values(["air_store_id", "visit_date"])
weekday_avgs = (train_data.groupby(["air_store_id", "weekday_num"])["visitors"].apply(lambda x: x.tail(4).mean()).reset_index(name="rules_based_prediction"))
test_data = test_data.merge(weekday_avgs, on=["air_store_id", "weekday_num"], how="left")
indiv_restaurant_avg = (train_data.groupby("air_store_id")["visitors"].mean())
global_avg = train_data["visitors"].mean()

test_data["rules_based_prediction"] = (test_data["rules_based_prediction"].fillna(test_data["air_store_id"].map(indiv_restaurant_avg)).fillna(global_avg))
test_data["rules_based_prediction"] = (test_data["rules_based_prediction"].round().clip(lower=0).astype(int))

rules_based_mae = mean_absolute_error(test_data["visitors"],test_data["rules_based_prediction"])
avg_customer = (train_data.groupby(["air_store_id", "weekday_num"])["visitors"].mean().reset_index(name="documented_weekday_avg"))
overall_customer_avg = (train_data.groupby("air_store_id")["visitors"].mean().reset_index(name="documented_overall_avg"))
test_data = test_data.merge(avg_customer, on=["air_store_id", "weekday_num"],how="left")
test_data = test_data.merge(overall_customer_avg, on="air_store_id", how="left")

global_avg = train_data["visitors"].mean()

test_data["documented_overall_avg"] = (test_data["documented_overall_avg"].fillna(global_avg))
test_data["documented_weekday_avg"] = (test_data["documented_weekday_avg"].fillna(test_data["documented_overall_avg"]))
train_data["week_start"] = (train_data["visit_date"] - pd.to_timedelta((train_data["weekday_num"] + 1) % 7, unit="D"))

weekday_weekly =  (train_data.groupby(["air_store_id", "weekday_num", "week_start"])["visitors"].agg(["sum","count"]).reset_index().sort_values(["air_store_id", "weekday_num", "week_start"]))
weekday_weekly["previous_sum"] = (weekday_weekly.groupby(["air_store_id", "weekday_num"])["sum"].cumsum() - weekday_weekly["sum"])
weekday_weekly["previous_count"] = (weekday_weekly.groupby(["air_store_id", "weekday_num"])["count"].cumsum() - weekday_weekly["count"])
weekday_weekly["documented_weekday_avg"] = (weekday_weekly["previous_sum"] / weekday_weekly["previous_count"].replace(0,float("nan")))

restaurant_weekly = (train_data.groupby(["air_store_id", "week_start"])["visitors"].agg(["sum", "count"]).reset_index().sort_values(["air_store_id", "week_start"]))
restaurant_weekly["previous_sum"] = (restaurant_weekly.groupby("air_store_id")["sum"].cumsum() - restaurant_weekly["sum"])
restaurant_weekly["previous_count"] = (restaurant_weekly.groupby("air_store_id")["count"].cumsum() - restaurant_weekly["count"])
restaurant_weekly["documented_overall_avg"] = (restaurant_weekly["previous_sum"] / restaurant_weekly["previous_count"].replace(0, float("nan")))


train_data = train_data.merge(weekday_weekly[["air_store_id", "weekday_num", "week_start", "documented_weekday_avg"]], on=["air_store_id", "weekday_num", "week_start"], how="left")
train_data = train_data.merge(restaurant_weekly[["air_store_id", "week_start", "documented_overall_avg"]], on=["air_store_id", "week_start"], how="left")
train_data = train_data.dropna(subset=["documented_overall_avg"]).copy()
train_data["documented_weekday_avg"] = (train_data["documented_weekday_avg"].fillna(train_data["documented_overall_avg"]))

pre_visits = data[data["visit_date"] < forecast_start][["air_store_id", "visit_date", "visitors"]].copy()
all_weeks = pd.concat([train_data[["air_store_id", "week_start"]],test_data[["air_store_id"]].assign(week_start=forecast_start)]).drop_duplicates()

recent_rows = []
print("Start of wait")
for row in all_weeks.itertuples(index=False): 
    filtered_data = pre_visits[(pre_visits["air_store_id"] == row.air_store_id) & (pre_visits["visit_date"] < row.week_start) & (pre_visits["visit_date"] >= row.week_start - pd.Timedelta(days=28))]
    recent_rows.append({"air_store_id": row.air_store_id, "week_start": row.week_start, "recent_mean_28": filtered_data["visitors"].mean()})
print("end of wait")

recent_features = pd.DataFrame(recent_rows)

train_data = train_data.merge(recent_features,on=["air_store_id","week_start"],how="left")
test_data["week_start"] = forecast_start
test_data = test_data.merge(recent_features,on=["air_store_id","week_start"],how="left")


train_data["recent_mean_28"] = (train_data["recent_mean_28"].fillna(train_data["documented_overall_avg"]))
test_data["recent_mean_28"] = (test_data["recent_mean_28"].fillna(test_data["documented_overall_avg"]))

reservations = pd.read_csv(data_folder / "air_reserve.csv")
reservations["visit_datetime"] = pd.to_datetime(reservations["visit_datetime"])
reservations["reserve_datetime"] = pd.to_datetime(reservations["reserve_datetime"])
reservations["visit_date"] = (reservations["visit_datetime"].dt.normalize())
reservations["week_start"] = (reservations["visit_date"] - pd.to_timedelta((reservations["visit_date"].dt.dayofweek + 1) % 7, unit="D"))
known_reservations = reservations[reservations["reserve_datetime"] < reservations["week_start"]].copy()
reservation_features = (known_reservations.groupby(["air_store_id", "visit_date"])["reserve_visitors"].sum().reset_index(name="known_reservations"))

train_data = train_data.merge(reservation_features, on=["air_store_id","visit_date"], how="left")
test_data = test_data.merge(reservation_features, on=["air_store_id","visit_date"], how="left")
train_data["known_reservations"] = (train_data["known_reservations"].fillna(0))
test_data["known_reservations"] = (test_data["known_reservations"].fillna(0))


validation_start = pd.Timestamp("2017-04-09")
validation_end = pd.Timestamp("2017-04-15")
validation_train = train_data[train_data["visit_date"] < validation_start].copy()
validation_test = train_data[(train_data["visit_date"] >= validation_start) & (train_data["visit_date"] <= validation_end)].copy()


features_list = ["restaurant_num", "weekday_num", "month", "day", "holiday_flg", "documented_weekday_avg", "documented_overall_avg", "recent_mean_28", "known_reservations"]

y_train = validation_train["visitors"]
y_validation = validation_test["visitors"]

restaurant_map = dict(enumerate(data["air_store_id"].astype("category").cat.categories))

eval_model = RandomForestRegressor(n_estimators=100, max_depth=15, min_samples_leaf=5, random_state=42, n_jobs=-1)
final_model = RandomForestRegressor(n_estimators=100, max_depth=15, min_samples_leaf=5, random_state=42, n_jobs=-1)
final_model.fit(train_data[features_list],train_data["visitors"])
eval_model.fit(validation_train[features_list],y_train)
eval_predictions = (eval_model.predict(validation_test[features_list]).round().clip(min=0))
eval_mae = mean_absolute_error(y_validation,eval_predictions)
absolute_errors = abs(y_validation.to_numpy() - eval_predictions)
error_threshold = np.quantile(absolute_errors, .9)

joblib.dump({"model": final_model, "features": features_list, "restaurant_map": restaurant_map, "error_threshold": error_threshold}, "random_forest_model.joblib")
print("Final model has been saved")