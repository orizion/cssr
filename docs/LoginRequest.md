## Post Request für Login

POST /login HTTP/1.1
Host: localhost:8090
Content-Type: application/json
Cache-Control: no-cache

{
	"email": "adrian.ehrsam@students.fhnw.ch",
	"password": "oij"
}

## Weitere Requests mit Authorization Header

GET /test/user HTTP/1.1
Host: localhost:8090
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyIjoic3R1ZCIsInN1YiI6ImFkcmlhbi5laHJzYW1Ac3R1ZGVudHMuZmhudy5jaCIsImV4cCI6MTQ5MDEwMzAwMn0.Q1Vz_waGw65FxPmteTCHpxqNPpQ3ukvuIY7zHYWgFqGpLpxVVKV6PQJJAASm0oazR0eZH3ZpY3dzwrCndyyT2g
Cache-Control: no-cache
