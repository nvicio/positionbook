# Simple Position Book 

##  Documentation
This is a simple position book web app - built as a Spring Restful application.
According to the problem definition, several assumptions were made:
* Event order can only accept one of the types "BUY", "SELL", "CANCEL", otherwise will return a 400 Bad Request.
* An account can be identified by an Integer accountId.
* A security can be identified by an Integer securityId. (In reality, it could also be identified by a String such as ticker or other fields typically held by a reference data source).
* On Cancelling an order: assuming we can only cancel your last order. Otherwise, if we cancel an order happened long ago in the past (processed, cleared), any the subsequent orders that relying on the position from that order will be impacted (For example a BUY order was canceled then the subsequent SELL is not possible if the current position does not allow).
* From the requirement: The system should keep the data in memory (not in a database or in the file system)

_Quick commands:_
- To run the application: `mvn spring-boot:run`
- To run the test scenarios: `mvn test`
- To run a particular test case: `mvn test -Dtest="PositionBookServiceTests#givenSecurity_whenSellThenCancel_thenAmountUnchanged*"`
- Once the application is started, swagger API link can be seen here: http://localhost:8080/swagger-ui.html

## API reference
### Processing order (event)
POST to `http:/localhost:8080/positionbook/event`

Request body: `orderType: one of "BUY", "SELL", "CANCEL"`
```json
{
  "accountId": 100,
  "amount": 10,
  "orderType": "BUY",
  "securityId": 111
}
```
Curl:
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{ \
"accountId": 100, \
"amount": 10, \
"orderType": "BUY", \
"securityId": 111 \
}' 'http://localhost:8080/positionbook/event'
```
Response Codes: 200 OK, 400 Bad Request. Response body: The order and timestamp that got saved in Order History:
```json
{
  "id": 1,
  "type": "BUY",
  "accountId": 100,
  "securityId": 111,
  "amount": 10,
  "date": "2022-07-05T18:18:38.872525",
  "cancelled": false
}
```
### Obtain given position - orders (events)
GET to `http:/localhost:8080/positionbook/position/{accountId}?securityId=11`

Response: Position of given securities in an account
```json
[
  {
    "id": 111,
    "name": "SEC1",
    "symbol": "SE1",
    "currentPosition": 10
  },
  {
    "id": 112,
    "name": "SEC2",
    "symbol": "SE2",
    "currentPosition": 5
  }
]
```
Curl:
```
curl -X GET --header 'Accept: application/json' 'http://localhost:8080/positionbook/position/100/111'
```
Response Codes: 200 OK, 400 Bad Request. 
### Obtain history orders (events)
GET to `http:/localhost:8080/positionbook/position/{accountId}?securityId=11`

Response: List of orders (events) of given securities in an account
```json
[
  {
    "id": 2,
    "type": "BUY",
    "accountId": 10,
    "securityId": 101,
    "amount": 10,
    "date": "2022-07-05T20:44:17.867893",
    "cancelled": false
  },
  {
    "id": 3,
    "type": "BUY",
    "accountId": 10,
    "securityId": 101,
    "amount": 10,
    "date": "2022-07-05T20:44:19.987943",
    "cancelled": false
  },
  {
    "id": 4,
    "type": "SELL",
    "accountId": 10,
    "securityId": 101,
    "amount": 10,
    "date": "2022-07-05T20:44:21.230272",
    "cancelled": false
  }
]
```
Curl:
```
curl -X GET --header 'Accept: application/json' 'http://localhost:8080/positionbook/order/10?securityId=101'
```
### Possible further improvements
- More logic on error handling and validation based on business requirements
- UI to display asset position movements over time
- When receive a sell order of a security with amount greater than current holding, TBD: only allow selling of that available amount / Or throw errors depending on what business required.
Probably the later is better, user needs to be able to decide what to do in that situation.


