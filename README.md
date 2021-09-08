# products-api

EndPoints

GET http://localhost:9000/products       Get all products

POST http://localhost:9000/products Create a product with 
body:
{    
"id":"005aaacd-c07e-412c-96ff-e25ed21a109c",
"name": "XProduct",
"price": 10.00,
"vendor": "Xiaomi",
"expiration": "2021-10-08"
}

GET http://localhost:9000/products/{productUUID}  Get a specific product

GET http://localhost:9000/products?vendor={vendor} Get all products from vendor
