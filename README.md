# Member Management System (MMS)

A comprehensive, text-based Java application for managing gym members, built with a strong focus on Object-Oriented Programming principles.

---

## Overview

The **Member Management System (MMS)** is a robust command-line interface (CLI) application that allows a gym administrator to handle member records, track monthly performance, manage fees, and maintain a persistent database using CSV files. The project serves as a practical demonstration of core OOP concepts, including **Inheritance, Polymorphism, Abstraction,** and **Encapsulation**.

---

## System Architecture

The application is designed with a clean separation between the core logic (the **engine**) and the user interface (the **view**), making it scalable and easy to maintain.

---

## ✨ Features

The application is packed with features to provide a complete member management experience:

### 📁 Data Management

- **Automatic Data Loading:** Automatically loads all records from `gym_records.csv` on startup.
- **Persistent Storage:** Automatically saves all changes back to `gym_records.csv` upon exiting the application.
- **Manual Save:** Allows the admin to save a snapshot of the current member list to a new custom-named file at any time.

### 👤 Member Administration

- **Dual Membership Tiers:** Supports **Regular** and **Premium** members, each with a unique fee structure.
- **Full CRUD Functionality:**
  - **Create:** Add new members of either type.
  - **Read:** View a clean, formatted list of all members.
  - **Update:** Modify a member's name or a premium member's personal trainer fee.
  - **Delete:** Remove a member's record from the system.
- **Membership Status Control:** Set a member's status to **ACTIVE** or **FROZEN**. A frozen membership incurs a significantly reduced monthly fee.

### 🚀 Advanced Functionality

- **Performance Tracking:** Admins can add monthly performance records for members, noting whether they achieved their fitness goals.
- **Performance-Based Discounts:** Automatically applies a 10% discount to the monthly fee of a PremiumMember if their latest performance record shows they met their goal.
- **Advanced Search & Filtering:** A powerful search sub-menu with multiple options:
  - Search by Member ID
  - Search by Member Name (partial matches supported)
  - Filter by Member Type (Regular / Premium)
  - Filter by Performance (goal achieved/not achieved for a specific month/year)

---

## 🛠️ Tech Stack & Concepts

- **Language:** Java (JDK 11+)
- **Core Concepts:**
  - Object-Oriented Programming: Abstraction, Inheritance, Polymorphism, Encapsulation
  - Java Collections API: `ArrayList` for data storage.
  - File I/O: Reading from and writing to `.csv` files using `BufferedReader` and `FileWriter`.
  - Exception Handling: Graceful error handling for file operations and invalid user input.

---

## 🚀 How to Run

### Prerequisites

- Java Development Kit (JDK) 11 or higher must be installed and configured on your system's PATH.
- An IDE like Visual Studio Code with the **Extension Pack for Java** is recommended.

### Execution Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/KOI-MIT-Cybersecurity-2025/Programming-and-Algorithms-T225.git


## 📂 Project Structure

