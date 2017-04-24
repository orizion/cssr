import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button } from "react-bootstrap";
import * as API from "../services/api";

import update = require("react-addons-update");

export interface CreateUserProps {

}

export interface CreateUserState {
    user: API.User;
}
export default class CreateUser extends React.Component<CreateUserProps,CreateUserState> {
    constructor(props:CreateUserProps) {
        super(props);
        this.state = {
            user : {
                displayName:"",
                email:"",
                passwordEnc:"",
                tempToken:"",
                tempTokenExpiresAt:"",
                userId: 0,
            }
        }
        this.handleChanged = this.handleChanged.bind(this);
        this.submit = this.submit.bind(this);
    }
    userAPI = new API.UsercontrollerApi();
    handleChanged(e:any) {
        let val:any = e.target.value;
        this.setState({
            user: update(this.state.user,{
                [e.target.name]: {$set: val}
            })
        });
    }
    submit() {
        
    }
    render(){
        return (
            <form>
                <FormGroup controlId="formControlsDisplayName">
                <ControlLabel>Benutzername</ControlLabel>
                <br/>
                <FormControl type="text" name="displayName" placeholder=""
                    onChange={this.handleChanged} required/>
                </FormGroup>

                <FormGroup controlId="formControlsEmail">
                <ControlLabel>Email</ControlLabel>
                <br/>
                <FormControl type="text" name="email" placeholder=""
                    onChange={this.handleChanged} required/>
                </FormGroup>

                <Button type="submit" bsStyle="primary" onClick={this.submit}>
                    Senden
                </Button>
            </form>
        );
    }
}