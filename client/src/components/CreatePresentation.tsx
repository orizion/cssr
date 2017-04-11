import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button } from "react-bootstrap";
import * as API from "../services/api";
import  Select = require('react-select');

export interface CreatePresentationProps {

}
export interface CreatePresentationState {
    presentation: API.Presentation;
}

export class CreatePresentation extends React.Component<CreatePresentationProps, CreatePresentationState> {
  constructor(props: CreatePresentationProps){
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

    //get token for api calls

    this.loginPromise = fetch(API.BASE_PATH+"/user/login",{
      method:"POST",
      mode: "cors",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        "email": "adrian.ehrsam@students.fhnw.ch",
	      "password": "oij"
      })
    })
     .then(r=> r.json() as Promise<{token: string}>)
     .then((json) => {
          console.log("The received token as json: "+ json)
          API.defaultHeaders["Authorization"] = "Bearer "+ json.token;
          return json.token;
        })
        .catch(()=>{
          console.log("No login access granted");
      }) as Promise<string>;
  }
  inputStyle = {
    borderColor: 'red',
  }
  presentationAPI = new API.PresentationcontrollerApi();
  userAPI = new API.UsercontrollerApi();
  loginPromise: Promise<String>;

  options: any[] = [
    {value: 1, label: "speaker1"},
    {value: 2, label: "speaker2"},
  ];

  selectedOption : any = {}
  token = "";
  getSpeakerList = (input:any) => {
    
    return this.loginPromise.then(token => {
        return this.userAPI.getAllUsingGET(input);
     })
     .then((response) => {
      return response;
    }).then((json) => {
      return { options: json };
    });
  }
  componentDidMount() {
    //fill options with the existing speakers
    
  }
  handleChanged(e:any) {
    this.setState({
      presentation: {
        [e.target.name]: e.target.value
      }
    });

    console.log(this.state);
  }
  handleSelectChanged(e:any) {
    this.setState({
      presentation: {
        speakerId:e.value
      }
    });

    console.log(this.state);
  }
  submit() {
    this.presentationAPI.addPresentationUsingPOST({ 'pres': this.state.presentation }).then((response) => {
        this.setState({
            presentation: response
        });
    });    
  }
  render() {
    return (
      <form>
        <FormGroup controlId="formControlsTitle">
          <ControlLabel>Titel</ControlLabel>
          <br/>
          <FormControl type="text" name="title" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>Abstract</ControlLabel>
          <br/>
          <FormControl componentClass="textarea" name="abstract" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsDateTime">
          <ControlLabel>Datum</ControlLabel>
          <br/>
          <FormControl type="date" name="datetime" placeholder="yyyy.mm.dd"
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsLocation">
          <ControlLabel>Raum</ControlLabel>
          <br/>
          <FormControl type="text" name="location" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsSpeakerEmail">
          <ControlLabel>Email Referent</ControlLabel>
          <br/>       
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
