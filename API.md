## User API Endpoints

- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/validate/{username}` - Validate user
- `GET /api/v1/users/exists/{username}` - Check if user exists

## Session API Endpoints

- `POST /api/v1/sessions` - Create new session
- `GET /api/v1/sessions/{sessionCode}` - Get session by code
- `PUT /api/v1/sessions/{sessionCode}/lock` - Lock session

## Restaurant API Endpoints

- `POST /api/v1/sessions/{sessionCode}/restaurants` - Submit restaurant
- `GET /api/v1/sessions/{sessionCode}/restaurants` - Get all restaurants in session
- `GET /api/v1/sessions/{sessionCode}/restaurants/random` - Get random restaurant
- `GET /api/v1/sessions/{sessionCode}/restaurants/count` - Get restaurant count
- `GET /api/v1/sessions/{sessionCode}/restaurants/can-request-random/{username}` - Check permission
- `POST /api/v1/sessions/{sessionCode}/restaurants/{restaurantId}/delete` - Delete restaurant
