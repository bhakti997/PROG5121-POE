# PROG5121 APOE: QuickChat Registration and Messaging App

A console-based Java application built for the PROG5121 (Programming 1A)
Portfolio of Evidence. A user can register, log in, and send messages
through a simple menu-driven interface.

Author: Bhakti

## Features

**Part 1: Registration and Login**
- Username validation (must contain an underscore, maximum 5 characters)
- Password complexity validation (minimum 8 characters, a capital letter, a number and a special character)
- South African cell number validation (+27 international code)
- Login against the registered details

**Part 2: QuickChat Messaging**
- Numeric menu: Send Messages / Show recently sent messages / Quit
- User-defined number of messages per session
- Message length validation (maximum 250 characters)
- Auto-generated 10-digit Message ID
- Auto-generated Message Hash (first two digits of the ID : message number : first and last word, in uppercase)
- Each message can be sent, disregarded or stored
- Stored messages are saved to a JSON file
- Total number of messages sent is displayed after each batch

## Running the project

Open the project in NetBeans as a Maven project and run `PROG5121_APOE.java`.
To run all unit tests, use `mvn test` from the project root.

## References

JSON.org, 2024. JSON in Java (Version 20240303) [Source code] Available
at: <https://github.com/stleary/JSON-java> [Accessed 17 October 2026].

Naravani, M., 2018. 16. Mobile number validation in Java using regular
expression | Data validation | Netbeans [video online] Available at:
<https://youtu.be/Fu4nimny_gw> [Accessed 28 September 2026].
