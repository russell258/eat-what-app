## Set up
0. cd /eat-what-app
1. cd /backend and create db/password.txt in backend folder with your own password
2. Set chmod 600 for this file
3. cd to /frontend and create key.pem and cert.pem (Self-signed certificates) in frontend folder with the following command
```
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes
```
4. Edit /backend/src/main/resources/users.csv to add your own users and roles
5. Run this command:
docker compose up -d --build
6. Go to http://localhost:3000
7. Refer to API.md for API endpoints

## Existing usernames to try
SESSION_INITIATOR: ruschin.chin
GUEST: tester
** You can add more users with the roles in /backend/src/main/resources/users.csv


## Tech Stack
`DB`: MySQL
`Backend`: Spring Boot
`Frontend`: React + Vite
`Batch Processing`: Spring Batch

## DATABASE
+---------------------+
| Tables_in_eatwhatdb |
+---------------------+
| restaurants         |
| sessions            |
| users               |
+---------------------+

+-------------+----------------------+-----------+-------------+----------------+----------------+
| TABLE_NAME  | COLUMN_NAME          | DATA_TYPE | IS_NULLABLE | COLUMN_DEFAULT | COLUMN_COMMENT |
+-------------+----------------------+-----------+-------------+----------------+----------------+
| restaurants | id                   | bigint    | NO          | NULL           |                |
| restaurants | submitted_at         | datetime  | NO          | NULL           |                |
| restaurants | submitted_by         | varchar   | NO          | NULL           |                |
| restaurants | session_id           | bigint    | NO          | NULL           |                |
| restaurants | restaurant_name      | varchar   | NO          | NULL           |                |
| sessions    | id                   | bigint    | NO          | NULL           |                |
| sessions    | created_at           | datetime  | NO          | NULL           |                |
| sessions    | locked _at           | datetime  | YES         | NULL           |                |
| sessions    | session_code         | varchar   | NO          | NULL           |                |
| sessions    | status               | enum      | NO          | NULL           |                |
| sessions    | initiator_id         | bigint    | NO          | NULL           |                |
| sessions    | locked_at            | datetime  | YES         | NULL           |                |
| sessions    | random_restaurant_id | bigint    | YES         | NULL           |                |
| users       | id                   | bigint    | NO          | NULL           |                |
| users       | created_at           | datetime  | NO          | NULL           |                |
| users       | email                | varchar   | NO          | NULL           |                |
| users       | role                 | enum      | NO          | NULL           |                |
| users       | username             | varchar   | NO          | NULL           |                |
+-------------+----------------------+-----------+-------------+----------------+----------------+

FOREIGN_KEY:
+-------------+----------------------+-----------------------+------------------------+
| TABLE_NAME  | COLUMN_NAME          | REFERENCED_TABLE_NAME | REFERENCED_COLUMN_NAME |
+-------------+----------------------+-----------------------+------------------------+
| restaurants | session_id           | sessions              | id                     |
| sessions    | random_restaurant_id | restaurants           | id                     |
| sessions    | initiator_id         | users                 | id                     |
+-------------+----------------------+-----------------------+------------------------+


## Features:
1. Create new session
2. Copy session code
3. Add suggested restaurant
4. Delete suggested restaurant (if session is not locked)
5. Get Random Restaurant (only first suggester can request)
6. Join existing / locked session
7. Locked session contains the chosen random restaurant



## Future Enhancements
1. Prevent same restaurant submission for the same session by the same user, or give warning
2. Setting limit of submissions per user in each unique session
3. Input sanitization of suggested restaurants to prevent SQL injections, etc.
4. WebMvcConfigurer for CORS
5. Enable password for "private" sessions
6. Pagination for list of suggested restaurants --> pagination API
