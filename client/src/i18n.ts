import i18next from 'i18next';

i18next.init({
  lng: 'en',
  resources: {
    en: {
      main : {
        "subscribe": "Subscribe",
        "createPresentation": "Create Presentation",
        "login":"log in",
        "overview":"Overview",
        "logout": "Log out",
        "createUser" : "Create User1",
      },
      createPresentation:{
      },
      subscribe: {
        "meat": "Meat",
        "vegetarian": "Vegetarian"
      },
      overview:{
        "edit": "Edit",
        "subscribe": "Subscribe",
        "sendInvitation": "Send Invitation"
      },
      common:{
        "room": "Room",
        "title" : "Title",
        "email_speaker": "Email Speaker",
        "time" : "Time",
        "date": "Date",
        "abstract": "Abstract",
        "save": "Save",
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
        "overview":"Übersicht",
        "logout": "Ausloggen",
        "createUser" : "Benutzer erstellen",
      },
      createPresentation:{
        
      },
      subscribe: {
        "meat": "Fleisch",
        "vegetarian": "Vegi"
      },
      overview:{
        "edit": "Bearbeiten",
        "subscribe": "Anmelden",
        "sendInvitation" : "Einladung senden"
      },
      common: {
        "room": "Raum",
        "title" : "Titel",
        "email_speaker": "Email Referent",
        "time" : "Zeit",
        "date": "Datum",
        "abstract": "Abstract",
        "save": "Speichern",
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