import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/apiWrapper";
import { translate } from 'react-i18next';

import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";

interface LoginProps {
  oldPassword: string,
  temptoken: boolean
}

@translate(['resetPassword', 'common'], { wait: true })
export class ResetPassword extends React.Component<any, any> {
  constructor(props: any) {
    super(props);
    this.state = {
        oldPassword:this.props.oldPassword,
        newPassword:"",
    };
    this.handleChanged = this.handleChanged.bind(this);
    this.submit = this.submit.bind(this);
  }
  userAPI = new API.UsercontrollerApi();
  handleChanged(e:any) {
    let val: any = e.target.value;
    console.log(val)
    console.log(e.target.name);
    this.setState({
        [e.target.name]: val
    });
  }
  submit(e:any) {
    e.preventDefault();
    this.userAPI.resetPasswordUsingPUT({
        "resetPwdParameter": {
            "oldPassword": this.state.oldPassword,
            "newPassword": this.state.newPassword,
            "oldPasswordTempToken": this.props.temptoken
        }
    })
    .then((response)=> {

    });
  }
  render(){
    let t = this.props.t;
    return (
      <form >
        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>{t('oldPassword')}</ControlLabel>
          <br/>
          <FormControl type="password" name="oldPassword" placeholder="" value={this.state.oldPassword}
          onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>{t('newPassword')}</ControlLabel>
          <br/>
          <FormControl type="password" name="newPassword" placeholder="" value={this.state.newPassword}
          onChange={this.handleChanged} required/>
        </FormGroup>

        <Button type="submit" bsStyle="primary" onClick={this.submit}>
            {t('resetPassword')}
        </Button>
      </form>
    );
  }
}