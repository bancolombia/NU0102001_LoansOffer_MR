@ignore
Feature: Validate that API returns the correct interest rate

  Background:
    * configure logPrettyResponse = true
    * configure ssl = true
    * configure retry = { count: 3, interval: 3000 }
    * def req_header = {message-id:'ce0b5865-24eb-4410-b6e8-75327d5554e1',id: '0b04f601-1120-4077-869b-0b6584cc08f0', deviceId: '1', token: '42d1b5d1-b650-4c3a-881a-dbd3df4df765', timestamp: '21-04-2021', consumer: 'APP', Content-Type:'application/vnd.bancolombia.v1+json',X-IBM-Client-Id:'da958d9a-d289-4629-94f6-7b4414295b19', X-IBM-Client-Secret:'xK3fI2wW6dC8oM2cX6nP8aI3hE2fK7jW2cP7sI4mK4mE0tG4vY'}

  @P59
  Scenario: Validate that the API return correct installment for P59 plan
    Given headers req_header
    And url 'https://internal-apigateway-qa.bancolombia.corp/int/testing/v1/operations/product-specific/loan-utilities/installments/calculateInstallment'
    And request {"data": {"creditPlan": "P59","amount": 15000000,"currency": "COP","term": 48,"insurances": [{"type": "SV","rate": 0.01740}]}}
    When method POST
    * print response
    Then response.status= 200

  @PA5
  Scenario: Validate that the API return correct installment for PA5 plan
    Given headers req_header
    And url 'https://internal-apigateway-qa.bancolombia.corp/int/testing/v1/operations/product-specific/loan-utilities/installments/calculateInstallment'
    And request {"data": {"creditPlan": "PA5","amount": 15000000,"currency": "COP","term": 48,"insurances": [{"type": "SV","rate": 0.01740},{"type": "SD","fixedAmount": 42120}]}}
    When method POST
    * print response
    Then response.status= 200