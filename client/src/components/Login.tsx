import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/apiWrapper";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";

interface LoginProps {
  userMetaFunction: Function;
}

export class Login extends React.Component<LoginProps, any> {
  constructor(props: any) {
    super(props);
    this.state = {
        username:"",
        password:""
    };
    this.handleChanged = this.handleChanged.bind(this);
    this.submit = this.submit.bind(this);
  }
  handleChanged(e:any) {
    let val:any = e.target.value;
    this.setState({
        [e.target.name]: val
    });
    
  }
  api = new API.UsercontrollerApi();
  submit(e:any) {
    e.preventDefault();
    
    this.api.loginUsingPOST({creds: 
        {"email": this.state.username,
        "password": this.state.password }})
    .then((json) => {
        let token:string = "";
        if(json.token != undefined){
            token = json.token;
        }
        API.setToken(token);
        
        this.api.getUsingGET()
        .then((response) =>{
          this.props.userMetaFunction(response);    
        });
        
    })
    .catch(() => {
        console.log("No login access granted");
    }) as Promise<void>;
  }
  render() {
    return (
      <form >
        <FormGroup controlId="formControlsTitle">
          <ControlLabel>Benutzername</ControlLabel>
          <br/>
          <FormControl type="text" name="username" placeholder="" 
          onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>Passwort</ControlLabel>
          <br/>
          <FormControl type="password" name="password" placeholder="" 
          onChange={this.handleChanged} required/>
        </FormGroup>

        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          Login
        </Button>
        <span> &nbsp; &nbsp; </span>
        <a href="resetpasswordwithtoken" data-navigo>Passwort vergessen? </a>
      </form>
    );
  }
}
