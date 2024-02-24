<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Users</title>
</head>
<body>

    <h2>All Users</h2>

    <c:if test="${not empty userList}">
        <table border="1">
            <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <!-- Add more columns as needed -->
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${userList}">
                    <tr>
                        <td>${user.userId}</td>
                        <td>${user.username}</td>
                        <!-- Add more columns as needed -->
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</body>
</html>
