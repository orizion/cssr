import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button } from "react-bootstrap";
import * as API from "../services/api";
import Select = require('react-select');
import update = require('react-addons-update');

export interface CreatePresentationProps {

}
export interface CreatePresentationState {
    presentation: API.Presentation;
}

export class CreatePresentation extends React.Component<CreatePresentationProps, CreatePresentationState> {
    constructor(props: CreatePresentationProps) {
        super(props);
        this.state = {
            presentation: {
                dateTime: new Date(),
                location: "",
                speakerId: 0,
                title: "",
                abstract: "",
            }
        };
        this.handleChanged = this.handleChanged.bind(this);
        this.handleSelectChanged = this.handleSelectChanged.bind(this);
        this.submit = this.submit.bind(this);

        //get token for api calls

        this.loginPromise = API.UsercontrollerApiFp.loginUsingPOST({creds: {"email": "adrian.ehrsam@students.fhnw.ch",
                "password": "oij" }})()
            .then((json) => {
                console.log("The received token as json: " + json)
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
    presentationAPI = new API.PresentationcontrollerApi();
    userAPI = new API.UsercontrollerApi();
    loginPromise: Promise<String>;
    token = "";
    getSpeakerList = (input: any) => {
        return this.loginPromise.then(token => {
            return this.userAPI.getAllUsingGET(input);
        })
            .then((response) => {
                let speakers: any = response.map((s) => {
                    return { label: s.email, value: s.userId };
                });
                return speakers;
            }).then((json) => {
                return { options: json };
            });
    }
    componentDidMount() {
        //fill options with the existing speakers
        console.log("changes applied");
    }
    handleChanged(e: any) {
        let val: any = e.target.value;
        this.setState({
            presentation: update(this.state.presentation, {
                [e.target.name]: { $set: val }
            })
        });
    }
    handleSelectChanged(e: any) {
        this.setState({
            presentation: update(this.state.presentation, {
                speakerId: { $set: e.value }
            })
        });

        console.log(this.state);
    }
    submit(e: any) {
        console.log("changes applied");
        console.log(API.defaultHeaders["Authorization"]);

        e.preventDefault();
        return this.loginPromise.then(token => {
            return this.presentationAPI.addPresentationUsingPOST({ 'pres': this.state.presentation });
        })
            .then((response) => {
                this.setState({
                    presentation: response
                });
                console.log(response);
            }).catch((r) => {
                console.log("Failed: " + r);
            });
    }
    render() {
        return (
            <form>
                <FormGroup controlId="formControlsTitle">
                    <ControlLabel>Titel</ControlLabel>
                    <br />
                    <FormControl type="text" name="title" placeholder=""
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsAbstract">
                    <ControlLabel>Abstract</ControlLabel>
                    <br />
                    <FormControl componentClass="textarea" name="abstract" placeholder=""
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsDateTime">
                    <ControlLabel>Datum</ControlLabel>
                    <br />
                    <FormControl type="date" name="dateTime" placeholder="yyyy.mm.dd"
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsLocation">
                    <ControlLabel>Raum</ControlLabel>
                    <br />
                    <FormControl type="text" name="location" placeholder=""
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsSpeakerEmail">
                    <ControlLabel>Email Referent</ControlLabel>
                    <br />
                    <Select.Async
                        name="speakerId"
                        value={this.state.presentation.speakerId}
                        loadOptions={this.getSpeakerList.bind(this)}
                        onChange={this.handleSelectChanged.bind(this)}
                        searchable={true}
                        clearable={true}
                    />
                </FormGroup>

                <Button type="submit" bsStyle="primary" onClick={this.submit}>
                    Senden
        </Button>
            </form>
        );
    }
}
