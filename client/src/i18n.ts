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
        "logout": "Log out"
      },
      createPresentation:{
        "send": "Send",
        "date": "Date",
        "email_speaker": "Email Speaker",
        "title": "Title",
        "room": "Room",
      },
      subscribe: {
        "meat": "Meat",
        "vegetarian": "Vegetarian"
      },
      overview:{
        "edit": "Edit",
        "subscribe": "Subscribe",
        "time" : "Time",
        "sendInvitation": "Send Invitation"
      },
      common:{
        "room": "Room",
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
        "logout": "Ausloggen"
      },
      createPresentation:{
        "send": "Senden",
        "date": "Datum",
        "email_speaker": "Email Referent",
      },
      subscribe: {
        "meat": "Fleisch",
        "vegetarian": "Vegi"
      },
      overview:{
        "edit": "Bearbeiten",
        "subscribe": "Anmelden",
        "time" : "Zeit",
        "sendInvitation" : "Einladung senden"
      },
      common: {
        "room": "Raum",
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