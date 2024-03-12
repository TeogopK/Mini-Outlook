# Mock Mail Client Application

## Overview

This repository contains a simulated mail client application designed to support multiple email accounts and offer various functionalities, including automatic mail sorting, rule management, and folder organization.

## Features

1. **Default Folders:**
   - Default folders "inbox" and "sent" are created.
   - "inbox" contains all received emails, while "sent" contains sent emails.

2. **Creating New Folders:**
   - Users can create new folders within "inbox" with unlimited nesting levels.
   - For instance, a folder named "important" can have subfolders like "work" and "personal".

3. **Automatic Mail Sorting:**
   - Emails can be automatically sorted into folders based on defined rules.
   - Rules specify criteria for automatically redirecting emails to specific folders.
   - For example, emails from a specific sender with a specific subject line can be redirected to a designated folder.

4. **Rule Management:**
   - Users can dynamically create, modify, and remove rules.
   - When a new rule is added, it applies to all emails in the "inbox" folder.
   - Active rules are executed upon receiving a new email, prioritizing rules with higher precedence if multiple rules apply.

5. **Conflict Handling:**
   - It is not possible to add a rule that conflicts with an existing one.
   - Conflicting rules have the same definition and priority but are intended for different folders.

## Usage



## Build

To build and run the project, ensure you have Maven installed on your system. Clone the repository to your local machine, navigate to the project folder and build the project using:

```bash
mvn package
```

## Testing

Automated tests are included to validate the functionality of the sentiment analyzer. To run the tests execute:

```bash
mvn test
```
