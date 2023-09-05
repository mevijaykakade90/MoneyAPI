**Project Functional Requirements Document**

**Introduction**

This document outlines the functional requirements of a project designed to facilitate monetary transactions and user management. The project's primary goal is to enable users to perform various financial operations securely and efficiently. The project includes multi-threaded functionality to handle concurrent requests effectively.

**Functional Requirements**

**1. Money Transfer Between User Accounts** : Users can send money from one account to another user's account.
        **Steps:**
        Create multiple users using the following endpoint:
        HTTP Method: POST
        Endpoint: /user/createUser/{name}
        Transfer money from one account to another user's account using the following endpoint:
        HTTP Method: POST
        Endpoint: /transaction/transferMoney/{senderId}/{receiverId}/{amount}
				
**2. External Funds Transfer :** Users can send money from their account to an external withdrawal address through an API. (API Stub is provided)
        **Steps:**
        Create at least one user using the following endpoint:
        HTTP Method: POST
        Endpoint: /user/createUser/{name}
        Transfer money from one account to an external address using the following endpoint:
        HTTP Method: POST
        Endpoint: /transferExternalAddress/{senderId}/{address}/{amount}
				
**3. Operation Progress Monitoring :** Users can check the progress of their operations.
        **Steps:**
        Check the transaction history using the following endpoint:
        HTTP Method: GET
        Endpoint: /transaction/transactionHistory
        To check the status of a specific transaction, provide the transaction ID using the following endpoint:
        HTTP Method: GET
        Endpoint: /transaction/transactionStatus/{transactionId}

**4. Multi-threaded Functionality**

    4.1. TaskExecutor Configuration : To handle multiple requests concurrently, a TaskExecutor is configured. This allows the application to process
       multiple tasks concurrently in separate threads, enhancing system responsiveness and throughput.
    4.2. External Address Transfer Logic : For external address transfers, a specific logic has been implemented to ensure smooth processing of requests:
		
        a. Blocking Queue Logic: 
    		For external address transfers, we've implemented a blocking queue mechanism to manage requests continuously. This means 
    		that incoming requests are placed in a queue, ensuring that they are processed in an orderly fashion while allowing other 
    		requests to be processed concurrently.
			
    	b. Transaction Status Update: 
    		When a transaction is initiated, it is assigned a status of PROCESSING. The logic involves a delay of 20 milliseconds (20 ms) 
    		before the transaction status is checked. During this delay, the transaction is assumed to be in progress.
			
    	c. Concurrent Status Check: 
    		Concurrently, a separate thread of logic is continuously checking the status of each transaction in the queue. This allows us 
    		to monitor the progress of transactions and take appropriate actions based on their status.
			
    	d. Transaction Failure Handling: 
    		If a transaction is found to have failed (e.g., due to external issues or insufficient funds), a reversal process is initiated. 
    		This process involves returning the amount to the respective senderId to maintain data integrity.
			
    	e. Logging and Monitoring: 
    		Throughout this process, detailed logging and monitoring are implemented to provide insights into the progress and outcomes of 
    		each transaction. This ensures that any issues or anomalies can be identified and addressed promptly.

**All Endpoints**

1. Create User: Handles the creation of new users in the system.
2. Update User: Updates user data based on provided parameters.
3. Remove User: Removes a user from the system based on their user ID.
4. Get Users Size: Retrieves the total number of users in the model.
5. Print Users: Retrieves a list of all users from the model and returns it.
6. Get User Balance: Retrieves the balance for a user identified by their unique user ID.
7. Transfer Money: Transfers a specified amount of money from one account to another.
8. Get Transaction Status: Retrieves the status of a transaction based on its unique transaction ID.
9. Get Transaction History: Retrieves the transaction history, which is a list of all transactions.
10. Get Transactions by User: Retrieves a list of transactions associated with a specific user.
11. Get transferExternalAddress: Transfers a specified amount of money from one account to an external address.
12. This project aims to provide users with a seamless and secure financial experience, supporting various operations and multi-threaded processing for scalability and efficiency.
