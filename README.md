# ☁️ Cloud Storage Project

A robust, secure, and scalable cloud storage backend built with **Spring Boot** and **AWS S3**. This application allows users to store files, manage directory structures, and share content securely.

🔗 **Live Demo:** [https://cloudstorage.gulsemin.top/](https://cloudstorage.gulsemin.top/)

## 🚀 Features

*   **🔐 Authentication & Security:**
    *   Secure user Signup and Login.
    *   **JWT (JSON Web Token)** based stateless authentication.
    *   Password encryption using BCrypt.
    *   Role-based endpoint protection with Spring Security.
*   **📂 File Management:**
    *   **High-Performance Uploads:** Uses AWS S3 **Presigned URLs** to upload files directly to the cloud, reducing server load.
    *   Secure file downloads via time-limited signed URLs.
    *   File deletion and metadata storage.
*   **📁 Folder System:**
    *   Create and manage folders.
    *   Recursive folder structure (Sub-folders support).
    *   Navigate through parent and child directories.
*   **🤝 Sharing Capabilities:**
    *   **Internal Sharing:** Share files securely with other registered users ("Shared with me" section).
    *   **Public Links:** Generate public download links with customizable **expiration times** (e.g., 1 hour, 24 hours).

## 🛠️ Tech Stack

*   **Language:** Java 21
*   **Framework:** Spring Boot 3.5.3
*   **Database:** PostgreSQL
*   **Cloud Storage:** AWS S3 (Amazon Simple Storage Service)
*   **Security:** Spring Security, JWT (io.jsonwebtoken)
*   **Tools:** Maven, Lombok, MapStruct
*   **Architecture:** RESTful API
