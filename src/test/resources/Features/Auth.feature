Feature: Autenticação do sistema

  Background:
    Given the API base url is "http://localhost:8081"

  Scenario: Login válido
    Given the API base url is "http://localhost:8081"
    When I POST "/auth/login" with JSON:
    """
    {
      "email": "contacontaconta0002@gmail.com",
      "password": "SenhaAdmin1!"
    }
    """
    Then the response status should be 200

