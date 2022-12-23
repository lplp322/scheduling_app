# Test cases

## Registering

### "user" - employee of EEMCS.

![User successful registration](postman_tests/register_success.png)

### "user2" - employee of IDE, admin of EEMCS

![User2 successful registration](postman_tests/register_user2.png)

### User already exists

![User already exists](postman_tests/register_already_exists.png)

## Logging in

### Correct NetID and password

![Successful login](postman_tests/login_success.png)

### Wrong password

![Wrong password](postman_tests/login_wrong_password.png)

### No such user

![No such user](postman_tests/login_no_user.png)

## Adding a request

### Request added successfully

![Successful new request](postman_tests/new_request_success.png)

![New request status](postman_tests/new_request_status.png)

### Memory > CPU

![Memory > CPU](postman_tests/new_request_ram_gt_cpu.png)

### GPU > CPU

![GPU > CPU](postman_tests/new_request_gpu_gt_cpu.png)

### Deadline same as current date (22.12.2022)

![Deadline today](postman_tests/new_request_deadline_same_day.png)

### Deadline in the past (before 22.12.2022)

![Deadline past](postman_tests/new_request_deadline_past.png)

### User not an employee of the faculty

![User not employee](postman_tests/new_request_not_employee.png)

## Getting requests by faculty

### Successfully getting requests for EEMCS.

![EEMCS requests](postman_tests/requests_by_faculty_EEMCS.png)

### User not admin of the faculty

![Reqeusts by faculty unauthorised](postman_tests/requests_by_faculty_unauthorised.png)

## Rejecting requests

### Successfully rejecting requests

![Rejection successful](postman_tests/reject_success.png)

![Rejected request status](postman_tests/reject_status.png)

### User not admin of the faculty

![Rejection unauthorised](postman_tests/reject_unauthorised.png)

![Request still pending](postman_tests/reject_unauthorised_status.png)

### Request not in waiting list

![Request not in waiting list](postman_tests/reject_no_request.png)

## Accepting requests

### Request successfully accepted

![Pending requests for EEMCS](postman_tests/accept_pending_list.png)

![Available resources before accepting](postman_tests/accept_available_resources_before_accepting.png)

![Request accepted successfully](postman_tests/accept_success.png)

![Available resources after accepting](postman_tests/accept_available_resources_after_accepting.png)

![Accepted request status](postman_tests/accept_success_status.png)

### Not enough resources

![Pending requests for EEMCS](postman_tests/accept_pending_list_2.png)

![Available resources](postman_tests/accept_available_resources_after_accepting.png)

![Accepting with not enough resources](postman_tests/accept_not_enough_resources.png)

![Status after failed accept attempt](postman_tests/accept_not_enough_resources_status.png)

### User not admin of the faculty

![Unauthorised accept](postman_tests/accept_unauthorised.png)

### Request not in waiting list

![Accepted request not in waiting list](postman_tests/accept_not_in_list.png)

### Date in past (before 23.12.2022)

![Accepted request date in past](postman_tests/accept_date_in_past.png)

## Getting request status

### Request status retrieved successfully

![Status retrieved](postman_tests/status_success.png)

### Trying to get the request status of another user

![Status unauthorised](postman_tests/status_unauthorised.png)

### Request does not exist

![Request does not exist](postman_tests/status_no_request.png)

## Getting all requests of user

![All user requests](postman_tests/user_all_requests.png)

## Adding a node

### Successfully adding a node

![EEMCS resources before new node](postman_tests/add_node_eemcs_resources_before.png)

![IDE resources before new node](postman_tests/add_node_ide_resources.png)

![Add new node to EEMCS](postman_tests/add_node_success.png)

![EEMCS resources after new node](postman_tests/add_node_eemcs_resources_after.png)

![IDE resources after new node](postman_tests/add_node_ide_resources.png)

### Adding a node with same name

![Adding a node with same name](postman_tests/add_node_same_name.png)
