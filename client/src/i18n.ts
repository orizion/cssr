import i18next from 'i18next';

i18next.init({
  lng: 'en',
  resources: {
    en: {
      main : {
        "subscribe": "Subscribe",
        "createPresentation": "Create Presentation",
        "login":"log in",
        "overview":"Overview"
      },
      createPresentation:{
        "send": "Send",
        "room": "Room",
        "date": "Date",
        "email_speaker": "Email Speaker",
      },
      common:{
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
        "overview":"Übersicht"
      },
      createPresentation:{
        "send": "Senden",
        "room": "Raum",
        "date": "Datum",
        "email_speaker": "Email Referent",
      },
      common: {
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