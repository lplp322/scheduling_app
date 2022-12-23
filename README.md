# How to interact with the microservices

## Authentication microservice

The `authentication-microservice` has two endpoints that are normally both accessed through the `user-microservice` acting as a gateway API, but can also be accessed separately. The first endpoint is the `/register` POST-mapping, which takes a net-ID, password and roles and if those are valid, adds them to a database and an OK response will be returned.

A registered user can then use the `/authenticate` POST-mapping where, given a correct net-ID and password, the user can retrieve a token with which they can authenticate themselves in the other microservices.

## User microservice

Regular users can easily access all the functionality of the application they need via the `user-microservice` which acts as a gateway API. Before a user can interact with the system, the user should first register themselves, which can be done via the `user-microservice` using the `login/register` POST-mapping, giving a net-ID, a password and the roles that the user exercises. Furthermore, the user should then authenticate themselves using the `login/authenticate` POST-mapping, giving the correct net-ID and password to get a token with which the user can authenticate themselves within the other microservices. These endpoints are in the `AuthenticationController` class, since these forward the requests to the `authentication-microservice`.

The `FacultyAdminController` has all the endpoints for the things a faculty admin should be able to do. This includes rejecting and accepting requests through the `waiting-list-microservice` using the `reject-request` DELETE-mapping and the `accept-request` POST-mapping, respectively. These endpoints also change the status of the request accordingly when getting the response back from the `waiting-list-microservice`. The `/get-requests-by-faculty` GET-mapping also forwards the request to the `waiting-list-microservice` and returns the response with the pending requests of a specific faculty back to the user.

The `FacultyAdminController` also contains endpoints to communicate with the `resources-microservice`, namely the `/release-resources` POST-mapping and the `/available-resources` GET-mapping. The first one forwards the request to release resources of a specific faculty on a specific day to the `resources-microservice` and the second one forwards the request to fetch the available resources of a specific faculty on a specific date to the same microservice. When a user is not authenticated as a faculty admin however, an Unauthorized response will be returned, since they are not authorized.

The `NodeResourceController` also communicates with the `/available-resources` GET-mapping in `resources-microservice` to check the available resources tomorrow via the `/get-resources-for-tomorrow` GET-mapping. This one however is meant for employees, since they are only authorized to see the available resources for the next day. If the user is not an employee however, an Unauthorized response will be returned. Furthermore, this controller has an endpoint to forward a request to add a node to the cluster to the `resources-microservice` and returns an OK response or Bad Request response depending on if the addition of the node succeeded.

The `RequestReceivingController` has one endpoint that communicates with the `waiting-list-microservice` and two that handle requests inside the `user-microservice`. The `/request` POST-mapping can take requests given in multiple formats and processes those to then send to the `waiting-list-microservice` where they then will be stored. The endpoint will also check if the user requesting to add the request is an employee, otherwise an Unauthorized response is returned. When the request is scheduled correctly, the endpoint will return the ID of the request, generated in the `waiting-list-microservice`.

Moreover, the `/request-status` GET-mapping takes an ID and returns the status of the corresponding request, provided that the user is the one that created the request in the first place, otherwise an Unauthorized response is returned. The `/get-my-requests` GET-mapping simply returns all the requests the user created and that are stored in the `user-microservice`.

The `RequestStatusChangeController` is the last controller of the `user-microservice` and only contains one endpoint, the `/change-status` POST-mapping. This endpoint takes an ID of a request and a status that a request could be in and changes the status of the request stored in the microservice to the one that is given. Only faculty admins are authorized to do this, so if the user isn’t a faculty admin, an Unauthorized response will be returned.

## Waiting list microservice

The `waiting-list-microservice` should be used by regular users via the `user-microservice`, but can also be accessed directly. Firstly, employees can add requests for a faculty they are assigned to. This can be done by passing a request to the `/add-request` POST-mapping. When a user is authorized to add the request and the request is successfully added, the generated ID of the pending request is returned to the user.

Furthermore, admins of a faculty can fetch the pending requests of their faculty via the `/get-requests-by-faculty` POST-mapping. For this, the faculty admin should pass the faculty they are assigned to in a string (which will be verified), and when the request is valid, a list of all pending requests of that faculty is returned.

Lastly, there are two endpoints which allow a faculty admin to accept or reject a pending request. A faculty admin can reject a request by passing the ID of the request that should be rejected to the `/reject-request` DELETE-mapping. If the ID is of a valid request and the request belongs to the faculty of the admin, the request is removed from the waiting list and an OK-response is returned. If a faculty admin wants to accept a request, they should make a JSON request containing the ID and planned date of the request to the `/accept-request` POST-mapping. In case the ID and the planned date are valid (after the current date, but before the deadline), the request will be forwarded to the `schedule-microservice` and deleted from the `waiting-list-microservice` if an OK response is returned from the `schedule-microservice`.

## Resources microservice

There are quite a few endpoints in the `resources-microservice` that are used to check and update the resources and the availability of them. First of all, a sysadmin can fetch a collection of all the nodes currently stored in the database by using the `/nodes` GET-mapping. Users that are not a sysadmin will get an Unauthorized response, since they are not authorized.

Moreover, there is an endpoint to add nodes to the cluster, by passing a node object, together with the personal details of the one adding the node, and the faculty to which the node should be added. If the node is successfully added, an OK response will be returned.

Resources from a faculty can also be released and put into the free pool of resources via the `/release` POST-mapping. This takes the amount of resources that should be released, the start and end date, and the faculty name, and sets the corresponding resources to `released`. This returns an OK response if it went well.

Lastly, the available resources of a faculty on a specific date can be fetched and updated using endpoints of the `resources-microservice`. The `/get-available-resources` POST-mapping needs a specific date and faculty from the user and then returns the corresponding available resources. If the faculty does not exist, a Not Found response will be returned. The `/available-resources` POST-mapping needs a specific date, faculty and resources to update the available resources for that faculty on that date. An OK response is returned when the resources can be changed accordingly, but when there are more resources used than available resources, an Unprocessable Entity response is returned. These endpoints are mainly used by the `schedule-microservice` to check if new requests can be scheduled in and to update the available resources if they are.

## Schedule microservice

The `schedule-microservice` contains two endpoints which can be interacted with. One endpoint allows a sysadmin (and only a sysadmin), which can interact directly with the microservice, to request the schedule of any day. It can be used by passing a date to the `/schedule` GET-mapping (as an authorized sysadmin) and getting a list of the requests scheduled on that day in return.

The other endpoint allows the user to schedule a request on a specific date by giving a request to schedule together with the date the request should be scheduled on to the `/schedule` POST-mapping. If the date and the resources are valid and enough resources available, an OK response is returned. This endpoint should only be used by the `waiting-list-microservice` when regular users are using the application (via the `user-microservice`), but can be reached some other way.

# Common module

The `common` module is a special module containing general classes that can be used by multiple microservices. These classes are mostly used as request and response objects, which makes it easier for microservices to communicate with each other by using the same objects. Also, other objects or providers that are often used by multiple classes are included in this module to prevent code duplication.

# Things to point out

The manual integration tests are in a file called `tests.md`, these give a better understanding how to make use of the endpoints described above. The folder `postman_tests` only contains the screenshots used in `tests.md` and thus do not give the full picture.

The endpoints and the integration with the database are split using service classes such as the `ScheduleService` class in the `schedule-microservice`, so the requests are first forwarded from the endpoints to the service classes and then these classes call the corresponding repository interface to add or get from the database.

Microservice internal logic is tested using unit and integration testing. Most of the tests are done using mockMVC to test APIs. The APIs are thoroughly tested in the following classes:
- `ScheduleControllerTest`
- `UsersTests`
- `WaitingListControllerTest`
- `AuthenticationControllerTest`
- `FacultyAdminControllerTest`
- `NodeResourceControllerTest`
- `RequestReceivingControllerTest`
- `RequestReceivingStrategyTest`
- `RequestStatusChangeControllerTest`
- `ReleaseResourceFacultyAdminTest`

Every day at 23:55, pending requests that have a deadline for the next day are automatically rejected in the `waiting-list-microservice`, as they can no longer be scheduled. Every day starting at 18:00, the `waiting-list-microservice` will automatically try to add pending requests that have a deadline for the next day to the schedule.

The classes used for implementing the adapter design pattern are in the `user-microservice` in the `acceptrequestadapter` folder and the API that is using them is defined in the `acceptRequest()` method in the `FacultyAdminController` class. The classes used for implementing the strategy design pattern are also in the `user-microservice` in the `requestmodelget` folder and the API that is using them is defined in the `addRequest()` method in the `RequestReceivingController` class.

OpenFeign interfaces are used for microservice connection and can be found in the `external` and `feigninterfaces` folders.

In the `user-microservice`, classes and interfaces for storing the status of the requests are stored in the `requests` folder and JPARepository is stored in the `domain` folder.

Main functionality of the `user-microservice` is inside the controllers (stored in the `controllers` folder), they are all external APIs for normal employees and for faculty admins (we were assuming that sysadmins can directly contact other microservices to get information).
