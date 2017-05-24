import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button } from "react-bootstrap";
import * as moment from 'moment';
import * as Datetime from 'react-datetime';
import * as API from "../services/api";
import Select = require('react-select');
import update = require('react-addons-update');

export interface SendInvitationProps {
    presentationId:number;
}
export interface SendInvitationState {
    template: API.EmailView;
}

export class SendInvitation extends React.Component<SendInvitationProps, SendInvitationState> {
    constructor(props: SendInvitationProps) {
        super(props);
        this.state = {
            template: {
                bcc:"",
                body:"",
                cc:"",
                subject:"",
                to:""
            }
        };
        API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
        this.handleChanged = this.handleChanged.bind(this)
        this.submit = this.submit.bind(this);
    }
    inputStyle = {
        borderColor: 'red',
    }
    userAPI = new API.UsercontrollerApi();
    mailAPI = new API.PresentationmailcontrollerApi();
    loginPromise: Promise<String>;
    date = new Date();

    componentDidMount() {
        this.mailAPI.getInvitationMailTemplateUsingGET({"id":this.props.presentationId})
        .then((_template) => {
           this.setState({template : _template});
        });
        console.log(this.state.template);
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
            
        this.mailAPI.sendInvitationMailUsingPOST( {"id":this.props.presentationId, "mail": this.state.template})
        .then(() =>{
            console.log("SendInvitation: Sent mail successfully");
        }).catch((err) =>{
            console.log("SendInvitation: Error sending invitational mail");
            console.log(err);
        });
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
                            value={this.state.template.subject}
                            onChange={this.handleChanged} required />
                    </FormGroup>

                    <FormGroup controlId="formControlsText">
                        <ControlLabel>Text</ControlLabel>
                        <br />
                        <FormControl componentClass="textarea" name="text" placeholder="" rows={6}
                            value={this.state.template.body}
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
