import * as React from "react";
import * as ReactDOM from "react-dom";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button,Nav,NavItem } from "react-bootstrap";

import  Navigo  = require("navigo");

import * as API from "../services/api";

import { Subscribe } from "./Subscribe";
import { CreatePresentation } from "./CreatePresentation";
import { EditPresentation } from "./EditPresentation";
import { CreateUser } from "./CreateUser";
import { SubscribeOverview } from "./SubscribeOverview";


export interface MainProps {
  apiUrl:string;
}

export interface MainState {
  component: JSX.Element,
  activeKey: any;
}

export class Main extends React.Component<MainProps,MainState> {
  constructor(props: any){
    super(props);
    this.state = {
      component: <div>Init State</div>,
      activeKey: "1"
    }
    this.handleNavigate = this.handleNavigate.bind(this);
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
      .on('/createuser',() => {
         self.setState(
           {component:<CreateUser  />}
         );
      })
      .on('/subscribeoverview',() => {
         self.setState(
           {component:<SubscribeOverview  />}
         );
      })
      .resolve();
      router.resolve();
  }
  handleNavigate(key: any) {
    this.setState({
      activeKey: key
    });
  }
  render() {
    return (

      <div id="main">
        <Nav bsStyle="tabs" activeKey={this.state.activeKey} onSelect={this.handleNavigate}>        
          <NavItem eventKey="1" href="subscribe" data-navigo> Subscribe </NavItem>
          <NavItem eventKey="2" href="createpresentation" data-navigo> Create Presentation </NavItem>
          <NavItem eventKey="3" href="editpresentation" data-navigo> Edit Presentation </NavItem>
          <NavItem eventKey="4" href="createuser" data-navigo> Create User </NavItem>
          <NavItem eventKey="5" href="subscribeoverview" data-navigo> Overview </NavItem>
        </Nav>
        <br/><br/>
        {this.state.component}
      </div>

    )
  }
}

