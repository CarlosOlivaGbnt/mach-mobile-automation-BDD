#language: es

#noinspection NonAsciiCharacters

Característica: Creación de cuenta MACH

  Yo como usuario de MACH con rol de cliente
  deseo crear una cuenta de usuario
  para realizar transacciones desde mi móvil.

  @CrearCuentaMACH
  Esquema del escenario: Creación de cuenta MACH
    Cuando cree una cuenta de usuario con los siguientes datos
      | userIdentifier   | name   | skipChallenge   | address   | targetChallenge   | replaceChallenge   | failChallenge   | checkChallenge   |
      | <userIdentifier> | <name> | <skipChallenge> | <address> | <targetChallenge> | <replaceChallenge> | <failChallenge> | <checkChallenge> |
    Entonces se mostrará el dashboard de la aplicación
    Ejemplos:
      | userIdentifier      | name | skipChallenge | address                | targetChallenge | replaceChallenge | failChallenge | checkChallenge |
      | random-user-default | ""   | ""            | default-create-account | ""              | ""               | ""            | ""             |