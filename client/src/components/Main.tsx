import * as React from "react";
import * as ReactDOM from "react-dom";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button } from "react-bootstrap";

import  Navigo  = require("navigo");

import { Subscribe } from "./Subscribe";
import { CreatePresentation } from "./CreatePresentation";
import { EditPresentation } from "./EditPresentation";


export interface MainProps {
  apiUrl:string;
}

export interface MainState {
  component: JSX.Element;
}

export class Main extends React.Component<MainProps,MainState> {
  constructor(props: any){
    super(props);
    this.state = {
      component: <div>Init State</div>
    }
  }
  componentDidMount() {
    let router:any  = new Navigo(null ,true);
    let self:Main = this;
      router
      .on('/subscribe', function () {
         self.setState(
           {component:<Subscribe apiUrl={self.props.apiUrl} />}
         );
      })
      .on('/createpresentation', function () {
         self.setState(
           {component:<CreatePresentation apiUrl={self.props.apiUrl} />}
         );
      })
      .on('/editpresentation', function () {
         self.setState(
           {component:<EditPresentation apiUrl={self.props.apiUrl} />}
         );
      })
      .resolve();

  }
  render() {
    return (
      <div id="main">
        <Button href="subscribe" data-navigo> Subscribe </Button>
        <Button href="createpresentation" data-navigo> Create Presentation </Button>
        <Button href="editpresentation" data-navigo> Edit Presentation </Button>
        {this.state.component}
      </div>

    )
  }
}
