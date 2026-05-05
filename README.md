# TPV - Sistema de Gestió per Botiga de Roba

![Java Version](https://img.shields.io/badge/Java-21%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## ▌Què és?

Aquest projecte és una aplicació de tipus TPV (Terminal Punt de Venda) dissenyada per gestionar una botiga de roba especialitzada en la venda de pantalons i camises.

L’aplicació permet gestionar de manera senzilla:
- Articles (alta, baixa, modificació i consulta)
- Clients
- Vendes mitjançant tiquets
- Control d’estoc
- Consultes de vendes i beneficis

També incorpora funcionalitats avançades com:
- Importació automàtica d’articles des d’un fitxer JSON
- Càlcul de beneficis per producte
- Propostes de recompra d’articles amb baix stock
- Simulació i impressió de tiquets de compra

Aquest sistema està pensat per facilitar la gestió diària del negoci de manera ràpida i eficient.

---

## ▌Execució

Compilar i executar la classe principal:

```java
App.java
```

En iniciar l’aplicació es mostrarà un menú principal des d’on es poden utilitzar totes les funcionalitats del sistema, com la gestió d’articles, clients, vendes i consultes.

---

## ▌Dependències

Aquest projecte utilitza les següents llibreries:

- **JUnit 5 (junit-jupiter-engine + junit-platform-runner)**  
  Utilitzat per a la realització de proves (testing) de l’aplicació.

- **Gson (2.10.1)**  
  Permet llegir i escriure fitxers JSON, utilitzat especialment per a la importació d’articles.

- **MySQL Connector/J (9.7.0)**  
  Driver necessari per connectar l’aplicació amb la base de dades MySQL.

- **HikariCP (6.3.0)**  
  Sistema de gestió de connexions (connection pooling) per millorar el rendiment amb la base de dades.

- **SLF4J Simple (1.7.36)**  
  Llibreria de logging per mostrar informació i errors durant l’execució.

---

## ▌Llicència

Aquest projecte està sota la llicència [MIT](LICENSE).

---

## ▌Autors

Jairo Linares  
GitHub: https://github.com/JJairo-16  

Pol Ibáñez  
Github: https://github.com/polibanezcano2

Eric Fradera  
Github: https://github.com/eghubg
