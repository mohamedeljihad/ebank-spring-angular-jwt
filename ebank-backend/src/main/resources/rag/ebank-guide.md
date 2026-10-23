# Guide de la banque digitale E-Bank ENSET

## Types de comptes
La banque propose deux types de comptes. Le **compte courant** (CurrentAccount) est destiné aux opérations quotidiennes ; il dispose d'un découvert autorisé (overDraft) : un débit est accepté tant que le solde plus le découvert reste supérieur ou égal au montant demandé. Le **compte épargne** (SavingAccount) rapporte des intérêts selon un taux d'intérêt annuel (interestRate) ; il n'a pas de découvert.

## Statuts d'un compte
Un compte est créé avec le statut CREATED. Après vérification des pièces du client, un administrateur le passe au statut ACTIVATED. Un compte peut être SUSPENDED en cas de fraude suspectée ou à la demande du client : aucune opération de débit ou de crédit n'est alors possible.

## Opérations
- Débit (DEBIT) : retrait d'argent ou paiement. Refusé si le solde est insuffisant (message "Balance not sufficient").
- Crédit (CREDIT) : versement sur le compte.
- Virement : un débit sur le compte source suivi d'un crédit sur le compte destinataire, avec la description "Transfer to/from <identifiant>".
Chaque opération enregistre la date, le montant, la description et l'identifiant de l'utilisateur authentifié qui l'a effectuée.

## Frais et plafonds
La tenue de compte courant coûte 10 DH par mois. Le compte épargne est gratuit. Le plafond de retrait par opération est de 20 000 DH ; au-delà, l'opération doit être validée par un conseiller. Les virements entre comptes E-Bank sont gratuits et immédiats.

## Ouvrir un compte
Pour ouvrir un compte, le client doit être enregistré (nom et e-mail), puis un administrateur crée un compte courant ou épargne avec un solde initial. Pièces demandées : carte nationale d'identité et justificatif de domicile.

## Sécurité
L'application utilise une authentification par JSON Web Token. Ne communiquez jamais votre mot de passe. Vous pouvez changer votre mot de passe depuis le menu "Changer le mot de passe". Les rôles sont USER (consultation) et ADMIN (gestion des clients, des comptes et des opérations).

## Contact
Service client : support@ebank-enset.ma, du lundi au vendredi de 9h à 17h.
