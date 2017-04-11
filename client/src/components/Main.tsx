import * as React from "react";
import * as ReactDOM from "react-dom";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button } from "react-bootstrap";

import  Navigo  = require("navigo");

import * as API from "../services/api";

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
    let router:any  = new Navigo(null , false);
    let self:Main = this;
      router
      .on('/subscribe/:id',(params:any,query:string) => {
        console.log("It succeeded");
        let presentationAPI: API.PresentationcontrollerApi  = new API.PresentationcontrollerApi();
        presentationAPI.getSingleUsingGET({"id":params.id})
        .then((response: API.Presentation)=> {
          console.log("It succeeded");
          self.setState(
           {component:<Subscribe presentation={response}/>}
         );
        }).catch((err) => {
          console.log("No Presentation found");
        });
         
      })//Only for testing
      .on('/subscribe',() => {
        self.setState(
           {
            component:<Subscribe presentation={{
              title:"",
              presentationId:1,
              abstract:"",
              dateTime: new Date(),
              location:"",
              speakerId:1
            }}/>
          }
        );
      })
      .on('/createpresentation',() => {
         self.setState(
           {component:<CreatePresentation  />}
         );
      })
      .on('/editpresentation', () => {
         self.setState(
           {component:<EditPresentation  />}
         );
      })
      .resolve();
      router.resolve();
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
