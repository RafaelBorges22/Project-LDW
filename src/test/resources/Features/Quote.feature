Feature: Gestão de Orçamentos (Quotes)
  Testes dos endpoints relacionados a orçamentos da API Kazu Tattoo.

  Background:
    Given the API base url is "http://localhost:8081"

  Scenario: Buscar orçamento por ID
    When I GET "/quotes/1"
    Then the response status should be 200

  Scenario: Listar todos os orçamentos
    When I GET "/quotes"
    Then the response status should be 200

  Scenario: Atualizar orçamento
    When I PUT "/quotes/1" with JSON:
      """
      {
        "finalValue": 500.0,
        "additionalCost": 50.0,
        "state": "PAID"
      }
      """
    Then the response status should be 200

  Scenario: Criar novo orçamento com imagem (Multipart)
    When I POST multipart "/quotes" with:
      | fieldName     | clientId                                 |
      | description   | Quero Tatuar o nome do meu filho      |
      | size          | MEDIUM                                |
      | bodyPart      | LEG                                   |
      | colored       | false                                 |
      | clientId      | 0252d83e-d2e6-11f0-b22d-7eb253ba6053  |
      | image         | uploads/3b3d4a55-1b4f-49dd-b76d-c79a5a63fb41_1_34DCJ1mW9TL2ztHgORlCrA.jpeg    |
    Then the response status should be 201


