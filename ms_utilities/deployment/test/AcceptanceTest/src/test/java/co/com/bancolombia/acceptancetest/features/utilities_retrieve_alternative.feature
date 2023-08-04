Feature: Validate all API error scenarios

  Background:
    * configure logPrettyResponse = true
    * configure ssl = true
    * configure retry = { count: 3, interval: 3000 }
    * def req_header = {message-id:'ce0b5865-24eb-4410-b6e8-75327d5554e1',id: '0b04f601-1120-4077-869b-0b6584cc08f0', deviceId: '1', token: '42d1b5d1-b650-4c3a-881a-dbd3df4df765', timestamp: '21-04-2021', consumer: 'APP', Content-Type:'application/vnd.bancolombia.v1+json',X-IBM-Client-Id:'3395d54f-19b8-4918-99d0-8d9afd634ac4', X-IBM-Client-Secret:'K6sR2tM2cS1xM6sY1pJ1uQ6cI3yA2eI7vV0oU3lX7kT0vH8eB0'}

  Scenario: Validate that the api does not return information when the user idType is not added
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G1"},"offer": {"productId": "1","amount": 10000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    And match response == {"errors":[{"code":"LI014","detail":"Es obligatorio indicar datos de identificación del cliente"}],"status":404}

  Scenario: Validate that the api does not return information when the user idNumber is not added
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": ""},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G1"},"offer": {"productId": "1","amount": 10000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    And match response == {"errors":[{"code":"LI014","detail":"Es obligatorio indicar datos de identificación del cliente"}],"status":404}

  Scenario: Validate that the api does not return information when the user customerReliability is not added
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": ""},"offer": {"productId": "1","amount": 10000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    And match response == {"errors":[{"code":"LI014","detail":"Es obligatorio indicar datos de identificación del cliente"}],"status":404}

  Scenario: Validate that the api does not return information when the user amount is not added
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "1","amount": "null","interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI015","detail": "El monto es un dato obligatorio"}],"status":404}

  Scenario: Validate that the api does not return information when the user term is not added
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "1","amount": 1000000,"interestRateType" : "F","term": "null","insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI016","detail": "El plazo es un dato obligatorio"}],"status":404}

  Scenario: Validate that the api does not return information when the insurance type is not added
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "1","amount": 1000000,"interestRateType" : "F","term": 48},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI017","detail": "El tipo de seguro es un dato obligatorio / Se debe indicar la información relacionada con seguros."}],"status":404}

  Scenario: Validate that the amount not entered is greater than 500,000,000
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "1","amount": 500000002,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI018","detail": "El monto no se encuentra dentro del rango permitido."}],"status":404}

  Scenario: Validate that the amount not entered is less than 1,000,000
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "1","amount": 100000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI018","detail": "El monto no se encuentra dentro del rango permitido."}],"status":404}



  Scenario: Validate that the API returns an error when the interest rate is not valid for this experience
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "DTF","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI021","detail": "El tipo de tasa no aplica para este producto."}],"status":404}


  Scenario: Validate that the API returns an error when the product is not valid for this experience
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "1","amount": 15000000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI020","detail": "La identificación del producto no corresponde con la experiencia."}],"status":404}


  Scenario: Validate that when the term is outside the range of the P59 plan, it shows an error
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 84,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI019","detail": "El plazo no se encuentra dentro del rango permitido."}],"status":404}

  Scenario: Validate that when the term is outside the range of the PA5 plan, it shows an error
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 24,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=404
    * print response
    And match response == {"errors": [{"code": "LI019","detail": "El plazo no se encuentra dentro del rango permitido."}],"status":404}