Feature: Gestão de Clientes
  Testes dos endpoints de clientes da API Kazu Tattoo.

  Background:
    Given the API base url is "http://localhost:8081"

  Scenario: Listar clientes
    When I GET "/clients"
    Then the response status should be 200

  Scenario: Buscar cliente por ID
    When I GET "/clients/0252d83e-d2e6-11f0-b22d-7eb253ba6053"
    Then the response status should be 200

  Scenario: Atualizar cliente
    When I PUT "/clients/087d87a5-0f12-4125-bccc-ff9011e4086a" with JSON:
      """
      {
        "name": "Cliente Atualizado"
      }
      """
    Then the response status should be 200

  Scenario: Deletar cliente por ID
    When I DELETE "/clients/087d87a5-0f12-4125-bccc-ff9011e4086a/admin"
    Then the response status should be 200

  Scenario: Criar cliente
    When I POST "/clients" with JSON:
      """
      {
        "id": "null",
        "name": "Cliente Teste",
        "email": "cliente_teste@example.com",
        "password": "Senha123!",
        "address": "Rua dos testes",
        "phone": "11999999999"
      }
      """
    Then the response status should be 201


#Todas as vezes que rodar um teste de cenario garante de mudar o clientId para um ID presente no Banco de Dados.
# Altere o id em todas os métodos que pedem

