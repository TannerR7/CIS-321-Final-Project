from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from login import login
from generate_forecast import generate_forecast

app = FastAPI()

class RequestingLogin(BaseModel):
    username: str
    password: str

class RequestingForecast(BaseModel):
    restaurant_id: str
    user_id: int
    forecast_start: str
    daily_reservations: list[int]
    daily_holiday_flags: list[int]


@app.get("/")
def home():
    return {"message": "API is now running"}

@app.post("/login")

def user_login(request: RequestingLogin):
    try:
        return login(request.username, request.password)
    except ValueError as error:
        raise HTTPException(status_code=401, detail=(error))
