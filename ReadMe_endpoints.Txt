Money API - User Controller

The UserController in the com.moneyapi.controller package is responsible for handling user-related operations in the Money API.

Endpoints

1. Create User
Handles the creation of new users in the system.

HTTP Method: POST
Endpoint: /user/createUser/{name}
Parameters:
    * name (String): The name of the user to be created.
Response: Returns a response indicating the result of user creation.

2. Update User
Updates user data based on provided parameters.

HTTP Method: POST
Endpoint: /user/updateUser/{userId}/{amount}
Parameters:
    * userId (Integer): The unique identifier of the user to update.
    * amount (BigDecimal): The amount to update for user balances.
Response: Returns a response indicating the success or result of the user update operation.

3. Remove User
Removes a user from the system based on their user ID.

HTTP Method: POST
Endpoint: /user/removeUser/{userId}
Parameters:
    * userId (Integer): The unique identifier of the user to be removed.
Response: Returns a message indicating the success or failure of the removal operation.

4. Get Users Size
Retrieves the total number of users in the model.

HTTP Method: GET
Endpoint: /user/usersSize
Response: The number of users currently stored in the model.

5. Print Users
Retrieves a list of all users from the model and returns it.

HTTP Method: GET
Endpoint: /user/printUsers
Response: A list of User objects containing all the users in the model.

6. Get User Balance
Retrieves the balance for a user identified by their unique user ID.

HTTP Method: GET
Endpoint: /user/getBalance/{userId}
Parameters:
    * userId (Integer): The unique identifier of the user for whom to fetch the balance.
Response: Returns the current balance associated with the specified user.

------------------------------------------------------------------------------------

Money API - Transaction Controller
The TransactionController in the com.moneyapi.controller package is responsible for handling user-related transactions in the Money API.

Endpoints

1. Transfer Money
Transfers a specified amount of money from one account to another.

HTTP Method: POST
Endpoint: /transaction/transferMoney/{senderId}/{receiverId}/{amount}
Parameters:
    * senderId (Integer): The unique identifier of the sender's account.
    * receiverId (String): The unique identifier of the receiver's account.
    * amount (BigDecimal): The amount of money to be transferred.

2. Get Transaction Status
Retrieves the status of a transaction based on its unique transaction ID.

HTTP Method: GET
Endpoint: /transaction/transactionStatus/{transactionId}
Parameters:
    * transactionId (Integer): The unique identifier for the transaction.

3. Get Transaction History
Retrieves the transaction history, which is a list of all transactions.

HTTP Method: GET
Endpoint: /transaction/transactionHistory


4. Get Transactions by User
Retrieves a list of transactions associated with a specific user.

HTTP Method: GET
Endpoint: /transaction/getTransactionByUser/{userId}
Parameters:
    * userId (Integer): The unique identifier of the user whose transactions are requested.
    
5. Get transferExternalAddress
Transfers a specified amount of money from one account to external address.

HTTP Method: POST
Endpoint: /transferExternalAddress/{senderId}/{address}/{amount}
Parameters:
    * senderId (Integer): The unique identifier of the sender's account.
    * address (String): The external address to which the funds will be transferred.
    * amount (BigDecimal): The amount of money to be transferred.
    
