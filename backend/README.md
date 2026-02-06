## Set up
Create db/password.txt within /backend with a password
set chmod 600

run ./setup-certs.sh 
or give isntruction to set up your own cert in the /frontend if https

auto fire up the browser








bugs:
1) When user don't have session_inititaor role, let them know la dont just network error
2) when tester join session, go to blank page /session/${sessionid}?user=tester
http://localhost:8080/api/sessions/D6A5F0/restaurants
returns everything but then not displayed

3) when joining correct session but username not inside, it just enters.