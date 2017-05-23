import * as React from "react";
import * as ReactDOM from "react-dom";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button,Nav,NavItem,Popover } from "react-bootstrap";
import  Navigo  = require("navigo");
import { translate } from 'react-i18next';

import * as API from "../services/api";
import { CreatePresentation } from "./CreatePresentation";
import { CreateUser } from "./CreateUser";
import { EditPresentation } from "./EditPresentation";
import { Login } from "./Login";
import { SendInvitation } from "./SendInvitation";
import { Subscribe } from "./Subscribe";
import { SubscribeOverview } from "./SubscribeOverview";

import PropTypes from 'prop-types';

export interface MainState {
  component: JSX.Element,
  userMeta: API.UserMeta,
  activeKey: any;
  language: String;
}

@translate(['main', 'common'], { wait: true })
export class Main extends React.Component<any,MainState> {
  constructor(props: any){
    super(props);
    this.state = {
      component: <Login  userMetaFunction={this.setUserInformation}/>,
      activeKey: "1",
      userMeta : "",
      language:  "en"
    }
    this.i18n.changeLanguage("en");
    this.handleNavigate = this.handleNavigate.bind(this);
    this.handleLangChange = this.handleLangChange.bind(this);
    this.setUserInformation = this.setUserInformation.bind(this);
  }
  i18n=this.props.i18n;
  userAPI = new API.UsercontrollerApi();
  
  setUserInformation(_userMeta:API.UserMeta) {
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
    this.setState({
      userMeta: _userMeta
    });
  }

  handleLangChange() {
    console.log(this.state.language);
    if(this.state.language == "en") {
      this.i18n.changeLanguage("de");
      this.setState({language: "de"});
    }else {
      this.i18n.changeLanguage("en");
      this.setState({language: "en"});
    }
    
  }
  componentDidMount() {
    let router:any  = new Navigo(null , false);
    let self:Main = this;
      router
      .on('/login',() => {
        self.setState(
           {component:<Login  userMetaFunction={this.setUserInformation}/>}
        );
      })
      .on('/subscribe/:id',(params:any,query:string) => {       
         self.setState(
           {component:<Subscribe presentationId={params.id} />}
         );
      })//Only for testing
      .on('/subscribe',() => {
        self.setState(
           {
            component:<Subscribe presentationId={1} />
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
           {component:<EditPresentation />}
         );
      })
      .on('/createuser',() => {
         self.setState(
           {component:<CreateUser />}
         );
      })
      .on('/overview',() => {
         self.setState(
           {component:<SubscribeOverview  />}
         );
      })
      .on('/sendinvitation',() => {
         self.setState(
           {component:<SendInvitation  />}
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
    let t = this.props.t;    
    let authorities:Array<String> = this.state.userMeta.authorities || new Array<String>();
    return (
         <div>
            <Nav bsStyle="tabs" activeKey={this.state.activeKey} onSelect={this.handleNavigate}>        
            {  authorities.indexOf("Role_Coord") !=-1 &&
               <NavItem eventKey="2" href="createpresentation" data-navigo> 
                {t('createPresentation')}  
              </NavItem> 
            }
            
            { authorities.indexOf("Role_Coord")  != -1 && 
              <NavItem eventKey="3" href="editpresentation" data-navigo> Edit Presentation </NavItem>
            }
            { authorities.indexOf("Role_Admin")  != -1 && 
              <NavItem eventKey="4" href="createuser" data-navigo> Create User </NavItem>
            }
              <NavItem eventKey="5" href="overview" data-navigo> Overview </NavItem>
              <NavItem eventKey="6" href="subscribe" data-navigo>               
                {t('subscribe')}
              </NavItem>
            { authorities.indexOf("Role_Coord")  != -1 || authorities.indexOf("Role_SGL")  != -1 && 
              <NavItem eventKey="7" href="sendinvitation" data-navigo>  
                {t('sendInvitation')}
              </NavItem>
            }
            </Nav>
            <Button  bsStyle="success" href="login" data-navigo> 
              {t('login')} 
            </Button>
           
            <Button bsStyle="info" onClick={this.handleLangChange}>
              {this.state.language}
            </Button>
            
           <br/><br/>
           <div>
             {this.state.component}
          </div>
      </div>
    ); 
  }
}

