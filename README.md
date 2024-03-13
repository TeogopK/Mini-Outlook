# Mini Outlook

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

```java
// Example code demonstrating usage
public static void main(String[] args) {
   Outlook outlook = new Outlook();

   // Add new account
   Account accountJohn = outlook.addNewAccount("John", "john@example.com");
   Account accountBoss = outlook.addNewAccount("Boss", "boss@example.com");

   // Create folders
   outlook.createFolder(accountJohn.name(), "/inbox/important");
   outlook.createFolder(accountJohn.name(), "/inbox/personal");

   // Add rule for automatic sorting
   outlook.addRule(accountJohn.name(), "/inbox/important", "from: boss@example.com", 5);

   // Receive new mail
   String receiveMailContent = String.join(System.lineSeparator(),
            "sender: boss@example.com",
            "subject: Important Meeting",
            "received: 2022-12-08 09:14",
            "recipients: john@example.com");
   outlook.receiveMail(accountJohn.name(), receiveMailContent, "Meeting agenda...");

   // Send mail
   String sendMailContent = String.join(System.lineSeparator(),
            "sender: john@example.com",
            "subject: Progress Report",
            "received: 2022-12-08 09:15",
            "recipients: boss@example.com");
   outlook.sendMail(accountJohn.name(), sendMailContent, "Project update...");

   // Retrieve and display mails from specific folders
   System.out.println("John's Important Mails:");
   Collection<Mail> importantMails = outlook.getMailsFromFolder(accountJohn.name(), "/inbox/important");
   for (Mail mail : importantMails) {
      System.out.println(mail);
   }

   System.out.println("Boss's Inbox Mails:");
   Collection<Mail> inboxMails = outlook.getMailsFromFolder(accountBoss.name(), "/inbox");
   for (Mail mail : inboxMails) {
      System.out.println(mail);
   }
}
```

## Solution notes

- Use of Java Stream API is prohibited.
- The solution does not create actual folders in the operating system of the executing machine.

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
