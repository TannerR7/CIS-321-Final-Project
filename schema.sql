PRAGMA foreign_keys = ON;

create table if not exists restaurant(
    restaurant_id TEXT primary key,
    restaurant_name TEXT not null

);

create table if not exists user(
    user_id integer primary key,
    restaurant_id TEXT not null,
    username TEXT not null unique,
    passwrd TEXT not null,
    position TEXT not null check (position IN ('Owner', 'Manager')),

    foreign key(restaurant_id) references restaurant(restaurant_id)
);

create index if not exists idx_user_restaurant ON user(restaurant_id);

create table if not exists weekly_forecast(
    forecast_id integer primary key,
    restaurant_id TEXT not null,
    user_id integer not null,
    week_start TEXT not null,
    week_end TEXT not null,
    when_generated TEXT not null default current_timestamp,

    foreign key(restaurant_id) references restaurant(restaurant_id),
    foreign key(user_id) references user(user_id),

    check (week_end >= week_start)
);

create index if not exists idx_forecast_restaurant_week ON weekly_forecast(restaurant_id, week_start);

create table if not exists forecast_input(
    input_id integer primary key,
    forecast_id integer not null,
    forecast_date TEXT not null,
    reserved_visitors integer not null check (reserved_visitors >= 0),
    holiday_flag integer not null check (holiday_flag IN(0,1)),

    foreign key(forecast_id) references weekly_forecast(forecast_id),
    unique(forecast_id, forecast_date)
);

create table if not exists past_predictions(
    prediction_id integer primary key,
    input_id integer unique not null,
    predicted_visitors integer not null check (predicted_visitors >= 0),
    lower_range integer check (lower_range >= 0),
    upper_range integer check (upper_range >= 0),

    foreign key(input_id) references forecast_input(input_id),

    check (lower_range <= upper_range or upper_range is null or lower_range is null)
);

create table if not exists past_visitors(
    visitor_id integer primary key,
    restaurant_id TEXT not null,
    day_date TEXT not null,
    customer_count integer not null check (customer_count >= 0),
    reserved_visitors integer check (reserved_visitors >= 0),
    holiday_flag integer not null check (holiday_flag IN(0,1)),

    foreign key(restaurant_id) references restaurant(restaurant_id),
    unique(restaurant_id, day_date)
);

create table if not exists data_correction(
    correction_id integer primary key,
    visitor_id integer not null,
    user_id integer not null,
    field_changed TEXT not null check (field_changed IN ('customer_count', 'reserved_visitors', 'holiday_flag')),
    previous_value integer check (previous_value IS null or previous_value >= 0),
    corrected_value integer check (corrected_value IS null or corrected_value >= 0),
    when_corrected TEXT not null default current_timestamp,

    foreign key(visitor_id) references past_visitors(visitor_id),
    foreign key(user_id) references user(user_id),

    check(field_changed != 'holiday_flag' or  ((previous_value is null or previous_value in (0,1)) and (corrected_value is null or corrected_value in (0,1))))
);

create index if not exists idx_correction_visitors ON data_correction(visitor_id, when_corrected);
create index if not exists idx_correction_user ON data_correction(user_id);

