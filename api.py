from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from login import login
from generate_forecast import generate_forecast
from retrieve_data import retrieve_data, retrieve_forecast_predictions
from visitor_count_preWeek import record_actual_visitors
from edit_data import edit_data

api = FastAPI()

class RequestingLogin(BaseModel):
    username: str
    password: str

class RequestingForecast(BaseModel):
    restaurant_id: str
    user_id: int
    forecast_start: str
    daily_reservations: list[int]
    daily_holiday_flags: list[int]

class RequestingActVisitors(BaseModel):
    forecast_id: int
    actual_visitors: list[int]

class RequestingEditData(BaseModel):
    restaurant_id: str
    visit_date: str
    user_id: int
    field_changed: str
    corrected_value: int


@api.get("/")
def home():
    return {"message": "The API is now running"}



@api.post("/login")
def user_login(request: RequestingLogin):
    try:
        return login(request.username, request.password)
    except ValueError as error:
        raise HTTPException(status_code=401, detail=(error))


@api.post("/forecast")
def gen_forecast(request: RequestingForecast):
    try:
        return generate_forecast(request.restaurant_id, request.user_id, request.forecast_start, request.daily_reservations, request.daily_holiday_flags)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=(error))


@api.get("/retrieveData/{restaurant_id}")
def ret_data(restaurant_id: str):
    return retrieve_data(restaurant_id)


@api.get("/retreiveForecast/{restaurant_id}")
def ret_forecasts(restaurant_id: str):
    return retrieve_forecast_predictions(restaurant_id)


@api.post("/actualVisitors")
def act_visitors(request: RequestingActVisitors):
    try:
        return record_actual_visitors(request.forecast_id, request.actual_visitors)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=(error))


@api.put("/edit-data")
def ed_data(request: RequestingEditData):
    try:
        return edit_data(request.restaurant_id, request.visit_date, request.user_id, request.field_changed, request.corrected_value)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=(error))

