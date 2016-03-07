CREATE DATABASE onemdm;
\c onemdm;
CREATE TABLE heartbeats (
id          SERIAL PRIMARY KEY,
deviceId    VARCHAR(100) NOT NULL,
createdAt   TIMESTAMP
);
