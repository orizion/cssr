# Tables to be created

## Presentation (Vortrag)

Fields 

- DateTime: DateTime 
- Location: string (Raum)
- Speaker: Email (Referent)
- SpeakerId: F�r Berechtigungshandling. Normalerweise sollten Speaker und SpeakerId ausgef�llt sein
- Title
- Abstract
- ExternUsers[] (muss als ZwischenTabelle gemacht werden)

### PresentationFiles:

- PresentationId
- Type: Folen / Infos
- Content: varbinary
- ContentLink: string
- Either Content or ContentLink must be provided (Check constraint)

## Subscription (Anmeldung)

- Presentation
- Email (Studenten Mail)
- SandwichType :)
- Drink: Boolean

## Email 

Log f�r Mails, Wird vom Timer abgegrast

- Sentdate (Null wenn nicht gesendet)
- tryCount (Wird bei jedem senden versuch erh�ht)
- Subject
- To
- Cc
- Bcc
- Body
- InserDate
- Error 

## ExternUsers

Evtl verlangt Java hier auch eine Struktur

- Id
- Email
- PasswordEncrypted
- Salt 

TODO: Alles �berpr�fen :)
