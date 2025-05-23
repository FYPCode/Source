Project overview and context:
My FYP was a car rental Android App. The objective was to provide a platform for peer-to-peer car rental services to a remote area in Hong Kong that is not covered by traditional car rental companies and increase the usage rate of the private cars that frequently sit in the car park.

Assumptions and constraints:
1. Law factors are not considered (eg. hire car permit, crime, insurance)
2. The data of users and cars are samples only

Roles and contributions:
This was a group project. My partner developed an AI chatbot for suggesting cars based on the Customer’s input and the frontend design of the Main page. I was responsible for everything else including the frontend of the Login system, the Rent A Car page and the backend logic involving database, maps and payments.

Key features and functionality:
1. Login system to separate User and Car owner
2. Car owner could input the required information such as the rental period and price along with uploading the vehicle photo to the Car Listing
3. User could open the Car Listing page, chose the desired car and the destination
4. The app would then alert the Car owner by sending notification
5. Car owner could choose to accept or decline
6. If the request is accepted, Car owner would need to drive to the destination requested by the User
7. When the Car owner arrived, a notification would alert the User
8. The User then paid the rental price and got the car keys
9. After the User returned the car, Car owner could retrieve the money with 10% charged by the app

Tools and technologies:
I used OOP principles like inheritance, Car owner is a Customer and I had encapsulation in mind, I kept most fields in private so that they couldn’t be modified outside of the class. 

I used Firebase to handle all the data, Firebase Cloud Messaging to handle notifications, Google Maps Geolocation Api to show the routing and Stripe Sandbox to handle card payments. 

Responsive layout for different screen sizes (ConstraintLayout) and used different phones for testing. 

I used RecyclerView to fetch data from Firebase and displayed them on layout, updated list when refreshed. 

Why I chose Firebase?
1. Offered Open Authentication to verify tokens in the Login system and got certified under an international security standard ISO 27001
2. Could also use the Cloud Messaging function for notification.
3. The data inside the app was not closely related. Nosql Database was used here to store data in Json and it could handle realtime data on a mobile app.
4. Free tier with minimal cost and ease of use.

Why I chose Stripe Sandbox?
1. Similar to Paypal, it was a hosted payment gateway handled by the provider. I only needed to adjust the payment flow.
2. The v1 payment Android SDK became deprecated and cannot function normally. According to the documentation, the v2 checkout SDK suggested developers to integrate with Braintree instead. In the end, the PayPal Api was rejected.

Challenges and solutions:
1. The first obstacle I encountered was that I had to learn how to use Android Studio without any mentoring.
-> I was overwhelmed at first as everything is new to me. I decided to go through the official documentation from scratch. From installing it properly to adjusting the environmental variables, setting the correct file path and choosing the appropriate smartphone emulator.
2. After completing the first step, another hurdle I came across was how to show the map routing properly to the user from point A to B. Initially, I thought of it as nodes in a graph, just like the greedy Dijkstra's algorithm. However, it didn’t work as intended due to the Google Maps Geolocation API was a more complex layer.
-> Then, I read plenty of other developers’ methods of dealing with similar problems on GitHub and found one that was suitable for my case. I referenced it and optimised it with less lines for better readability.

Outcomes and improvements:
The FYP was a success and I got a B+ grade.
1. User ratings and reviews
2. Loyalty programs to offer discounts to regular customers
3. Set flags to vehicles based on mileage and rental frequency
