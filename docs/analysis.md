# Analysis – GameZone Unicesar

## People

### Q1: What attributes are common to all people who interact with the store, and which are specific to each type of person? How is this distinction reflected in a class hierarchy?

We noticed that everyone has a name, an ID number, and a contact number. Those three fields go in the `Person` class. A customer also has an email, so we put that in `Customer`. A seller has an employee code and a shift, so those go in `Seller`. The hierarchy is simple: `Person` is the parent, and `Customer` and `Seller` are the children. This way we don't repeat the common fields in both subclasses.

### Q2: Should there be a class representing a "generic person" without specifying a role? Why or why not? What implication does this decision have on the possibility of instantiating this class?

We made `Person` abstract. That means you can't create a plain `Person` object — you have to create either a `Customer` or a `Seller`. This makes sense because in the store, nobody is just a "person"; everyone is either buying something or working there. Making it abstract also prevents mistakes where someone might try to use a generic person when they really need a specific role.

---

## Products

### Q3: What characteristics do all products sold by the store share, regardless of type? Which characteristics are specific to each product type?

All products have an ID, a title, a price, and a stock count. Those are the basics you need for any item in the inventory. For a video game, we also store the platform, genre, and age rating. For a console, we store the brand, model, and generation. We put the common fields in `Product` and the specific ones in `VideoGame` and `Console`.

### Q4: Each type of product must be able to present a description that integrates its particular characteristics. How should this behavior be declared in the base class to guarantee that all subclasses implement it in their own way? What object-oriented programming mechanism enables this?

We declared `getDescription()` as abstract in `Product`. That forces `VideoGame` and `Console` to write their own version. When we call `getDescription()` on a product, Java runs the right one depending on what the object actually is. This is polymorphism at work. It's clean and ensures every product type can describe itself properly.

---

## Sales and relationships

### Q5: A sale involves a customer, a seller, and one or more products. What kinds of relationships exist between the class representing the sale and the other classes of the system? Are these relationships of inheritance, association, composition, or another type? Justify.

A `Sale` links to a `Customer` and a `Seller` through references — that's a simple association. The customer and seller exist independently of the sale. With products, it's aggregation: a sale has a list of products, but those products are also in the inventory and don't get deleted when the sale ends. None of this is inheritance because a sale is not a type of customer, seller, or product.

### Q6: Should the sale be responsible for calculating its own total, or should this responsibility fall on another class? Justify your decision.

We put `calculateTotal()` inside `Sale`. The sale already has the product list, so it can just loop through and sum the prices. It felt natural to let the sale handle its own math. The service layer (`SaleService`) deals with validation and coordination, not arithmetic.

---

## Business constraints

### Q7: How does the design guarantee that a sale cannot be registered without at least one product? At what point in the system should this rule be validated?

`SaleService.registerSale()` checks if the product list is null or empty before doing anything else. If it's empty, it throws an exception. This check happens before we even try to create a `Sale` object. The service layer is the right place for this because it's where the business rules live.

### Q8: How does the design reflect the automatic update of inventory when a sale is registered? Which classes are involved in this operation?

In `SaleService.registerSale()`, after validating stock, we call `adjustStock(-1)` on each product. That method is inside `Product`, so the product updates its own stock. Then we save the changes through `ProductRepository`. The flow is: `SaleService` orchestrates, `Product` does the math, and repositories handle the saving.

---

## Layered organization

### Q9: The system must be organized into four layers: model, persistence, services, and user interface. What type of classes belong in each layer? What criterion allows one to decide in which layer a class should be placed?

- **Model**: the domain classes — `Person`, `Customer`, `Seller`, `Product`, `VideoGame`, `Console`, `Sale`. They just hold data and behavior.
- **Persistence**: the repository classes — `PersonRepository`, `ProductRepository`, `SaleRepository`. They know how to read and write files.
- **Services**: the business logic classes — `PersonService`, `ProductService`, `SaleService`. They apply rules and use repositories.
- **UI**: the console menu — `ConsoleMenu` and `Main`. They just handle user interaction.

We decide based on responsibility: what does the class do? If it models a concept, it's model. If it saves/loads, it's persistence. If it enforces rules, it's service. If it talks to the user, it's UI.

### Q10: Why should the logic for saving and retrieving data from files not be inside the domain classes? What problems arise when these responsibilities are mixed?

If we put file-saving code inside `Product` or `Sale`, those classes would have to know about file paths and formats. That's not their job. They should only care about business logic. Mixing them makes the code harder to change because a change in file format would force us to modify the domain classes. It also makes testing messy because tests would need to deal with actual files. Keeping persistence in separate repository classes keeps everything cleaner and more maintainable.

### Q11: What dependencies are allowed between the layers, and which are forbidden? Justify the meaning of the allowed dependencies.

The dependency flow is inward:
- `ui` can depend on `service`
- `service` can depend on `persistence`
- `service` can depend on `model`
- `persistence` can depend on `model`
- `model` depends on nothing.

We forbid dependencies going the other way. For example, `model` cannot depend on `persistence` or `ui`. This keeps the domain classes independent and reusable. It also prevents circular dependencies, which would make the system hard to understand and modify. Each layer only knows about the layers below it, which is the whole point of layered architecture.