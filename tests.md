# Test cases

## Registering

### "user" - employee of EEMCS.

![User successful registration](postman_tests/register/register_success.png)

### "user2" - employee of IDE, admin of EEMCS

![User2 successful registration](postman_tests/register/register_user2.png)

### User already exists

![User already exists](postman_tests/register/register_already_exists.png)

## Logging in

### Correct NetID and password

![Successful login](postman_tests/login/login_success.png)

### Wrong password

![Wrong password](postman_tests/login/login_wrong_password.png)

### No such user

![No such user](postman_tests/login/login_no_user.png)

## Adding a request

### Request added successfully

Adding a request to EEMCS faculty.

![Successful new request](postman_tests/new_request/new_request_success.png)

The status of the added request is PENDING.

![New request status](postman_tests/new_request/new_request_status.png)

### Memory > CPU

![Memory > CPU](postman_tests/new_request/new_request_ram_gt_cpu.png)

### GPU > CPU

![GPU > CPU](postman_tests/new_request/new_request_gpu_gt_cpu.png)

### Deadline same as current date (22.12.2022)

![Deadline today](postman_tests/new_request/new_request_deadline_same_day.png)

### Deadline in the past (before 22.12.2022)

![Deadline past](postman_tests/new_request/new_request_deadline_past.png)

### User not an employee of the faculty

![User not employee](postman_tests/new_request/new_request_not_employee.png)

## Getting requests by faculty

### Successfully getting requests for EEMCS.

![EEMCS requests](postman_tests/requests_by_faculty/requests_by_faculty_EEMCS.png)

### User not admin of the faculty

![Reqeusts by faculty unauthorised](postman_tests/requests_by_faculty/requests_by_faculty_unauthorised.png)

## Rejecting requests

### Successfully rejecting requests

Rejecting request with id 1.

![Rejection successful](postman_tests/reject/reject_success.png)

The status of the request is now REJECTED.

![Rejected request status](postman_tests/reject/reject_status.png)

### User not admin of the faculty

Trying to reject request with non-admin account.

![Rejection unauthorised](postman_tests/reject/reject_unauthorised.png)

The status of the request is still PENDING.

![Request still pending](postman_tests/reject/reject_unauthorised_status.png)

### Request not in waiting list

![Request not in waiting list](postman_tests/reject/reject_no_request.png)

## Accepting requests

### Request successfully accepted

The list of pending request for the EEMCS faculty.

![Pending requests for EEMCS](postman_tests/accept/accept_pending_list.png)

The amount of free resources for EEMCS.

![Available resources before accepting](postman_tests/accept/accept_available_resources_before_accepting.png)

Accepting request with id 1.

![Request accepted successfully](postman_tests/accept/accept_success.png)

The number of available resources for that day have decreased.

![Available resources after accepting](postman_tests/accept/accept_available_resources_after_accepting.png)

The request status is now ACCEPTED.

![Accepted request status](postman_tests/accept/accept_success_status.png)

### Not enough resources

The list of pending requests for EEMCS.

![Pending requests for EEMCS](postman_tests/accept/accept_pending_list_2.png)

The number of free resources for EEMCS.

![Available resources](postman_tests/accept/accept_available_resources_after_accepting.png)

Accepting request 2 failed, because there were not enough resources.

![Accepting with not enough resources](postman_tests/accept/accept_not_enough_resources.png)

The status of request 2 is still PENDING.

![Status after failed accept attempt](postman_tests/accept/accept_not_enough_resources_status.png)

### User not admin of the faculty

![Unauthorised accept](postman_tests/accept/accept_unauthorised.png)

### Request not in waiting list

![Accepted request not in waiting list](postman_tests/accept/accept_not_in_list.png)

### Date in past (before 23.12.2022)

![Accepted request date in past](postman_tests/accept/accept_date_in_past.png)

## Getting request status

### Request status retrieved successfully

![Status retrieved](postman_tests/status/status_success.png)

### Trying to get the request status of another user

![Status unauthorised](postman_tests/status/status_unauthorised.png)

### Request does not exist

![Request does not exist](postman_tests/status/status_no_request.png)

## Getting all requests of user

![All user requests](postman_tests/user/user_all_requests.png)

## Adding a node

### Successfully adding a node

The number of resources for EEMCS.

![EEMCS resources before new node](postman_tests/add_node/add_node_eemcs_resources_before.png)

The number of resources for IDE.

![IDE resources before new node](postman_tests/add_node/add_node_ide_resources.png)

Adding a new node to EEMCS.

![Add new node to EEMCS](postman_tests/add_node/add_node_success.png)

The number of resources for EEMCS has increased accordingly.

![EEMCS resources after new node](postman_tests/add_node/add_node_eemcs_resources_after.png)

The number of resources for IDE stayed the same.

![IDE resources after new node](postman_tests/add_node/add_node_ide_resources.png)

### Adding a node with same name

![Adding a node with same name](postman_tests/add_node/add_node_same_name.png)

## Releasing resources

### Successfully releasing resources

The number of resources for IDE.

![IDE resources before releasing](postman_tests/release/release_ide_before.png)

Releasing resources from EEMCS.

![Releasing EEMCS resources](postman_tests/release/release_eemcs_success.png)

The availabe resources for IDE have increased accordingly (IDE resources + free resources).

![IDE resources after releasing](postman_tests/release/release_ide_after.png)

### Releasing more than available resources

The amount of EEMCS resources.

![EEMCS resources before releasing](postman_tests/release/release_eemcs_before.png)

Trying to release more EEMCS resources than possible.

![Failed release attempt](postman_tests/release/release_not_enough_resources.png)

The amount of EEMCS resources has not changed.

![EEMCS resources after failed release attempt](postman_tests/release/release_eemcs_before.png)
