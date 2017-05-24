import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button, Popover } from "react-bootstrap";
import * as moment from 'moment';
import * as Datetime from 'react-datetime';
import * as API from "../services/api";
import Select = require('react-select');
import update = require('react-addons-update');
import { translate } from 'react-i18next';

export interface CreatePresentationProps {
    
}
export interface CreatePresentationState {
    presentation: API.Presentation;
}

@translate(['createPresentation', 'common'], { wait: true })
export class CreatePresentation extends React.Component<any, CreatePresentationState> {
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

        API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
        
        this.handleChanged = this.handleChanged.bind(this);
        this.handleSelectChanged = this.handleSelectChanged.bind(this);
        this.handleDateChanged = this.handleDateChanged.bind(this);
        this.submit = this.submit.bind(this);
    }
    inputStyle = {
        borderColor: 'red',
    }
    presentationAPI = new API.PresentationcontrollerApi();
    userAPI = new API.UsercontrollerApi();
    date = new Date();
    getSpeakerList = (input: any) => {
        return this.userAPI.getAllUsingGET(input)
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
    }
    handleChanged(e: any) {
        let val: any = e.target.value;
        this.setState({
            presentation: update(this.state.presentation, {
                [e.target.name]: { $set: val }
            })
        });
    }
    handleDateChanged(moment: any) {
        this.setState({
            presentation: update(this.state.presentation, {
                dateTime: { $set: moment.toDate() }
            })
        });
    }
    handleSelectChanged(e: any) {
        this.setState({
            presentation: update(this.state.presentation, {
                speakerId: { $set: e.value }
            })
        });
    }
    submit(e: any) {
        e.preventDefault();     
            return this.presentationAPI.addPresentationUsingPOST({ 'pres': this.state.presentation })      
            .then((response) => {
                this.setState({
                    presentation: {
                        dateTime: new Date(),
                        location: "",
                        speakerId: 0,
                        title: "",
                        abstract: "",
                    }
                });
                console.log(response);
            }).catch((r) => {
                console.log("Failed: " + r);
            });
    }
    render() {
        let t = this.props.t;    
        return (
            <form>
                <FormGroup controlId="formControlsTitle">
                    <ControlLabel>{t('title')}</ControlLabel>
                    <br />
                    <FormControl type="text" name="title" placeholder="" value={this.state.presentation.title}
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsAbstract">
                    <ControlLabel>Abstract</ControlLabel>
                    <br />
                    <FormControl componentClass="textarea" name="abstract" placeholder=""
                        value={this.state.presentation.abstract}
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsDateTime">
                    <ControlLabel>{t('date')}</ControlLabel>
                    <br />
                    
                    <Datetime input={true} locale="de" value={this.state.presentation.dateTime}
                        onChange={this.handleDateChanged} />
                </FormGroup>

                <FormGroup controlId="formControlsLocation">
                    <ControlLabel>{t('room')}</ControlLabel>
                    <br />
                    <FormControl type="text" name="location" placeholder="" value={this.state.presentation.location}
                        onChange={this.handleChanged} required />
                </FormGroup>

                <FormGroup controlId="formControlsSpeakerEmail">
                    <ControlLabel>{t('email_speaker')}</ControlLabel>
                    <br />
                    <Select.Async
                        name="speakerId"
                        value={this.state.presentation.speakerId}
                        loadOptions={this.getSpeakerList}
                        onChange={this.handleSelectChanged}
                        searchable={true}
                        clearable={true}
                    />
                </FormGroup>

                <Button type="submit" bsStyle="primary" onClick={this.submit}>
                    {t('send')}
                </Button> 
            </form>        
        );
    }
}
