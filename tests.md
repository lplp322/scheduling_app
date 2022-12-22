# Test cases

## Authentication

### Registering

* "user" - employee of EEMCS.

![User successful registration](postman_tests/register_success.png)

* "user2" - employee of IDE, admin of EEMCS

![User2 successful registration](postman_tests/register_user2.png)

* User already exists

![User already exists](postman_tests/register_already_exists.png)

### Logging in

* Correct NetID and password

![Successful login](postman_tests/login_success.png)

* Wrong password

![Wrong password](postman_tests/login_wrong_password.png)

* No such user

![No such user](postman_tests/login_no_user.png)

## Waiting list

### Adding a request

* Request added successfully

![Successful new request](postman_tests/new_request_success.png)

![New request status](postman_tests/new_request_status.png)

* Memory > CPU

![Memory > CPU](postman_tests/new_request_ram_gt_cpu.png)

* GPU > CPU

![GPU > CPU](postman_tests/new_request_gpu_gt_cpu.png)

* Deadline same as current date (22.12.2022)

![Deadline today](postman_tests/new_request_deadline_same_day.png)

* Deadline in the past (before 22.12.2022)

![Deadline past](postman_tests/new_request_deadline_past.png)

* User not an employee of the faculty

![User not employee](postman_tests/new_request_not_employee.png)

### Getting requests by faculty

* Successfully getting requests

* User not admin of the faculty

### Rejecting requests

* Successfully rejecting requests

* User not admin of the faculty

* Request not in waiting list

![Request not in waiting list](postman_tests/reject_no_request.png)

### Accepting requests

* Request successfully accepted

* Not enough resources

* User not admin of the faculty

* Request not in waiting list

* Date in past

## User

### Getting request status

* Request status retrieved successfully

![Status retrieved](postman_tests/status_success.png)

* Trying to get the request status of another user

![Status unauthorised](postman_tests/status_unauthorised.png)

* Request does not exist

![Request does not exist](postman_tests/status_no_request.png)

### Getting all requests of user

![All user requests](postman_tests/user_all_requests.png)
