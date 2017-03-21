import * as React from "react";
import * as ReactDOM from "react-dom";

import { Subscribe } from "./components/Subscribe";
import { CreatePresentation } from "./components/CreatePresentation";
import { Main } from "./components/Main";

ReactDOM.render(
    <Main apiUrl="test"/>,
    document.getElementById("container")
);
