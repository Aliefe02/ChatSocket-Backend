# ChatSocket-Backend

Java Spring Boot backend for ChatSocket mobile messaging application

## Changelog

### Version 0.1.2
- **Exists Endpoint Update**: /api/user/exists endpoint now returns details of user such as public key and username.

### Version 0.1
- **Store messages**: If receiver is not connected, store messages on server.
- **Retrieve Messages**: Retrieve unreceived messages of a user on the server. Once received, messages are deleted from server.
- **/api/**: add /api/ on Rest controllers for future thymeleaf controller addition.
