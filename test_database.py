import sqlite3

conn = sqlite3.connect("forecast_database.db")

tables = conn.execute("""
    SELECT name
    FROM sqlite_master
    WHERE type = 'table'
    AND name NOT LIKE 'sqlite_%'
""").fetchall()

for (table,) in tables:
    safe_name = table.replace('"', '""')
    count = conn.execute(
        f'SELECT COUNT(*) FROM "{safe_name}"'
    ).fetchone()[0]

    print(f"{table}: {count} records")

conn.close()