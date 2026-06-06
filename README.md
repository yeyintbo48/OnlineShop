# Online Shop API

A robust RESTful API for an E-commerce backend platform built with Spring Boot. It provides comprehensive features for managing users, products, categories, shopping carts, and order processing, securely authenticated using JSON Web Tokens (JWT).

## 🔗 GitHub Repository

* **Repository Link:** [https://github.com/yeyintbo48/onlineshop](https://github.com/yeyintbo48/onlineshop)

---

## 🚀 Features

* **User Authentication & Authorization**: Secure registration and login using JWT. Role-based access control (Admin & User).
* **Product & Category Management**: Complete CRUD operations for products and categories. (Admin-only access for creation, updates, and deletion).
* **Shopping Cart**: Users can add products to their cart, update quantities, and clear the cart.
* **Order Processing**: Place orders seamlessly. Includes real-time stock validation and pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) to prevent concurrency issues (overselling).
* **Pagination**: Retrieve products and orders using paginated responses.
* **Exception Handling**: Global exception handling for validation errors, data integrity, and custom business logic errors.
* **Interactive API Documentation**: Auto-generated Swagger/OpenAPI documentation.

---

## 🛠️ Tech Stack

* **Language:** Java 25
* **Framework:** Spring Boot 4.0.6
* **Security:** Spring Security + JWT
* **Database:** MySQL, Spring Data JPA, Hibernate
* **DTO Mapping:** MapStruct
* **Build Tool:** Maven
* **API Documentation:** SpringDoc OpenAPI (Swagger UI)
* **Boilerplate Reduction:** Lombok

---

## 🏃‍♂️ How to Run the Application

### Prerequisites (လိုအပ်ချက်များ)
* **Java:** JDK Version 25 (As specified in `pom.xml`)
* **Database:** MySQL Server (Running on Port 3306)
* **Build Tool:** Maven (သို့မဟုတ် Project တွင်ပါဝင်သော `./mvnw` ကိုအသုံးပြုနိုင်ပါသည်)

### Step-by-Step Guide

1. **Clone the repository:**
   Terminal (သို့မဟုတ်) Git Bash ကိုဖွင့်ပြီး အောက်ပါ command ဖြင့် Project ကို clone လုပ်ပါ။
   ```bash
   git clone [https://github.com/yeyintbo48/onlineshop.git](https://github.com/yeyintbo48/onlineshop.git)
   cd onlineshop

2. **Create a new database in MySQL named shop_db**
   CREATE DATABASE shop_db;

3. **To build the project using Maven, type the following command. (On Windows, type mvnw instead of ./mvnw )**
    ./mvnw clean install

4. **To start and run the project, use the following command.**
    ./mvnw spring-boot:run


  ## Core API Endpoints 

    **Project run နေချိန်တွင် အောက်ပါ API များကို အသုံးပြုနိုင်ပါသည်။ (Authentication လိုအပ်သော Endpoint များအတွက် JWT Token အား Authorization: Bearer <token> Header ဖြင့် ထည့်သွင်းအသုံးပြုရပါမည်)။**

1. Authentication (/api/auth)
    POST /api/auth/register - အကောင့်အသစ်ဖွင့်ရန် (Register a new user)
    POST /api/auth/login - အကောင့်ဝင်ပြီး JWT Token ရယူရန် (Login & get token)

2. Category Management (/api/category)
    GET /api/category - Category အားလုံးကို ကြည့်ရန် (Public)
    GET /api/category/{id} - သတ်မှတ်ထားသော Category အချက်အလက်ကို ကြည့်ရန် (Public)
    POST /api/category/add - Category အသစ်ထည့်ရန် (Requires ADMIN)
    PUT /api/category/{id} - Category အချက်အလက်များ ပြင်ဆင်ရန် (Requires ADMIN)
    DELETE /api/category/{id} - Category ကို ဖျက်ရန် (Requires ADMIN)

3. Product Management (/api/products)
    GET /api/products - Product အားလုံးကို ကြည့်ရန် (Public)
    GET /api/products/page - Product များကို စာမျက်နှာ (Pagination) ဖြင့်ကြည့်ရန် ?userId=&page=&size= (Public)
    GET /api/products/{id} - သတ်မှတ်ထားသော Product အချက်အလက်ကို ကြည့်ရန် (Public)
    POST /api/products/add - Product အသစ်ထည့်ရန် (Requires ADMIN)
    PUT /api/products/{id} - Product အချက်အလက်များ ပြင်ဆင်ရန် (Requires ADMIN)
    DELETE /api/products/{id} - Product ကို ဖျက်ရန် (Requires ADMIN)

4. Shopping Cart (/api/cart)
    POST /api/cart/add - Cart ထဲသို့ ပစ္စည်းထည့်ရန် (ဥပမာ - ?userId=1&productId=2&quantity=3)
    GET /api/cart - Cart ထဲရှိ ပစ္စည်းအားလုံးကို ကြည့်ရန်
    DELETE /api/cart/remove/{cartItemId} - Cart ထဲမှ ပစ္စည်းတစ်ခုချင်းစီကို ပြန်ဖျက်ရန်
    DELETE /api/cart/clear/{userId} - User တစ်ယောက်၏ Cart တစ်ခုလုံးကို ရှင်းလင်းရန်

5. Order Processing (/api/order)
    POST /api/order/place/{userId} - Cart ထဲရှိပစ္စည်းများကို Order တင်ရန် (Stock မလုံလောက်ပါက OrderProcessingException ပြပေးမည်ဖြစ်သည်)
    GET /api/order/page - မိမိမှာယူခဲ့သော Order မှတ်တမ်းများကို စာမျက်နှာ (Pagination) ဖြင့် ပြန်လည်ကြည့်ရှုရန် ?userId=&page=&size=

    ## API Documentation (Swagger UI)

    **This project uses OpenAPI 3.0 for API documentation. You can interact with all the endpoints directly from your browser:**
    Swagger UI: http://localhost:8080/swagger-ui/index.html
    OpenAPI JSON: http://localhost:8080/v3/api-docs

    **How to test secured endpoints in Swagger:**
    Call /api/auth/login with valid credentials.
    Copy the token from the response.
    Click the "Authorize" button at the top of the Swagger UI.
    Paste the token into the input box and click Authorize. Now you can test protected API routes.