        <<abstract>> Product
	<<abstract>> Accesory
	<<abstract>> Promotion
	<<abstract>> Warranty
	<<abstract>> Person

    Person <|-- Customer
    Person <|-- Seller
    Product <|-- Console
    Product <|-- VideoGame
    Product <|-- Accesory
    Accesory <|-- Controller
    Accesory <|-- Cable
    Accesory <|-- Memory
    Promotion <|-- PercentagePromotion
    Promotion <|-- CategoryPromotion
    Promotion <|-- VolumePromotion
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty