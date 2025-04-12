# ChatSocket-Backend

Java Spring Boot backend for ChatSocket mobile messaging application

## Changelog

### Version 0.1.4
- **Templates Added**: Landing page added.

### Version 0.1.3
- **Security Update**: Fixed a vulnerability where previous jwt codes were still valid after a password change.
- **Token Validation Updated**: Updated Jwt Authentication filter steps. A new field is added to user model, jwtTokenCode. When a request is made, token is validated and checked if it has expired. Then token's jwt token code is compared to user's jwt token code. If there is a mismatch, then token is invalid (It was generated using previous password).
- **Retrieve User in Controllers Updated**: After succesfull token validation, user model is stored on the SecurityContext, controllers can retrieve the currently logged in user from there. No need to make another query.

### Version 0.1.2
- **Exists Endpoint Update**: /api/user/exists endpoint now returns details of user such as public key and username.

### Version 0.1
- **Store messages**: If receiver is not connected, store messages on server.
- **Retrieve Messages**: Retrieve unreceived messages of a user on the server. Once received, messages are deleted from server.
- **/api/**: add /api/ on Rest controllers for future thymeleaf controller addition.
