import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button } from "react-bootstrap";
import * as moment from 'moment';
import * as Datetime from 'react-datetime';
import * as API from "../services/api";
import Select = require('react-select');
import update = require('react-addons-update');

export interface SendInvitationProps {
}
export interface SendInvitationState {
    template: {
        title:string,
        text:string
    }
}

export class SendInvitation extends React.Component<SendInvitationProps, SendInvitationState> {
    constructor(props: SendInvitationProps) {
        super(props);
        this.state = {
            template: {
                title:"",
                text:""
            }
        };
        API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
        this.handleChanged = this.handleChanged.bind(this)
        this.submit = this.submit.bind(this);

        //get token for api calls 
        this.loginPromise = API.UsercontrollerApiFp.loginUsingPOST({creds: {"email": "adrian.ehrsam@students.fhnw.ch",
                "password": "oij" }})()
            .then((json) => {
                API.defaultHeaders["Authorization"] = "Bearer " + json.token;
                return json.token;
            })
            .catch(() => {
                console.log("No login access granted");
            }) as Promise<string>;
    }
    inputStyle = {
        borderColor: 'red',
    }
    userAPI = new API.UsercontrollerApi();
    loginPromise: Promise<String>;
    date = new Date();
    componentDidMount() {
        //fill options with the existing speakers
    }
    handleChanged(e: any) {
        let val: any = e.target.value;
        this.setState({
            template: update(this.state.template, {
                [e.target.name]: { $set: val }
            })
        });
    }
    submit(e: any) {
        e.preventDefault();     
            return null;
    }
    render() {
        return (
            <div>
                <p>
                    <h3>Einladung versenden</h3>
                    Hier können Sie den Standardeinladungstext bearbeiten, bevor er an alle Mitarbeitenden der 
                    Informatik-Institute sowie an alle entsprechende Studierenden versendet wird.
                </p>
                <form>
                    <FormGroup controlId="formControlsTitle">
                        <ControlLabel>Titel</ControlLabel>
                        <br />
                        <FormControl type="text" name="title" placeholder="" 
                            value={this.state.template.title}
                            onChange={this.handleChanged} required />
                    </FormGroup>

                    <FormGroup controlId="formControlsText">
                        <ControlLabel>Raum</ControlLabel>
                        <br />
                        <FormControl componentClass="textarea" name="text" placeholder="" 
                            value={this.state.template.text}
                            onChange={this.handleChanged} required />
                    </FormGroup>

                    <Button type="submit" bsStyle="primary" onClick={this.submit}>
                        Senden
                    </Button>
                </form>
            </div>
        );
    }
}
