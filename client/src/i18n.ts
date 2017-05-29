import i18next from 'i18next';

i18next.init({
  lng: 'en',
  resources: {
    en: {
      main : {
        "subscribe": "Subscribe",
        "createPresentation": "Create Presentation",
        "login":"log in",
        "login_aai":"log in (aai)",
        "overview":"Overview",
        "logout": "Log out",
        "createUser" : "Create User",
        "passwordReset": "Reset Password"
      },
      createPresentation:{
      },
      editPresentation: {
        "addFiles": "Add more files",
        "saveFileAs": "Save file as",
        "dataType": "Datatype"
      },
      subscribe: {
        "meat": "Meat",
        "vegetarian": "Vegetarian",
        "subscribe": "Subscribe",
        "sandwichType" : "Sandwich Type",
      },
      overview:{
        "edit": "Edit",
        "subscribe": "Subscribe",
        "sendInvitation": "Send Invitation"
      },
      presentationFileUpload: {
        "presentationFile" : "Files for the Presentation",
      },
      resetPassword: {
        "resetPassword":"Reset Password",
        "oldPassword" : "Old Password or  temporary token",
        "newPassword" :"New Password"
      },
      common:{
        "room": "Room",
        "title" : "Title",
        "email_speaker": "Email Speaker",
        "time" : "Time",
        "date": "Date",
        "abstract": "Abstract",
        "save": "Save",
        "presentation" : "Presentation",
        actions: {
            toggleToGerman: 'Deutsch',
            toggleToEnglish: 'English'
        }
      }
    },
    de: {
      main: {
        "subscribe": "Anmelden",
        "createPresentation": "Präsentation erstellen",
        "login": "Einloggen",
        "login_aai":"Einloggen (aai)",
        "overview":"Übersicht",
        "logout": "Ausloggen",
        "createUser" : "Benutzer erstellen",
        "passwordReset": "Passwort reset"
      },
      createPresentation:{
        
      },
      editPresentation: {
        "addFiles": "Weitere Datein hinzufügen",
        "saveFileAs": "Speichere Datei als",
        "dataType": "Datentyp"
      },
      subscribe: {
        "meat": "Fleisch",
        "vegetarian": "Vegi",
        "subscribe": "Einschreiben",
        "sandwichType" : "Sandwich Sorte",
      },
      overview:{
        "edit": "Bearbeiten",
        "subscribe": "Anmelden",
        "sendInvitation" : "Einladung senden"
      },
      presentationFileUpload: {
        "presentationFile" : "Datei für die Präsentation",
      },
      resetPassword: {
        "resetPassword":"Reset Passwort",
        "oldPassword" : "Altes Passwort oder temporäres Token",
        "newPassword" :"Neues Passwort"
      },
      common: {
        "room": "Raum",
        "title" : "Titel",
        "email_speaker": "Email Referent",
        "time" : "Zeit",
        "date": "Datum",
        "abstract": "Abstract",
        "save": "Speichern",
        "presentation" : "Präsentation",
        actions: {
            toggleToGerman: 'Deutsch',
            toggleToEnglish: 'English'
        }
      }
    }
  }
}, (err, t) => {
  // initialized and ready to go!
  
});

export default i18next;