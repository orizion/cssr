import * as React from "react";
import * as ReactDOM from "react-dom";
import { FormControl,FormGroup,ControlLabel,Checkbox,Button,Nav,NavItem,Popover, Row, Col } from "react-bootstrap";
import  Navigo  = require("navigo");
import { translate } from 'react-i18next';

import * as API from "../services/apiWrapper";
import { CreatePresentation } from "./CreatePresentation";
import { CreateUser } from "./CreateUser";
import { EditPresentation } from "./EditPresentation";
import { Login } from "./Login";
import { SendInvitation } from "./SendInvitation";
import { Subscribe } from "./Subscribe";
import { SubscribeOverview } from "./SubscribeOverview";
import { ResetPassword} from "./ResetPassword";

import PropTypes from 'prop-types';
export type keyType = "1"|"2"|"3"|"4";
export interface MainState {
  component: JSX.Element,
  userMeta: API.UserMeta,
  activeKey: keyType;
  language: string;
}

@translate(['main', 'common'], { wait: true })
export class Main extends React.Component<any,MainState> {
  constructor(props: any){
    super(props);
    this.state = {
      component: <br />,
      activeKey: "4",
      userMeta : {},
      language:  (localStorage.lang != null ? localStorage.lang : "en"),
    }
    if(localStorage.token){      
      new API.UsercontrollerApi().getUsingGET()
        .then((_userMeta) =>{
          this.setState({
            userMeta: _userMeta
          });
        });
    }
    this.i18n.changeLanguage(this.state.language);
    this.handleNavigate = this.handleNavigate.bind(this);
    this.handleLangChange = this.handleLangChange.bind(this);
    this.setUserInformation = this.setUserInformation.bind(this);
  }
  i18n=this.props.i18n;
  userAPI = new API.UsercontrollerApi();
  
  setUserInformation(_userMeta:API.UserMeta) {
    this.setState({
      userMeta: _userMeta,      
    });
    this.router.navigate('/presentation');
  }
  authorities:Array<String> = [];
  handleLangChange() {
    console.log(this.authorities);
    if(this.state.language == "en") {
      this.i18n.changeLanguage("de");
      this.setState({language: "de"}, () =>{
        localStorage.setItem("lang",this.state.language);
      });
    }else {
      this.i18n.changeLanguage("en");
      this.setState({language: "en"}, () =>{
        localStorage.setItem("lang",this.state.language);
      });
    }
  }
  isAuthorized(authorizedRoles:Array<string>):boolean {
    let authorities = this.state.userMeta.authorities || new Array<string>();
    for (var i = 0; i < authorizedRoles.length; i++) {
      if(authorities.indexOf(authorizedRoles[i]) != -1) {
        return true;
      }
    }
    return false;
  }
  renderOrReturn(_component:any,allowedRoles:Array<string>,key:keyType) {
    if(this.isAuthorized(allowedRoles)){
      this.setState(
        {component:_component, activeKey: key}
      );
    }else {
      this.router.navigate('/login');
    }
  }
  router : any;
  componentDidMount() {
    this.router = new Navigo(null , false);
    let self:Main = this;
      this.router
      .on('/resetpassword',(params:any,query:string) => {
        
        let token = window.location.hash
        console.log(token);
          self.setState(
           {component:<ResetPassword  oldPassword={""} temptoken={false}/>}
          );
      }) 
      .on('/resetpasswordwithtoken',(params:any,query:string) => {      
        let token = window.location.hash
        console.log(token);
          self.setState(
           {component:<ResetPassword  oldPassword={token} temptoken={true}/>}
          );
      })
      .on('/invitemail',(params:any,query:string) => {
        let token = window.location.hash
        console.log(token);
        self.setState(
           {component:<ResetPassword  oldPassword={token} temptoken={true}/>}
        );
      }) 
      .on('/login',() => {
        self.setState(
           {component:<Login  userMetaFunction={this.setUserInformation}/>}
        );
      })
      .on('/logout',()=> {
        localStorage.setItem("token","");
        this.setState({
          component: <br />,
          activeKey: "4",
          userMeta : {},
          language:  (localStorage.lang != null ? localStorage.lang : "en")
        });
        this.router.navigate('/login');
      })
      .on('/presentation/:id/subscribe',(params:any,query:string) => {       
         self.setState(
           {component:<Subscribe presentationId={params.id} t={this.props.t}/>}
         );
      })
      .on('/presentation/create',() => {
        self.setState(
           {component:<CreatePresentation  />, activeKey:"3"} 
        );    
      })
      .on('/presentation/:id/edit', (params:any,query:string) => {
         self.setState(
           {component:<EditPresentation  presentationId={params.id} 
              authorities={this.state.userMeta.authorities} t={this.props.t}/>}
         );
      })
      .on('/createuser',() => {
         self.setState(
           {
            component:<CreateUser />,
            activeKey: "3"
          },
         );
      })
      .on('/presentation',() => {
         self.setState(
           {component:<SubscribeOverview  t={this.props.t} userMeta={this.state.userMeta} />, activeKey: "1"}
         );
      })
      .on('/presentation/:id/sendinvitation',(params:any,query:string) => {
         self.setState(
           {component:<SendInvitation  presentationId={params.id} />}
         );
      })
      .resolve();
      this.router.resolve();
  }
  handleNavigate(key: any) {
    this.setState({
      activeKey: key
    });
  }
  render() {
    let t = this.props.t;       
    setTimeout(()=>{
      this.router.updatePageLinks(); 
    }, 0)
    return (      
         <div>

            <Row>
              <Col xs={6} md={6}>
                <img src={ API.applicatonFilesBase + 'logo_text.png' } className="" width={150}/>
              </Col>
              
              <Col  xs={2} md={2} mdPush={4} xsPush={4}>
                { localStorage.token ?
                  (
                    <div>
                      {this.state.userMeta ? this.state.userMeta.email: ""} <br />
                      <Button  bsStyle="info" href="logout"  className="btn-block" data-navigo> 
                        {t('logout')}                   
                       </Button>
                    </div>
                  )
                  :
                  (
                    <div>
                        <Button  bsStyle="info" href="login"  className="btn-block" data-navigo> 
                          {t('login')}
                        </Button>   
                        <br />
                        <Button  bsStyle="info" href="/wodss17-6-aai"  className="btn-block" > 
                          {t('login_aai')}
                        </Button>   
                   </div>
                  )
                } 
                  
                <Button bsStyle="info" className="btn-block" onClick={this.handleLangChange}>
                  { this.state.language == "en" ?
                    t('common:actions.toggleToGerman') :
                    t('common:actions.toggleToEnglish')
                  }
                </Button>
                <Button  bsStyle="info" href="resetpassword"  className="btn-block" data-navigo> 
                  {t('passwordReset')}
                </Button>
              </Col>
            </Row>

            {  true && 
              <Nav bsStyle="tabs" activeKey={this.state.activeKey} onSelect={this.handleNavigate}>        
              
              {  this.isAuthorized(["ROLE_COORD"]) &&
                <NavItem eventKey="3" href="/presentation/create" data-navigo> {t('createPresentation')} </NavItem> 
              }
              { this.isAuthorized(["ROLE_ADMIN"]) && 
                <NavItem eventKey="2" href="/createuser" data-navigo>{t('createUser')}</NavItem>
                
              }
                <NavItem eventKey="1" href="/presentation" data-navigo>{t('overview')} </NavItem>
              </Nav>
            }                
           <br/>           
           {this.state.component}         
        </div>
    ); 
  }
}

