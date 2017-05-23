import * as React from "react";
import * as ReactDOM from "react-dom";
import { I18nextProvider,translate } from 'react-i18next';

import { Subscribe } from "./components/Subscribe";
import { CreatePresentation } from "./components/CreatePresentation";
import { Main } from "./components/Main";
import i18n from './i18n'; // initialized i18next instance



ReactDOM.render(
    <I18nextProvider i18n={ i18n }>
    <Main />
    </I18nextProvider>,
    document.getElementById("container")
);
