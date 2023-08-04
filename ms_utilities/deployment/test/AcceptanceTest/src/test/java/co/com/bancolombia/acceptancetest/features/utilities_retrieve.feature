Feature: Validate that API Utilities returns correct information

  Background:
    * configure logPrettyResponse = true
    * configure ssl = true
    * configure retry = { count: 3, interval: 3000 }
    * def req_header = {message-id:'ce0b5865-24eb-4410-b6e8-75327d5554e1',id: '0b04f601-1120-4077-869b-0b6584cc08f0', deviceId: '1', token: '42d1b5d1-b650-4c3a-881a-dbd3df4df765', timestamp: '21-04-2021', consumer: 'APP', Content-Type:'application/vnd.bancolombia.v1+json',X-IBM-Client-Id:'3395d54f-19b8-4918-99d0-8d9afd634ac4', X-IBM-Client-Secret:'K6sR2tM2cS1xM6sY1pJ1uQ6cI3yA2eI7vV0oU3lX7kT0vH8eB0'}
    * def snippet_P59 = call read('snippet_interestrate.feature@P59')
    * def snippet_PA5 = call read('snippet_interestrate.feature@PA5')
    * def snippet_installment_P59 = call read('snippet_installment.feature@P59')
    * def snippet_installment_PA5 = call read('snippet_installment.feature@PA5')


  Scenario: Validate that the API returns all fields when the plan is PA5
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G1"},"offer": {"productId": "14","amount": 10000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SV"},{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=200
    And match response == {"data": {"installmentsData": {"installmentData": [{"interestRate":#null,"insurances": {"insurance": [{"amount": #number,"type": #string}]},"amortizationSchedule":#null,"variableInterestRateAdditionalPoints":#number,"arreasInterestRate":#number,"interestRateType":#string,"nominalAnnualInterestRate":#number,"availabilityHandlingFee":#null,"paymentDay":#null,"effectiveAnnualInterestRate":#number,"installment":#number,"fng":#null,"monthOverdueInterestRate":#number,"expirationDate":#null},{"interestRate":#null,"insurances": {"insurance":[{"amount":#number,"type":#string},{"amount":#number,"type":#string}]},"amortizationSchedule":#null,"variableInterestRateAdditionalPoints":#number,"arreasInterestRate":#number,"interestRateType":#string,"nominalAnnualInterestRate":#number,"availabilityHandlingFee":#null,"paymentDay":#null,"effectiveAnnualInterestRate":#number,"installment":#number,"fng":#null,"monthOverdueInterestRate":#number,"expirationDate":#null}]},"customer": {"customerReliability":#string,"companyIdType":#string,"identification": {"number": #string,"type": #string},"companyIdNumber":#string}},"status":#present}

  Scenario: Validate that the API returns all fields when the plan is P59
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G1"},"offer": {"productId": "14","amount": 10000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status=200
    And match response == {"data": {"installmentsData": {"installmentData": [{"interestRate":#null,"insurances": {"insurance": [{"amount": #number,"type": #string}]},"amortizationSchedule":#null,"variableInterestRateAdditionalPoints":#number,"arreasInterestRate":#number,"interestRateType":#string,"nominalAnnualInterestRate":#number,"availabilityHandlingFee":#null,"paymentDay":#null,"effectiveAnnualInterestRate":#number,"installment":#number,"fng":#null,"monthOverdueInterestRate":#number,"expirationDate":#null}]},"customer": {"customerReliability":#string,"companyIdType":#string,"identification": {"number": #string,"type": #string},"companyIdNumber":#string}},"status":#present}

  Scenario: Validate that the API return correct interest rate for G1 risk group and P59 plan
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G1"},"offer": {"productId": "14","amount": 1000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    * print snippet_P59.response
    Then response.status= 200
    * def val = response.data.installmentsData.installmentData[0].arreasInterestRate
    Then match snippet_P59.response.data.rateRange[0].rangeType[*].arrearsRate contains val
    * print response.data.installmentsData.installmentData

  Scenario: Validate that the API return correct interest rate for G1 risk group and PA5 plan
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G1"},"offer": {"productId": "14","amount": 10000000,"interestRateType" : "F","term": 36,"insurances": {"insurance":[{"type": "SV"},{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    * print snippet_PA5.response
    Then response.status= 200
    * def val = response.data.installmentsData.installmentData[1].arreasInterestRate
    Then match snippet_PA5.response.data.rateRange[0].rangeType[*].arrearsRate contains val
    * print response.data.installmentsData.installmentData

  Scenario: Validate that the quota API response matches the installment response
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status==200
    * def installment = {amount: '#(response.data.installmentsData.installmentData[0].installment)'}
    And match snippet_installment_P59.response.data.feeConcepts[1] contains installment

  Scenario: Validate that the life insurance API response matches the installment response
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status==200
    * def life_insurance = {amount: '#(response.data.installmentsData.installmentData[0].insurances.insurance[0].amount)'}
    And match snippet_installment_P59.response.data.regularFeeConcepts[0] contains life_insurance


  Scenario: Validate that the life insurance API response represents the true value
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "244010333932009"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status==200
    * def life_insurance = {amount: '#(parseInt(15000000* 0.00145))'}
    And match response.data.installmentsData.installmentData[0].insurances.insurance[0] contains life_insurance



  Scenario: Validate that the unemployment insurance API response matches the installment response
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"},{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status==200
    * def unemployment_insurance = {amount: '#(response.data.installmentsData.installmentData[1].insurances.insurance[1].amount)'}
    And match snippet_installment_PA5.response.data.regularFeeConcepts[0] contains unemployment_insurance


  Scenario: Validate that the unemployment insurance API response represents the true value
    Given headers req_header
    And url 'https://financiacion-int-qa.apps.ambientesbc.com/loans-offer/utilities/api/v1/retrieve'
    And request {"data": {"customer": {"identification": {"type": "TIPDOC_FS001","number": "2101068007"},"companyIdType": "TIPDOC_FS003","companyIdNumber": "45678945","customerReliability": "G2"},"offer": {"productId": "14","amount": 15000000,"interestRateType" : "F","term": 48,"insurances": {"insurance":[{"type": "SV"},{"type": "SD"}]}},"amortizationSchedule": false}}
    When method POST
    Then response.status==200
    * def unemployment_insurance = {amount: '#(parseInt(15000000*  0.002808))'}
    And match response.data.installmentsData.installmentData[1].insurances.insurance[1] contains unemployment_insurance