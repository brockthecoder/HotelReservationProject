Java project with 3 parts:

  1) Customer client : 
          A command line interface that allows the user to search for room availability and manage existing reservations

  2) Management console : 
          A command line interface for managing hotels, complete with account 
          sign-in / creation, and provides the ability for a hotel manager to add and remove hotels, view upcoming 
          check-ins, create new reservations, manage availability, search for existing reservations, and modify hotel listing details.

  3) Server: 
         Written in java, uses PostgreSQL database for persisting hotel and reservation data,
         communication is with client is done using websockets. New client connections are assigned
         to worker threads that handle all client requests, until the client closes the connection.
         Requests and responses are sent in JSON format over the socket streams.
