@ignore
Feature: Validate that API returns the correct interest rate

  Background:
    * configure logPrettyResponse = true
    * configure ssl = true
    * configure retry = { count: 3, interval: 3000 }
    * def req_header = {message-id:'ce0b5865-24eb-4410-b6e8-75327d5554e1',id: '0b04f601-1120-4077-869b-0b6584cc08f0', deviceId: '1', token: '42d1b5d1-b650-4c3a-881a-dbd3df4df765', timestamp: '21-04-2021', consumer: 'APP', Content-Type:'application/vnd.bancolombia.v1+json',X-IBM-Client-Id:'3395d54f-19b8-4918-99d0-8d9afd634ac4', X-IBM-Client-Secret:'K6sR2tM2cS1xM6sY1pJ1uQ6cI3yA2eI7vV0oU3lX7kT0vH8eB0'}
@P59
  Scenario: Validate that the API return correct interest rate for G1 risk group and P59 plan
  Given headers req_header
  And url 'https://api-aws-qa.apps.ambientesbc.com/int/testing/v1/operations/product-specific/loans/deposits/consumer-loan/interest-rate/internal/loanType/interestRate'
  And request {"data": {"customerSegment": "S2","customerRelaibility": "G1","fixedRateLoans": [{"fixedRateLoanId": "P59"}],"variableRateLoans": [{"variableRateLoanType": "P59"}],"amount": 10000000,"loanTerm": 24}}
  When method POST
    * print response
  Then response.status= 200
 @PA5
  Scenario: Validate that the API return correct interest rate for G1 risk group and PA5 plan
  Given headers req_header
  And url 'https://api-aws-qa.apps.ambientesbc.com/int/testing/v1/operations/product-specific/loans/deposits/consumer-loan/interest-rate/internal/loanType/interestRate'
  And request {"data": {"customerSegment": "S2","customerRelaibility": "G1","fixedRateLoans": [{"fixedRateLoanId": "PA5"}],"variableRateLoans": [{"variableRateLoanType": "PA5"}],"amount": 10000000,"loanTerm": 24}}
  When method POST
    * print response
  Then response.status= 200