Responsibilities:
Consume order-related events, including direct orders, scheduled orders, process scheduled orders.

Operations:
Direct Orders: Call the external API of suppliers to place immediate orders based on direct order events.
Scheduled Orders: Save order details in the database for later processing in accordance with supplier policies.
Process Scheduled Orders: Retrieve all scheduled orders from the database and place them with the suppliers.
