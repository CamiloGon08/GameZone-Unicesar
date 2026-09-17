# Developer 2 AI Usage log:

Estefanía Márquez Martínez
AI Tool: ChatGPT

---------------------------------------------------------------------------------------------------------------------------------

Topic: Layer responsibilities
My Prompt:
I am developing the Customer and Seller modules using a four-layer architecture: Model, Service, Persistence/DAO, and UI. I am confused 
about what should specifically be handled by the Service layer versus the DAO. I understand that the DAO works directly with the 
persistence files, but I need to determine where validation, business decisions, searching, listing, updating, and deleting should 
be handled.
AI Response:
The responsibilities should remain separated. Model represents the entities and their behavior; Service contains business logic, 
validations, and coordinates operations; DAO/Persistence handles reading and writing data; and UI manages interaction with the user.
The general flow is:
UI → Service → DAO → Persistence
My Decision:
I will maintain a strict separation of responsibilities between the four layers. Service will handle business logic and validation, 
while DAO will handle persistence operations.
Justification:
I decided to maintain a strict separation of responsibilities between the layers. The Service layer will handle business logic and 
validation, while the DAO layer will focus exclusively on persistence operations. This keeps the architecture organized and easier 
to maintain.

---------------------------------------------------------------------------------------------------------------------------------

Topic: list and search methods
My Prompt:
My CustomerDAO already contains listCustomers() and searchCustomer(). At first, I thought implementing the same methods in 
CustomerService would be unnecessary duplication. I wanted to understand whether Service should expose these operations 
even though the DAO already implements them.
AI Response:
The methods can exist in both layers because they serve different purposes. DAO performs the actual persistence operation, 
while Service provides controlled access to that functionality and delegates the operation to DAO.
My Decision:
I will implement listCustomers() and searchCustomer() in both Service and DAO. The Service methods will delegate the actual 
data operation to the DAO.
Justification:
I decided to keep these methods in both layers because they serve different architectural purposes. The DAO interacts 
directly with persistence, while the Service provides controlled access to that functionality.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Managing the Customer list
My Prompt:
Since CustomerService needs to search, list, update, and delete customers, I considered declaring an ArrayList<Customer> 
as an attribute of the Service so that the customers would remain available in memory. I wanted to know whether this was 
necessary or whether the Service should retrieve the current list from the DAO whenever it is needed.
AI Response:
A permanent ArrayList<Customer> is not necessary in Service. The DAO can retrieve the current information from the 
persistence file when needed. The persistence file remains the source of truth.
My Decision:
I will not maintain a permanent ArrayList<Customer> in CustomerService. The Service will request the list from the 
DAO when necessary.
Justification:
I decided not to maintain a permanent customer list in the Service because the persistence file is the source of 
truth. Maintaining another collection would duplicate the application's state and could cause inconsistencies.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Business validation
My Prompt:
I need to validate Customer information before saving it. The requirements are that the email cannot be empty and 
must contain @ and ., the name cannot be empty or contain numbers, and the ID and contact number must be positive. 
I wanted to know whether these validations should be performed in Service or Persistence.
AI Response:
These validations belong in the Service layer because they are business rules. Service should validate the 
information before creating or modifying the entity and before sending it to DAO. Invalid information should result 
in an IllegalArgumentException.
My Decision:
I will implement validateCustomer() in CustomerService and perform the required validations before persistence operations.
Justification:
I decided to place the validations in the Service layer because they represent business rules rather than persistence 
operations. This prevents invalid information from reaching the DAO.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Separating validation from existence checks
My Prompt:
I initially included the ID-existence check inside validateCustomer(), but I realized that registration requires the ID 
not to exist, whereas update and delete require the Customer to already exist. I wanted to determine how these 
validations should be separated.
AI Response:
Entity existence should not be part of general field validation because the required condition depends on the operation.
Registration → ID must not exist.
Update → Customer must exist.
Delete → Customer must exist.
Therefore, validateCustomer() should only validate the fields.
My Decision:
I removed the existence check from validateCustomer(). Each operation will perform its own existence check using 
searchCustomer() when required.
Justification:
I decided to separate field validation from entity existence checks because different operations require opposite 
conditions. This makes the validation method reusable for both registration and update operations.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Customer update
My Prompt:
For updateCustomer(), I want to search for the existing Customer using the ID, validate the new information, 
modify the existing object using setters, and then send that object to the DAO. I do not want to create another 
Customer object unnecessarily when I already have the object that must be updated.
AI Response:
This is an appropriate approach. Service should search for the existing Customer, verify that it exists, 
validate the new information, modify the existing object using setters, and send it to customerDAO.updateCustomer().
My Decision:
I will modify the existing Customer object using setters such as setName(), seteMail(), and setContactNumber(), 
and then send it to the DAO.
Justification:
I decided to modify the existing Customer object instead of creating an unnecessary new object. This makes 
the update operation clearer and directly represents the modification of the existing entity.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Updating and deleting .txt records
My Prompt:
My Customers and Sellers are stored in .txt files. I understand that new records can be saved using append mode, 
but updating or deleting a specific record is different. I considered loading all records into an ArrayList, 
modifying or removing the corresponding object, and then saving the entire list again.
AI Response:
Because the current persistence mechanism is a .txt file, the practical approach is to load all records, modify
 or remove the corresponding object, and overwrite the file with the updated collection.
Append mode should only be used when adding new records.
My Decision:
I will use append mode for new Customers and Sellers and create methods such as saveCustomers(ArrayList<Customer>) 
and saveSellers(ArrayList<Seller>) to rewrite the complete file after update or delete operations.
Justification:
I decided to use append mode only for new records and overwrite the file when updating or deleting records. This 
keeps the CSV synchronized with the current state of the collection.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Inheritance between Person, Customer, and Seller
My Prompt:
My project has an abstract Person class containing information shared by Customers and Sellers, such as name, 
ID, and contact number. Customer and Seller also have specific attributes. I wanted to make sure I was using 
inheritance correctly instead of duplicating common attributes and methods in both subclasses.
AI Response:
Person should contain the common attributes and behavior, while Customer and Seller should extend it and add 
only their specific information. This allows both subclasses to reuse the common implementation.
My Decision:
I will keep Person as the parent class and have Customer and Seller inherit the common information and behavior from it.
Justification:
I decided to use inheritance because Customer and Seller share common personal information but also have 
entity-specific characteristics. This reduces unnecessary duplication and represents their relationship clearly.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Polymorphism with toText()
My Prompt:
I decided that converting model objects into the text format required by Persistence should belong to the Model layer. 
Since Person, Customer, and Seller contain different information, I wanted to define toText() in Person and override it 
in Customer and Seller so that the project visibly applies inheritance and polymorphism.
AI Response:
toText() can be defined in Person and overridden in the subclasses. Each subclass can provide its own implementation 
according to its specific attributes. The @Override annotation explicitly indicates that the inherited method is being 
redefined.
My Decision:
I will implement toText() in Person and override it in Customer and Seller using @Override.
Justification:
I decided to implement toText() polymorphically because each subclass has different information that must be converted 
into the persistence format. This demonstrates inheritance and polymorphism while keeping the conversion logic inside 
the corresponding model.

---------------------------------------------------------------------------------------------------------------------------------

Topic: Duplication between CustomerDAO and SellerDAO
My Prompt:
I noticed that CustomerDAO and SellerDAO have very similar methods for saving, listing, searching, updating, and 
deleting records. My teammates pointed out that this could be considered code duplication. I wanted to know whether
I should create a generic DAO to eliminate the repeated structure or keep the DAOs separate.
AI Response:
There is structural duplication, but introducing a generic DAO does not necessarily improve the design. A generic 
abstraction could add unnecessary complexity, especially because Customers and Sellers have different attributes 
and persistence formats.
My Decision:
I will keep CustomerDAO and SellerDAO as independent classes and accept the necessary structural similarity.
Justification:
I decided to keep CustomerDAO and SellerDAO separate because introducing a generic DAO would add unnecessary 
complexity to the current project. Although their structures are similar, independent DAOs make the entity-specific 
persistence logic clearer and easier to understand.
